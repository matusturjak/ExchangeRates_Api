package sk.matusturjak.exchange_rates.vaadin.views.actual_page;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class CurrencyTableRow {
    private String firstCountry;
    private String secondCountry;
    private double rate;
//    private Icon diffIcon;
    private double diff;

    public CurrencyTableRow(String firstCountry, String secondCountry, double rate, double diff) {
        this.firstCountry = firstCountry;
        this.secondCountry = secondCountry;
        this.rate = rate;
//        this.diffIcon = diffIcon;
        this.diff = diff;
    }

    public String getFirstCountry() {
        return firstCountry;
    }

    public void setFirstCountry(String firstCountry) {
        this.firstCountry = firstCountry;
    }

    public String getSecondCountry() {
        return secondCountry;
    }

    public void setSecondCountry(String secondCountry) {
        this.secondCountry = secondCountry;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

//    public Icon getDiffIcon() {
//        return diffIcon;
//    }
//
//    public void setDiffIcon(Icon diffIcon) {
//        this.diffIcon = diffIcon;
//    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }
}
