package sk.matusturjak.exchange_rates;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.repository.ExchangeRateRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExchangeRateRepositoryTest {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Before
    public void fillDB() {
        exchangeRateRepository.save(new ExchangeRate("CAD", "EUR", 0, "2010-01-04"));
        exchangeRateRepository.save(new ExchangeRate("CAD", "HKD", 0, "2010-01-05"));
        exchangeRateRepository.save(new ExchangeRate("EUR", "CAD", 0, "2010-02-04"));
        exchangeRateRepository.save(new ExchangeRate("EUR", "HKD", 0, "2010-02-05"));
        exchangeRateRepository.save(new ExchangeRate("HKD", "CAD", 0, "2010-03-04"));
        exchangeRateRepository.save(new ExchangeRate("HKD", "EUR", 0, "2010-03-05"));
    }

    @After
    public void clearDB() {
        this.exchangeRateRepository.deleteAll();
    }

    @Test
    public void rateCAD_EURShouldHave1Record() {
        assertEquals(2, this.exchangeRateRepository.getAllRates("CAD", "EUR").size());
    }

}
