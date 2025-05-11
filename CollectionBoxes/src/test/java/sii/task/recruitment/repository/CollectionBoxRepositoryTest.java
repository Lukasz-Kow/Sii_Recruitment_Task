package sii.task.recruitment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import sii.task.recruitment.model.CollectionBox;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CollectionBoxRepositoryTest {

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    @Test
    public void testSaveCorrectCollectionBox() {
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("BOX-001");

        CollectionBox savedCollectionBox = collectionBoxRepository.save(collectionBox);

        assertNotNull(savedCollectionBox.getId());
        assertNotNull(savedCollectionBox.getIdentifier());
        assertEquals("BOX-001", savedCollectionBox.getIdentifier());
    }

    @Test
    public void testUniqueIdentifier() {
        CollectionBox box1 = new CollectionBox();
        box1.setIdentifier("BOX-001");

        collectionBoxRepository.saveAndFlush(box1);

        CollectionBox box2 = new CollectionBox();
        box2.setIdentifier("BOX-001");

        assertThrows(DataIntegrityViolationException.class, () -> collectionBoxRepository.saveAndFlush(box2));

    }

    @Test
    public void testExistsByIdentifier() {
        CollectionBox box1 = new CollectionBox();
        box1.setIdentifier("BOX-001");
        collectionBoxRepository.save(box1);
        assertTrue(collectionBoxRepository.existsByIdentifier("BOX-001"));
    }

    @Test
    public void testFindByIdentifier() {
        CollectionBox box = new CollectionBox();
        box.setIdentifier("BOX-001");
        collectionBoxRepository.save(box);

        Optional<CollectionBox> foundBox = collectionBoxRepository.findByIdentifier(box.getIdentifier());

        assertTrue(foundBox.isPresent());
        assertEquals(box.getIdentifier(), foundBox.get().getIdentifier());
    }

    @Test
    public void testDeleteCollectionBox() {
        CollectionBox box = new CollectionBox();
        box.setIdentifier("BOX-001");
        collectionBoxRepository.save(box);

        assertTrue(collectionBoxRepository.existsByIdentifier("BOX-001"));

        collectionBoxRepository.delete(box);
        assertFalse(collectionBoxRepository.existsByIdentifier("BOX-001"));
    }
}