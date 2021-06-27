package sk.matusturjak.exchange_rates;

import javafx.application.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.repository.LatestRateRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LatestRateRepositoryTest {

    @Autowired
    private LatestRateRepository latestRateRepository;

    @Before
    public void fillDB(){
        latestRateRepository.save(new LatestRate("EUR", "HRK", 0));
        latestRateRepository.save(new LatestRate("EUR", "CHF", 0));
        latestRateRepository.save(new LatestRate("EUR", "MXN", 0));
        latestRateRepository.save(new LatestRate("EUR", "ZAR", 0));
        latestRateRepository.save(new LatestRate("EUR", "INR", 0));
        latestRateRepository.save(new LatestRate("CAD", "HRK", 0));
        latestRateRepository.save(new LatestRate("CAD", "CHF", 0));
        latestRateRepository.save(new LatestRate("CAD", "MXN", 0));
        latestRateRepository.save(new LatestRate("CAD", "ZAR", 0));
        latestRateRepository.save(new LatestRate("CAD", "INR", 0));
        latestRateRepository.save(new LatestRate("HKD", "HRK", 0));
        latestRateRepository.save(new LatestRate("HKD", "CHF", 0));
        latestRateRepository.save(new LatestRate("HKD", "MXN", 0));
        latestRateRepository.save(new LatestRate("HKD", "ZAR", 0));
        latestRateRepository.save(new LatestRate("HKD", "INR", 0));
    }

    @After
    public void clearDB() {
        this.latestRateRepository.deleteAll();
    }

    @Test
    public void numberOfRatesShouldBe15(){
        assertEquals(15, latestRateRepository.getSize());
    }

    @Test
    public void rateEUR_CZKShouldBeNull(){
        assertNull(this.latestRateRepository.getLatestRate("EUR","CZK"));
    }

    @Test
    public void getAllCADRates(){
        List<LatestRate> rates = this.latestRateRepository.getLatestRates("CAD");

        assertEquals(5, rates.size());
        rates.forEach(latestRate -> assertEquals("CAD",latestRate.getRate().getFirstCountry()));
    }

    @Test
    public void updateValueOfEUR_HRKFrom0To1(){
        this.latestRateRepository.updateRate("EUR","HRK",1);
        LatestRate rate = this.latestRateRepository.getLatestRate("EUR", "HRK");
        assertEquals(1, rate.getRate().getValue());
    }

}
