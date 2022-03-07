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

import java.util.List;
import java.util.stream.Collectors;

public class ResidualsChart extends Div {
    private ApexCharts chart;

    public ResidualsChart(List<Double> residuals, List<Double> positiveSigma, List<Double> negativeSigma, List<String> times) {
        ApexCharts apexCharts = this.buildChart(residuals, positiveSigma, negativeSigma, times);

        add(apexCharts);
        setWidth("70%");
        this.chart = apexCharts;
    }

    public ApexCharts buildChart(List<Double> residuals, List<Double> positiveSigma, List<Double> negativeSigma, List<String> times) {
        Double[] arrRes = new Double[residuals.size()];
        arrRes = residuals.toArray(arrRes);

        Double[] arrPos = new Double[positiveSigma.size()];
        arrPos = positiveSigma.toArray(arrPos);

        Double[] arrNeg = new Double[negativeSigma.size()];
        arrNeg = negativeSigma.toArray(arrNeg);

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
                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).withWidth(0.7d).build())
                .withSeries(new Series<>("Residuals", arrRes), new Series<>("Negative sigma", arrNeg), new Series<>("Positive sigma", arrPos))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Residuals and sigma")
                        .withAlign(Align.left).build())
                .withSubtitle(TitleSubtitleBuilder.get()
                        .withAlign(Align.left).build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(times).build())
                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                .withColors("#005BFF","#FF2D00","#FF2D00")
                .build();
    }

    public ApexCharts buildChartRes(List<Double> residuals, List<String> times) {
        Double[] arrRes = new Double[residuals.size()];
        arrRes = residuals.toArray(arrRes);

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
                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).withWidth(0.7d).build())
                .withSeries(new Series<>("Residuals", arrRes))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Residuals")
                        .withAlign(Align.left).build())
                .withSubtitle(TitleSubtitleBuilder.get()
                        .withAlign(Align.left).build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(times).build())
                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                .withColors("#005BFF")
                .build();
    }

    public void updateChart(List<Double> residuals, List<Double> positiveSigma, List<String> times) {
        remove(this.chart);
        if (positiveSigma != null) {
            List<Double> negativeSigma = positiveSigma.stream().map(aDouble -> -aDouble).collect(Collectors.toList());
            this.chart = this.buildChart(residuals, positiveSigma, negativeSigma, times);
        } else {
            this.chart = this.buildChartRes(residuals, times);
        }

        add(this.chart);
    }
}
