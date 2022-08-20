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

    @Query(value = "SELECT id,from_curr,to_curr,date_value,rate_value" +
            "    from (" +
            "        select id,from_curr,to_curr,date_value,rate_value" +
            "            FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 ORDER BY date_value DESC" +
            "    ) where rownum <= ?3", nativeQuery = true)
    List<ExchangeRate> getLastRates(String from, String to, Integer count);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 AND to_date(e.date_value,'%YYYY-%MM-%DD') = ?3", nativeQuery = true)
    List<ExchangeRate> getRatesByDate(String from, String to, String date);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.from_curr = ?1 and e.to_curr = ?2 AND to_date(e.date_value,'%YYYY-%MM-%DD') BETWEEN ?3 AND ?4", nativeQuery = true)
    List<ExchangeRate> getRates(String from, String to, Date start_at, Date end_at);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.date_value LIKE ?1", nativeQuery = true)
    List<ExchangeRate> getRates(String date);

    @Query(value = "SELECT * FROM exchange_rates e where exists (SELECT from_curr,to_curr,date_value,rate_value FROM " +
            "( select id,from_curr,to_curr,date_value,rate_value, row_number() over (PARTITION BY from_curr,to_curr ORDER BY date_value DESC) as rn" +
            " from exchange_rates ee) tab WHERE rn = 1 and e.id = tab.id)", nativeQuery = true)
    List<ExchangeRate> getLatest();

    @Query(value = "select id, date_value, from_curr, to_curr, rate_value" +
            "    from" +
            "        (select id, date_value, from_curr, to_curr, rate_value, row_number() over (partition by from_curr, to_curr order by date_value desc) as row_num" +
            "         from exchange_rates order by date_value desc) d" +
            "    where d.row_num = 2", nativeQuery = true)
    List<ExchangeRate> get2ndLatestRates();

    @Query(value = "SELECT count(*) FROM exchange_rates", nativeQuery = true)
    Integer getSize();
}
