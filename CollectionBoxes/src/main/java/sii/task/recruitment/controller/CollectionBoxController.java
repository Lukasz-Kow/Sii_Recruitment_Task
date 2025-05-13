package sii.task.recruitment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.dto.CollectionBoxResponse;
import sii.task.recruitment.dto.RegisterCollectionBoxRequest;
import sii.task.recruitment.model.CollectionBox;
import sii.task.recruitment.service.CollectionBoxService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collection-boxes")
public class CollectionBoxController {

    private final CollectionBoxService collectionBoxService;

    @Autowired
    public CollectionBoxController(CollectionBoxService collectionBoxService) {
        this.collectionBoxService = collectionBoxService;
    }

    @PostMapping
    public ResponseEntity<CollectionBoxResponse> registerCollectionBox(@Valid @RequestBody RegisterCollectionBoxRequest request) {
        CollectionBox collectionBox = collectionBoxService.registerNewCollectionBox(request.identifier());
        return new ResponseEntity<>(collectionBoxService.toDto(collectionBox), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> getCollectionBoxes() {
        List<CollectionBoxResponse> collectionBoxes = collectionBoxService.getAllCollectionBoxes().stream().map(collectionBoxService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(collectionBoxes);
    }

    @DeleteMapping("/{identifier}")
    public void unregisterCollectionBox(@PathVariable String identifier) {
        collectionBoxService.unregisterCollectionBox(identifier);
    }

    @PutMapping("/{boxId}/assign/{eventId}")
    public ResponseEntity<String> assignToEvent(@PathVariable Long boxId, @PathVariable Long eventId) {
        collectionBoxService.assignToFundraisingEvent(boxId, eventId);
        return new ResponseEntity<>("Successfully assigned to event", HttpStatus.OK);
    }

    @PostMapping("/{boxId}/transfer")
    public ResponseEntity<String> transferToEvent(@PathVariable Long boxId) {
        collectionBoxService.transferMoneyToEventsAccount(boxId);
        return ResponseEntity.ok("Successfully transferred to event");
    }


}
