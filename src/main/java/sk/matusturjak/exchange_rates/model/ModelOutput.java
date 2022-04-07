package sk.matusturjak.exchange_rates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "model_output")
public class ModelOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "method")
    private String method;

    @Column(name = "from_curr", columnDefinition = "VARCHAR(3)")
    private String fromCurr;

    @Column(name = "to_curr", columnDefinition = "VARCHAR(3)")
    private String toCurr;

    @Column(name = "fitted", columnDefinition = "TEXT")
    private String fitted;

    @Column(name = "residuals", columnDefinition = "TEXT")
    private String residuals;

    @Column(name = "sigma", columnDefinition = "TEXT")
    private String sigma;

    public ModelOutput() {
    }

    public ModelOutput(String method, String fromCurr, String toCurr, String residuals, String sigma, String fitted) {
        this.method = method;
        this.fromCurr = fromCurr;
        this.toCurr = toCurr;
        this.residuals = residuals;
        this.sigma = sigma;
        this.fitted = fitted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public String getResiduals() {
        return residuals;
    }

    public void setResiduals(String residuals) {
        this.residuals = residuals;
    }

    public String getSigma() {
        return sigma;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFitted() {
        return fitted;
    }

    public void setFitted(String fitted) {
        this.fitted = fitted;
    }
}
