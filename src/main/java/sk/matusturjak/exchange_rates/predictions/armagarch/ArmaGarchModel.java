package sk.matusturjak.exchange_rates.predictions.armagarch;


import net.finmath.timeseries.TimeSeries;
import net.finmath.timeseries.models.parametric.ARMAGARCH;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.Vector;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashMap;

public class ArmaGarchModel {
    private ARMAGARCH armagarch;
    private TimeSeries values;

    private RenjinScriptEngine engine;

    public ArmaGarchModel(double[] times, double[] values) throws Exception {
        this.values = new TimeSeries(times, values);
        this.armagarch = new ARMAGARCH(this.values);

        this.engine = new RenjinScriptEngine();
        this.engine.eval("library('org.renjin.cran:forecast')");
        this.engine.eval("library('org.renjin.cran:tseries')");
    }

    public boolean isSerieStacionary() throws ScriptException {
        String kpssTest = "test = adf.test(" + this.getVector() + ")";
        Vector result = (Vector) this.engine.eval(kpssTest);
        double pValue = result.getElementAsDouble(3);

        return pValue <= 0.05;
    }

    private String getVector() {
        String script = "c(";
        for (Double value : this.values.getValues()) {
            script += value + ",";
        }
        return script.substring(0, script.length() - 1) + ")";
    }

}
