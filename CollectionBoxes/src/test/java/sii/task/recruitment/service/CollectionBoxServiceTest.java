package sii.task.recruitment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sii.task.recruitment.CurrencyConverter;
import sii.task.recruitment.model.CollectionBox;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.FundraisingEventRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollectionBoxServiceTest {

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;

    @Mock
    private FundraisingEventService fundraisingEventService;

    @Mock
    private CurrencyConverter currencyConverter;

    @InjectMocks
    private CollectionBoxService collectionBoxService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterNewCollectionBox_Success() {
        String identifier = "Box-001";
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier(identifier);

        when(collectionBoxRepository.existsByIdentifier(identifier)).thenReturn(false);
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenAnswer(invocation -> {
            CollectionBox box = invocation.getArgument(0);
            box.setId(1L);
            return box;
        });


        CollectionBox registeredBox = collectionBoxService.registerNewCollectionBox(identifier);

        assertNotNull(registeredBox);
        assertEquals(identifier, registeredBox.getIdentifier());
        verify(collectionBoxRepository).save(registeredBox);


    }

}