package sk.matusturjak.exchange_rates.model.others;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            "EUR","CAD","HKD","PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
            "BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
            "ZAR","USD","MXN","ILS","GBP","KRW","MYR","ISK"
    };

    public DownloadExchangeRates(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void downloadAndSaveRatesFromECB() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        for (int i = 1; i < currency.length; i++) {
            String url = "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/"+ currency[i].toLowerCase() + ".xml";

            try {

                URLConnection conn = new URL(url).openConnection();

                try (InputStream is = conn.getInputStream()) {

                    // unknown XML better turn on this
                    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

                    DocumentBuilder dBuilder = dbf.newDocumentBuilder();

                    org.w3c.dom.Document doc = dBuilder.parse(is);

                    org.w3c.dom.Element element = doc.getDocumentElement();

                    NodeList nodeList = element.getElementsByTagName("Obs");

                    for (int j = 0; j < nodeList.getLength(); j++) {
                        Node obs = nodeList.item(j);
                        String rate = obs.getAttributes().getNamedItem("OBS_VALUE").getNodeValue();
                        String date = obs.getAttributes().getNamedItem("TIME_PERIOD").getNodeValue();

                        if (Integer.parseInt(date.split("-")[0]) >= 2021) {
                            this.exchangeRateService.addRate(
                                    new ExchangeRate("EUR", currency[i], Double.parseDouble(rate), date)
                            );
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Invalid request for domain : " + url);
            }
        }

        for (int i = 1; i < currency.length; i++) {
            List<ExchangeRate> eurToI = this.exchangeRateService.getAllRates("EUR", currency[i]);
            // EUR to
            String currName = currency[i];
            eurToI.forEach(exchangeRate -> this.exchangeRateService.addRate(
                        new ExchangeRate(currName, "EUR", (double) 1 / exchangeRate.getRate().getValue(), exchangeRate.getDate())
                    )
            );

            for (int j = 1; j < currency.length; j++) {
                if (!currency[i].equals(currency[j])) {
                    List<ExchangeRate> eurToJ = this.exchangeRateService.getAllRates("EUR", currency[j]);

                    int diff = 0;
                    int diff1 = 0;
                    if (eurToI.size() < eurToJ.size()) {
                        diff = eurToJ.size() - eurToI.size();
                    } else if (eurToI.size() > eurToJ.size()) {
                        diff1 = eurToI.size() - eurToJ.size();
                    }

                    for (int k = 0; k < eurToJ.size(); k++) {
                        double rateIJ = eurToJ.get(k + diff).getRate().getValue() / eurToI.get(k + diff1).getRate().getValue();
                        this.exchangeRateService.addRate(
                                new ExchangeRate(currency[i], currency[j], rateIJ, eurToI.get(k + diff1).getDate())
                        );
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 17 * *", zone = "Europe/Paris")
    public void downloadAndSaveLatestRatesFromECB() throws IOException, ParseException {
        List<LatestRate> latestRates = new ArrayList<>();

        Document document = Jsoup.connect("https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html").get();
        String dateFromPage = document.select("h3").first().text();

        DateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = originalFormat.parse(dateFromPage);
        String formattedDate = targetFormat.format(date);

        Element table = document.getElementsByClass("forextable").get(0);

        Iterator<Element> lines = table.select("tr").iterator();
        lines.next();

        while (lines.hasNext()) {
            Element line = lines.next();
            String[] arr = line.text().split(" ");

            latestRates.add(new LatestRate("EUR", arr[0], Double.parseDouble(arr[arr.length - 1])));
        }

        List<LatestRate> help = new ArrayList<>();
        for (int i = 0; i < latestRates.size(); i++) {
            String first = latestRates.get(i).getRate().getSecondCountry();

            for (int j = 0; j < latestRates.size(); j++) {
                if (!first.equals(latestRates.get(j).getRate().getSecondCountry())) {
                    double rateEurI = latestRates.get(i).getRate().getValue();
                    double rateEurJ = latestRates.get(j).getRate().getValue();
                    double rateIJ = rateEurJ / rateEurI;

                    help.add(new LatestRate(latestRates.get(i).getRate().getSecondCountry(), latestRates.get(j).getRate().getSecondCountry(), rateIJ));
                }
            }
        }

        latestRates.forEach(
                latestRate -> help.add(
                        new LatestRate(latestRate.getRate().getSecondCountry(), "EUR", (double) 1 / (double) latestRate.getRate().getValue())
                )
        );

        latestRates.addAll(help);

        latestRates.forEach(latestRate -> {
            LatestRate r = this.latestRateService.getLatestRate(latestRate.getRate().getFirstCountry(), latestRate.getRate().getSecondCountry());
            if (r != null) {
                this.latestRateService.updateRate(latestRate.getRate().getFirstCountry(), latestRate.getRate().getSecondCountry(), latestRate.getRate().getValue());
            } else {
                this.latestRateService.addRate(new LatestRate(latestRate.getRate().getFirstCountry(), latestRate.getRate().getSecondCountry(), latestRate.getRate().getValue()));
            }
        });

        List<ExchangeRate> latestExchangeRates = this.exchangeRateService.getRates(formattedDate);

        if (latestExchangeRates.size() == 0) {
            latestRates.forEach(latestRate -> this.exchangeRateService.addRate(
                    new ExchangeRate(latestRate.getRate().getFirstCountry(), latestRate.getRate().getSecondCountry(), latestRate.getRate().getValue(), formattedDate)
            ));
        } else {
            latestExchangeRates.forEach(exchangeRate -> {
                if (!exchangeRate.getDate().equals(formattedDate)) {
                    latestRates.stream()
                            .filter(
                                    latestRate -> latestRate.getRate().getFirstCountry().equals(exchangeRate.getRate().getFirstCountry()) &&
                                            latestRate.getRate().getSecondCountry().equals(exchangeRate.getRate().getSecondCountry())
                            )
                            .findFirst()
                            .ifPresent(founded -> this.exchangeRateService.addRate(
                                    new ExchangeRate(
                                            exchangeRate.getRate().getFirstCountry(), exchangeRate.getRate().getSecondCountry(),
                                            founded.getRate().getValue(),
                                            formattedDate
                                    )
                                    )
                            );
                }
            });
        }
    }
}
