package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.repository.LatestRateRepository;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LatestRateServiceImpl implements LatestRateService {

    @Autowired
    private LatestRateRepository latestRateRepository;

    @Override
    public List<LatestRate> getAllLatestRates() {
        return this.latestRateRepository.findAll();
    }

    @Override
    public List<LatestRate> getLatestRates(String from) {
        return this.latestRateRepository
                .findAll()
                .stream()
                .filter(latestRate -> latestRate.getRate().getFirstCountry().equals(from))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getSize() {
        return this.latestRateRepository.getSize();
    }

    @Override
    public void updateRate(String from, String to, double value) {
        this.latestRateRepository.updateRate(from, to, value);
    }

    @Override
    public void addRate(LatestRate latestRate) {
        this.latestRateRepository.save(latestRate);
    }

    @Override
    public void deleteAllRates() {
        this.latestRateRepository.deleteAll();
    }
}
