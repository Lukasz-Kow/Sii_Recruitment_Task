package sii.task.recruitment.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sii.task.recruitment.dto.FinancialReportDto;
import sii.task.recruitment.dto.FundraisingEventResponse;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.FundraisingEvent;
import sii.task.recruitment.service.FundraisingEventService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(FundraisingEventController.class)
class FundraisingEventControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FundraisingEventService fundraisingEventService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCreateFundraisingEvent() throws Exception {
        FundraisingEvent fundraisingEvent = FundraisingEvent.builder()
                .eventName("Event_1")
                .eventCurrency(Currency.EUR)
                .accountBalance(BigDecimal.ZERO)
                .build();

        when(fundraisingEventService.createFundraisingEvent("Event_1", Currency.EUR)).thenReturn(fundraisingEvent);
        when(fundraisingEventService.toDto(fundraisingEvent)).thenReturn(new FundraisingEventResponse("Event_1", Currency.EUR, BigDecimal.ZERO));


        mvc.perform(post("/api/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventName\":\"Event_1\",\"currency\":\"EUR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("Event_1"))
                .andExpect(jsonPath("$.eventCurrency").value("EUR"));
    }

    @Test
    void shouldGetAllFundraisingEvents() throws Exception {
        FundraisingEvent fundraisingEvent = FundraisingEvent.builder()
                .eventName("Event_1")
                .eventCurrency(Currency.USD)
                .accountBalance(new BigDecimal("100.00"))
                .build();

        when(fundraisingEventService.getAllFundraisingEvents()).thenReturn(List.of(fundraisingEvent));
        when(fundraisingEventService.toDto(fundraisingEvent)).thenReturn(new FundraisingEventResponse("Event_1", Currency.USD, new BigDecimal("100.00")));

        mvc.perform(get("/api/fundraising-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName").value("Event_1"))
                .andExpect(jsonPath("$[0].eventCurrency").value("USD"));
    }

    @Test
    void shouldGetFundraisingEventByName() throws Exception {
        FundraisingEvent fundraisingEvent = FundraisingEvent.builder()
                .eventName("Event_1")
                .eventCurrency(Currency.GBP)
                .accountBalance(new BigDecimal("200.00"))
                .build();

        when(fundraisingEventService.getFundraisingEventByName("Event_1")).thenReturn(Optional.of(fundraisingEvent));
        when(fundraisingEventService.toDto(fundraisingEvent)).thenReturn(new FundraisingEventResponse("Event_1", Currency.GBP, new BigDecimal("200.00")));

        mvc.perform(get("/api/fundraising-events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventName\":\"Event_1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Event_1"))
                .andExpect(jsonPath("$.eventCurrency").value("GBP"));
    }

    @Test
    void shouldReturnFinancialReport() throws Exception {
        List<FinancialReportDto> report = List.of(
                new FinancialReportDto("Event_1", new BigDecimal("100.00"), Currency.USD),
                new FinancialReportDto("Event_2", new BigDecimal("200.00"), Currency.GBP)
        );

        when(fundraisingEventService.generateFinancialReport()).thenReturn(report);
        mvc.perform(get("/api/fundraising-events/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName").value("Event_1"))
                .andExpect(jsonPath("$[0].eventCurrency").value("USD"))
                .andExpect(jsonPath("$[0].accountBalance").value(100.00))
                .andExpect(jsonPath("$[1].eventName").value("Event_2"))
                .andExpect(jsonPath("$[1].eventCurrency").value("GBP"))
                .andExpect(jsonPath("$[1].accountBalance").value(200.00));

        verify(fundraisingEventService).generateFinancialReport();

    }

    @Test
    void shouldThrowExceptionWhenGetFundraisingEventByName() throws Exception {
        when(fundraisingEventService.getFundraisingEventByName("Event_1")).thenReturn(Optional.empty());

        mvc.perform(get("/api/fundraising-events/search").contentType(MediaType.APPLICATION_JSON).content("{\"eventName\":\"Event_1\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBedRequestWhenEventNameIsMissing() throws Exception {
        mvc.perform(post("/api/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\":\"EUR\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors.eventName").exists());
    }


}