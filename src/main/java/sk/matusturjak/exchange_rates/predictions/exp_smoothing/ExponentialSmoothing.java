package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

public interface ExponentialSmoothing {
    void fit(Double[] data, Double alpha);
    Double[] predict(Double[] data, Double alpha);
    Double getResiduals();
}
