package sk.matusturjak.exchange_rates.model.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.ModelOutput;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArimaGarchModel;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArimaIGarchModel;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArimaModel;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArmaGarchModel;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.DoubleExponentialSmoothing;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.ExponentialSmoothing;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.SingleExponentialSmoothing;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.ModelOutputService;
import sk.matusturjak.exchange_rates.service.PredictionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Trieda zodpovedná za výpočet predikcií a ich následné uloženie do databázy
 */
@Component
public class CalculatePredictions {
    private final PredictionService predictionService;
    private final ExchangeRateService exchangeRateService;
    private final ModelOutputService modelOutputService;

    private MyDate date;

//    private static String[] currency = {
//            "EUR","CAD","HKD","PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
//            "BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
//            "ZAR","USD","MXN","ILS","GBP","KRW","MYR","ISK"
//    };

    private static String[] currency = StaticVariables.currencies;

    public CalculatePredictions(PredictionService predictionService, ExchangeRateService exchangeRateService, ModelOutputService modelOutputService) {
        this.predictionService = predictionService;
        this.exchangeRateService = exchangeRateService;
        this.modelOutputService = modelOutputService;
        this.date = new MyDate();
    }

    @Scheduled(cron = "0 10 23 * * 1-5", zone = "Europe/Paris")
    public void calculateAndSave() throws Exception {
        this.predictionService.removePredictions();

//        ArmaGarchModel armaGarchModel = new ArmaGarchModel();
        ArimaModel arimaModel = new ArimaModel();
        for (String i : currency) {
            for (String j : currency) {
                List<ExchangeRate> rates = this.exchangeRateService.getLastRates(i, j, StaticVariables.MODEL_DAYS);
                if (rates == null || rates.size() < 5)
                    continue;

                int[] ahead = {1, 3, 5};

                for (int k = 0; k < ahead.length; k++) {
                    if (ahead[k] == 1) {
                        double[] ratesArray = new double[rates.size()];
                        for (int l = 0; l < ratesArray.length; l++) ratesArray[l] = rates.get(l).getRate().getValue();
                        arimaModel.setValues(ratesArray);
                        arimaModel.calculateArmaModel();

                        ArimaGarchModel arimaGarchModel = null;
                        ArimaIGarchModel arimaIGarchModel = null;
                        if (arimaModel.isHeteroskedasticityInResiduals()) {
                            arimaGarchModel = new ArimaGarchModel(arimaModel);
                            arimaIGarchModel = new ArimaIGarchModel(arimaModel);

                            double arimaGarchPrediction = NumHelper.roundAvoid(arimaGarchModel.predict(), 4);
                            double arimaIGarchPrediction = NumHelper.roundAvoid(arimaIGarchModel.predict(), 4);

                            this.savePredictions(
                                    rates.get(rates.size() - 1), new double[]{arimaGarchPrediction}, i, j, StaticVariables.ARMA_GARCH, arimaGarchModel.getResiduals(), arimaGarchModel.getSigmaString(), arimaGarchModel.getFittedValues()
                            );

                            this.savePredictions(
                                    rates.get(rates.size() - 1), new double[]{arimaIGarchPrediction}, i, j, StaticVariables.ARMA_IGARCH, arimaIGarchModel.getResiduals(), arimaIGarchModel.getSigmaString(), arimaIGarchModel.getFittedValues()
                            );
                        } else {
                            arimaModel.calculateArmaModel();

                            double armaPrediction = NumHelper.roundAvoid(arimaModel.predict(), 4);
                            this.savePredictions(
                                    rates.get(rates.size() - 1), new double[]{armaPrediction}, i, j, StaticVariables.ARMA_GARCH, arimaModel.getResiduals(), null, arimaModel.getFittedValues()
                            );
                        }

//                        armaGarchModel = armaGarchModel.calculateArmaGarchModel(ratesArray);
//
//                        double armaPrediction = NumHelper.roundAvoid(armaGarchModel.predict(), 4);
//                        this.savePredictions(
//                                rates.get(rates.size() - 1), new double[]{armaPrediction}, i, j, StaticVariables.ARMA_GARCH, armaGarchModel.getResiduals(), armaGarchModel.getSigma(), armaGarchModel.getFittedValues()
//                        );
                    } else if (ahead[k] == 3) {
                        ExponentialSmoothing singleExponentialSmoothing = new SingleExponentialSmoothing(rates.size(), ahead[k]);
                        ExponentialSmoothing doubleExponentialSmoothing = new DoubleExponentialSmoothing(rates.size(), ahead[k]);

                        if (singleExponentialSmoothing.getMSE() < doubleExponentialSmoothing.getMSE()) {
                            double[] ses = singleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[3]), 0.4);

                            this.savePredictions(rates.get(rates.size() - 1), ses, i, j, StaticVariables.EXP_SMOOTHING, singleExponentialSmoothing.getResiduals(), null, singleExponentialSmoothing.getFitted());
                        } else {
                            double[] des = doubleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[3]), 0.4);

                            this.savePredictions(rates.get(rates.size() - 1), des, i, j, StaticVariables.EXP_SMOOTHING, doubleExponentialSmoothing.getResiduals(), null, doubleExponentialSmoothing.getFitted());
                        }
                    } else {
                        ExponentialSmoothing singleExponentialSmoothing = new SingleExponentialSmoothing(rates.size(), ahead[k]);
                        ExponentialSmoothing doubleExponentialSmoothing = new DoubleExponentialSmoothing(rates.size(), ahead[k]);

                        if (singleExponentialSmoothing.getMSE() < doubleExponentialSmoothing.getMSE()) {
                            double[] ses = singleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[5]), 0.3);

