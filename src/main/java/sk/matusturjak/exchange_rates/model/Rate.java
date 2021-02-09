package sk.matusturjak.exchange_rates.model;

import javax.persistence.*;

@Embeddable
public class Rate {

    @Column(name = "first_country", nullable = false)
    private String firstCountry;

    @Column(name = "second_country", nullable = false)
    private String secondCountry;

    @Column(name = "value", nullable = false)
    private double value;

    public Rate() {
    }

    Rate(String firstCountry, String secondCountry, double value) {
        this.firstCountry = firstCountry;
        this.secondCountry = secondCountry;
        this.value = value;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
