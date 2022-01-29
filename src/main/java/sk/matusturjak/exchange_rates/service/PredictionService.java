package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.Prediction;

import java.util.Date;
import java.util.List;

public interface PredictionService {
    List<Prediction> getPredictions(String from, String to, Integer numberOfPredictions);
    Prediction findPrediction(String from, String to, String method);

    void removePredictions();
    void addPrediction(Prediction prediction);
    void updatePredictions(Prediction prediction);
}
