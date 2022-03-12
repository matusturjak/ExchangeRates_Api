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
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.model.utils.StaticVariables;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
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
    private ExchangeRateService exchangeRateService;

    public ActualRatesView(LatestRateService latestRateService, ExchangeRateService exchangeRateService) {
        this.latestRateService = latestRateService;
        this.exchangeRateService = exchangeRateService;

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

        List<LatestRate> latestRates = this.latestRateService.getLatestRates(this.baseCurr.getValue());

        if (latestRates.stream().noneMatch(latestRate -> latestRate.getDifference() != null)) {
            List<ExchangeRate> exchangeRates = this.exchangeRateService.get2ndLatestRates();
            latestRates.forEach(latestRate -> {
                ExchangeRate second = exchangeRates.stream().filter(exchangeRate -> exchangeRate.getRate().equals(latestRate.getRate())).findFirst().orElse(null);
                if (second != null) {
                    double diff = NumHelper.roundAvoid(latestRate.getRate().getValue() - second.getRate().getValue(), 4);
                    this.latestRateService.updateRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr(), latestRate.getRate().getValue(), diff);
                    latestRate.setDifference(diff);
                }
            });
        }

        latestRates.forEach(latestRate -> {
            Icon icon;
            if (latestRate.getDifference() == null) {
                latestRate.setDifference(0d);
            }
            list.add(
                    new CurrencyTableRow(
                            this.baseCurr.getValue(),
                            latestRate.getRate().getToCurr(),
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
            if (item.getDiff() == 0) {
                icon = VaadinIcon.MINUS.create();
                icon.setColor("black");
            } else if (item.getDiff() < 0) {
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

        List<LatestRate> latestRates = this.latestRateService.getLatestRates(this.baseCurr.getValue());

        if (latestRates.stream().noneMatch(latestRate -> latestRate.getDifference() != null)) {
            List<ExchangeRate> exchangeRates = this.exchangeRateService.get2ndLatestRates();
            latestRates.forEach(latestRate -> {
                ExchangeRate second = exchangeRates.stream().filter(exchangeRate -> exchangeRate.getRate().equals(latestRate.getRate())).findFirst().orElse(null);
                if (second != null) {
                    double diff = NumHelper.roundAvoid(latestRate.getRate().getValue() - second.getRate().getValue(), 4);
                    this.latestRateService.updateRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr(), latestRate.getRate().getValue(), diff);
                    latestRate.setDifference(diff);
                }
            });
        }

        latestRates
                .forEach(latestRate -> {
                    Icon icon;
                    if (latestRate.getDifference() == null) {
                        latestRate.setDifference(0d);
                    }
                    list.add(
                            new CurrencyTableRow(
                                    this.baseCurr.getValue(),
                                    latestRate.getRate().getToCurr(),
                                    NumHelper.roundAvoid(latestRate.getRate().getValue() * this.baseValueField.getValue(), 4),
                                    latestRate.getDifference()
                            )
                    );
                });

        this.table.setItems(list);
        this.table.setColumns("firstCountry", "secondCountry", "rate", "diff");

        this.table.addComponentColumn(item -> {
            Icon icon;
            if (item.getDiff() == 0) {
                icon = VaadinIcon.MINUS.create();
                icon.setColor("black");
            } else if (item.getDiff() < 0) {
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
