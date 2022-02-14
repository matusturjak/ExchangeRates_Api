package sk.matusturjak.exchange_rates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.matusturjak.exchange_rates.model.ModelOutput;

import javax.transaction.Transactional;

@Repository
public interface ModelOutputRepository extends JpaRepository<ModelOutput, Long> {
    @Query(value = "SELECT * FROM model_output mo WHERE first_country like ?1 and second_country like ?2 and method like ?3", nativeQuery = true)
    ModelOutput getModelOutput(String from, String to, String method);

    @Query(value = "SELECT * FROM model_output p WHERE p.first_country LIKE ?1 and p.second_country LIKE ?2 AND " +
            "p.method LIKE CASE WHEN ?3 < 3 THEN \"%arma_garch%\" WHEN ?3 = 3 THEN \"%exp3\" WHEN ?3 = 5 THEN \"%exp5\" ELSE \"\" END", nativeQuery = true)
    ModelOutput getModelOutput(String from, String to, Integer predictions);

    @Query(value = "SELECT * FROM model_output mo WHERE first_country like ?1 and second_country like ?2 and method like 'arma_garch1'", nativeQuery = true)
    ModelOutput getSigma(String from, String to);

    @Modifying
    @Transactional
    @Query(value = "UPDATE model_output set residuals = ?4, sigma = ?5 WHERE first_country like ?1 and second_country like ?2 and method like ?3", nativeQuery = true)
    void updateModelOutput(String from, String to, String method, String residuals, String sigma);
}