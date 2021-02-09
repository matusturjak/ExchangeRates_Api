package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.ExchangeRate;

import java.util.Date;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.first_country = ?1 and e.second_country = ?2", nativeQuery = true)
    List<ExchangeRate> getAllRates(String from, String to);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.first_country = ?1 and e.second_country = ?2 ORDER BY e.date DESC LIMIT ?3", nativeQuery = true)
    List<ExchangeRate> getLastRates(String from, String to, Integer count);

    @Query(value = "SELECT * FROM exchange_rates e WHERE e.first_country = ?1 and e.second_country = ?2 AND e.date BETWEEN ?3 AND ?4", nativeQuery = true)
    List<ExchangeRate> getRates(String from, String to, Date start_at, Date end_at);

    @Query(value = "SELECT * FROM exchange_rates e where exists (SELECT first_country,second_country,date,value FROM " +
            "( select id,first_country,second_country,date,value, row_number() over (PARTITION BY first_country,second_country ORDER BY date DESC) as rn" +
            " from exchange_rates ee) tab WHERE rn = 1 and e.id = tab.id)", nativeQuery = true)
    List<ExchangeRate> getLatest();

    @Query(value = "SELECT count(*) FROM exchange_rates", nativeQuery = true)
    Integer getSize();
}
