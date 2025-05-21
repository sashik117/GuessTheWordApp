package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.Word;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordTest {

    @Test
    void testGettersAndSetters() {
        Word word = new Word();
        word.setId(1);
        word.setText("яблуко");
        word.setDifficulty(1);
        word.setLanguage("uk");
        word.setDescription("Фрукт");

        assertEquals(1, word.getId());
        assertEquals("яблуко", word.getText());
        assertEquals(1, word.getDifficulty());
        assertEquals("uk", word.getLanguage());
        assertEquals("Фрукт", word.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        Word word = new Word(1, "яблуко", 1, "uk", "Фрукт");

        assertEquals(1, word.getId());
        assertEquals("яблуко", word.getText());
        assertEquals("uk", word.getLanguage());
    }

    @Test
    void testThreeArgConstructor() {
        Word word = new Word("яблуко", 1, "uk");

        assertEquals("яблуко", word.getText());
        assertEquals(1, word.getDifficulty());
        assertEquals("uk", word.getLanguage());
        assertNull(word.getDescription());
    }
}