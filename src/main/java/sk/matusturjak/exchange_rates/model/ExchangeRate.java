package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Objekt mapovaný do DB, ktorý ukladá informácie o historickej hodnote menového kurzu za určitý dátum
 */
@Entity
@Table(name = "exchange_rates", indexes = {@Index(name = "ind_date_u", columnList = "from_curr,to_curr,date_value", unique = false)})
public class ExchangeRate {

    @Id
    @SequenceGenerator(name = "seq_exchange_rate_id", sequenceName = "SEQ_EXCHANGE_RATE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exchange_rate_id")
    @JsonIgnore
    private Long id;

    @Embedded
    private Rate rate;

    @Column(name = "date_value", nullable = false)
    private String date;

    public ExchangeRate() {
    }

    public ExchangeRate(String fromCurr, String toCurr, double value, String date) {
        this.rate = new Rate(fromCurr, toCurr, value);
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
