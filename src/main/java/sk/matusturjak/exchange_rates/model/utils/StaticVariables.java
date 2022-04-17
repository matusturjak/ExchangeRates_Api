package sk.matusturjak.exchange_rates.model.utils;

/**
 * Trieda, ktorá definuje atribúty využivajúce sa v celom projekte.
 */
public class StaticVariables {
//    public static String[] currencies = {"EUR","CAD","CZK","HUF","AUD","HRK","CHF","PLN", "BGN", "TRY", "USD", "GBP", "SEK","NOK"};
    public static String[] currencies = {"EUR","CAD","CZK","HRK"};
    public static String ARMA_GARCH="arma_garch";
    public static String ARMA_IGARCH="arma_igarch";
    public static String ARMA="arma";
    public static String SINGLE_EXP="sexp";
    public static String DOUBLE_EXP="dexp";
    public static String EXP_SMOOTHING="exp";

    public static int MODEL_DAYS = 100365;
}
