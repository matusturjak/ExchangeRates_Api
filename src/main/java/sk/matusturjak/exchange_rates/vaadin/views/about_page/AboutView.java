package sk.matusturjak.exchange_rates.vaadin.views.about_page;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import sk.matusturjak.exchange_rates.vaadin.views.MainLayout;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class AboutView extends VerticalLayout {
    private TextField name;
    private Button sayHello;

    public AboutView() {
        setSpacing(false);

        add(new H2("Exchange Rates Predictions"));
        add(new Paragraph("Application for prediction exchange rates"));

        FlexLayout footerWrapper = new FlexLayout();

        Div footer = new Div();
        footer.setText("Developed by Matúš Turják matus.turjak81@gmail.com");
        footerWrapper.setAlignItems(Alignment.END);
        footerWrapper.getElement().getStyle().set("order", "999");
        footerWrapper.add(footer);
        add(footerWrapper);
        expand(footerWrapper);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
