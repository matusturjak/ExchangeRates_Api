package sk.matusturjak.exchange_rates.model.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.matusturjak.exchange_rates.model.ExchangeRate;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DownloadExchangeRates {
    private final ExchangeRateService exchangeRateService;
    private final LatestRateService latestRateService;

//    private static String[] currency = {
//            "EUR","CAD","HKD","PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
//            "BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
//            "ZAR","USD","MXN","ILS","GBP","KRW","MYR","ISK"
//    };

    private static String[] currency = StaticVariables.currencies;

    public DownloadExchangeRates(ExchangeRateService exchangeRateService, LatestRateService latestRateService) {
        this.exchangeRateService = exchangeRateService;
        this.latestRateService = latestRateService;
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

                        if (Integer.parseInt(date.split("-")[0]) >= 2010) {
                            this.exchangeRateService.addRate(
                                    new ExchangeRate("EUR", currency[i], NumHelper.roundAvoid(Double.parseDouble(rate), 4), date)
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
                        new ExchangeRate(currName, "EUR", NumHelper.roundAvoid((double) 1 / exchangeRate.getRate().getValue(), 4), exchangeRate.getDate())
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
                        double rateIJ = NumHelper.roundAvoid(eurToJ.get(k + diff).getRate().getValue() / eurToI.get(k + diff1).getRate().getValue(), 4);
                        this.exchangeRateService.addRate(
                                new ExchangeRate(currency[i], currency[j], rateIJ, eurToI.get(k + diff1).getDate())
                        );
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Europe/Paris")
    public void downloadAndSaveLatestRatesFromECB() throws Exception {
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
            String first = latestRates.get(i).getRate().getToCurr();

            for (int j = 0; j < latestRates.size(); j++) {
                if (!first.equals(latestRates.get(j).getRate().getToCurr())) {
                    double rateEurI = latestRates.get(i).getRate().getValue();
                    double rateEurJ = latestRates.get(j).getRate().getValue();
                    double rateIJ = NumHelper.roundAvoid(rateEurJ / rateEurI, 4);

                    help.add(new LatestRate(latestRates.get(i).getRate().getToCurr(), latestRates.get(j).getRate().getToCurr(), rateIJ));
                }
            }
        }

        latestRates.forEach(
                latestRate -> help.add(
                        new LatestRate(latestRate.getRate().getToCurr(), "EUR", NumHelper.roundAvoid((double) 1 / (double) latestRate.getRate().getValue(), 4))
                )
        );

        latestRates.addAll(help);

        latestRates.forEach(latestRate -> {
            LatestRate r = this.latestRateService.getLatestRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr());
            if (r != null) {
                double diff = NumHelper.roundAvoid(latestRate.getRate().getValue() - r.getRate().getValue(), 4);
                this.latestRateService.updateRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr(), latestRate.getRate().getValue(), diff);
            } else {
                this.latestRateService.addRate(new LatestRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr(), latestRate.getRate().getValue(), null));
            }
        });

        List<ExchangeRate> lastExchangeRates = this.exchangeRateService.getRates(formattedDate);

        if (lastExchangeRates.size() == 0) {
            latestRates.forEach(latestRate -> this.exchangeRateService.addRate(
                    new ExchangeRate(latestRate.getRate().getFromCurr(), latestRate.getRate().getToCurr(), latestRate.getRate().getValue(), formattedDate)
            ));
        } else {
            lastExchangeRates.forEach(exchangeRate -> {
                if (!exchangeRate.getDate().equals(formattedDate)) {
                    latestRates.stream()
                            .filter(
                                    latestRate -> latestRate.getRate().getFromCurr().equals(exchangeRate.getRate().getFromCurr()) &&
                                            latestRate.getRate().getToCurr().equals(exchangeRate.getRate().getToCurr())
                            )
                            .findFirst()
                            .ifPresent(founded -> this.exchangeRateService.addRate(
                                    new ExchangeRate(
                                            exchangeRate.getRate().getFromCurr(), exchangeRate.getRate().getToCurr(),
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
