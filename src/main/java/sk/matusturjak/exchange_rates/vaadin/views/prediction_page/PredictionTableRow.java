package sk.matusturjak.exchange_rates.vaadin.views.prediction_page;

/**
 * Trieda, ktorej dáta sa mapujú do tabuľky zobrazujúcej predikcie menových kurzov.
 */
public class PredictionTableRow {
    private String date;
    private double value;

    public PredictionTableRow(String date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
