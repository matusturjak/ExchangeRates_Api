package sk.matusturjak.exchange_rates.predictions.armagarch;

import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.*;
import org.renjin.sexp.Vector;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

public class ArmaGarchModel implements PredictionModelInterface {
    private HashMap<String, double[]> armaParam;
    private HashMap<String, Double> garchParam;
    private String sigma;

    private boolean isStacionary = false;

    private double[] values;
    private double[] fittedValues;

    private RenjinScriptEngine engine;

    public ArmaGarchModel() throws Exception {
        this.engine = new RenjinScriptEngine();
        this.engine.eval("library('org.renjin.cran:forecast')");
        this.engine.eval("library('org.renjin.cran:tseries')");
        this.engine.eval("library('org.renjin.cran:FinTS')");
        this.engine.eval("library('org.renjin.cran:TSA')");
        this.engine.eval("library('org.renjin.cran:readxl')");
        this.sigma = "";
    }

    public ArmaGarchModel(double[] values) throws Exception {
        this();
        this.values = values;
    }

    public ArmaGarchModel calculateArmaGarchModel(double[] values) throws Exception {
        this.values = values;
        this.sigma = "";

        this.armaParam = this.getArmaParam();

        if (this.isHeteroskedasticityInResiduals()) {
            this.garchParam = this.getGarchParam();
        }
        return this;
    }

    public boolean isHeteroskedasticityInResiduals() throws ScriptException {
        String test = "ArchTest(" + this.getVector(this.armaParam.get("RESIDUALS")) + ", lag=1)";
        Vector result = (Vector) this.engine.eval(test);
        double pValue = result.getElementAsDouble(2);
        return pValue < 0.05;
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

    private HashMap<String, Double> getGarchParam() throws Exception {
        HashMap<String, Double> map = new HashMap<>();

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
        double[] residuals = this.armaParam.get("RESIDUALS");

        double[] h = new double[residuals.length];
        double[] z = new double[residuals.length];
        double[] e = new double[residuals.length];
//        Arrays.fill(e, 0);

        List<Double> listSigma = new LinkedList<>();
        if (garchParam != null) {
            h[0]=this.garchParam.get("OMEGA") / (1 - this.garchParam.get("BETA") - this.garchParam.get("ALPHA")); //TODO variance
            listSigma.add(h[0]);
            for (int i = 1; i <= residuals.length; i++) {
                double h_t = 0.0d;
                h_t = this.garchParam.get("OMEGA") +
                        this.garchParam.get("ALPHA") * Math.pow(residuals[i - 1] - this.armaParam.get("MEAN")[0], 2) +
                        this.garchParam.get("BETA") * h[i - 1];

                listSigma.add(Math.sqrt(h_t));

                if (i < residuals.length) {
                    h[i] = h_t;
                }
                z[i - 1] = residuals[i - 1] / Math.sqrt(h[i - 1]);
                e[i - 1] = z[i - 1] * Math.sqrt(h[i - 1]);
            }
        }

        this.setSigma(listSigma);

        return this.armaParam.get("FITTED");
    }

    public double predict() {
        this.fittedValues = this.fittedValues();
        return this.fittedValues[this.fittedValues.length - 1];
    }

    public String getFittedValues() {
        StringBuilder arr = new StringBuilder();

        for (double fittedValue : this.fittedValues) arr.append(NumHelper.roundAvoid(fittedValue, 6)).append(",");
        return arr.substring(0, arr.length() - 1);
    }

    private void setSigma(List<Double> sigmaList) {
        List<String> sigmaListString = sigmaList.stream().map(aDouble -> NumHelper.roundAvoid(aDouble,4) + "").collect(Collectors.toList());
        this.sigma = String.join(",", sigmaListString);
    }

    public String getSigma() {
        return this.sigma;
    }

    public String getResiduals() {
        double[] resi = this.armaParam.get("RESIDUALS");
        StringBuilder arr = new StringBuilder();

        for (double v : resi) arr.append(NumHelper.roundAvoid(v, 4)).append(",");

        return arr.substring(0, arr.length() - 1);
    }
}
