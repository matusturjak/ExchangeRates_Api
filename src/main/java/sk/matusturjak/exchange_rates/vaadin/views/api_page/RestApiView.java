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

        add(new H2("Predictions endpoint"));
        this.getPredictionRequest();

        add(new H2("Latest rates endpoints"));
        this.addLatestRatesEndPoints();

        add(new H2("Historical rates endpoints"));
        this.addHistoricalRatesEndPoints();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void getPredictionRequest() {
        TextArea textArea = new TextArea("Description");
        textArea.setValue("This endpoint will return predictions of specific exchange rate. Predictions are " +
                "calculated by autoregressive models: ARIMA-GARCH, ARIMA-IGACH and Exponential Smoothing.");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(textArea);

        Grid<RequestParam> requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        List<RequestParam> requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));
        requestParams.add(new RequestParam("ahead", "Number of predictions (1, 3, 5)"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/prediction?from=EUR&to=CZK&ahead=1","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"value\": 25.2834,\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\"\n" +
                "        },\n" +
                "        \"date\": \"2022-03-11\",\n" +
                "        \"method\": \"ARIMA-GARCH\",\n" +
                "        \"volatility\": 0.2184\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"value\": 25.2834,\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\"\n" +
                "        },\n" +
                "        \"date\": \"2022-03-11\",\n" +
                "        \"method\": \"ARIMA-IGARCH\",\n" +
                "        \"volatility\": 0.2319\n" +
                "    }\n" +
                "]", requestParamGrid));
    }

    private void addLatestRatesEndPoints() {
        H3 h3 = new H3("1. Endpoint");
        TextArea textArea = new TextArea("Description");
        textArea.setValue("Endpoint which will return latest exchange rates for base currency." +
                "These rates are updated every day.");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

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
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"AUD\",\n" +
                "            \"value\": 1.5639\n" +
                "        },\n" +
                "        \"difference\": 0.0307\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"BGN\",\n" +
                "            \"value\": 1.9558\n" +
                "        },\n" +
                "        \"difference\": 0\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"BRL\",\n" +
                "            \"value\": 6.4015\n" +
                "        },\n" +
                "        \"difference\": 0.0634\n" +
                "    },\n" +
                "...\n" +
                "...\n" +
                "]"
                , requestParamGrid));

        h3 = new H3("2. Endpoint");
        textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return latest rate of specific currencies.");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/latest?from=EUR&to=CZK","{\n" +
                "    \"rate\": {\n" +
                "        \"from\": \"EUR\",\n" +
                "        \"to\": \"CZK\",\n" +
                "        \"value\": 25.088\n" +
                "    },\n" +
                "    \"difference\": 0.7307\n" +
                "}"
                , requestParamGrid));

        h3 = new H3("3. Endpoint");
        textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return latest rate of specific currencies, but user can choose amount" +
                " of first currency.");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));
        requestParams.add(new RequestParam("amount", "amount to be converted"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/latest/conversion?from=EUR&to=CZK&amount=15","{\n" +
                "    \"rate\": {\n" +
                "        \"value\": 379.74,\n" +
                "        \"from\": \"EUR\",\n" +
                "        \"to\": \"CZK\"\n" +
                "    },\n" +
                "    \"difference\": 0.7307\n" +
                "}"
                , requestParamGrid));
    }

    private void addHistoricalRatesEndPoints() {
        H3 h3 = new H3("1. Endpoint");
        h3.getStyle().set("text-align", "left");
        TextArea textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return historical exchange rates of currencies in date range.");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        Grid<RequestParam> requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        List<RequestParam> requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));
        requestParams.add(new RequestParam("2019-11-27", "start date"));
        requestParams.add(new RequestParam("2019-11-29", "end date"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/2019-11-27/2019-11-29?from=EUR&to=CZK","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.515\n" +
                "        },\n" +
                "        \"date\": \"2019-11-27\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.574\n" +
                "        },\n" +
                "        \"date\": \"2019-11-28\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.515\n" +
                "        },\n" +
                "        \"date\": \"2019-11-29\"\n" +
                "    }\n" +
                "]"
                , requestParamGrid));

        h3 = new H3("2. Endpoint");
        textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return last N historical exchange rates for specific currencies");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));
        requestParams.add(new RequestParam("count", "number of latest rates from now"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/latest?from=EUR&to=CZK&count=2","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.642\n" +
                "        },\n" +
                "        \"date\": \"2022-03-08\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.364\n" +
                "        },\n" +
                "        \"date\": \"2022-03-09\"\n" +
                "    }\n" +
                "]"
                , requestParamGrid));

        h3 = new H3("3. Endpoint");
        textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return all historical exchange rates for specific currencies. Historical exchange" +
                "rates of currencies are available from 01.01.2010");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/all?from=EUR&to=CZK","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.752\n" +
                "        },\n" +
                "        \"date\": \"2019-01-02\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.683\n" +
                "        },\n" +
                "        \"date\": \"2019-01-03\"\n" +
                "    },\n" +
                "...\n" +
                "...\n" +
                "]"
                , requestParamGrid));

        h3 = new H3("4. Endpoint");
        textArea = new TextArea("Description");
        textArea.setValue("Endpoint will return historical exchange rate of currencies from entered date");
        textArea.getStyle().set("width", "40em");
        textArea.setReadOnly(true);
        add(h3, textArea);

        requestParamGrid = new Grid<>(RequestParam.class,false);
        requestParamGrid.setAllRowsVisible(true);
        requestParamGrid.addColumn(RequestParam::getValue).setHeader("Value");
        requestParamGrid.addColumn(RequestParam::getDescription).setHeader("Description");

        requestParams = new LinkedList<>();
        requestParams.add(new RequestParam("2022-03-09", "specific date"));
        requestParams.add(new RequestParam("from", "Code of base currency"));
        requestParams.add(new RequestParam("to", "Code of output currency"));

        requestParamGrid.setItems(requestParams);
        requestParamGrid.getColumns().get(1).setWidth("20em");

        add(new EndPointView("http://localhost:8080/rates/2022-03-09?from=EUR&to=CZK","[\n" +
                "    {\n" +
                "        \"rate\": {\n" +
                "            \"from\": \"EUR\",\n" +
                "            \"to\": \"CZK\",\n" +
                "            \"value\": 25.364\n" +
                "        },\n" +
                "        \"date\": \"2022-03-09\"\n" +
                "    }\n" +
                "]"
                , requestParamGrid));
    }
}
