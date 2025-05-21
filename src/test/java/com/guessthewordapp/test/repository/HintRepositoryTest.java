package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.Hint;
import com.guessthewordapp.infrastructure.persistence.contract.HintRepository;
import com.guessthewordapp.infrastructure.persistence.impl.HintRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool.PoolConfig;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HintRepositoryTest {

    private HintRepository repository;
    private ConnectionPool connectionPool;
    private Connection sharedConnection;

    @BeforeAll
    void setupDatabase() throws Exception {
        // Використовуємо shared in-memory базу
        connectionPool = new ConnectionPool(new PoolConfig.Builder()
            .withUrl("jdbc:sqlite:file:testdb?mode=memory&cache=shared")
            .withMaxConnections(5)
            .build());

        // Створюємо shared connection
        sharedConnection = connectionPool.getConnection();
        createSchema();

        repository = new HintRepositoryImpl(connectionPool);
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = sharedConnection.createStatement()) {
            // Включаємо підтримку foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");

            // Створюємо таблицю Hint
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Hint (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word_id INTEGER NOT NULL,
                    text TEXT NOT NULL
                )
            """);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Очищаємо дані перед кожним тестом
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Hint");
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        if (sharedConnection != null && !sharedConnection.isClosed()) {
            sharedConnection.close();
        }
        connectionPool.shutdown();
    }

    @Test
    void testSaveAndFindById() {
        Hint hint = new Hint();
        hint.setWordId(1L);
        hint.setText("Test Hint");

        Hint saved = repository.save(hint);
        assertNotNull(saved.getId(), "ID збереженої підказки не повинен бути null");

        Optional<Hint> found = repository.findById(saved.getId());
        assertTrue(found.isPresent(), "Підказка повинна бути знайдена");
        assertEquals("Test Hint", found.get().getText(), "Текст підказки має співпадати");
        assertEquals(1L, found.get().getWordId(), "ID слова має співпадати");
    }

    @Test
    void testFindByWordId() {
        // Перша підказка
        Hint hint1 = new Hint();
        hint1.setWordId(10L);
        hint1.setText("Hint 1");
        repository.save(hint1);

        // Друга підказка для того ж слова
        Hint hint2 = new Hint();
        hint2.setWordId(10L);
        hint2.setText("Hint 2");
        repository.save(hint2);

        // Третя підказка для іншого слова
        Hint hint3 = new Hint();
        hint3.setWordId(20L);
        hint3.setText("Hint 3");
        repository.save(hint3);

        List<Hint> hints = repository.findByWordId(10L);
        assertEquals(2, hints.size(), "Повинно бути знайдено 2 підказки для слова з ID 10");
        assertTrue(hints.stream().allMatch(h -> h.getWordId().equals(10L)),
            "Усі знайдені підказки повинні належати слову з ID 10");
    }

    @Test
    void testUpdateHint() {
        // Створюємо та зберігаємо підказку
        Hint hint = new Hint();
        hint.setWordId(3L);
        hint.setText("Original");
        Hint saved = repository.save(hint);

        // Оновлюємо текст підказки
        saved.setText("Updated");
        repository.update(saved.getId(), saved);

        // Перевіряємо оновлену підказку
        Optional<Hint> updated = repository.findById(saved.getId());
        assertTrue(updated.isPresent(), "Підказка повинна існувати після оновлення");
        assertEquals("Updated", updated.get().getText(), "Текст підказки має бути оновленим");
        assertEquals(3L, updated.get().getWordId(), "ID слова має залишитися незмінним");
    }

    @Test
    void testDeleteHint() {
        // Створюємо та зберігаємо підказку
        Hint hint = new Hint();
        hint.setWordId(5L);
        hint.setText("To delete");
        Hint saved = repository.save(hint);

        // Видаляємо підказку
        repository.delete(saved.getId());

        // Перевіряємо, що підказка більше не існує
        Optional<Hint> deleted = repository.findById(saved.getId());
        assertFalse(deleted.isPresent(), "Підказка не повинна існувати після видалення");
    }
}