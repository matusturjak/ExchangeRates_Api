package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

import sk.matusturjak.exchange_rates.model.utils.NumHelper;

public class DoubleExponentialSmoothing implements ExponentialSmoothing {

    private Double[] modelData;
    private Double b0;
    private Double b1;
    private Double st0;
    private Double st1;
    private Double mse;

    /**
     * Parametricky konstruktor triedy.
     * @param length
     * @param ahead
     */
    public DoubleExponentialSmoothing(int length, int ahead){
        this.modelData = new Double[length + ahead];
        this.b0 = 0d;
        this.b1 = 0d;
        this.st0 = 0d;
        this.st1 = 0d;
        this.mse = 0d;
    }

    /**
     * Metoda, ktora pocita predikcie dat vstupujucich ako parameter o jedno casove obdobie dopredu
     * pomocou dvojiteho exponencialneho vyrovnavania.
     * @param data
     * @param alpha
     */
    @Override
    public void fit(Double[] data, Double alpha) {
        if(alpha > 1 || alpha <= 0) {
            throw new RuntimeException("parameter alpha je nekorektne zadany");
        }

        int[] time = new int[data.length];
        for(int i = 0;i < data.length; i++)
            time[i] = (i + 1);

        LinearRegression linearRegression = new LinearRegression();
        linearRegression.fit(time,data);
        this.b0 = linearRegression.getIntercept();
        this.b1 = linearRegression.getSlope();

        this.st0 = this.b0 - this.b1*((1 - alpha)/alpha);
        this.st1 = this.b0 - 2*((1-alpha)/alpha)*this.b1;
        this.modelData[0] = this.b0 + this.b1;

        for (int i = 0; i < data.length; i++){
            this.st0 = alpha*data[i] + (1-alpha)*this.st0;
            this.st1 = alpha*this.st0 + (1- alpha)*this.st1;

            this.b0 = 2*this.st0 - this.st1;
            this.b1 = ((alpha)/(1-alpha))*(this.st0 - this.st1);

            this.modelData[i+1] = this.b0 + this.b1;
        }

        for(int i=0; i < data.length; i++) {
            this.mse += Math.pow(data[i] - this.modelData[i],2);
        }
        this.mse = this.mse/data.length;
    }

    /**
     * Funkcia, ktora vypocita predikcie o n stanovenych casovych obdobi dopredu
     * pomocou dvojiteho exponencialneho vyrovnavania. Nasledne hodnoty predikcii vrati.
     * @param data
     * @param alpha
     * @return
     */
    @Override
    public double[] predict(Double[] data, Double alpha) {
        this.fit(data,alpha);
        double[] predictions = new double[this.modelData.length - data.length];

        predictions[0] = NumHelper.roundAvoid(this.modelData[data.length], 4);
        for(int i = 0; i < this.modelData.length - data.length - 1; i++){
            this.st0 = alpha*this.modelData[data.length + i] + (1-alpha)*this.st0;
            this.st1 = alpha*this.st0 + (1- alpha)*this.st1;

            b0 = 2*this.st0 - this.st1;
            b1 = ((alpha)/(1-alpha))*(this.st0 - this.st1);

            this.modelData[data.length + i + 1] = b0 + b1;
            predictions[i+1] = NumHelper.roundAvoid(this.modelData[data.length + i + 1], 4);
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

    @Override
    public double[] fittedValues() {
        return new double[0];
    }
}
