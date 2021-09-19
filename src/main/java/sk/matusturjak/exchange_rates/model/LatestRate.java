package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "latest_rates", indexes = {@Index(name = "ind_latest", columnList = "first_country, second_country", unique = false)})
public class LatestRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Embedded
    private Rate rate;

    private Double difference;

    public LatestRate() {
    }

    public LatestRate(String firstCountry, String secondCountry, double value) {
        this.rate = new Rate(firstCountry, secondCountry, value);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    @Override
    public String toString() {
        return "LatestRate{" +
                "id=" + id +
                ", rate=" + rate.toString() +
                '}';
    }
}
