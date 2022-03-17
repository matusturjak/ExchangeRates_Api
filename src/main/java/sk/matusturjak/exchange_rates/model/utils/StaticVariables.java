package sk.matusturjak.exchange_rates.model.utils;

public class StaticVariables {
    //            "EUR","CAD","HKD","PHP","DKK","HUF","CZK","AUD","RON","SEK","IDR","INR",
//            "BRL","RUB","HRK","JPY","THB","CHF","SGD","PLN","BGN","TRY","CNY","NOK","NZD",
//            "ZAR","USD","MXN","ILS","GBP","KRW","MYR","ISK"
    public static String[] currencies = {"EUR","CAD","CZK","HUF","AUD","HRK","CHF","PLN", "BGN", "TRY", "USD", "GBP", "SEK","NOK"};
    public static String ARMA_GARCH="arma_garch";
    public static String ARMA_IGARCH="arma_igarch";
    public static String ARMA="arma";
    public static String SINGLE_EXP="sexp";
    public static String DOUBLE_EXP="dexp";
    public static String EXP_SMOOTHING="exp";

    public static int MODEL_DAYS = 100365;
}
