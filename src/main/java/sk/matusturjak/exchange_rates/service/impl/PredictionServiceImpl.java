package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.repository.PredictionsRepository;
import sk.matusturjak.exchange_rates.service.PredictionService;

import java.util.List;

/**
 * Service, ktorá komunikuje s vrstvou Repository a v aplikácií poskytuje informácie o budúcich hodnotách menových kurzov.
 */
@Service
public class PredictionServiceImpl implements PredictionService {

    private final PredictionsRepository predictionsRepository;

    public PredictionServiceImpl(PredictionsRepository predictionsRepository) {
        this.predictionsRepository = predictionsRepository;
    }

    @Override
    public List<Prediction> getPredictions(String from, String to, Integer numberOfPredictions) {
        List<Prediction> predictions = this.predictionsRepository.getPredictions(from, to, numberOfPredictions);
        predictions.forEach(prediction -> {
            String method = prediction.getMethod();
            if (method.equals("arma_garch1")) {
                prediction.setMethod("ARIMA-GARCH");
            } else if (method.equals("arma_igarch1")) {
                prediction.setMethod("ARIMA-IGARCH");
            } else if (method.equals("exp3") || method.equals("exp5")) {
                prediction.setMethod("Exponential smoothing");
            }
        });
        return predictions;
    }

    @Override
    public List<Prediction> getPredictions(String from, String to, String method) {
        return this.predictionsRepository.getPredictions(from, to, method);
    }

    @Override
    public void removePredictions() {
        this.predictionsRepository.deleteAll();
    }

    @Override
    public void addPrediction(Prediction prediction) {
        this.predictionsRepository.save(prediction);
    }

    @Override
    public void updatePredictions(Prediction prediction) {
        this.predictionsRepository.updatePredictions(
                prediction.getRate().getFromCurr(),
                prediction.getRate().getToCurr(),
                prediction.getMethod(),
                prediction.getRate().getValue(),
                prediction.getDate()
        );
    }

    @Override
    public Prediction findPrediction(String from, String to, String method) {
        return this.predictionsRepository.findPrediction(from, to, method);
    }
}
