package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.ModelOutput;

import javax.transaction.Transactional;

@Repository
public interface ModelOutputRepository extends JpaRepository<ModelOutput, Long> {
    @Query(value = "SELECT * FROM model_output mo WHERE from_curr like ?1 and to_curr like ?2 and method like ?3", nativeQuery = true)
    ModelOutput getModelOutput(String from, String to, String method);

    @Query(value = "SELECT * FROM model_output p WHERE p.from_curr LIKE ?1 and p.to_curr LIKE ?2 AND " +
            "p.method LIKE CASE WHEN ?3 < 3 THEN \"%arma_garch%\" WHEN ?3 = 3 THEN \"%exp3\" WHEN ?3 = 5 THEN \"%exp5\" ELSE \"\" END", nativeQuery = true)
    ModelOutput getModelOutput(String from, String to, Integer predictions);

    @Query(value = "SELECT * FROM model_output mo WHERE from_curr like ?1 and to_curr like ?2 and method like ?3", nativeQuery = true)
    ModelOutput getSigma(String from, String to, String method);

    @Modifying
    @Transactional
    @Query(value = "UPDATE model_output set residuals = ?4, sigma = ?5 WHERE from_curr like ?1 and to_curr like ?2 and method like ?3", nativeQuery = true)
    void updateModelOutput(String from, String to, String method, String residuals, String sigma);
}
