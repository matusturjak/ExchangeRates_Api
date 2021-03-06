package sk.matusturjak.exchange_rates;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sk.matusturjak.exchange_rates.model.others.CalculatePredictions;
import sk.matusturjak.exchange_rates.model.others.DownloadExchangeRates;
import sk.matusturjak.exchange_rates.repository.ExchangeRateRepository;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

@SpringBootApplication
public class ExchangeRatesApplication {

	public static void main(String[] args) {
		ApplicationContext run = SpringApplication.run(ExchangeRatesApplication.class, args);
		DownloadExchangeRates downloadExchangeRates = run.getBean(DownloadExchangeRates.class);
		CalculatePredictions calculatePredictions = run.getBean(CalculatePredictions.class);
		ExchangeRateService service = run.getBean(ExchangeRateService.class);
		calculatePredictions.calculateAndSave();
		if(service.getSize() < 1) {
			try {
				downloadExchangeRates.downloadAndSaveRatesFromECB();
				downloadExchangeRates.downloadAndSaveLatestRatesFromECB();
				calculatePredictions.calculateAndSave();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
