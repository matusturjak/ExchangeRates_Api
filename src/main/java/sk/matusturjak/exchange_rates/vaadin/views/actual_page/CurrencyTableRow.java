package sk.matusturjak.exchange_rates.vaadin.views.actual_page;

/**
 * Trieda, ktorej dáta sa mapujú do tabuľky zobrazujúcej aktuálne hodnoty menových kurzov v aplikácii.
 */
public class CurrencyTableRow {
    private String from;
    private String to;
    private double rate;
//    private Icon diffIcon;
    private double diff;

    public CurrencyTableRow(String from, String to, double rate, double diff) {
        this.from = from;
        this.to = to;
        this.rate = rate;
//        this.diffIcon = diffIcon;
        this.diff = diff;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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
