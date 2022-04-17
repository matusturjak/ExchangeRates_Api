package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.ExchangeRate;

import java.util.Date;
import java.util.List;

/**
 * Trieda, ktorá priamo komunikuje s DB a poskytuje vrstve Service údaje o historických dátach menových kurzov.
 */
@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2", nativeQuery = true)
    List<ExchangeRate> getAllRates(String from, String to);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 ORDER BY e.date DESC LIMIT ?3", nativeQuery = true)
    List<ExchangeRate> getLastRates(String from, String to, Integer count);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 AND STR_TO_DATE(e.date,'%Y-%m-%d') = ?3", nativeQuery = true)
    List<ExchangeRate> getRatesByDate(String from, String to, String date);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 AND STR_TO_DATE(e.date,'%Y-%m-%d') BETWEEN ?3 AND ?4", nativeQuery = true)
    List<ExchangeRate> getRates(String from, String to, Date start_at, Date end_at);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.date LIKE ?1", nativeQuery = true)
    List<ExchangeRate> getRates(String date);

    @Query(value = "SELECT * FROM exchange_rates e where exists (SELECT from_curr,to_curr,date,value FROM " +
            "( select id,from_curr,to_curr,date,value, row_number() over (PARTITION BY from_curr,to_curr ORDER BY date DESC) as rn" +
            " from exchange_rates ee) tab WHERE rn = 1 and e.id = tab.id)", nativeQuery = true)
    List<ExchangeRate> getLatest();

    @Query(value = "select id, date, from_curr, to_curr, value" +
            "    from" +
            "        (select id, date, from_curr, to_curr, value, row_number() over (partition by from_curr, to_curr order by date desc) as row_num" +
            "         from exchange_rates order by date desc) d" +
            "    where d.row_num = 2", nativeQuery = true)
    List<ExchangeRate> get2ndLatestRates();

    @Query(value = "SELECT count(*) FROM exchange_rates", nativeQuery = true)
    Integer getSize();
}
