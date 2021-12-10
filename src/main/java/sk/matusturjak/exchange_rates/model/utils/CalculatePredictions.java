package sk.matusturjak.exchange_rates.model.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArmaGarchModel;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.DoubleExponentialSmoothing;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.ExponentialSmoothing;
import sk.matusturjak.exchange_rates.predictions.exp_smoothing.SingleExponentialSmoothing;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.LatestRateService;
import sk.matusturjak.exchange_rates.service.PredictionService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CalculatePredictions {
    private final PredictionService predictionService;
    private final ExchangeRateService exchangeRateService;
    private final LatestRateService latestRateService;

    private MyDate date;

//    private static String[] currency = {
//            "EUR","CAD","HKD","PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
//            "BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
//            "ZAR","USD","MXN","ILS","GBP","KRW","MYR","ISK"
//    };

    private static String[] currency = StaticVariables.currencies;

    public CalculatePredictions(PredictionService predictionService, ExchangeRateService exchangeRateService, LatestRateService latestRateService) {
        this.predictionService = predictionService;
        this.exchangeRateService = exchangeRateService;
        this.latestRateService = latestRateService;
        this.date = new MyDate();
    }

    @Scheduled(cron = "0 10 17 * *", zone = "Europe/Paris")
    public void calculateAndSave() throws Exception {
        this.predictionService.removePredictions();

        ArmaGarchModel armaGarchModel = new ArmaGarchModel();
        for (String i : currency) {
            for (String j : currency) {
                List<ExchangeRate> rates = this.exchangeRateService.getAllRates(i, j);
                if (rates == null || rates.size() < 5)
                    continue;

                int[] ahead = {1, 2, 3, 4, 5};

                //ArmaGarchModel armaGarchModel = new ArmaGarchModel(rates.stream().mapToDouble(value -> value.getRate().getValue()).toArray());

                for (int k = 0; k < ahead.length; k++) {
                    if (ahead[k] <= 2) {
//                        double[] predictions = armaGarchModel.predict(ahead[k]);
//                        this.savePredictions(rates,predictions, i, j, "arma-garch");
                    } else if (ahead[k] == 3) {
                        ExponentialSmoothing singleExponentialSmoothing = new SingleExponentialSmoothing(rates.size(), ahead[k]);
                        ExponentialSmoothing doubleExponentialSmoothing = new DoubleExponentialSmoothing(rates.size(), ahead[k]);

                        if (singleExponentialSmoothing.getResiduals() < doubleExponentialSmoothing.getResiduals()) {
                            double[] ses = singleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[3]), 0.4);

                            this.savePredictions(rates.get(rates.size() - 1), ses, i, j, StaticVariables.SINGLE_EXP);
                        } else {
                            double[] des = doubleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[3]), 0.4);

                            this.savePredictions(rates.get(rates.size() - 1), des, i, j, StaticVariables.DOUBLE_EXP);
                        }
                    } else {
                        ExponentialSmoothing singleExponentialSmoothing = new SingleExponentialSmoothing(rates.size(), ahead[k]);
                        ExponentialSmoothing doubleExponentialSmoothing = new DoubleExponentialSmoothing(rates.size(), ahead[k]);

                        if (singleExponentialSmoothing.getResiduals() < doubleExponentialSmoothing.getResiduals()) {
                            double[] ses = singleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                                .collect(Collectors.toList()).toArray(new Double[5]), 0.3);

                            this.savePredictions(rates.get(rates.size() - 1), ses, i, j, StaticVariables.SINGLE_EXP);
                        } else {
                            double[] des = doubleExponentialSmoothing.predict(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue())
                                    .collect(Collectors.toList()).toArray(new Double[5]), 0.3);

                            this.savePredictions(rates.get(rates.size() - 1), des, i, j, StaticVariables.DOUBLE_EXP);
                        }
                    }
                }
            }
        }
    }

    private void savePredictions(ExchangeRate actualRate, double[] arr, String firstCountry, String secondCountry, String method) {
        for (int l = 0; l < arr.length; l++) {
            this.predictionService.addPrediction(
                    new Prediction(firstCountry, secondCountry, arr[l],
                            this.date.addDays(actualRate.getDate(), l + 1), method + (arr.length)
                    )
            );
        }
    }
}
