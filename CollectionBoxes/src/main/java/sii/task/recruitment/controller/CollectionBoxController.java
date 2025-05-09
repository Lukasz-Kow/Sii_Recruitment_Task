package sii.task.recruitment.controller;

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
    public ResponseEntity<CollectionBoxResponse> registerCollectionBox(@RequestBody RegisterCollectionBoxRequest request) {
        CollectionBox collectionBox =collectionBoxService.registerNewCollectionBox(request.identifier());
        return new ResponseEntity<>(toDto(collectionBox), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> getCollectionBoxes() {
        List<CollectionBoxResponse> collectionBoxes = collectionBoxService.getAllCollectionBoxes().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(collectionBoxes);
    }

    @DeleteMapping("/{identifier}")
    public void unregisterCollectionBox(@RequestBody String identifier) {
        collectionBoxService.unregisterCollectionBox(identifier);
    }

    @PutMapping("/{boxId}/assign/{eventId}")
    public ResponseEntity<String> assignToEvent(@PathVariable Long boxId, @PathVariable Long eventId) {
        boolean success = collectionBoxService.assignToFundraisingEvent(boxId, eventId);
        if (success) {
            return new ResponseEntity<>("Successfully assigned to event", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to assign to event", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{boxId}/transfer")
    public ResponseEntity<String> transferToEvent(@PathVariable Long boxId) {
        boolean success = collectionBoxService.transferMoneyToEventsAccount(boxId);
        if (success) {
            return ResponseEntity.ok("Successfully transferred to event");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to transferred to event");
        }
    }

    private CollectionBoxResponse toDto(CollectionBox collectionBox) {
        boolean isAssigned = collectionBox.getFundraisingEvent() != null;
        boolean isEmpty = collectionBox.getFundraisingEvent() == null || collectionBox.getCollectedMoney().isEmpty();
        return new CollectionBoxResponse(collectionBox.getId(), collectionBox.getIdentifier(), isAssigned, isEmpty);
    }

}
