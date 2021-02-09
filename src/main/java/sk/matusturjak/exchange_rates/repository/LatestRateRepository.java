package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.LatestRate;

import javax.transaction.Transactional;

@Repository
public interface LatestRateRepository extends JpaRepository<LatestRate, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE latest_rates SET value = ?3 WHERE first_country = ?1 AND second_country = ?2", nativeQuery = true)
    void updateRate(String from, String to, double value);

    @Query(value = "SELECT count(*) from latest_rates", nativeQuery = true)
    Integer getSize();
}
