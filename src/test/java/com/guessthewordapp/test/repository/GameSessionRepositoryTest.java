package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.GameSession;
import com.guessthewordapp.infrastructure.persistence.contract.GameSessionRepository;
import com.guessthewordapp.infrastructure.persistence.impl.GameSessionRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool.PoolConfig;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameSessionRepositoryTest {

    private GameSessionRepository repository;
    private ConnectionPool connectionPool;
    private Connection sharedConnection; // Додали shared connection

    @BeforeAll
    void setupDatabase() throws Exception {
        // Використовуємо in-memory базу даних з shared cache
        connectionPool = new ConnectionPool(new PoolConfig.Builder()
            .withUrl("jdbc:sqlite:file:testdb?mode=memory&cache=shared")  // Shared in-memory база
            .withMaxConnections(5)
            .build());

        // Створюємо shared connection, який буде тримати базу "живою"
        sharedConnection = connectionPool.getConnection();
        createSchema();

        repository = new GameSessionRepositoryImpl(connectionPool);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Перед кожним тестом очищаємо дані
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM GameSession");
        }
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = sharedConnection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS GameSession (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    started_at TIMESTAMP NOT NULL,
                    ended_at TIMESTAMP
                )
            """);
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        // Закриваємо shared connection та пул після всіх тестів
        if (sharedConnection != null && !sharedConnection.isClosed()) {
            sharedConnection.close();
        }
        connectionPool.shutdown();
    }

    // Решта тестових методів залишаються без змін
    @Test
    void testSaveGameSession() {
        GameSession session = new GameSession();
        session.setUserId(1L);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));

        GameSession savedSession = repository.save(session);

        assertNotNull(savedSession.getId());
        assertEquals(1L, savedSession.getUserId());
    }
    @Test
    void testFindById() {
        // Підготовка тестових даних
        GameSession session = new GameSession();
        session.setUserId(2L);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));
        GameSession savedSession = repository.save(session);

        // Виконання методу, який тестуємо
        Optional<GameSession> foundSession = repository.findById(savedSession.getId());

        // Перевірка результатів
        assertTrue(foundSession.isPresent(), "Сесія повинна бути знайдена");
        assertEquals(savedSession.getId(), foundSession.get().getId(), "ID знайденої сесії має співпадати");
    }

    @Test
    void testFindByUserId() {
        // Підготовка тестових даних
        GameSession session1 = new GameSession();
        session1.setUserId(3L);
        session1.setStartedAt(new Timestamp(System.currentTimeMillis()));
        repository.save(session1);

        GameSession session2 = new GameSession();
        session2.setUserId(3L);
        session2.setStartedAt(new Timestamp(System.currentTimeMillis()));
        repository.save(session2);

        // Виконання методу, який тестуємо
        List<GameSession> sessions = repository.findByUserId(3L);

        // Перевірка результатів
        assertEquals(2, sessions.size(), "Повинно бути знайдено 2 сесії для користувача");
        assertTrue(sessions.stream().allMatch(s -> s.getUserId().equals(3L)),
            "Усі знайдені сесії повинні належати користувачу з ID 3");
    }

    @Test
    void testUpdateGameSession() {
        // Підготовка тестових даних
        GameSession session = new GameSession();
        session.setUserId(4L);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));
        GameSession savedSession = repository.save(session);

        // Оновлення даних
        savedSession.setEndedAt(new Timestamp(System.currentTimeMillis()));
        GameSession updatedSession = repository.update(savedSession.getId(), savedSession);

        // Перевірка результатів
        Optional<GameSession> foundSession = repository.findById(savedSession.getId());
        assertTrue(foundSession.isPresent(), "Сесія повинна існувати після оновлення");
        assertNotNull(foundSession.get().getEndedAt(), "Дата завершення не повинна бути null після оновлення");
    }

    @Test
    void testDeleteGameSession() {
        // Підготовка тестових даних
        GameSession session = new GameSession();
        session.setUserId(5L);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));
        GameSession savedSession = repository.save(session);

        // Виконання методу, який тестуємо
        repository.delete(savedSession.getId());

        // Перевірка результатів
        Optional<GameSession> foundSession = repository.findById(savedSession.getId());
        assertFalse(foundSession.isPresent(), "Сесія не повинна існувати після видалення");
    }
}
