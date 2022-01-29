package sk.matusturjak.exchange_rates.vaadin.charts;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Style;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.html.Div;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;

import java.util.List;

public class ExchangeRatesChart extends Div {
    private ApexCharts chart;

    public ExchangeRatesChart(List<Double> data, List<String> times, String legend) {
        ApexCharts apexCharts = this.buildChart(data, times, legend);

        add(apexCharts);
        setWidth("70%");
        this.setChart(apexCharts);
    }

    public ApexCharts buildChart(List<Double> data, List<String> times, String legend) {
        Double[] arr = new Double[data.size()];
        arr = data.toArray(arr);

        return ApexChartsBuilder.get()
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
                        .withText(legend)
                        .withAlign(Align.left).build())
                .withSubtitle(TitleSubtitleBuilder.get()
                        .withText("" + NumHelper.roundAvoid(data.get(data.size() - 1) - data.get(data.size() - 2),4))
                        .withStyle(this.setStyleOfText(data.get(data.size() - 1) > data.get(data.size() - 2)))
                        .withAlign(Align.left).build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(times).build())
                .withYaxis(YAxisBuilder.get()
                        .withOpposite(true).build())
                .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                .withColors(data.get(data.size() - 1) > data.get(data.size() - 2) ? "#00FF22" : "#FF0000")
                .build();
    }

    private Style setStyleOfText(boolean diff) {
        Style style = new Style();
        style.setColor(diff ? "#00FF22" : "#FF0000");
        style.setFontSize("20px");
        return style;
    }

    public ApexCharts getChart() {
        return chart;
    }

    public void setChart(ApexCharts chart) {
        this.chart = chart;
    }

    public void updateChart(List<Double> data, List<String> times, String legend) {
        remove(this.chart);
        this.chart = this.buildChart(data, times, legend);
        add(this.chart);
    }
}


