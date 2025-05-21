package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.GuessService;
import com.guessthewordapp.application.contract.dto.GuessDTO;
import com.guessthewordapp.application.impl.GuessServiceImpl;
import com.guessthewordapp.domain.enteties.Guess;
import com.guessthewordapp.infrastructure.persistence.contract.GuessRepository;
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
class GuessServiceTest {

    @Mock
    private GuessRepository guessRepository;

    @InjectMocks
    private GuessServiceImpl guessService;

    @Test
    void testSaveGuess() {
        // Підготовка тестових даних
        GuessDTO dto = new GuessDTO(null, 1L, 2L, "test", true);
        Guess savedEntity = new Guess(1L, 1L, 2L, "test", true);

        // Налаштування моків
        when(guessRepository.save(any(Guess.class))).thenReturn(savedEntity);

        // Виконання методу
        GuessDTO result = guessService.saveGuess(dto);

        // Перевірка результатів
        assertNotNull(result.id());
        assertEquals(1L, result.sessionId());
        assertEquals("test", result.guessedText());

        // Перевірка викликів
        verify(guessRepository, times(1)).save(any(Guess.class));
    }

    @Test
    void testGetGuessesForSession() {
        // Підготовка тестових даних
        Guess guess1 = new Guess(1L, 10L, 1L, "guess1", false);
        Guess guess2 = new Guess(2L, 10L, 2L, "guess2", true);
        List<Guess> guesses = List.of(guess1, guess2);

        // Налаштування моків
        when(guessRepository.findBySessionId(10L)).thenReturn(guesses);

        // Виконання методу
        List<GuessDTO> results = guessService.getGuessesForSession(10L);

        // Перевірка результатів
        assertEquals(2, results.size());
        assertEquals("guess1", results.get(0).guessedText());
        assertTrue(results.get(1).correct());

        // Перевірка викликів
        verify(guessRepository, times(1)).findBySessionId(10L);
    }
}