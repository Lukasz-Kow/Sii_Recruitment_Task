package sii.task.recruitment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import sii.task.recruitment.model.Currency;
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
    public void testFindInitializedExchangeRateEURToUSD() {
        exchangeRateRepository.save(new ExchangeRate(null, Currency.EUR, Currency.USD, new BigDecimal("1.072350")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.EUR, Currency.USD);

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("1.072350"), rateOptional.get().getRate());
    }

    @Test
    public void testFindInitializedExchangeRateEURToGBP() {
        exchangeRateRepository.save(new ExchangeRate(null, Currency.EUR, Currency.GBP, new BigDecimal("0.857310")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.EUR, Currency.GBP);

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("0.857310"), rateOptional.get().getRate());
    }

    @Test
    public void testFindInitializedExchangeRateUSDToGBP() {
        exchangeRateRepository.save(new ExchangeRate(null, Currency.GBP, Currency.USD, new BigDecimal("1.250750")));
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.GBP, Currency.USD);

        assertTrue(rateOptional.isPresent());
        assertEquals(new BigDecimal("1.250750"), rateOptional.get().getRate());
    }
}