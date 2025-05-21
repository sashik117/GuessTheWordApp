package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.Hint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HintTest {

    @Test
    void testGettersAndSetters() {
        Hint hint = new Hint();
        hint.setId(1L);
        hint.setWordId(2L);
        hint.setText("Test Hint");

        assertEquals(1L, hint.getId());
        assertEquals(2L, hint.getWordId());
        assertEquals("Test Hint", hint.getText());
    }

    @Test
    void testAllArgsConstructor() {
        Hint hint = new Hint(1L, 2L, "Test Hint");

        assertEquals(1L, hint.getId());
        assertEquals(2L, hint.getWordId());
        assertEquals("Test Hint", hint.getText());
    }

    @Test
    void testEqualsAndHashCode() {
        Hint hint1 = new Hint(1L, 2L, "Hint");
        Hint hint2 = new Hint(1L, 3L, "Different Hint");
        Hint hint3 = new Hint(2L, 2L, "Hint");

        assertEquals(hint1, hint2); // Порівнюємо за ID
        assertNotEquals(hint1, hint3);
        assertEquals(hint1.hashCode(), hint2.hashCode());
    }
}