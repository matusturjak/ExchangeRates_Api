package sk.matusturjak.exchange_rates.vaadin.views.api_page;

import com.github.appreciated.prism.element.Language;
import com.github.appreciated.prism.element.PrismHighlighter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class EndPointView extends VerticalLayout {
    public EndPointView(String requestName, String jsonResponse, Grid<RequestParam> grid) {
        TextField predictions = new TextField();
        predictions.setLabel("API Request");
        predictions.setValue(requestName);
        predictions.setClearButtonVisible(true);
        predictions.setPrefixComponent(VaadinIcon.CODE.create());
        predictions.setReadOnly(true);
        predictions.getStyle().set("width", "35em");

        PrismHighlighter json = new PrismHighlighter(jsonResponse, Language.json);

        add(predictions, new HorizontalLayout(json, grid));
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }
}
