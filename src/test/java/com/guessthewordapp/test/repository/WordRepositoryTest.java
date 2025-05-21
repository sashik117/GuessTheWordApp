package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import com.guessthewordapp.infrastructure.persistence.impl.WordRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WordRepositoryTest {

    private WordRepository repository;
    private ConnectionPool connectionPool;

    @BeforeAll
    void setupDatabase() throws Exception {
        DataSource dataSource = Database.getTestDataSource();
        connectionPool = new ConnectionPool(new ConnectionPool.PoolConfig.Builder()
            .withUrl("jdbc:sqlite:file::memory:?cache=shared")
            .build());

        try (var conn = connectionPool.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Word (
                    word_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text TEXT NOT NULL,
                    difficulty INTEGER NOT NULL,
                    language TEXT NOT NULL,
                    description TEXT
                )
            """);
        }
        repository = new WordRepositoryImpl(connectionPool);
    }

    @AfterEach
    void cleanupDatabase() throws Exception {
        try (var conn = connectionPool.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Word");
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        connectionPool.shutdown();
    }

    @Test
    void testSaveAndFindById() {
        Word word = new Word("яблуко", 1, "uk");
        word.setDescription("Фрукт");

        // Зберігаємо слово
        Word saved = repository.save(word);
        assertNotNull(saved.getId());

        // Шукаємо слово за ID
        Optional<Word> found = repository.findById(saved.getId());
        assertTrue(found.isPresent(), "Слово має бути знайдене");
        assertEquals("яблуко", found.get().getText());
        assertEquals("Фрукт", found.get().getDescription());
    }

    @Test
    void testFindAll() {
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("сонце", 2, "uk"));

        List<Word> words = repository.findAll();
        assertEquals(2, words.size());
    }

    @Test
    void testFindByLanguage() {
        // Підготовка даних
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("apple", 1, "en"));

        // Тестуємо
        List<Word> ukWords = repository.findByLanguage("uk");
        assertEquals(1, ukWords.size(), "Має бути 1 слово українською");
        assertEquals("яблуко", ukWords.get(0).getText());
    }

    @Test
    void testFindByDifficulty() {
        // Підготовка даних
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("сонце", 2, "uk"));

        // Тестуємо
        List<Word> easyWords = repository.findByDifficulty(1);
        assertEquals(1, easyWords.size(), "Має бути 1 слово зі складністю 1");
        assertEquals("яблуко", easyWords.get(0).getText());
    }

    @Test
    void testUpdateWord() {
        // Створюємо та зберігаємо слово
        Word word = new Word("яблуко", 1, "uk");
        Word saved = repository.save(word);

        // Оновлюємо дані
        saved.setText("яблуко золоте");
        saved.setDifficulty(2);
        repository.update(saved.getId(), saved);

        // Перевіряємо оновлення
        Optional<Word> updated = repository.findById(saved.getId());
        assertTrue(updated.isPresent(), "Слово має існувати після оновлення");
        assertEquals("яблуко золоте", updated.get().getText());
        assertEquals(2, updated.get().getDifficulty());
    }

    @Test
    void testDeleteWord() {
        Word word = new Word("гравітація", 3, "uk");
        Word saved = repository.save(word);

        repository.delete(saved.getId());
        Optional<Word> found = repository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}