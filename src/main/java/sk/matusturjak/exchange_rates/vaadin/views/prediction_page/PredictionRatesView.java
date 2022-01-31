package sk.matusturjak.exchange_rates.vaadin.views.prediction_page;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.ModelOutputService;
import sk.matusturjak.exchange_rates.service.PredictionService;
import sk.matusturjak.exchange_rates.vaadin.charts.ExchangeRatesChart;
import sk.matusturjak.exchange_rates.vaadin.charts.FittedRatesChart;
import sk.matusturjak.exchange_rates.vaadin.charts.ResidualsChart;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "prediction_rates", layout = MainLayout.class)
public class PredictionRatesView extends VerticalLayout {
    private ComboBox<String> firstCurr;
    private ComboBox<String> secondCurr;

    private Tab oneDay;
    private Tab threeDays;
    private Tab fiveDays;

    private Tabs tabs;

    private VerticalLayout tabLayout;

    private Button button;

    private Grid<PredictionTableRow> predictionTable;
    private Span nameOfModel;
    private Checkbox residualsCheckbox;

    private PredictionService predictionService;
    private ExchangeRateService exchangeRateService;
    private ModelOutputService modelOutputService;

    private FittedRatesChart chart;
    private ResidualsChart resChart;

    public PredictionRatesView(PredictionService predictionService, ExchangeRateService exchangeRateService, ModelOutputService modelOutputService) {
        this.predictionService = predictionService;
        this.exchangeRateService = exchangeRateService;
        this.modelOutputService = modelOutputService;

        HorizontalLayout currLayout = new HorizontalLayout();

        this.firstCurr = new ComboBox<>();
        this.secondCurr = new ComboBox<>();

        this.firstCurr.setItems(StaticVariables.currencies);
        this.secondCurr.setItems(StaticVariables.currencies);

        this.firstCurr.setLabel("From");
        this.secondCurr.setLabel("To");

        this.firstCurr.setValue("EUR");
        this.secondCurr.setValue("CZK");

        this.oneDay = new Tab("One day");
        this.threeDays = new Tab("Three days");
        this.fiveDays = new Tab("Five days");

        this.tabs = new Tabs(this.oneDay, this.threeDays, this.fiveDays);
        this.tabs.setMaxWidth("100%");
        
        this.tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));

        this.predictionTable = new Grid<>(PredictionTableRow.class, false);
        this.predictionTable.setAllRowsVisible(true);

        this.tabLayout = new VerticalLayout();
        this.tabLayout.add(this.predictionTable);
        this.tabLayout.setWidth("35em");

//        this.oneDay.add(tabLayout);

        this.button = new Button("Predict", new Icon(VaadinIcon.ENTER));
        this.button.addClickListener(buttonClickEvent -> {
//            this.updateTable(3);
            this.predictionTable.getDataProvider().refreshAll();
            setContent(this.tabs.getSelectedTab());
        });

        List<ExchangeRate> rates = this.getRates();

        List<String> times = rates.stream().map(rate -> rate.getDate()).collect(Collectors.toList());
        times.addAll(this.predictionService.getPredictions(this.firstCurr.getValue(), this.secondCurr.getValue(), 1)
                .stream().map(Prediction::getDate).collect(Collectors.toList()));

        this.chart = new FittedRatesChart(
                rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()),
                this.modelOutputService.getFitted(this.firstCurr.getValue(), this.secondCurr.getValue(), "arma_garch1"),
                times
        );

        this.resChart = new ResidualsChart(
                this.modelOutputService.getResiduals(this.firstCurr.getValue(), this.secondCurr.getValue(), "arma_garch1"),
                this.modelOutputService.getSigma(this.firstCurr.getValue(), this.secondCurr.getValue()),
                this.modelOutputService.getSigma(this.firstCurr.getValue(), this.secondCurr.getValue()).stream().map(aDouble -> -aDouble).collect(Collectors.toList())
        );
        this.resChart.setVisible(false);

        currLayout.add(this.firstCurr, this.secondCurr);

        this.nameOfModel = new Span("ARMA - GARCH");

        this.residualsCheckbox = new Checkbox("Show residuals");
        this.residualsCheckbox.addValueChangeListener(checkboxClickEvent -> {
            if (this.residualsCheckbox.getValue()) {
                this.resChart.setVisible(true);
            } else {
                this.resChart.setVisible(false);
            }
        });

        this.residualsCheckbox.setValue(true);

        this.nameOfModel.getStyle().set("font-weight", "bold");
        HorizontalLayout headerLayout = new HorizontalLayout(this.nameOfModel, this.residualsCheckbox);
        headerLayout.setAlignItems(Alignment.START);
        headerLayout.setFlexGrow(1, this.nameOfModel);

        setContent(this.tabs.getSelectedTab());

        add(new H1("Predictions"));
        add(currLayout);
        add(this.button);
        add(this.tabs);
        add(headerLayout);
        add(this.tabLayout);

        HorizontalLayout chartsLayout = new HorizontalLayout(this.chart, this.resChart);
        chartsLayout.setWidth("100%");
        add(this.chart);
        add(this.resChart);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void setContent(Tab selectedTab) {
        tabLayout.removeAll();

        if (selectedTab.equals(this.oneDay)) {
            this.nameOfModel.setText("ARMA - GARCH");
            this.residualsCheckbox.setVisible(true);
            this.updateTable(1);
            tabLayout.add(this.predictionTable);
        } else if (selectedTab.equals(this.threeDays)) {
            this.nameOfModel.setText("Exponential smoothing");
            this.residualsCheckbox.setVisible(false);
            this.updateTable(3);
            tabLayout.add(this.predictionTable);
        } else {
            this.nameOfModel.setText("Exponential smoothing");
            this.residualsCheckbox.setVisible(false);
            this.updateTable(5);
            tabLayout.add(this.predictionTable);
        }
    }

    public void updateTable(int num) {
        List<PredictionTableRow> list = new ArrayList<>();

        this.predictionService.getPredictions(this.firstCurr.getValue(), this.secondCurr.getValue(), num)
                .forEach(prediction -> list.add(new PredictionTableRow(prediction.getDate(), prediction.getRate().getValue())));

        List<ExchangeRate> rates = this.getRates();
        this.predictionTable.setItems(list);
        this.predictionTable.setColumns("date", "value");

        List<Double> fitted = this.modelOutputService.getLatestFitted(
                this.firstCurr.getValue(),
                this.secondCurr.getValue(),
                num,
                rates.size() + num + 1
        );
        List<String> times = rates.stream().map(rate -> rate.getDate()).collect(Collectors.toList());

        times.addAll(list.stream().map(PredictionTableRow::getDate).collect(Collectors.toList()));
        this.chart.updateChart(rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()), fitted, times);
    }

    public List<ExchangeRate> getRates() {
        return this.exchangeRateService.getLastRates(this.firstCurr.getValue(), this.secondCurr.getValue(), 10);
    }
}
