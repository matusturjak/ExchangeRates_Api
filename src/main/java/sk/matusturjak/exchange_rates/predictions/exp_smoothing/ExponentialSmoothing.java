package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

public interface ExponentialSmoothing extends PredictionModelInterface {
    void fit(Double[] data, Double alpha);
    double[] predict(Double[] data, Double alpha);
    double getResiduals();
}
