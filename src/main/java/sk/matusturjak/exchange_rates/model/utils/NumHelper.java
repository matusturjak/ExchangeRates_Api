package sk.matusturjak.exchange_rates.model.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class NumHelper {
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static double getRMSE(List<Double> real, List<Double> predicted) {
        double sum = 0d;
        for (int i = 0; i < real.size(); i++) {
            sum += Math.pow(predicted.get(i) - real.get(i), 2);
        }
        return Math.sqrt(sum / (double) real.size());
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
