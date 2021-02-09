package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "exchange_rates", indexes = {@Index(name = "ind_date_u", columnList = "first_country,second_country,date", unique = false)})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Embedded
    private Rate rate;

    @Column(name = "date", nullable = false)
    private String date;

    public ExchangeRate() {
    }

    public ExchangeRate(String firstCountry, String secondCountry, double value, String date) {
        this.rate = new Rate(firstCountry, secondCountry, value);
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }
}
