package sk.matusturjak.exchange_rates.model.utils;

public class NumHelper {
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
