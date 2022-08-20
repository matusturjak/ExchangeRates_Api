package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Objekt mapovaný do DB, ktorý obsahuje aktuálnu hodnotu menového kurzu a taktiež informáciu o tom ako veľmi sa táto hodnota zmenila
 * v porovnaní s predchádzajúcou aktuálnou hodnotou
 */
@Entity
@Table(name = "latest_rates", indexes = {@Index(name = "ind_latest", columnList = "from_curr, to_curr", unique = false)})
public class LatestRate {

    @Id
    @SequenceGenerator(name = "seq_latest_rate_id", sequenceName = "SEQ_LATEST_RATE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_latest_rate_id")
    @JsonIgnore
    private Long id;

    @Embedded
    private Rate rate;

    @Column(name = "difference")
    private Double difference;

    public LatestRate() {
    }

    public LatestRate(String fromCurr, String toCurr, double value) {
        this.rate = new Rate(fromCurr, toCurr, value);
    }

    public LatestRate(String fromCurr, String toCurr, double value, Double difference) {
        this(fromCurr, toCurr, value);
        this.difference = difference;
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
