package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

public class SingleExponentialSmoothing implements ExponentialSmoothing {
    private Double[] modelData;
    private Double st0;
    private Double mse;

    /**
     * Parametricky konstruktor triedy
     * @param length
     * @param ahead
     */
    public SingleExponentialSmoothing(int length, int ahead){
        this.modelData = new Double[length + ahead];
        this.st0 = 0d;
        this.mse = 0d;
    }

    /**
     * Metoda, ktora pocita predikcie dat vstupujucich ako parameter o jedno casove obdobie dopredu
     * pomocou jednoducheho exponencialneho vyrovnavania.
     * @param data
     * @param alpha
     */
    @Override
    public void fit(Double[] data, Double alpha) {
        if(alpha > 1 || alpha <= 0) {
            throw new RuntimeException("parameter alpha je nekorektne zadany");
        }

        Double sum = 0d;
        for(int i = 0; i < data.length; i++){
            sum += data[i];
        }

        this.st0 = sum/data.length;
        this.modelData[0] = sum/data.length;

        for(int i = 0; i < data.length; i++){
            this.modelData[i+1] = alpha*data[i] + (1 - alpha)*this.st0;
            this.st0 = this.modelData[i+1];
        }

        for(int i=0; i < data.length; i++) {
            this.mse += Math.pow(data[i] - this.modelData[i],2);
        }
        this.mse = this.mse/data.length;

    }

    /**
     * Funkcia, ktora vypocita predikcie o n stanovenych casovych obdobi dopredu
     * pomocou jednoducheho exponencialneho vyrovnavania. Nasledne hodnoty predikcii vrati.
     * @param data
     * @param alpha
     * @return
     */
    @Override
    public double[] predict(Double[] data, Double alpha) {
        this.fit(data, alpha);
        double[] predictions = new double[this.modelData.length - data.length];
        predictions[0] = this.modelData[data.length];
        for(int i = 0;i < this.modelData.length - data.length - 1; i++){
            this.modelData[data.length + i + 1] = alpha*this.modelData[data.length + i] + (1 - alpha)*this.st0;
            this.st0 = this.modelData[data.length + i + 1];
            predictions[i+1] = this.modelData[data.length + i + 1];
        }
        return predictions;
    }

    /**
     * Vrati priemernu stvorcovu chybu predikcie.
     * @return
     */
    @Override
    public double getResiduals() {
        return this.mse;
    }
}
