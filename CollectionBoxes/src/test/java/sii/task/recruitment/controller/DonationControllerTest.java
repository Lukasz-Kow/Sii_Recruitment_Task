package sii.task.recruitment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sii.task.recruitment.dto.AddDonation;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.service.DonationService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DonationService donationService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldAddDonation() throws Exception {
        AddDonation request = new AddDonation(1L, new BigDecimal("50.00"), Currency.USD);

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setId(1L);
        collectionBox.setIdentifier("Box-001");


        Donation donation = Donation.builder()
                .id(100L)
                .amount(request.amount())
                .currency(request.currency())
                .collectionBox(collectionBox)
                .build();

        when(donationService.addMoneyToTheCollectionBox(1L, request.amount(), request.currency())).thenReturn(donation);

        mvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collectionBoxId\":\"1\",\"amount\":\"50.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void shouldThrowNotFoundWhenCollectionBoxNotFound() throws Exception {
        when(donationService.addMoneyToTheCollectionBox(1L, new BigDecimal("50.00"), Currency.USD)).thenThrow(new CollectionBoxNotFoundException("Collection box with ID: 1, not found."));

        mvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collectionBoxId\":\"1\",\"amount\":\"50.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Collection box with ID: 1, not found."));
    }

    @Test
    void shouldThrowBadRequestWhenConversionFails() throws Exception {
        when(donationService.addMoneyToTheCollectionBox(anyLong(), any(), any())).thenThrow(new CurrencyConversionException("Amount and currency are required."));

        mvc.perform(post("/api/donations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collectionBoxId\":\"1\",\"amount\":\"50.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount and currency are required."));

    }

    @Test
    void shouldReturnBadRequestWhenAmountIllegal() throws Exception {
        when(donationService.addMoneyToTheCollectionBox(1L, new BigDecimal("0.00"), Currency.USD)).thenThrow(new IllegalAmountException("Amount must be greater than 0."));

        mvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collectionBoxId\":\"1\",\"amount\":\"0.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than 0."));
    }

    @Test
    void shouldReturnBadRequestWhenCurrencyEnumInvalid() throws Exception {
        mvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collectionBoxId\":\"1\",\"amount\":\"50.00\", \"currency\":\"INVALID\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors.currency").value("Invalid value for 'currency': 'INVALID'. Expected type: Currency"));

    }
}