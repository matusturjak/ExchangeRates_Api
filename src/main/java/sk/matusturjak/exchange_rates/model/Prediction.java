package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Objekt mapovaný do DB, ktorý obsahuje vypočítanú hodnotu predikcie menového kurzu pre určitý dátum v budúcnosti.
 */
@Entity
@Table(name = "predictions", indexes = {@Index(name = "ind_pred", columnList = "from_curr,to_curr,method", unique = false)})
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String method;

    @Column(name = "volatility")
    private Double volatility;

    public Prediction() {
    }

    public Prediction(String fromCurr, String toCurr, double value, String date, String method, Double volatility) {
        this.rate = new Rate(fromCurr, toCurr, value);
        this.method = method;
        this.date = date;
        this.volatility = volatility;
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

    public Double getVolatility() {
        return volatility;
    }

    public void setVolatility(Double volatility) {
        this.volatility = volatility;
    }
}
