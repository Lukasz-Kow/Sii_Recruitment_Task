package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findBySourceCurrencyAndTargetCurrency(Currency sourceCurrency, Currency targerCurrency);

}
