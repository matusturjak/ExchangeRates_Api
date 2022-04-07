package sk.matusturjak.exchange_rates.vaadin.charts;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.html.Div;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;

import java.util.List;

public class FittedRatesChart extends Div {

    private ApexCharts chart;

    public FittedRatesChart(List<Double> realValues, List<Double> fittedValues, List<String> times) {
        ApexCharts apexCharts = this.buildChart(realValues, fittedValues, times);

        add(apexCharts);
        setWidth("70%");
        this.chart = apexCharts;
    }

    public ApexCharts buildChart(List<Double> realValues, List<Double> fittedValues, List<String> times) {
        Double[] arrReal = new Double[realValues.size()];
        arrReal = realValues.toArray(arrReal);

        Double[] arrFitted = new Double[fittedValues.size()];
        arrFitted = fittedValues.toArray(arrFitted);

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).build())
                .withSeries(new Series<>("Real values", arrReal), new Series<>("Fitted values", arrFitted))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Predictions of ARIMA model")
                        .withAlign(Align.left).build())
                .withSubtitle(TitleSubtitleBuilder.get()
                        .withAlign(Align.left).build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(times).build())
                .withYaxis(YAxisBuilder.get()
                        .withOpposite(true).build())
                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                .withColors("#00FF22", "#FF0000")
                .build();
    }

    public void updateChart(List<Double> realValues, List<Double> fittedValues, List<String> times) {
        remove(this.chart);
        this.chart = this.buildChart(realValues, fittedValues, times);
        add(this.chart);
    }
}
