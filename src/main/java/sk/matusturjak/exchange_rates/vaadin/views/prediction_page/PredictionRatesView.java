package sk.matusturjak.exchange_rates.vaadin.views.prediction_page;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.PredictionService;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.util.ArrayList;
import java.util.List;

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

    private PredictionService predictionService;

    public PredictionRatesView(PredictionService predictionService) {
        this.predictionService = predictionService;

        HorizontalLayout currLayout = new HorizontalLayout();

        this.firstCurr = new ComboBox<>();
        this.secondCurr = new ComboBox<>();

        this.firstCurr.setItems(StaticVariables.currencies);
        this.secondCurr.setItems(StaticVariables.currencies);

        this.firstCurr.setLabel("From");
        this.secondCurr.setLabel("To");

        this.firstCurr.setValue("EUR");
        this.secondCurr.setValue("CZK");

        currLayout.add(this.firstCurr, this.secondCurr);

        this.oneDay = new Tab("One day");
        this.threeDays = new Tab("Three days");
        this.fiveDays = new Tab("Five days");

        this.tabs = new Tabs(this.oneDay, this.threeDays, this.fiveDays);
        this.tabs.setMaxWidth("100%");
        
        this.tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));

        this.predictionTable = new Grid<>(PredictionTableRow.class);

        tabLayout = new VerticalLayout();
        tabLayout.add(this.predictionTable);

        this.oneDay.add(tabLayout);

//        this.tabs.add(this.predictionTable);

        this.button = new Button("Predict", new Icon(VaadinIcon.ENTER));
        this.button.addClickListener(buttonClickEvent -> {
            this.updateTable(3);
            this.predictionTable.getDataProvider().refreshAll();
        });

        setContent(this.tabs.getSelectedTab());

        add(currLayout);
        add(this.button);
//        add(this.oneDay, this.threeDays, this.fiveDays);
        add(this.tabs, this.tabLayout);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void setContent(Tab selectedTab) {
        tabLayout.removeAll();

        if (selectedTab.equals(this.oneDay)) {
            this.updateTable(1);
            tabLayout.add(this.predictionTable);
        } else if (selectedTab.equals(this.threeDays)) {
            this.updateTable(3);
            tabLayout.add(this.predictionTable);
        } else {
            this.updateTable(5);
            tabLayout.add(this.predictionTable);
        }
    }

    public void updateTable(int num) {
        List<PredictionTableRow> list = new ArrayList<>();

        this.predictionService.getPredictions(this.firstCurr.getValue(), this.secondCurr.getValue(), num)
                .forEach(prediction -> list.add(new PredictionTableRow(prediction.getDate(), prediction.getRate().getValue())));

        this.predictionTable.setItems(list);
        this.predictionTable.setColumns("date", "value");
    }
}
