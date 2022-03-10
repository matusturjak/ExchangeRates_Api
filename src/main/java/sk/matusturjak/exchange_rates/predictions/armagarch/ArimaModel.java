package sk.matusturjak.exchange_rates.predictions.armagarch;

import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashMap;

public class ArimaModel extends PredictionModel {
    private HashMap<String, double[]> armaParam;
    private boolean isStacionary = false;

    public ArimaModel() throws ScriptException {
        super();
        this.engine.eval("library('org.renjin.cran:forecast')");
        this.engine.eval("library('org.renjin.cran:tseries')");
        this.engine.eval("library('org.renjin.cran:FinTS')");
        this.engine.eval("library('org.renjin.cran:TSA')");
        this.engine.eval("library('org.renjin.cran:readxl')");
    }

    public ArimaModel(double[] values) throws ScriptException {
        super(values);
        this.engine.eval("library('org.renjin.cran:forecast')");
        this.engine.eval("library('org.renjin.cran:tseries')");
        this.engine.eval("library('org.renjin.cran:FinTS')");
        this.engine.eval("library('org.renjin.cran:TSA')");
        this.engine.eval("library('org.renjin.cran:readxl')");
    }

    public void calculateArmaModel() throws Exception {
        this.isStacionary = this.isSerieStacionary();
        this.armaParam = this.getArmaParam();
    }

    public boolean isHeteroskedasticityInResiduals() throws ScriptException {
        String test = "ArchTest(" + this.getVector(this.armaParam.get("RESIDUALS")) + ", lag=1)";
        Vector result = (Vector) this.engine.eval(test);
        double pValue = result.getElementAsDouble(2);
        return pValue < 0.05;
    }

    public boolean isSerieStacionary() throws ScriptException {
        String test = "test = adf.test(" + this.getVector(this.values) + ")";
        Vector result = (Vector) this.engine.eval(test);
        double pValue = result.getElementAsDouble(3);

        return pValue <= 0.01;
    }

    public HashMap<String, double[]> getArmaParam() throws ScriptException {
        HashMap<String, double[]> map = new HashMap<>();

        String script = "ar = auto.arima(" + this.getVector(this.values) + ")";

        Vector result = (Vector) this.engine.eval(script);

        Vector predictionScript = (Vector) this.engine.eval("predict(ar, n.ahead=1)");
        double prediction = ((DoubleArrayVector) predictionScript.getElementAsSEXP(0)).toDoubleArray()[0];

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

        double[] residuals = ((DoubleArrayVector) result.getElementAsSEXP(7)).toDoubleArray();
        for (int i = 0; i < residuals.length; i++)  residuals[i] = NumHelper.roundAvoid(residuals[i], 6);

        double[] fitted = new double[residuals.length + 1];
        Arrays.fill(fitted, 0);

        for (int i = 0; i < residuals.length; i++) {
            fitted[i] = this.values[i] - residuals[i];
        }
        fitted[fitted.length - 1] = prediction;

        map.put("AR", ar.toDoubleArray());
        map.put("MA", ma.toDoubleArray());
        map.put("MEAN", new double[]{mean});
        map.put("RESIDUALS", residuals);
        map.put("SRESIDUALS", new double[]{});
        map.put("FITTED", fitted);

        return map;
    }

    public String getVector(double[] values) {
        String script = "c(";
        for (Double value : values) {
            script += value + ",";
        }
        return script.substring(0, script.length() - 1) + ")";
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
        return this.armaParam.get("FITTED");
    }

    public double predict() {
        this.fittedValues = this.fittedValues();
        return this.fittedValues[this.fittedValues.length - 1];
    }

    public String getResiduals() {
        double[] resi = this.armaParam.get("RESIDUALS");
        StringBuilder arr = new StringBuilder();

        for (double v : resi) arr.append(NumHelper.roundAvoid(v, 4)).append(",");

        return arr.substring(0, arr.length() - 1);
    }

    public String getFittedValues() {
        StringBuilder arr = new StringBuilder();

        for (double fittedValue : this.fittedValues) arr.append(NumHelper.roundAvoid(fittedValue, 6)).append(",");
        return arr.substring(0, arr.length() - 1);
    }
}
