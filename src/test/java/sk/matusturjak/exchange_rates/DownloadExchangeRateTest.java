package sk.matusturjak.exchange_rates;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.model.utils.DownloadExchangeRates;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DownloadExchangeRateTest {

    @Autowired
    private DownloadExchangeRates downloadExchangeRates;

    @Autowired
    private LatestRateService latestRateService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @After
    public void clearDB() {
        this.latestRateService.deleteAllRates();
    }

    @Test
    public void downloadRatesFromECB() throws IOException, ParseException {
        this.downloadExchangeRates.downloadAndSaveRatesFromECB();
        this.downloadExchangeRates.downloadAndSaveLatestRatesFromECB();
        List<LatestRate> latestRates = this.latestRateService.getAllLatestRates();
        List<ExchangeRate> exchangeRates = this.exchangeRateService.getRates("2021-06-25");
        assertNotNull(latestRates);
        assertNotNull(exchangeRates);

        assertEquals(latestRates.size(), exchangeRates.size());
    }
}
