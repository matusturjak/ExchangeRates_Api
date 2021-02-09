package sk.matusturjak.exchange_rates;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.model.others.DownloadExchangeRates;
import sk.matusturjak.exchange_rates.repository.LatestRateRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DownloadExchangeRateTest {

    @Autowired
    private DownloadExchangeRates downloadExchangeRates;

    @Autowired
    private LatestRateRepository latestRateRepository;

    @Test
    public void downloadAndSaveRates(){
        this.downloadExchangeRates.downloadAndSaveLatest();

        List<LatestRate> list = this.latestRateRepository.findAll();
        assertNotNull(list);
    }
}
