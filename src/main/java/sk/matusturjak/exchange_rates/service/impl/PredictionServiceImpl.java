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

    private final PredictionsRepository predictionsRepository;

    public PredictionServiceImpl(PredictionsRepository predictionsRepository) {
        this.predictionsRepository = predictionsRepository;
    }

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
    public void updatePredictions(Prediction prediction) {
        this.predictionsRepository.updatePredictions(
                prediction.getRate().getFirstCountry(),
                prediction.getRate().getSecondCountry(),
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
