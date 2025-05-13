package sii.task.recruitment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.RestExchangeRatesProvider;
import sii.task.recruitment.dto.ExchangeRateResponse;
import sii.task.recruitment.exception.CurrencyConversionException;
//import sii.task.recruitment.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private final RestExchangeRatesProvider restExchangeRatesProvider;

    @Autowired
    public ExchangeRateService(RestExchangeRatesProvider restExchangeRatesProvider) {
        this.restExchangeRatesProvider = restExchangeRatesProvider;
    }

    public BigDecimal convert(String from, String to, BigDecimal amount) {
        if (from.equals(to)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        ExchangeRateResponse response = restExchangeRatesProvider.fetchExchangeRate(from);

        BigDecimal rate = response.rates().get(to);

        if (rate == null) {
            throw new CurrencyConversionException(String.format("Currency %s not found", to));
        }

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
