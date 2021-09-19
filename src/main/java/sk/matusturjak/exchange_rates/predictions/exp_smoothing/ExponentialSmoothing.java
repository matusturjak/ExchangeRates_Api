package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

public interface ExponentialSmoothing {
    void fit(Double[] data, Double alpha);

    double[] predict(Double[] data, Double alpha);

    double getResiduals();
}
