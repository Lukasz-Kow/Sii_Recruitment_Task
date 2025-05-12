package sii.task.recruitment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.dto.*;
import sii.task.recruitment.model.FundraisingEvent;
import sii.task.recruitment.service.FundraisingEventService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fundraising-events")
public class FundraisingEventController {
    private final FundraisingEventService fundraisingEventService;

    @Autowired
    public FundraisingEventController(FundraisingEventService fundraisingEventService) {
        this.fundraisingEventService = fundraisingEventService;
    }

    @PostMapping
    public ResponseEntity<FundraisingEventResponse> createFundraisingEvent(@Valid @RequestBody CreateFundraisingEventRequest request) {
        FundraisingEvent fundraisingEvent = fundraisingEventService.createFundraisingEvent(request.eventName(), request.currency());
        return new ResponseEntity<>(fundraisingEventService.toDto(fundraisingEvent), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FundraisingEventResponse>> getAllFundraisingEvents() {
        List<FundraisingEventResponse> responses = fundraisingEventService.getAllFundraisingEvents().stream()
                .map(fundraisingEventService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<FundraisingEventResponse> getEventByName(@Valid @RequestBody SearchFundraisingEventByNameRequest request) {
        return fundraisingEventService.getFundraisingEventByName(request.eventName())
                .map(event -> ResponseEntity.ok(fundraisingEventService.toDto(event))).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
