package sk.matusturjak.exchange_rates.vaadin.views.historical_page;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.vaadin.charts.AreaChartExample;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "historic_rates", layout = MainLayout.class)
public class HistoricRatesView extends VerticalLayout {
    private ComboBox<String> firstCurr;
    private ComboBox<String> secondCurr;

    private DatePicker dateFrom;
    private DatePicker dateTo;

    private Button showGraphButton;

    private ExchangeRateService exchangeRateService;

    private AreaChartExample chart;

    public HistoricRatesView(ExchangeRateService exchangeRateService) {
        HorizontalLayout layoutCurr = new HorizontalLayout();
        HorizontalLayout layoutDate = new HorizontalLayout();

        this.exchangeRateService = exchangeRateService;

        this.firstCurr = new ComboBox<>();
        this.secondCurr = new ComboBox<>();

        this.firstCurr.setItems(StaticVariables.currencies);
        this.secondCurr.setItems(StaticVariables.currencies);

        this.firstCurr.setLabel("From");
        this.secondCurr.setLabel("To");

        this.firstCurr.setValue("EUR");
        this.secondCurr.setValue("CZK");

        this.dateFrom = new DatePicker("Start date");
        this.dateTo = new DatePicker("End date");

        this.dateFrom.setValue(LocalDate.of(2021,1,1));
        this.dateTo.setValue(LocalDate.now());

        this.showGraphButton = new Button("Show graph", new Icon(VaadinIcon.ENTER));
        this.showGraphButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            chart.updateChart(getRates().stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()));
        });

        List<ExchangeRate> rates = this.getRates();
        this.chart = new AreaChartExample(
                rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()),
                rates.stream().map(exchangeRate -> exchangeRate.getDate()).collect(Collectors.toList())
        );;

        layoutCurr.add(this.firstCurr, this.secondCurr);
        layoutDate.add(this.dateFrom, this.dateTo);

        add(layoutCurr, layoutDate);
        add(this.showGraphButton);
        add(this.chart);

//        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    public List<ExchangeRate> getRates() {
        return this.exchangeRateService.getRates(this.firstCurr.getValue(), this.secondCurr.getValue(), formatDate(this.dateFrom.getValue()), formatDate(this.dateTo.getValue()));
    }

    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
