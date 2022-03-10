package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.repository.ExchangeRateRepository;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public void addRate(ExchangeRate exchangeRate) {
        this.exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public List<ExchangeRate> getAllRates(String from, String to) {
        return this.exchangeRateRepository.getAllRates(from, to);
    }

    @Override
    public List<ExchangeRate> getLastRates(String from, String to, Integer count) {
        return this.exchangeRateRepository.getLastRates(from, to, count)
                .stream().
                sorted((exchangeRate, t1) -> {
                            if (exchangeRate.getDate().compareTo(t1.getDate()) > 0) {
                                return 1;
                            } else if (exchangeRate.getDate().compareTo(t1.getDate()) < 0) {
                                return -1;
                            } else {
                                return 0;
                            }
                        })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExchangeRate> getRatesByDate(String from, String to, String date) {
        return this.exchangeRateRepository.getRatesByDate(from, to, date);
    }

    @Override
    public List<ExchangeRate> getRates(String from, String to, String start_at, String end_at) {
        try {
            return this.exchangeRateRepository.getRates(from, to, new SimpleDateFormat("yyyy-MM-dd").parse(start_at),
                    new SimpleDateFormat("yyyy-MM-dd").parse(end_at));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ExchangeRate> getRates(String date) {
        return this.exchangeRateRepository.getRates(date);
    }

    @Override
    public List<ExchangeRate> getLatest() {
        return this.exchangeRateRepository.getLatest();
    }

    @Override
    public Integer getSize() {
        return this.exchangeRateRepository.getSize();
    }
}
