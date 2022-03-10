package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class Rate {

    @Column(name = "from_curr", nullable = false, columnDefinition = "VARCHAR(3)")
    @JsonProperty("from")
    private String fromCurr;

    @Column(name = "to_curr", nullable = false, columnDefinition = "VARCHAR(3)")
    @JsonProperty("to")
    private String toCurr;

    @Column(name = "value", nullable = false)
    private double value;

    public Rate() {
    }

    Rate(String fromCurr, String toCurr, double value) {
        this.fromCurr = fromCurr;
        this.toCurr = toCurr;
        this.value = value;
    }

    public String getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(String firstCountry) {
        this.fromCurr = firstCountry;
    }

    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String secondCountry) {
        this.toCurr = secondCountry;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "from='" + fromCurr + '\'' +
                ", to='" + toCurr + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        Rate rate = (Rate) o;
        return Objects.equals(fromCurr, rate.fromCurr) && Objects.equals(toCurr, rate.toCurr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurr, toCurr, value);
    }
}
