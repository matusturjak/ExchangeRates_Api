package sk.matusturjak.exchange_rates.predictions.exp_smoothing;

import sk.matusturjak.exchange_rates.model.utils.NumHelper;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Trieda, ktorá je zodpovedná za tvorbu modelu jednoduchého exponenciálneho vyrovnávania pre zadaný časový rad.
 */
public class SingleExponentialSmoothing implements ExponentialSmoothing {
    private Double[] modelData;
    private Double st0;
    private Double mse;
    private String residuals;

    /**
     * Parametricky konstruktor triedy
     * @param length
     * @param ahead
     */
    public SingleExponentialSmoothing(int length, int ahead){
        this.modelData = new Double[length + ahead];
        this.st0 = 0d;
        this.mse = 0d;
        this.residuals = "";
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
            this.residuals = this.residuals + NumHelper.roundAvoid(data[i] - this.modelData[i],4) + ",";
        }
        this.residuals = this.residuals.substring(0, this.residuals.length() - 1);
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

        predictions[0] = NumHelper.roundAvoid(this.modelData[data.length], 4);
        for(int i = 0;i < this.modelData.length - data.length - 1; i++){
            this.modelData[data.length + i + 1] = alpha*this.modelData[data.length + i] + (1 - alpha)*this.st0;
            this.st0 = this.modelData[data.length + i + 1];
            predictions[i+1] = NumHelper.roundAvoid(this.modelData[data.length + i + 1], 4);
        }
        return predictions;
    }

    /**
     * Vrati priemernu stvorcovu chybu predikcie.
     * @return
     */
    @Override
    public double getMSE() {
        return this.mse;
    }

    @Override
    public double[] fittedValues() {
        return new double[0];
    }

    @Override
    public String getResiduals() {
        return residuals;
    }

    @Override
    public String getFitted() {
        for (int i = 0; i < this.modelData.length; i++) this.modelData[i] = NumHelper.roundAvoid(this.modelData[i], 4);
        return Stream.<Double[]>of(this.modelData).map(Arrays::toString).collect(Collectors.joining(","))
                .replace("[", "").replace("]","").replace(" ", "");
    }
}
