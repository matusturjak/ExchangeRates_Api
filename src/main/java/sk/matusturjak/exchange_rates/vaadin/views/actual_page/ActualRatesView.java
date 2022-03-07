package sk.matusturjak.exchange_rates.vaadin.views.actual_page;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.LatestRateService;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.util.ArrayList;
import java.util.List;

@Route(value = "actual_rates", layout = MainLayout.class)
public class ActualRatesView extends VerticalLayout {
    private ComboBox<String> baseCurr;
    private Button enterButton;
    private Grid<CurrencyTableRow> table;
    private NumberField baseValueField;

    private LatestRateService latestRateService;

    public ActualRatesView(LatestRateService latestRateService) {
        this.latestRateService = latestRateService;

        this.baseCurr = new ComboBox<>();
        this.baseCurr.setItems(StaticVariables.currencies);
        this.baseCurr.setValue("EUR");
        this.baseCurr.setLabel("Base");

        this.baseValueField = new NumberField();
        this.baseValueField.setLabel("Value");
        this.baseValueField.setValue(1.0);

        this.enterButton = new Button("Show", new Icon(VaadinIcon.ENTER));

        this.table = new Grid<>(CurrencyTableRow.class);
        this.updateTable();

        this.enterButton.addClickListener(buttonClickEvent -> {
            this.updateTable();
            this.table.getDataProvider().refreshAll();
        });

        add(new H1("Latest rates"));
        add(this.baseCurr);
        add(new HorizontalLayout(this.baseValueField,new VerticalLayout(new Label(""),this.enterButton)));
        add(this.table);
    }

    private Grid<CurrencyTableRow> createTable() {
        List<CurrencyTableRow> list = new ArrayList<>();

        this.latestRateService.getLatestRates(this.baseCurr.getValue())
                .forEach(latestRate -> {
                    Icon icon;
                    if (latestRate.getDifference() < 0) {
                        icon = VaadinIcon.ARROW_DOWN.create();
                        icon.setColor("red");
                    } else {
                        icon = VaadinIcon.ARROW_UP.create();
                        icon.setColor("green");
                    }
                    list.add(
                            new CurrencyTableRow(
                                    this.baseCurr.getValue(),
                                    latestRate.getRate().getSecondCountry(),
                                    latestRate.getRate().getValue(),
                                    latestRate.getDifference()
                            )
                    );
                });

        Grid<CurrencyTableRow> grid = new Grid<>(CurrencyTableRow.class);
        grid.setItems(list);
        grid.setColumns("firstCountry", "secondCountry", "rate", "diff");

        grid.addComponentColumn(item -> {
            Icon icon;
            if (item.getDiff() < 0) {
                icon = VaadinIcon.ARROW_DOWN.create();
                icon.setColor("red");
            } else {
                icon = VaadinIcon.ARROW_UP.create();
                icon.setColor("green");
            }
            return icon;
        })
                .setKey("icon")
                .setHeader("Icon");
        return grid;
    }

    public void updateTable() {
        List<CurrencyTableRow> list = new ArrayList<>();

        this.latestRateService.getLatestRates(this.baseCurr.getValue())
                .forEach(latestRate -> {
                    Icon icon;
                    if (latestRate.getDifference() < 0) {
                        icon = VaadinIcon.ARROW_DOWN.create();
                        icon.setColor("red");
                    } else {
                        icon = VaadinIcon.ARROW_UP.create();
                        icon.setColor("green");
                    }
                    list.add(
                            new CurrencyTableRow(
                                    this.baseCurr.getValue(),
                                    latestRate.getRate().getSecondCountry(),
                                    NumHelper.roundAvoid(latestRate.getRate().getValue() * this.baseValueField.getValue(), 4),
                                    latestRate.getDifference()
                            )
                    );
                });

        this.table.setItems(list);
        this.table.setColumns("firstCountry", "secondCountry", "rate", "diff");

        this.table.addComponentColumn(item -> {
            Icon icon;
            if (item.getDiff() < 0) {
                icon = VaadinIcon.ARROW_DOWN.create();
                icon.setColor("red");
            } else {
                icon = VaadinIcon.ARROW_UP.create();
                icon.setColor("green");
            }
            return icon;
        })
                .setKey("icon")
                .setHeader("Icon");
    }
}
