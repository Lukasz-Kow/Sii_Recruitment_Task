package sii.task.recruitment.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sii.task.recruitment.CurrencyConverter;
import sii.task.recruitment.exception.CurrencyConversionException;
import sii.task.recruitment.exception.ExchangeRateNotFoundException;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.ExchangeRate;
import sii.task.recruitment.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class CurrencyConverterTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private CurrencyConverter currencyConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldConvertCurrentWithDirectRate() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setSourceCurrency(Currency.USD);
        exchangeRate.setTargetCurrency(Currency.EUR);
        exchangeRate.setRate(new BigDecimal("0.86"));

        when(exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.USD, Currency.EUR)).thenReturn(Optional.of(exchangeRate));

        BigDecimal result = currencyConverter.convertCurrency(new BigDecimal("100.00"), Currency.USD, Currency.EUR);
        Assertions.assertThat(result).isEqualTo(new BigDecimal("86.000000"));
    }

    @Test
    void shouldConvertCurrentWithReverseRate() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setSourceCurrency(Currency.USD);
        exchangeRate.setTargetCurrency(Currency.GBP);
        exchangeRate.setRate(new BigDecimal("1.072350"));

        when(exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.GBP, Currency.USD)).thenReturn(Optional.empty());
        when(exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.USD, Currency.GBP)).thenReturn(Optional.of(exchangeRate));

        BigDecimal result = currencyConverter.convertCurrency(new BigDecimal("100.00"), Currency.GBP, Currency.USD);
        Assertions.assertThat(result).isEqualTo(new BigDecimal("93.253100"));
    }

    @Test
    void shouldReturnSameAmountWhenCurrenciesMatch() {
        BigDecimal result = currencyConverter.convertCurrency(new BigDecimal("100.00"), Currency.GBP, Currency.GBP);
        Assertions.assertThat(result).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(new BigDecimal("-100.00"), Currency.GBP, Currency.USD);
        }).isInstanceOf(CurrencyConversionException.class).hasMessageContaining("Amount must be greater than zero.");

        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(BigDecimal.ZERO, Currency.GBP, Currency.USD);
        }).isInstanceOf(CurrencyConversionException.class).hasMessageContaining("Amount must be greater than zero.");


    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(null, Currency.GBP, Currency.USD);
        }).isInstanceOf(CurrencyConversionException.class).hasMessageContaining("Amount and currency are required");

        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(new BigDecimal("100.00"), null, Currency.USD);
        }).isInstanceOf(CurrencyConversionException.class).hasMessageContaining("Amount and currency are required");

        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(new BigDecimal("100.00"), Currency.USD, null);
        }).isInstanceOf(CurrencyConversionException.class).hasMessageContaining("Amount and currency are required");
    }

    @Test
    void shouldThrowExceptionWhenNoExchangeRate() {
        when(exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.GBP, Currency.USD)).thenReturn(Optional.empty());
        when(exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(Currency.USD, Currency.GBP)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            currencyConverter.convertCurrency(new BigDecimal("100.00"), Currency.GBP, Currency.USD);
        }).isInstanceOf(ExchangeRateNotFoundException.class).hasMessageContaining("No exchange rate found between GBP and USD.");
    }


}
