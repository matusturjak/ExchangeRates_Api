package sk.matusturjak.exchange_rates.predictions.armagarch;

/**
 * Trieda, ktorá je zodpovedná za vytvorenie združeného ARIMA - GARCH(1,1) modelu.
 */
public class ArimaGarchModel extends AbstractGarchModel {

    public ArimaGarchModel(ArimaModel arimaModel) throws Exception {
        super(arimaModel);
    }

    public ArimaGarchModel(double[] values) {
        super(values);
    }
}
