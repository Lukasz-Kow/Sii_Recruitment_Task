package sii.task.recruitment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sii.task.recruitment.dto.CollectionBoxResponse;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.CollectionBox;
import sii.task.recruitment.service.CollectionBoxService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CollectionBoxController.class)
class CollectionBoxControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CollectionBoxService collectionBoxService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldRegisterCollectionBox() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(1L);

        CollectionBoxResponse response = new CollectionBoxResponse(1L, "Box-001", false, true);

        when(collectionBoxService.registerNewCollectionBox("Box-001")).thenReturn(collectionBox);
        when(collectionBoxService.toDto(collectionBox)).thenReturn(response);

        mvc.perform(post("/api/collection-boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"Box-001\",\"id\":\"1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value("Box-001"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.isAssigned").value(false))
                .andExpect(jsonPath("$.isEmpty").value(true));

    }

    @Test
    void shouldGetAllCollectionBoxes() throws Exception {
        List<CollectionBoxResponse> boxes = List.of(
                new CollectionBoxResponse(1L, "Box-001", false, true),
                new CollectionBoxResponse(2L, "Box-002", true, true));

        CollectionBox collectionBox1 = new CollectionBox();
        collectionBox1.setIdentifier("Box-001");
        collectionBox1.setId(1L);

        CollectionBox collectionBox2 = new CollectionBox();
        collectionBox2.setIdentifier("Box-002");
        collectionBox2.setId(2L);

        when(collectionBoxService.getAllCollectionBoxes()).thenReturn(List.of(collectionBox1, collectionBox2));
        when(collectionBoxService.toDto(any())).thenReturn(boxes.get(0), boxes.get(1));

        mvc.perform(get("/api/collection-boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identifier").value("Box-001"))
                .andExpect(jsonPath("$[1].identifier").value("Box-002"));
    }

    @Test
    void shouldUnregisterCollectionBox() throws Exception {
        doNothing().when(collectionBoxService).unregisterCollectionBox("Box-001");

        mvc.perform(delete("/api/collection-boxes/Box-01")).andExpect(status().isOk());
    }

    @Test
    void shouldAssignCollectionBoxToEvent() throws Exception {
        doNothing().when(collectionBoxService).assignToFundraisingEvent(1L, 100L);

        mvc.perform(put("/api/collection-boxes/1/assign/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully assigned to event"));
    }

    @Test
    void shouldTransferMoneyFromBoxToFundraisingEvent() throws Exception {
        doNothing().when(collectionBoxService).transferMoneyToEventsAccount(1L);

        mvc.perform(post("/api/collection-boxes/1/transfer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully transferred to event"));
    }

    @Test
    void shouldReturnBadRequestWhenCollectionBoxExists() throws Exception {
        doThrow(new CollectionBoxExistsException("Identifier already exists")).when(collectionBoxService)
                .registerNewCollectionBox("Box-001");

        mvc.perform(post("/api/collection-boxes").contentType(MediaType.APPLICATION_JSON).content("{\"identifier\":\"Box-001\"}")
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Identifier already exists"));
    }

    @Test
    void shouldReturnBedRequestWhenAssigningNonEmptyCollectionBoxToEvent() throws Exception {
        doThrow(new CollectionBoxIsNotEmptyException("You can only assign empty collection boxes to a fundraising event.")).when(collectionBoxService)
                .assignToFundraisingEvent(1L, 100L);

        mvc.perform(put("/api/collection-boxes/1/assign/100")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You can only assign empty collection boxes to a fundraising event."));
    }

    @Test
    void shouldReturnBadRequestWhenCurrencyConversionFails() throws Exception {
        doThrow(new CurrencyConversionException("Amount and currency required")).when(collectionBoxService)
                .transferMoneyToEventsAccount(1L);

        mvc.perform(post("/api/collection-boxes/1/transfer")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount and currency required"));
    }

    @Test
    void shouldReturnBadRequestWhenIdentifierAlreadyExists() throws Exception {
        doThrow(new DataIntegrityViolationException("UNIQUE constraint violated")).when(collectionBoxService)
                .registerNewCollectionBox("Box-001");
        mvc.perform(post("/api/collection-boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\": \"Box-001\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Identifier already exists. Please choose a different identifier."));
    }

    @Test
    void shouldReturnBedRequestWhenTransferFailsDueToEmptyBox() throws Exception {
        doThrow(new CollectionBoxEmptyException("Collection box with ID: 1, does not contain any money.")).when(collectionBoxService)
                .transferMoneyToEventsAccount(1L);
        mvc.perform(post("/api/collection-boxes/1/transfer")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Transfer Error"))
                .andExpect(jsonPath("$.message").value("Collection box with ID: 1, does not contain any money."));
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {
        when(collectionBoxService.registerNewCollectionBox("Box-001")).thenThrow(new DataIntegrityViolationException("DB error", new RuntimeException("Foreign key error")));

        mvc.perform(post("/api/collection-boxes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identifier\": \"Box-001\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value("500"))
                .andExpect(jsonPath("$.message").value("Internal error occurred. Please try again later."));


    }

}