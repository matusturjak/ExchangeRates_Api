package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.Prediction;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface PredictionsRepository extends JpaRepository<Prediction, Long> {
    @Query(value = "SELECT * FROM predictions p WHERE p.first_country LIKE ?1 and p.second_country LIKE ?2 AND " +
            "p.method LIKE CASE WHEN ?3 < 3 THEN \"%arma_garch%\" WHEN ?3 = 3 THEN \"%exp3\" WHEN ?3 = 5 THEN \"%exp5\" ELSE \"\" END LIMIT ?3", nativeQuery = true)
    List<Prediction> getPredictions(String from, String to, Integer numberOfPredictions);

    @Modifying
    @Transactional
    @Query(value = "UPDATE predictions p set p.value = ?4, p.date = ?5 where p.first_country = ?1 and p.second_country = ?2 and p.method = ?3", nativeQuery = true)
    void updatePredictions(String from, String to, String method, double value, Date date);

    @Query(value = "SELECT * FROM predictions p WHERE p.first_country = ?1 AND p.second_country = ?2 AND p.method = ?3", nativeQuery = true)
    Prediction findPrediction(String from, String to, String method);
}
