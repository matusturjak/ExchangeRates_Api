package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.repository.PredictionsRepository;
import sk.matusturjak.exchange_rates.service.PredictionService;

import java.util.Date;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private PredictionsRepository predictionsRepository;

    @Override
    public List<Prediction> getPredictions(String from, String to, Integer numberOfPredictions) {
        return this.predictionsRepository.getPredictions(from, to, numberOfPredictions);
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
    public void updatePredictions(String from, String to, String method, double value, Date date) {
        this.predictionsRepository.updatePredictions(from, to, method, value, date);
    }

    @Override
    public Prediction findPrediction(String from, String to, String method) {
        return this.predictionsRepository.findPrediction(from, to, method);
    }
}
