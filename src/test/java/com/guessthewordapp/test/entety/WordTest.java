package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.Word;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordTest {

    @Test
    void testGettersAndSetters() {
        Word word = new Word();
        word.setId(1); // <--- ID тепер Integer
        word.setText("яблуко");
        word.setDifficulty(1);
        word.setLanguage("uk");
        word.setDescription("Фрукт");

        assertEquals(1, word.getId()); // <--- Порівнюємо Integer з Integer (або int)
        assertEquals("яблуко", word.getText());
        assertEquals(1, word.getDifficulty());
        assertEquals("uk", word.getLanguage());
        assertEquals("Фрукт", word.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        // Тепер використовуємо ручний AllArgsConstructor, який ми додали в Word.java
        Word word = new Word(1, "яблуко", 1, "uk", "Фрукт"); // <--- ID тепер Integer

        assertEquals(1, word.getId()); // <--- Порівнюємо Integer з Integer (або int)
        assertEquals("яблуко", word.getText());
        assertEquals(1, word.getDifficulty());
        assertEquals("uk", word.getLanguage());
        assertEquals("Фрукт", word.getDescription());
    }

    @Test
    void testThreeArgConstructor() {
        // Використовуємо ваш ручний конструктор з 3-ма аргументами
        Word word = new Word("яблуко", 1, "uk");

        assertNull(word.getId()); // ID має бути null, оскільки він не встановлюється цим конструктором
        assertEquals("яблуко", word.getText());
        assertEquals(1, word.getDifficulty());
        assertEquals("uk", word.getLanguage());
        assertNull(word.getDescription()); // Description має бути null
    }
}