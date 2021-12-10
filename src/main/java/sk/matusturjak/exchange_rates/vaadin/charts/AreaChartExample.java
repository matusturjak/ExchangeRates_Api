package sk.matusturjak.exchange_rates.vaadin.charts;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.html.Div;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class AreaChartExample extends Div {
    private ApexCharts chart;

    public AreaChartExample(List<Double> data, List<String> times) {
        Double[] arr = new Double[data.size()];
        arr = data.toArray(arr);
        ApexCharts areaChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.area)
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.straight).build())
                .withSeries(new Series<>("Rate", arr))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Exchange rates")
                        .withAlign(Align.left).build())
                .withSubtitle(TitleSubtitleBuilder.get()
                        .withText("Price Movements")
                        .withAlign(Align.left).build())
                .withXaxis(XAxisBuilder.get()
                        .withType(XAxisType.numeric).build())
                .withYaxis(YAxisBuilder.get()
                        .withOpposite(true).build())
                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                .build();
        add(areaChart);
        setWidth("70%");
        this.setChart(areaChart);
    }

    public ApexCharts getChart() {
        return chart;
    }

    public void setChart(ApexCharts chart) {
        this.chart = chart;
    }

    public void updateChart(List<Double> data) {
        Double[] arr = new Double[data.size()];
        arr = data.toArray(arr);

        chart.updateSeries(new Series<>("Rate", arr));
    }
}


