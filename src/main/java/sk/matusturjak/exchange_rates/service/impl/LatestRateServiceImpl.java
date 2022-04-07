package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.model.utils.NumHelper;
import sk.matusturjak.exchange_rates.repository.LatestRateRepository;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import java.util.List;

@Service
public class LatestRateServiceImpl implements LatestRateService {

    private final LatestRateRepository latestRateRepository;

    public LatestRateServiceImpl(LatestRateRepository latestRateRepository) {
        this.latestRateRepository = latestRateRepository;
    }

    @Override
    public List<LatestRate> getAllLatestRates() {
        return this.latestRateRepository.findAll();
    }

    @Override
    public List<LatestRate> getLatestRates(String from) {
        return this.latestRateRepository.getLatestRates(from);
    }

    @Override
    public LatestRate getLatestRate(String from, String to) {
        return this.latestRateRepository.getLatestRate(from, to);
    }

    @Override
    public LatestRate getLatestRate(String from, String to, Double amount) {
        LatestRate latestRate = this.latestRateRepository.getLatestRate(from, to);
        double value = latestRate.getRate().getValue();
        latestRate.getRate().setValue(NumHelper.roundAvoid(value * amount, 4));
        return latestRate;
    }

    @Override
    public Integer getSize() {
        return this.latestRateRepository.getSize();
    }

    @Override
    public void updateRate(String from, String to, double value, double difference) {
        this.latestRateRepository.updateRate(from, to, value, difference);
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
