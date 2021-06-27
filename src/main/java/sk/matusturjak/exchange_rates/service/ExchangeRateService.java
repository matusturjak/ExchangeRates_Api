package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    void addRate(ExchangeRate exchangeRate);

    List<ExchangeRate> getAllRates(String from, String to);
    List<ExchangeRate> getLastRates(String from, String to, Integer count);
    List<ExchangeRate> getRates(String from, String to, String start_at, String end_at);
    List<ExchangeRate> getRates(String date);
    List<ExchangeRate> getLatest();

    Integer getSize();
}
