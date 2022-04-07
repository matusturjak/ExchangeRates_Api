package sk.matusturjak.exchange_rates.predictions.armagarch;

public class ArimaGarchModel extends AbstractGarchModel {

    public ArimaGarchModel(ArimaModel arimaModel) throws Exception {
        super(arimaModel);
    }

    public ArimaGarchModel(double[] values) {
        super(values);
    }
}
