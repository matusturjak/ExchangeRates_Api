package sk.matusturjak.exchange_rates.predictions.armagarch;

import org.renjin.script.RenjinScriptEngine;
import sk.matusturjak.exchange_rates.predictions.PredictionModelInterface;

public abstract class PredictionModel implements PredictionModelInterface {
    protected RenjinScriptEngine engine;
    protected double[] values;
    protected double[] fittedValues;

    public PredictionModel() {
        this.engine = new RenjinScriptEngine();
    }

    public PredictionModel(double[] values) {
        this.engine = new RenjinScriptEngine();
        this.values = values;
    }

    public RenjinScriptEngine getEngine() {
        return engine;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public void setFittedValues(double[] fittedValues) {
        this.fittedValues = fittedValues;
    }
}
