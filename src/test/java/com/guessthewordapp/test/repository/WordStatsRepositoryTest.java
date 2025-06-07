package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;
import com.guessthewordapp.infrastructure.persistence.impl.WordStatsRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection; // Імпорт Connection
import java.sql.PreparedStatement; // Імпорт PreparedStatement
import java.sql.ResultSet; // Імпорт ResultSet
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WordStatsRepositoryTest {

    private WordStatsRepository repository;
    private ConnectionPool connectionPool;

    @BeforeAll
    void setupDatabase() throws Exception {
        // Database.getTestDataSource() можливо вже налаштовує HikariCP або інший пул
        // Але для тестування в пам'яті краще мати окремий ConnectionPool
        connectionPool = new ConnectionPool(new ConnectionPool.PoolConfig.Builder()
            .withUrl("jdbc:sqlite:file::memory:?cache=shared")
            .build());

        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            var stmt = conn.createStatement()) {
            // ОНОВЛЕНО: Додано стовпець user_id до схеми
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS WordStats (
                    stat_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    correct_count INTEGER NOT NULL,
                    total_count INTEGER NOT NULL
                )
            """);
        }
        repository = new WordStatsRepositoryImpl(connectionPool);
    }

    @AfterEach
    void cleanupDatabase() throws Exception {
        try (Connection conn = connectionPool.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM WordStats");
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        connectionPool.shutdown();
    }

    @Test
    void testSaveAndFindById() throws Exception {
        WordStats stats = new WordStats();
        stats.setWordId(1L);
        stats.setUserId(10L); // Важливо: встановлюємо userId
        stats.setCorrectCount(5);
        stats.setTotalCount(10);

        WordStats saved = repository.save(stats);
        assertNotNull(saved.getId());

        Optional<WordStats> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1L, found.get().getWordId());
        assertEquals(10L, found.get().getUserId());
        assertEquals(5, found.get().getCorrectCount());
        assertEquals(10, found.get().getTotalCount());
    }

    @Test
    void testFindByWordId() throws Exception {
        WordStats stats = new WordStats();
        stats.setWordId(2L);
        stats.setUserId(20L); // Важливо: встановлюємо userId
        stats.setCorrectCount(3);
        stats.setTotalCount(7);
        repository.save(stats); // Збереження WordStats з wordId = 2L

        Optional<WordStats> found = repository.findByWordId(2L); // Пошук за wordId = 2L
        assertTrue(found.isPresent()); // <--- ПОМИЛКА БУЛА ТУТ, якщо findByWordId повертав порожній Optional
        assertEquals(2L, found.get().getWordId());
        assertEquals(20L, found.get().getUserId());
        assertEquals(3, found.get().getCorrectCount());
        assertEquals(7, found.get().getTotalCount());
    }

    @Test
    void testUpdateStats() throws Exception {
        WordStats stats = new WordStats();
        stats.setWordId(3L);
        stats.setUserId(30L); // Важливо: встановлюємо userId
        stats.setCorrectCount(2);
        stats.setTotalCount(4);
        WordStats saved = repository.save(stats);

        saved.setCorrectCount(10);
        saved.setTotalCount(20);
        repository.update(saved.getId(), saved);

        Optional<WordStats> updated = repository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals(saved.getId(), updated.get().getId());
        assertEquals(3L, updated.get().getWordId());
        assertEquals(30L, updated.get().getUserId());
        assertEquals(10, updated.get().getCorrectCount());
        assertEquals(20, updated.get().getTotalCount());
    }

    @Test
    void testDeleteStats() throws Exception {
        WordStats stats = new WordStats();
        stats.setWordId(4L);
        stats.setUserId(40L); // Важливо: встановлюємо userId
        stats.setCorrectCount(1);
        stats.setTotalCount(1);
        WordStats saved = repository.save(stats);

        repository.delete(saved.getId());
        Optional<WordStats> deleted = repository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testFindAll() throws Exception {
        repository.save(new WordStats(null, 5L, 50L, 1, 2));
        repository.save(new WordStats(null, 6L, 60L, 3, 5));

        List<WordStats> allStats = repository.findAll();
        assertEquals(2, allStats.size());
    }
}