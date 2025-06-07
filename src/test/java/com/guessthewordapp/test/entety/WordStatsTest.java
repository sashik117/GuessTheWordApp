package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.WordStats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordStatsTest {

    @Test
    void testGettersAndSetters() {
        WordStats wordStats = new WordStats(); // Використовує @NoArgsConstructor
        wordStats.setId(1L);
        wordStats.setWordId(10L);
        wordStats.setUserId(20L); // Нове поле
        wordStats.setCorrectCount(5);
        wordStats.setTotalCount(10);

        assertEquals(1L, wordStats.getId());
        assertEquals(10L, wordStats.getWordId());
        assertEquals(20L, wordStats.getUserId()); // Перевірка нового поля
        assertEquals(5, wordStats.getCorrectCount());
        assertEquals(10, wordStats.getTotalCount());
    }

    @Test
    void testAllArgsConstructor() {
        // Конструктор тепер вимагає 5 аргументів:
        // id (Long), wordId (Long), userId (Long), correctCount (Integer), totalCount (Integer)
        WordStats wordStats = new WordStats(1L, 10L, 20L, 5, 10);

        assertEquals(1L, wordStats.getId());
        assertEquals(10L, wordStats.getWordId());
        assertEquals(20L, wordStats.getUserId());
        assertEquals(5, wordStats.getCorrectCount());
        assertEquals(10, wordStats.getTotalCount());
    }

    @Test
    void testEquality() {
        // Конструктор тепер вимагає 5 аргументів
        WordStats stats1 = new WordStats(1L, 10L, 20L, 5, 10);
        WordStats stats2 = new WordStats(1L, 10L, 20L, 5, 10);
        WordStats stats3 = new WordStats(2L, 11L, 21L, 6, 12); // Змінені дані

        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
    }
}