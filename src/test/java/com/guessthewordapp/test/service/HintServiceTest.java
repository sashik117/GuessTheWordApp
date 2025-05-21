package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.HintService;
import com.guessthewordapp.application.contract.dto.HintDTO;
import com.guessthewordapp.application.impl.HintServiceImpl;
import com.guessthewordapp.domain.enteties.Hint;
import com.guessthewordapp.infrastructure.persistence.contract.HintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HintServiceTest {

    @Mock
    private HintRepository hintRepository;

    @InjectMocks
    private HintServiceImpl hintService;

    @Test
    void testSaveHint() {
        HintDTO dto = new HintDTO(null, 1L, "Test Hint");
        Hint savedEntity = new Hint(1L, 1L, "Test Hint");

        when(hintRepository.save(any(Hint.class))).thenReturn(savedEntity);

        HintDTO result = hintService.saveHint(dto);

        assertNotNull(result.id());
        assertEquals(1L, result.wordId());
        assertEquals("Test Hint", result.text());

        verify(hintRepository, times(1)).save(any(Hint.class));
    }

    @Test
    void testGetHintsForWord() {
        Hint hint1 = new Hint(1L, 10L, "Hint 1");
        Hint hint2 = new Hint(2L, 10L, "Hint 2");
        List<Hint> hints = List.of(hint1, hint2);

        when(hintRepository.findByWordId(10L)).thenReturn(hints);

        List<HintDTO> results = hintService.getHintsForWord(10L);

        assertEquals(2, results.size());
        assertEquals("Hint 1", results.get(0).text());
        assertEquals("Hint 2", results.get(1).text());

        verify(hintRepository, times(1)).findByWordId(10L);
    }
}