package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

/**
 * Interface, ktorý definuje metódy pre modely exponenciálneho vyrovnávania
 */
public interface ExponentialSmoothing extends PredictionModelInterface {
    void fit(Double[] data, Double alpha);
    double[] predict(Double[] data, Double alpha);
    double getMSE();
    String getResiduals();
    String getFitted();
}
