package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.GameSession;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @Test
    void testEqualsAndHashCode() {
        // Підготовка тестових даних
        Timestamp now = new Timestamp(System.currentTimeMillis());

        GameSession session1 = new GameSession();
        session1.setId(1L);
        session1.setUserId(1L);
        session1.setStartedAt(now);

        GameSession session2 = new GameSession();
        session2.setId(1L);  // Такий самий ID
        session2.setUserId(2L);  // Інший користувач
        session2.setStartedAt(new Timestamp(now.getTime() + 1000));  // Інший час

        GameSession session3 = new GameSession();
        session3.setId(2L);  // Інший ID
        session3.setUserId(1L);  // Такий самий користувач
        session3.setStartedAt(now);  // Такий самий час

        // Перевірка equals
        assertEquals(session1, session2, "Сесії з однаковим ID повинні бути рівні");
        assertNotEquals(session1, session3, "Сесії з різними ID не повинні бути рівні");

        // Перевірка hashCode
        assertEquals(session1.hashCode(), session2.hashCode(),
            "Хеш-коди для сесій з однаковим ID повинні співпадати");
        assertNotEquals(session1.hashCode(), session3.hashCode(),
            "Хеш-коди для сесій з різними ID не повинні співпадати");
    }

    @Test
    void testGettersAndSetters() {
        // Підготовка тестових даних
        GameSession session = new GameSession();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Встановлення значень
        session.setId(1L);
        session.setUserId(2L);
        session.setStartedAt(now);
        session.setEndedAt(now);

        // Перевірка геттерів
        assertEquals(1L, session.getId(), "ID має співпадати");
        assertEquals(2L, session.getUserId(), "ID користувача має співпадати");
        assertEquals(now, session.getStartedAt(), "Час початку має співпадати");
        assertEquals(now, session.getEndedAt(), "Час завершення має співпадати");
    }
}