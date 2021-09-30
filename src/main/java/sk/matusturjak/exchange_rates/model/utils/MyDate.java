package sk.matusturjak.exchange_rates.model.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class MyDate {
    private SimpleDateFormat sdf;

    /**
     * Konstruktor triedy.
     */
    public MyDate(){
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Vrati pocet dni medzi dvoma datumami.
     * @param s1 prvy datum
     * @param s2 druhy datum
     * @return pocet dni medzi datumami
     */
    public int daysBetween(String s1, String s2){
        try {
            Date date1 = this.sdf.parse(s1);
            Date date2 = this.sdf.parse(s2);
            long diff = date2.getTime() - date1.getTime();
            return (int)(diff / (1000*60*60*24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Prida ku datumu pocet dni a novy datum vrati.
     * @param s1 datum
     * @param numOfDays pocet dni
     * @return novy datum
     */
    public String addDays(String s1, int numOfDays) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(s1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DAY_OF_MONTH, numOfDays);
        return sdf.format(c.getTime());
    }

    public Date addDays(Date date, int numOfDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, numOfDays);

        return c.getTime();
    }

    /**
     * Vrati pocet dni medzi datumom v parametri a datumom 1970-01-01.
     * @param d1 datum
     * @return pocet dni
     */
    public int getDayNumber(String d1){
        return this.daysBetween("1970-01-01",d1);
    }
}
