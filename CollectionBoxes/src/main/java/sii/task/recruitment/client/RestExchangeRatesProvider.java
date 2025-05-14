package sii.task.recruitment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import sii.task.recruitment.dto.ExchangeRateResponse;

@Component
public class RestExchangeRatesProvider {

    private static final String BASE_PATH = "/v6/latest/{base}";

    private final WebClient webClient;

    public RestExchangeRatesProvider(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://open.er-api.com")
                .build();
    }

    public ExchangeRateResponse fetchExchangeRate(String baseCurrency) {
        return webClient.get()
                .uri(BASE_PATH, baseCurrency)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
    }
}
