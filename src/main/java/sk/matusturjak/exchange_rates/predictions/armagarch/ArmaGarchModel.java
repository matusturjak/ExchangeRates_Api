package sk.matusturjak.exchange_rates.predictions.armagarch;

import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.*;
import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

import javax.script.ScriptException;
import java.util.HashMap;

public class ArmaGarchModel implements PredictionModelInterface {
    private HashMap<String, double[]> armaParam;
    private HashMap<String, Double> garchParam;

    private boolean isStacionary = false;

    private double[] times;
    private double[] values;

    private RenjinScriptEngine engine;

    public ArmaGarchModel() throws Exception {
        this.engine = new RenjinScriptEngine();
        this.engine.eval("library('org.renjin.cran:forecast')");
        this.engine.eval("library('org.renjin.cran:tseries')");
        this.engine.eval("library('org.renjin.cran:FinTS')");
        this.engine.eval("library('org.renjin.cran:TSA')");
        this.engine.eval("library('org.renjin.cran:tseries')");
        this.engine.eval("library('org.renjin.cran:readxl')");
    }

    public ArmaGarchModel(double[] times, double[] values) throws Exception {
        this();
        this.values = values;
        this.times = times;
    }

    public void calculateArmaGarchModel(double[] times, double[] values) throws Exception {
        this.times = times;
        this.values = values;

        this.isStacionary = this.isSerieStacionary();
        this.armaParam = this.getArmaParam();

        if (this.isHeteroskedasticityInResiduals()) {
            this.garchParam = this.getGarchParam();
        }
    }

    public boolean isHeteroskedasticityInResiduals() throws ScriptException {
        String test = "ArchTest(" + this.getVector(this.armaParam.get("RESIDUALS")) + ", lag = 1)";
        Vector result = (Vector) this.engine.eval(test);
        double pValue = result.getElementAsDouble(2);
        return pValue < 0.05;
    }

    public boolean isSerieStacionary() throws ScriptException {
        String test = "test = adf.test(" + this.getVector(this.values) + ")";
        Vector result = (Vector) this.engine.eval(test);
        double pValue = result.getElementAsDouble(3);

        return pValue <= 0.05;
    }

    public HashMap<String, double[]> getArmaParam() throws ScriptException {
        HashMap<String, double[]> map = new HashMap<>();

        String script = "";
        if (this.isStacionary) {
            script = "auto.arima(" + this.getVector(this.values) + ")";
        } else {
            this.engine.eval("diffData = as.double(diff(" + this.getVector(this.values) + "))");
            script = "auto.arima(diffData)";
        }

        Vector result = (Vector) this.engine.eval(script);
        double[] meanVector = ((DoubleArrayVector) result.getElementAsSEXP(0)).toDoubleArray();

        ListVector listVector = result.getElementAsSEXP(13);

        DoubleArrayVector ar = (DoubleArrayVector) listVector.getElementAsSEXP(0);
        DoubleArrayVector ma = (DoubleArrayVector) listVector.getElementAsSEXP(1);

        double mean = 0d;
        if (meanVector.length > 0 && meanVector.length == ar.length() + ma.length() + 1) {
            mean = meanVector[meanVector.length - 1];
        } else  {
            mean = 0;
        }

        map.put("AR", ar.toDoubleArray());
        map.put("MA", ma.toDoubleArray());
        map.put("MEAN", new double[]{mean});
        map.put("RESIDUALS", ((DoubleArrayVector) result.getElementAsSEXP(7)).toDoubleArray());

        if (!this.isStacionary) {
            map.put("DIFFERENCED", this.getDifferencedData(this.values));
        }
        return map;
    }

    public HashMap<String, Double> getGarchParam() throws Exception {
        HashMap<String, Double> map = new HashMap<>();

        if (this.armaParam.get("AR").length == 0 && this.armaParam.get("MA").length == 0) {
            return null;
        }
        this.engine.eval(new java.io.FileReader("src/main/scripts/mle_garch.R"));

        String script = "mlef<-optim(para, garch_loglik, gr = NULL,method = c(\"Nelder-Mead\"),hessian=FALSE," +
                this.getVector(this.armaParam.get("RESIDUALS")) + ",0)";
        Vector result = (Vector) this.engine.eval(script);
        double[] parameters = ((DoubleArrayVector) result.getElementAsSEXP(0)).toDoubleArray();

        map.put("OMEGA", parameters[0]);
        map.put("ALPHA", parameters[1]);
        map.put("BETA", parameters[2]);

        return map;
    }

    public String getVector(double[] values) {
        String script = "c(";
        for (Double value : values) {
            script += value + ",";
        }
        return script.substring(0, script.length() - 2) + ")";
    }

    public double[] getDifferencedData(double[] values) {
        double[] diffValues = new double[values.length - 1];
        for (int i = 1; i < values.length; i++) {
            diffValues[i-1] = values[i] - values[i-1];
        }
        return diffValues;
    }

    @Override
    public double[] fittedValues() {
        double[] residuals = this.armaParam.get("RESIDUALS");

        double[] h = new double[residuals.length];
        double[] e = new double[residuals.length];

        h[0]=residuals[0]; //TODO variance
        for (int i = 1; i < residuals.length; i++) {
            double h_t = this.garchParam.get("OMEGA") +
                    this.garchParam.get("ALPHA") * (residuals[i - 1] - this.armaParam.get("MEAN")[0]) +
                    this.garchParam.get("BETA") * h[i - 1];
            h[i] = h_t;
            e[i] = h_t * residuals[i];
        }

        double sumAR = 0;
        double sumMA = 0;

        double[] ar = this.armaParam.get("AR");
        double[] ma = this.armaParam.get("MA");

        double[] diff = this.armaParam.get("DIFFERENCED");
        double[] fitted = new double[diff == null ? this.values.length : diff.length];

        for (int i = 0; i < fitted.length; i++) {
            if (Math.max(ar.length, ma.length) > i) {
                fitted[i] = diff == null ? this.values[i] : diff[i];
                continue;
            }

            int pom = 1;
            for (int j = 0; j < ar.length; j++) {
                if (diff != null) {
                    sumAR += ar[j] * diff[i - pom++];
                } else {
                    sumAR += ar[j] * this.values[i - pom++];
                }
            }

            pom = 1;
            for (int j = 0; j < ma.length; j++) {
                sumMA += ma[j] * residuals[i - pom++];
            }
            fitted[i] = sumAR + sumMA + e[i];
        }


        if (diff != null) {
            double[] unDiffFitted = new double[fitted.length];
            unDiffFitted[0] = this.values[0];
            for (int i = 1; i < fitted.length; i++) {
                unDiffFitted[i] = fitted[i - 1] + this.values[i - 1];
            }
            return unDiffFitted;
        }

        return fitted;
    }

    public double predict() {

        return 0;
    }

}
