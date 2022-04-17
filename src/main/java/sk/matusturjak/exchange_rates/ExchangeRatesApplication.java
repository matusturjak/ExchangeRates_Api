package sk.matusturjak.exchange_rates;

import com.vaadin.flow.component.dependency.NpmPackage;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import sk.matusturjak.exchange_rates.model.utils.CalculatePredictions;
import sk.matusturjak.exchange_rates.model.utils.DownloadExchangeRates;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

import java.io.IOException;
import java.text.ParseException;

/**
 * Hlavná trieda zodpovedná za spustenie aplikácie.
 */
@SpringBootApplication
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class ExchangeRatesApplication {

	public static void main(String[] args) {
		ApplicationContext run = SpringApplication.run(ExchangeRatesApplication.class, args);
		DownloadExchangeRates downloadExchangeRates = run.getBean(DownloadExchangeRates.class);
		CalculatePredictions calculatePredictions = run.getBean(CalculatePredictions.class);
		ExchangeRateService service = run.getBean(ExchangeRateService.class);

		if(service.getSize() < 1) {
			try {
				downloadExchangeRates.downloadAndSaveRatesFromECB();
				downloadExchangeRates.downloadAndSaveLatestRatesFromECB();
				calculatePredictions.calculateAndSave();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
