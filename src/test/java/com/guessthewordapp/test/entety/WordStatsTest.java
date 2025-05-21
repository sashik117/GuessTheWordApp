package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.WordStats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordStatsTest {

    @Test
    void testGettersAndSetters() {
        WordStats stats = new WordStats();
        stats.setId(1L);
        stats.setWordId(2L);
        stats.setCorrectCount(5);
        stats.setTotalCount(10);

        assertEquals(1L, stats.getId());
        assertEquals(2L, stats.getWordId());
        assertEquals(5, stats.getCorrectCount());
        assertEquals(10, stats.getTotalCount());
    }

    @Test
    void testAllArgsConstructor() {
        WordStats stats = new WordStats(1L, 2L, 5, 10);

        assertEquals(1L, stats.getId());
        assertEquals(2L, stats.getWordId());
        assertEquals(5, stats.getCorrectCount());
    }

    @Test
    void testEqualsAndHashCode() {
        WordStats stats1 = new WordStats(1L, 2L, 5, 10);
        WordStats stats2 = new WordStats(1L, 3L, 1, 2);
        WordStats stats3 = new WordStats(2L, 2L, 5, 10);

        assertEquals(stats1, stats2); // Порівнюємо за ID
        assertNotEquals(stats1, stats3);
        assertEquals(stats1.hashCode(), stats2.hashCode());
    }
}