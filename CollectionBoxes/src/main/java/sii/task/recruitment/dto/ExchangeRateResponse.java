package sii.task.recruitment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record ExchangeRateResponse(String result, String provider, @JsonProperty("rates") Map<String, BigDecimal> rates) {
}
