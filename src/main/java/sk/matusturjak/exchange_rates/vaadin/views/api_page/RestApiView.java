package sk.matusturjak.exchange_rates.vaadin.views.api_page;

import com.github.appreciated.prism.element.Language;
import com.github.appreciated.prism.element.PrismHighlighter;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

import java.util.LinkedList;
import java.util.List;

@Route(value = "rest_api", layout = MainLayout.class)
public class RestApiView extends VerticalLayout {

    public RestApiView() {
        add(new H1("Rest API endpoints"));

        add(new H3("Predictions endpoints"));
        this.getPredictionRequest();

        add(new H3("Latest rates endpoints"));
        this.addLatestRatesEndPoints();

        add(new H3("Historical rates endpoints"));
        this.addHistoricalRatesEndPoints();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void getPredictionRequest() {
        Grid<RequestParam> requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        List<RequestParam> requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));
        requestParams.add(new RequestParam("CZK", "Code of output currency"));
        requestParams.add(new RequestParam("3", "Number of predictions (1, 3, 5)"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/prediction/EUR-CZK/3","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.0993\n" +
                "        },\n" +
                "        \"date\": \"2021-12-24\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.0649\n" +
                "        },\n" +
                "        \"date\": \"2021-12-25\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.0306\n" +
                "        },\n" +
                "        \"date\": \"2021-12-26\"\n" +
                "    }\n" +
                "]", requestParamGrid));
    }

    private void addLatestRatesEndPoints() {
        Grid<RequestParam> requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        List<RequestParam> requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/latest/EUR","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"AUD\",\n" +
                "            \"value\": 1.5639\n" +
                "        },\n" +
                "        \"difference\": 0.0307\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"BGN\",\n" +
                "            \"value\": 1.9558\n" +
                "        },\n" +
                "        \"difference\": 0\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"BRL\",\n" +
                "            \"value\": 6.4015\n" +
                "        },\n" +
                "        \"difference\": 0.0634\n" +
                "    },\n" +
                "...\n" +
                "...\n" +
                "]"
                , requestParamGrid));

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));
        requestParams.add(new RequestParam("CZK", "Code of output currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/latest/EUR/CZK","{\n" +
                "    \"rate\": {\n" +
                "        \"firstCountry\": \"EUR\",\n" +
                "        \"secondCountry\": \"CZK\",\n" +
                "        \"value\": 25.088\n" +
                "    },\n" +
                "    \"difference\": 0.7307\n" +
                "}"
                , requestParamGrid));
    }

    private void addHistoricalRatesEndPoints() {
        Grid<RequestParam> requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        List<RequestParam> requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));
        requestParams.add(new RequestParam("CZK", "Code of output currency"));
        requestParams.add(new RequestParam("2019-11-26", "start at"));
        requestParams.add(new RequestParam("2019-11-29", "end at"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/EUR-CZK/2019-11-26/2019-11-29","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.515\n" +
                "        },\n" +
                "        \"date\": \"2019-11-27\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.574\n" +
                "        },\n" +
                "        \"date\": \"2019-11-28\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.515\n" +
                "        },\n" +
                "        \"date\": \"2019-11-29\"\n" +
                "    }\n" +
                "]"
                , requestParamGrid));

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));
        requestParams.add(new RequestParam("CZK", "Code of output currency"));
        requestParams.add(new RequestParam("3", "number of latest rates <1, infinity>"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/EUR-CZK/3","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.24\n" +
                "        },\n" +
                "        \"date\": \"2021-12-21\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.24\n" +
                "        },\n" +
                "        \"date\": \"2021-12-22\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.088\n" +
                "        },\n" +
                "        \"date\": \"2021-12-23\"\n" +
                "    }\n" +
                "]"
                , requestParamGrid));

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("EUR", "Code of base currency"));
        requestParams.add(new RequestParam("CZK", "Code of output currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/EUR-CZK","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.752\n" +
                "        },\n" +
                "        \"date\": \"2019-01-02\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"firstCountry\": \"EUR\",\n" +
                "            \"secondCountry\": \"CZK\",\n" +
                "            \"value\": 25.683\n" +
                "        },\n" +
                "        \"date\": \"2019-01-03\"\n" +
                "    },\n" +
                "...\n" +
                "...\n" +
                "]"
                , requestParamGrid));
    }
}
