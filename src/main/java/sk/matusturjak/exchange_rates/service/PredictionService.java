package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.Prediction;

import java.util.Date;
import java.util.List;

/**
 * Interface reprezentujúci funkcie, ktoré poskytujú informácie o budúcich hodnotách menových kurzov.
 */
public interface PredictionService {
    List<Prediction> getPredictions(String from, String to, Integer numberOfPredictions);
    List<Prediction> getPredictions(String from, String to, String method);
    Prediction findPrediction(String from, String to, String method);

    void removePredictions();
    void addPrediction(Prediction prediction);
    void updatePredictions(Prediction prediction);
}
