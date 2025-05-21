package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.Guess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuessTest {

    @Test
    void testGettersAndSetters() {
        Guess guess = new Guess();
        guess.setId(1L);
        guess.setSessionId(2L);
        guess.setWordId(3L);
        guess.setGuessedText("test");
        guess.setCorrect(true);

        assertEquals(1L, guess.getId());
        assertEquals(2L, guess.getSessionId());
        assertEquals("test", guess.getGuessedText());
        assertTrue(guess.isCorrect());
    }

    @Test
    void testAllArgsConstructor() {
        Guess guess = new Guess(1L, 2L, 3L, "test", true);

        assertEquals(1L, guess.getId());
        assertEquals(2L, guess.getSessionId());
        assertEquals(3L, guess.getWordId());
        assertEquals("test", guess.getGuessedText());
        assertTrue(guess.isCorrect());
    }
}