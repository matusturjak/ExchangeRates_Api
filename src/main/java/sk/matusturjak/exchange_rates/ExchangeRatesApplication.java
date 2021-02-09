package sk.matusturjak.exchange_rates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sk.matusturjak.exchange_rates.model.others.CalculatePredictions;
import sk.matusturjak.exchange_rates.model.others.DownloadExchangeRates;
import sk.matusturjak.exchange_rates.repository.ExchangeRateRepository;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

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
		ExchangeRateService service = run.getBean(ExchangeRateService.class);

		if(service.getSize() < 1) {
			downloadExchangeRates.downloadAndSaveJson();
		}

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = dateFormatter.parse("2021-02-04 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//Now create the time and schedule it
		Timer timer = new Timer();

		//Use this if you want to execute it repeatedly
		int period = 60000*60*60;//10sec
		timer.schedule(run.getBean(CalculatePredictions.class), date, period);
	}

}
