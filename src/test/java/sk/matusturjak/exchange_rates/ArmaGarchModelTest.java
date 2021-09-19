package sk.matusturjak.exchange_rates;

import net.finmath.timeseries.TimeSeries;
import net.finmath.timeseries.models.parametric.ARMAGARCH;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.predictions.armagarch.ArmaGarchModel;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArmaGarchModelTest {

    @Autowired
    private ExchangeRateService rateService;

    @Test
    public void test() {
        List<ExchangeRate> list = rateService.getAllRates("EUR", "CZK");
        double[] times = new double[list.size()];

        for (int i = 0; i < times.length; i++) {
            times[i] = i;
        }
        double[] values = new double[times.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i).getRate().getValue();
        }

        TimeSeries timeSeries = new TimeSeries(times, values);
        ARMAGARCH armagarch = new ARMAGARCH(timeSeries);

        HashMap<String, Object> map = (HashMap<String, Object>) armagarch.getBestParameters();
        int b = 0;
    }

    @Test
    public void testArimaRenjin() throws ScriptException {
        List<ExchangeRate> list = rateService.getAllRates("EUR", "CZK");

        String script = "c(";
        for (ExchangeRate rate : list) {
            script += rate.getRate().getValue() + ",";
        }
        script = "ar <- arima(" + script.substring(0, script.length() - 1) + "), order=c(2,1,2))";

        RenjinScriptEngine engine = new RenjinScriptEngine();
        engine.eval("library('org.renjin.cran:forecast')");
        Vector df = (Vector)engine.eval(script);
        int b = 0;
    }

    @Test
    public void testADFTest() throws Exception {
        List<ExchangeRate> list = rateService.getAllRates("EUR", "CZK");
        double[] times = new double[list.size()];

        for (int i = 0; i < times.length; i++) {
            times[i] = i;
        }
        double[] values = new double[times.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i).getRate().getValue();
        }

        ArmaGarchModel model = new ArmaGarchModel(times, values);
        model.isSerieStacionary();
    }
}
