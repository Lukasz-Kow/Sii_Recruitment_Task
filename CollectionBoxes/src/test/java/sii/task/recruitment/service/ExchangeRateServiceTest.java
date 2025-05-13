package sii.task.recruitment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sii.task.recruitment.RestExchangeRatesProvider;
import sii.task.recruitment.dto.ExchangeRateResponse;
import sii.task.recruitment.exception.CurrencyConversionException;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private RestExchangeRatesProvider restExchangeRatesProvider;

    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setup() {
        exchangeRateService = new ExchangeRateService(restExchangeRatesProvider);
    }

    @Test
    void shouldConvertUsingExchangeRatesProvider() {
        when(restExchangeRatesProvider.fetchExchangeRate(any())).thenReturn(ExchangeRateResponse.builder()
                .provider("Tets")
                .result("success")
                .rates(Map.of("EUR", new BigDecimal("1.20"), "USD", new BigDecimal("1.40")))
                .build());
        BigDecimal result = exchangeRateService.convert("USD", "EUR", new BigDecimal("10"));

        verify(restExchangeRatesProvider).fetchExchangeRate("USD");
        verifyNoMoreInteractions(restExchangeRatesProvider);
        assertThat(result).isEqualTo(new BigDecimal("12.00"));
    }

    @Test
    void shouldReturnAmountWhenCurrenciesAreEqual() {
        BigDecimal amount = new BigDecimal("120.00");
        BigDecimal result = exchangeRateService.convert("USD", "USD", amount);
        assertThat(result).isEqualTo(amount);
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        BigDecimal amount = new BigDecimal("120.00");
        Map<String, BigDecimal> rates = Map.of();

        ExchangeRateResponse response = new ExchangeRateResponse("success", "test", rates);
        when(restExchangeRatesProvider.fetchExchangeRate(any())).thenReturn(response);

        Assertions.assertThatThrownBy(() -> exchangeRateService.convert("USD", "EUR", amount))
                .isInstanceOf(CurrencyConversionException.class)
                .hasMessageContaining("Currency EUR not found");

    }
}