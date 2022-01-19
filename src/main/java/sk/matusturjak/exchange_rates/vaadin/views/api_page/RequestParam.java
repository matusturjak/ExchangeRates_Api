package sk.matusturjak.exchange_rates.vaadin.views.api_page;

public class RequestParam {
    private String value;
    private String description;

    public RequestParam(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
