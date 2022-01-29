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

    @Column(name = "first_country", columnDefinition = "VARCHAR(3)")
    private String firstCountry;

    @Column(name = "second_country", columnDefinition = "VARCHAR(3)")
    private String secondCountry;

    @Column(name = "fitted", columnDefinition = "TEXT")
    private String fitted;

    @Column(name = "residuals", columnDefinition = "TEXT")
    private String residuals;

    @Column(name = "sigma", columnDefinition = "TEXT")
    private String sigma;

    public ModelOutput() {
    }

    public ModelOutput(String method, String firstCountry, String secondCountry, String residuals, String sigma, String fitted) {
        this.method = method;
        this.firstCountry = firstCountry;
        this.secondCountry = secondCountry;
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
