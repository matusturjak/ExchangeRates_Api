package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "predictions", indexes = {@Index(name = "ind_pred", columnList = "first_country,second_country,method", unique = false)})
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Embedded
    private Rate rate;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "method", nullable = false)
    @JsonIgnore
    private String method;

    public Prediction() {
    }

    public Prediction(String firstCountry, String secondCountry, double value, String date, String method) {
        this.rate = new Rate(firstCountry, secondCountry, value);
        this.method = method;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
