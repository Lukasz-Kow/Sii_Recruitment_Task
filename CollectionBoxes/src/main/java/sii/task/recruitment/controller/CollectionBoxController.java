package sii.task.recruitment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.CollectionBoxesApplication;
import sii.task.recruitment.model.CollectionBox;
import sii.task.recruitment.service.CollectionBoxService;

import java.util.List;

@RestController
@RequestMapping("/api/collection-boxes")
public class CollectionBoxController {

    private final CollectionBoxService collectionBoxService;

    @Autowired
    public CollectionBoxController(CollectionBoxService collectionBoxService) {
        this.collectionBoxService = collectionBoxService;
    }

    @PostMapping
    public ResponseEntity<CollectionBox> registerCollectionBox(@RequestBody String identifier) {
        return new ResponseEntity<>(collectionBoxService.registerNewCollectionBox(identifier), HttpStatus.CREATED);
    }

    @GetMapping
    public List<CollectionBox> getCollectionBoxes() {
        return collectionBoxService.getAllCollectionBoxes();
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

}
