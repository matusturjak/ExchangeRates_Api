package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.ExchangeRate;

import java.util.List;

/**
 * Interface reprezentujúci funkcie, ktoré poskytujú informácie o historických hodnotách menových kurzov.
 */
public interface ExchangeRateService {
    void addRate(ExchangeRate exchangeRate);

    List<ExchangeRate> getAllRates(String from, String to);
    List<ExchangeRate> getLastRates(String from, String to, Integer count);
    List<ExchangeRate> getRatesByDate(String from, String to, String date);
    List<ExchangeRate> getRates(String from, String to, String start_at, String end_at);
    List<ExchangeRate> getRates(String date);
    List<ExchangeRate> getLatest();
    List<ExchangeRate> get2ndLatestRates();

    Integer getSize();
}
