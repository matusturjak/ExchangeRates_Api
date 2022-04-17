package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.LatestRate;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Trieda, ktorá priamo komunikuje s DB a poskytuje vrstve Service údaje o aktuálnych dátach menových kurzov.
 */
@Repository
public interface LatestRateRepository extends JpaRepository<LatestRate, Long> {

    @Query(value = "SELECT * FROM latest_rates WHERE from_curr LIKE ?1", nativeQuery = true)
    List<LatestRate> getLatestRates(String from);

    @Query(value = "SELECT * FROM latest_rates WHERE from_curr LIKE ?1 AND to_curr LIKE ?2", nativeQuery = true)
    LatestRate getLatestRate(String from, String to);

    @Modifying
    @Transactional
    @Query(value = "UPDATE latest_rates SET value = ?3, difference = ?4 WHERE from_curr = ?1 AND to_curr = ?2", nativeQuery = true)
    void updateRate(String from, String to, double value, double difference);

    @Query(value = "SELECT count(*) from latest_rates", nativeQuery = true)
    Integer getSize();
}
