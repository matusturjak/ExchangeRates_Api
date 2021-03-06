package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.LatestRate;

import java.util.List;

public interface LatestRateService {
    List<LatestRate> getAllLatestRates();
    List<LatestRate> getLatestRates(String from);
    LatestRate getLatestRate(String from, String to);

    Integer getSize();

    void updateRate(String from, String to, double value);
    void addRate(LatestRate latestRate);
    void deleteAllRates();
}
