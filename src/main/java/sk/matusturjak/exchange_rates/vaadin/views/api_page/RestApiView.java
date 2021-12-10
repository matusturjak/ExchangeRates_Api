package sk.matusturjak.exchange_rates.vaadin.views.api_page;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

@Route(value = "rest_api", layout = MainLayout.class)
public class RestApiView extends VerticalLayout {
    public RestApiView() {
        add(new H1("rest api view"));
    }
}
