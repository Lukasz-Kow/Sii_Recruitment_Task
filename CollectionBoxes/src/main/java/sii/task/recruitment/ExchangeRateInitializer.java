package sii.task.recruitment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.ExchangeRate;
import sii.task.recruitment.repository.ExchangeRateRepository;

import java.math.BigDecimal;

@Component
public class ExchangeRateInitializer {
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateInitializer(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @PostConstruct
    public void initExchangeRates() {
        exchangeRateRepository.save(new ExchangeRate(null, Currency.EUR, Currency.USD, new BigDecimal("1.072350")));
        exchangeRateRepository.save(new ExchangeRate(null, Currency.EUR, Currency.GBP, new BigDecimal("0.857310")));
        exchangeRateRepository.save(new ExchangeRate(null, Currency.GBP, Currency.USD, new BigDecimal("1.250750")));
    }
}
