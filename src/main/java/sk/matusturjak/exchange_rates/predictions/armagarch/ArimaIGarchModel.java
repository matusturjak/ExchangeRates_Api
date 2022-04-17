package sk.matusturjak.exchange_rates.predictions.armagarch;

/**
 * Trieda, ktorá je zodpovedná za vytvorenie združeného ARIMA - IGARCH(1,1) modelu.
 */
public class ArimaIGarchModel extends AbstractGarchModel {

    public ArimaIGarchModel(ArimaModel model) throws Exception {
        super(model);
    }

    public ArimaIGarchModel(double[] values) {
        super(values);
    }
}
