package sk.matusturjak.exchange_rates.predictions.armagarch;

import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.Vector;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractGarchModel extends PredictionModel implements PredictionModelInterface {
    protected ArimaModel arimaModel;

    protected double[] fittedValues;

    protected Map<String, double[]> armaParam;
    protected Map<String, Double> garchParam;

    protected List<Double> sigma;

    public AbstractGarchModel(ArimaModel model) throws Exception {
        this.arimaModel = model;
        this.values = model.getValues();
        this.engine = model.getEngine();

        this.armaParam = model.getArmaParam();
        this.garchParam = this.getGarchParam();

        this.sigma = new LinkedList<>();
    }

    public AbstractGarchModel(double[] values) {
        super(values);
    }

    @Override
    public double[] fittedValues() {
        double[] residuals = this.armaParam.get("RESIDUALS");

        double[] h = new double[residuals.length];
        double[] z = new double[residuals.length];
        double[] e = new double[residuals.length];

        List<Double> listSigma = new LinkedList<>();
        if (garchParam != null) {
            h[0]=this.garchParam.get("OMEGA") / (1 - this.garchParam.get("ALPHA")); //TODO variance
            listSigma.add(h[0]);
            for (int i = 1; i <= residuals.length; i++) {
                double h_t = 0.0d;
                if (this instanceof ArimaGarchModel) {
                    h_t = this.garchParam.get("OMEGA") +
                            this.garchParam.get("ALPHA") * Math.pow(residuals[i - 1] - this.armaParam.get("MEAN")[0], 2) +
                            this.garchParam.get("BETA") * h[i - 1];
                } else {
                    h_t = this.garchParam.get("OMEGA") +
                            this.garchParam.get("ALPHA") * Math.pow(residuals[i - 1] - this.armaParam.get("MEAN")[0], 2) +
                            (1 - this.garchParam.get("ALPHA")) * h[i - 1];
                }

                listSigma.add(Math.sqrt(h_t));

                if (i < residuals.length) {
                    h[i] = h_t;
                }
                z[i - 1] = residuals[i - 1] / Math.sqrt(h[i - 1]);
                e[i - 1] = z[i - 1] * Math.sqrt(h[i - 1]);
            }
        }

        this.setSigma(listSigma);
        this.armaParam.replace("SRESIDUALS", z);

        double[] ar = this.armaParam.get("AR");
        double[] ma = this.armaParam.get("MA");

        double[] diff = this.armaParam.get("DIFFERENCED");
        double[] fitted = new double[diff == null ? this.values.length + 1 : diff.length + 1];

        for (int i = 0; i < fitted.length; i++) {
            double sumAR = 0;
            double sumMA = 0;
            double error = 0;

            if (Math.max(ar.length, ma.length) > i) {
                fitted[i] = diff == null ? this.values[i] : diff[i];
                continue;
            }

            if (i == fitted.length - 1) {
                int pom = 1;
                for (int j = 0; j < ar.length; j++) {
                    if (diff != null) {
                        sumAR += ar[j] * diff[diff.length - pom++];
                    } else {
                        sumAR += ar[j] * this.values[this.values.length - pom++];
                    }
                }

                pom = 1;
                for (int j = 0; j < ma.length; j++) {
                    sumMA += ma[j] * residuals[residuals.length - pom++];
                }
            } else {
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
                error = e[i];
            }
            fitted[i] = sumAR + sumMA + error;
        }


        if (diff != null) {
            double[] unDiffFitted = new double[fitted.length + 1];
            unDiffFitted[0] = this.values[0];
            for (int i = 1; i <= fitted.length; i++) {
                unDiffFitted[i] = fitted[i - 1] + this.values[i - 1];
            }
            return unDiffFitted;
        }

        return fitted;
    }

    private HashMap<String, Double> getGarchParam() throws Exception {
        HashMap<String, Double> map = new HashMap<>();

        this.engine.eval(new java.io.FileReader("src/main/scripts/mle_garch.R"));

        String script  = null;
        if (this instanceof ArimaGarchModel) {
            script = "mlef<-optim(para, garch_loglik, gr = NULL,method = c(\"Nelder-Mead\"),hessian=FALSE," +
                    this.getVector(this.armaParam.get("RESIDUALS")) + ",0)";
        } else {
            script = "mlef<-optim(para, igarch_loglik, gr = NULL,method = c(\"Nelder-Mead\"),hessian=FALSE," +
                    this.getVector(this.armaParam.get("RESIDUALS")) + ",0)";
        }

        Vector result = (Vector) this.engine.eval(script);
        double[] parameters = ((DoubleArrayVector) result.getElementAsSEXP(0)).toDoubleArray();

        map.put("OMEGA", parameters[0]);
        map.put("ALPHA", parameters[1]);
        if (this instanceof ArimaGarchModel) {
            map.put("BETA", parameters[2]);
        }

        return map;
    }

    public String getVector(double[] values) {
        String script = "c(";
        for (Double value : values) {
            script += value + ",";
        }
        return script.substring(0, script.length() - 1) + ")";
    }

    public String getFittedValues() {
        StringBuilder arr = new StringBuilder();

        for (double fittedValue : this.fittedValues) arr.append(NumHelper.roundAvoid(fittedValue, 6)).append(",");
        return arr.substring(0, arr.length() - 1);
    }

    public String getResiduals() {
        double[] resi = this.armaParam.get("RESIDUALS");
        StringBuilder arr = new StringBuilder();

        for (double v : resi) arr.append(NumHelper.roundAvoid(v, 4)).append(",");

        return arr.substring(0, arr.length() - 1);
    }

    public void setSigma(List<Double> sigmaList) {
        this.sigma = sigmaList;
    }

    public List<Double> getSigma() {
        return this.sigma;
    }

    public String getSigmaString() {
        List<String> sigmaListString = this.sigma.stream().map(aDouble -> NumHelper.roundAvoid(aDouble,4) + "").collect(Collectors.toList());
        return String.join(",", sigmaListString);
    }

    public double predict() {
        this.fittedValues = this.fittedValues();
        return this.fittedValues[this.fittedValues.length - 1];
    }
}
