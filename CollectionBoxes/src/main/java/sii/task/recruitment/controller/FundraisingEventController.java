package sii.task.recruitment.controller;

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
    public ResponseEntity<FundraisingEventResponse> createFundraisingEvent(@RequestBody CreateFundraisingEventRequest request) {
        FundraisingEvent fundraisingEvent = fundraisingEventService.createFundraisingEvent(request.eventName(), request.currency());
        return new ResponseEntity<>(toDto(fundraisingEvent), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FundraisingEventResponse>> getAllFundraisingEvents() {
        List<FundraisingEventResponse> responses = fundraisingEventService.getAllFundraisingEvents().stream().map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<FundraisingEventResponse> getEventByName(@RequestBody SearchFundraisingEventByNameRequest request) {
        return fundraisingEventService.getFundraisingEventByName(request.eventName()).map(event -> ResponseEntity.ok(toDto(event))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private FundraisingEventResponse toDto(FundraisingEvent fundraisingEvent) {
        return new FundraisingEventResponse(fundraisingEvent.getEventName(), fundraisingEvent.getEventCurrency(), fundraisingEvent.getAccountBalance());
    }

}
