package sk.matusturjak.exchange_rates.vaadin.views.prediction_page;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.ModelOutputService;
import sk.matusturjak.exchange_rates.service.PredictionService;
import sk.matusturjak.exchange_rates.vaadin.charts.FittedRatesChart;
import sk.matusturjak.exchange_rates.vaadin.charts.ResidualsChart;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "prediction_rates", layout = MainLayout.class)
public class PredictionRatesView extends VerticalLayout {
    private PredictionService predictionService;
    private ExchangeRateService exchangeRateService;
    private ModelOutputService modelOutputService;

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

    private FittedRatesChart chart;
    private ResidualsChart resChart;

    private DatePicker dateFromFit;
    private DatePicker dateToFit;

    private DatePicker dateFromRes;
    private DatePicker dateToRes;

    private Map<String, Integer> dateMapRes;
    private Map<String, Integer> dateMapFit;

    public PredictionRatesView(PredictionService predictionService, ExchangeRateService exchangeRateService, ModelOutputService modelOutputService) throws Exception {
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

        this.dateFromFit = new DatePicker("Start_date");
        this.dateToFit = new DatePicker("End_date");

        this.initFittedChart();

        this.dateFromRes = new DatePicker("Start date");
        this.dateToRes = new DatePicker("End date");

        this.initResidualsChart();

        currLayout.add(this.firstCurr, this.secondCurr);

        this.nameOfModel = new Span("ARMA - GARCH");

        this.residualsCheckbox = new Checkbox("Show residuals");
        this.residualsCheckbox.addValueChangeListener(checkboxClickEvent -> {
            if (this.residualsCheckbox.getValue()) {
                this.dateFromRes.setVisible(true);
                this.dateToRes.setVisible(true);
                this.resChart.setVisible(true);
            } else {
                this.dateFromRes.setVisible(false);
                this.dateToRes.setVisible(false);
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
        add(new HorizontalLayout(this.dateFromFit, this.dateToFit));
        add(this.chart);
        add(new HorizontalLayout(this.dateFromRes, this.dateToRes));
        add(this.resChart);

        datePickerListenersRes();
        datePickersListenerFit();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void datePickerListenersRes() {
        this.dateFromRes.addValueChangeListener(d -> updateResChart());
        this.dateToRes.addValueChangeListener(d -> updateResChart());
    }

    private void datePickersListenerFit() {
        this.dateFromFit.addValueChangeListener(d -> updateFittedChart());
        this.dateToFit.addValueChangeListener(d -> updateFittedChart());
    }

    private void updateResChart() {
        Integer fDate = this.dateMapRes.get(this.dateFromRes.getValue().toString());
        Integer tDate = this.dateMapRes.get(this.dateToRes.getValue().toString());

        if (fDate == null) {
            fDate = 0;
        }
        List<String> times = this.dateMapRes.keySet().stream().sorted().collect(Collectors.toList());
        if (tDate == null) {
            tDate = times.size() - 1; //
        }
        times = times.subList(fDate, tDate);

        if (this.tabs.getSelectedTab().equals(this.oneDay)) {
            this.resChart.updateChart(
                    this.modelOutputService.getResiduals(this.firstCurr.getValue(), this.secondCurr.getValue(),"arma_garch1", fDate, tDate),
                    this.modelOutputService.getSigma(this.firstCurr.getValue(), this.secondCurr.getValue(), fDate, tDate),
                    times
            );
        } else if (this.tabs.getSelectedTab().equals(this.threeDays)) {
            this.resChart.updateChart(
                    this.modelOutputService.getResiduals(this.firstCurr.getValue(), this.secondCurr.getValue(),"exp3", fDate, tDate),
                    null,
                    times
            );
        } else {
            this.resChart.updateChart(
                    this.modelOutputService.getResiduals(this.firstCurr.getValue(), this.secondCurr.getValue(),"exp5", fDate, tDate),
                    null,
                    times
            );
        }

    }

    private void updateFittedChart() {
        Integer fDate = this.dateMapFit.get(this.dateFromFit.getValue().toString());
        Integer tDate = this.dateMapFit.get(this.dateToFit.getValue().toString());

        if (fDate == null) {
            fDate = 0;
        }
        List<String> times = this.dateMapFit.keySet().stream().sorted().collect(Collectors.toList());
        if (tDate == null) {
            tDate = times.size();
        }
        times = times.subList(fDate, tDate);

        this.chart.updateChart(
                this.exchangeRateService.getRates(
                        this.firstCurr.getValue(),
                        this.secondCurr.getValue(),
                        this.dateFromFit.getValue().toString(),
                        this.dateToFit.getValue().toString()
                ).stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()),
                this.modelOutputService.getFitted(
                        this.firstCurr.getValue(),
                        this.secondCurr.getValue(),
                        this.tabs.getSelectedTab().equals(this.oneDay) ? "arma_garch1" :
                                this.tabs.getSelectedTab().equals(this.threeDays) ? "exp3" : "exp5",
                        fDate,
                        tDate
                ),
                times
        );
    }

    private void initFittedChart() throws Exception {
        List<ExchangeRate> rates = this.getRates();

        List<String> times = rates.stream().map(ExchangeRate::getDate).collect(Collectors.toList());
        times.addAll(this.predictionService.getPredictions(this.firstCurr.getValue(), this.secondCurr.getValue(), 1)
                .stream().map(Prediction::getDate).collect(Collectors.toList()));

        List<String> allTimes = this.exchangeRateService.getAllRates(this.firstCurr.getValue(), this.secondCurr.getValue())
                .stream().map(ExchangeRate::getDate).collect(Collectors.toList());

        this.dateMapFit = new HashMap<>();
        for (int i = 0; i < allTimes.size(); i++) this.dateMapFit.put(allTimes.get(i), i);

        this.dateFromFit.setValue(NumHelper.dateToLocalDate(new SimpleDateFormat("yyyy-MM-dd").parse(rates.get(0).getDate())));
        this.dateToFit.setValue(NumHelper.dateToLocalDate(new SimpleDateFormat("yyyy-MM-dd").parse(rates.get(rates.size() - 1).getDate())));

        this.chart = new FittedRatesChart(
                rates.stream().map(exchangeRate -> exchangeRate.getRate().getValue()).collect(Collectors.toList()),
                this.modelOutputService.getFitted(this.firstCurr.getValue(), this.secondCurr.getValue(), "arma_garch1"),
                times
        );
    }

    private void initResidualsChart() throws Exception {
        List<ExchangeRate> rates = this.getRates();
        List<String> times = rates.stream().map(ExchangeRate::getDate).collect(Collectors.toList());

        List<String> allTimes = this.exchangeRateService.getAllRates(this.firstCurr.getValue(), this.secondCurr.getValue())
                .stream().map(ExchangeRate::getDate).collect(Collectors.toList());

        this.dateMapRes = new HashMap<>();
        for (int i = 0; i < allTimes.size(); i++) this.dateMapRes.put(allTimes.get(i), i);

        this.dateFromRes.setValue(NumHelper.dateToLocalDate(new SimpleDateFormat("yyyy-MM-dd").parse(rates.get(0).getDate())));
        this.dateToRes.setValue(NumHelper.dateToLocalDate(new SimpleDateFormat("yyyy-MM-dd").parse(rates.get(rates.size() - 1).getDate())));

        List<Double> sigmas = this.modelOutputService.getSigma(
                this.firstCurr.getValue(),
                this.secondCurr.getValue(),
                this.dateMapRes.get(this.dateFromRes.getValue().toString()),
                this.dateMapRes.get(this.dateToRes.getValue().toString())
        );
        List<Double> residuals = this.modelOutputService.getResiduals(
                this.firstCurr.getValue(),
                this.secondCurr.getValue(),
                "arma_garch1",
                this.dateMapRes.get(this.dateFromRes.getValue().toString()),
                this.dateMapRes.get(this.dateToRes.getValue().toString())
        );
        this.resChart = new ResidualsChart(
                residuals,
                sigmas,
                sigmas.stream().map(aDouble -> -aDouble).collect(Collectors.toList()),
                times
        );
    }

    private void setContent(Tab selectedTab) {
        tabLayout.removeAll();

        if (selectedTab.equals(this.oneDay)) {
            this.nameOfModel.setText("ARMA - GARCH");
            this.updateTable(1);
            tabLayout.add(this.predictionTable);
        } else if (selectedTab.equals(this.threeDays)) {
            this.nameOfModel.setText("Exponential smoothing");
            this.updateTable(3);
            tabLayout.add(this.predictionTable);
        } else {
            this.nameOfModel.setText("Exponential smoothing");
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
        if (num == 1) {
            List<Double> residuals =
                    this.modelOutputService.getResiduals(
                            this.firstCurr.getValue(), this.secondCurr.getValue(), "arma_garch1",
                            this.dateMapRes.get(this.dateFromRes.getValue().toString()),
                            this.dateMapRes.get(this.dateToRes.getValue().toString())
                    );
            List<Double> sigmas =
                    this.modelOutputService.getSigma(
                            this.firstCurr.getValue(), this.secondCurr.getValue(),
                            this.dateMapRes.get(this.dateFromRes.getValue().toString()),
                            this.dateMapRes.get(this.dateToRes.getValue().toString())
                    );

            this.resChart.updateChart(residuals, sigmas, times);
        } else {
            List<Double> residuals =
                    this.modelOutputService.getResiduals(
                            this.firstCurr.getValue(), this.secondCurr.getValue(), "exp" + num,
                            this.dateMapRes.get(this.dateFromRes.getValue().toString()),
                            this.dateMapRes.get(this.dateToRes.getValue().toString())
                    );
            this.resChart.updateChart(residuals, null, times);
        }
    }

    public List<ExchangeRate> getRates() {
        return this.exchangeRateService.getLastRates(this.firstCurr.getValue(), this.secondCurr.getValue(), 10);
    }
}
