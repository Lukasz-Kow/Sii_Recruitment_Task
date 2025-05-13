package sii.task.recruitment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sii.task.recruitment.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ExchangeRateRepositoryTest {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Test
    void testFindInitializedExchangeRateEURToUSD() {
        exchangeRateRepository.save(new ExchangeRate(null, "EUR", "USD", new BigDecimal("1.072350")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency("EUR", "USD");

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("1.072350"), rateOptional.get().getRate());
    }

    @Test
    void testFindInitializedExchangeRateEURToGBP() {
        exchangeRateRepository.save(new ExchangeRate(null, "EUR", "GBP", new BigDecimal("0.857310")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency("EUR", "GBP");

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("0.857310"), rateOptional.get().getRate());
    }

    @Test
    void testFindInitializedExchangeRateUSDToGBP() {
        exchangeRateRepository.save(new ExchangeRate(null, "GBP", "USD", new BigDecimal("1.250750")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency("GBP", "USD");

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("1.250750"), rateOptional.get().getRate());
    }
}