                            this.savePredictions(rates.get(rates.size() - 1), ses, i, j, StaticVariables.EXP_SMOOTHING, singleExponentialSmoothing.getResiduals(), null, singleExponentialSmoothing.getFitted());
                        } else {
                            double[] des = doubleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                    .collect(Collectors.toList()).toArray(new Double[5]), 0.3);

                            this.savePredictions(rates.get(rates.size() - 1), des, i, j, StaticVariables.EXP_SMOOTHING, doubleExponentialSmoothing.getResiduals(), null, doubleExponentialSmoothing.getFitted());
                        }
                    }
                }
            }
        }
    }

    private void savePredictions(ExchangeRate actualRate, double[] arr, String firstCountry, String secondCountry, String method, String residuals, String sigma, String fitted) {
        for (int l = 0; l < arr.length; l++) {
            Prediction prediction = this.predictionService.findPrediction(firstCountry, secondCountry, method);

            String[] sigmaArr = sigma == null ? null : sigma.split(",");
            Double predictedSigma = sigmaArr == null ? null : Double.parseDouble(sigmaArr[sigmaArr.length - 1]);

            if (prediction == null) {
                Prediction newPrediction = new Prediction(firstCountry, secondCountry, arr[l], this.date.addDays(actualRate.getDate(), l + 1), method + (arr.length), predictedSigma);
                this.predictionService.addPrediction(newPrediction);
            } else {
                Prediction updatedPrediction = new Prediction(firstCountry, secondCountry, arr[l], this.date.addDays(actualRate.getDate(), l + 1), method + (arr.length), predictedSigma);
                this.predictionService.updatePredictions(updatedPrediction);
            }

            ModelOutput modelOutput = this.modelOutputService.findModelOutput(firstCountry, secondCountry, method + (arr.length));
            ModelOutput toBeAdded = new ModelOutput(method + (arr.length), firstCountry, secondCountry, residuals, sigma, fitted);
            if (modelOutput == null) {
                this.modelOutputService.addModelOutput(toBeAdded);
            } else {
                this.modelOutputService.updateModelOutput(toBeAdded);
            }
        }
    }
}
