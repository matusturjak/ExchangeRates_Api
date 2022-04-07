package sk.matusturjak.exchange_rates.predictions.armagarch;

public class ArimaIGarchModel extends AbstractGarchModel {

    public ArimaIGarchModel(ArimaModel model) throws Exception {
        super(model);
    }

    public ArimaIGarchModel(double[] values) {
        super(values);
    }
}
