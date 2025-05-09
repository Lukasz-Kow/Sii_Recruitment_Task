package sii.task.recruitment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class CurrencyConverter {

    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public CurrencyConverter(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }


    public BigDecimal convertCurrency(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {

        if (amount == null || sourceCurrency == null || targetCurrency == null) {
            throw new CurrencyConversionException("Amount and currency are required.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CurrencyConversionException("Amount must be greater than zero.");
        }
        if (sourceCurrency == targetCurrency) {
            return amount;
        }

        Optional<ExchangeRate> rate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency);
        if (rate.isPresent()) {
            return amount.multiply(rate.get().getRate()).setScale(6, RoundingMode.HALF_UP);
        }

        Optional<ExchangeRate> reverseRate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(targetCurrency, sourceCurrency);

        if (reverseRate.isPresent()) {
            BigDecimal reversedRate = BigDecimal.ONE.divide(reverseRate.get().getRate(), 6, RoundingMode.HALF_UP);
            return amount.multiply(reversedRate).setScale(6, RoundingMode.HALF_UP);
        }
        throw new ExchangeRateNotFoundException("No exchange rate found between " + sourceCurrency + " and " + targetCurrency + ".");

    }
}

