package sk.matusturjak.exchange_rates.model.others;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.LatestRateService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DownloadExchangeRates {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private LatestRateService latestRateService;

    private final RestTemplate restTemplate;

    private static String[] currency = {
            "EUR","CAD","HKD"//,"PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
            //"BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
            //"ZAR","USD","MXN","ILS","GBP","KRW","MYR"
    };

    public DownloadExchangeRates(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void downloadAndSaveJson(){
        HashMap<String,Double> list = new HashMap<>();

        for(int i = 0; i < currency.length; i++){
            for(int j = 0; j < currency.length; j++){
                if (i == j)
                    continue;

                list.clear();
                String url = "https://api.exchangeratesapi.io/history?start_at=2010-01-01&end_at=" +
                            DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()) +
                            "&symbols=" + currency[j] + "&base="+currency[i];
                String json = null;

                try {
                    json = this.restTemplate.getForObject(url, String.class);
                } catch(HttpClientErrorException ex) {
                    ex.printStackTrace();
                    continue; // ???
                }

                JSONObject jsonObject = new JSONObject(json);

                JSONObject currencies = jsonObject.getJSONObject("rates");
                Iterator x = currencies.keys();
                JSONArray dates = currencies.names();
                JSONArray jsonArray = new JSONArray();

                while (x.hasNext()){
                    String value = (String) x.next();
                    JSONObject obj = currencies.getJSONObject(value);
                    jsonArray.put(obj.get(currency[j]));
                }

                if(dates.length() == jsonArray.length()) {
                    for (int k = 0; k < jsonArray.length(); k++) {
                        list.put(dates.get(k).toString(), Double.parseDouble(jsonArray.get(k).toString()));
                    }
                }

                TreeMap<String, Double> sorted = new TreeMap<>(list);
                for( Map.Entry<String, Double> entry : sorted.entrySet()) {
                    this.exchangeRateService.addRate(
                            new ExchangeRate(currency[i],currency[j],entry.getValue(),entry.getKey())
                    );
                }
            }
        }
        System.out.println("done..");
    }

    public void downloadAndSaveLatest(){
        for (String s : currency) {
            String url = "https://api.exchangeratesapi.io/latest?base=" + s;
            String json = this.restTemplate.getForObject(url, String.class);
            if (json == null || json.contains("error"))
                continue;

            JSONObject jsonObject = new JSONObject(json);
            JSONObject rates = jsonObject.getJSONObject("rates");
            String date = jsonObject.getString("date");

            Iterator x = rates.keys();
            JSONArray names = rates.names();
            JSONArray jsonArray = new JSONArray();

            while (x.hasNext()) {
                String key = (String) x.next();
                jsonArray.put(rates.get(key));
            }

            //add latest rates to exchange_rates table
//            for (int j = 0; j < jsonArray.length(); j++) {
//                try {
//                    this.exchangeRateService.addRate(
//                            new ExchangeRate(
//                                    currency[i], (String) names.get(j),Double.parseDouble(String.valueOf(jsonArray.get(j))),
//                                    new SimpleDateFormat("yyyy-MM-dd").parse(date)
//                            )
//                    );
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }

            if (this.latestRateService.getLatestRates(s).size() == 0) {
                for (int j = 0; j < jsonArray.length(); j++) {
                    this.latestRateService.addRate(
                            new LatestRate(s, (String) names.get(j), Double.parseDouble(String.valueOf(jsonArray.get(j))))
                    );
                }
            } else {
                for (int j = 0; j < jsonArray.length(); j++) {
                    this.latestRateService.updateRate(s, (String) names.get(j), Double.parseDouble(String.valueOf(jsonArray.get(j))));
                }
            }
        }
    }
}
