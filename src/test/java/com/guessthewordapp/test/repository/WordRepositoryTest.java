package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import com.guessthewordapp.infrastructure.persistence.impl.WordRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WordRepositoryTest {

    private WordRepository repository;
    private ConnectionPool connectionPool;

    @BeforeAll
    void setupDatabase() throws Exception { // throws Exception має бути тут
        // Переконайтеся, що Database.getTestDataSource() надає коректний DataSource
        // Якщо ви використовуєте ConnectionPool напряму, як у цьому прикладі,
        // то dataSource з Database не використовується для connectionPool.
        // Залиште його, якщо він потрібен для інших конфігурацій, або видаліть, якщо ні.
        // DataSource dataSource = Database.getTestDataSource(); // Цей рядок може бути зайвим, якщо connectionPool ініціалізується тут
        connectionPool = new ConnectionPool(new ConnectionPool.PoolConfig.Builder()
            .withUrl("jdbc:sqlite:file::memory:?cache=shared")
            .build());

        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool для створення таблиць
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
    void cleanupDatabase() throws Exception { // throws Exception має бути тут
        try (Connection conn = connectionPool.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Word");
        }
    }

    @AfterAll
    void tearDown() throws Exception { // throws Exception має бути тут
        connectionPool.shutdown();
    }

    @Test
    void testSaveAndFindById() throws Exception { // <--- ДОДАНО throws Exception
        // Створюємо Word за допомогою конструктора з 3 аргументами
        Word word = new Word("яблуко", 1, "uk");
        word.setDescription("Фрукт"); // Встановлюємо description окремо

        // Зберігаємо слово
        Word saved = repository.save(word);
        assertNotNull(saved.getId(), "Збережене слово повинно мати ID.");

        // Шукаємо слово за ID
        Optional<Word> found = repository.findById(Math.toIntExact(saved.getId()));
        assertTrue(found.isPresent(), "Слово має бути знайдене після збереження.");
        assertEquals(saved.getId(), found.get().getId()); // Перевіряємо ID
        assertEquals("яблуко", found.get().getText());
        assertEquals(1, found.get().getDifficulty());
        assertEquals("uk", found.get().getLanguage());
        assertEquals("Фрукт", found.get().getDescription());
    }

    @Test
    void testFindAll() throws Exception { // <--- ДОДАНО throws Exception
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("сонце", 2, "uk"));

        List<Word> words = repository.findAll();
        assertEquals(2, words.size(), "Має бути знайдено 2 слова.");
    }

    @Test
    void testFindByLanguage() throws Exception { // <--- ДОДАНО throws Exception
        // Підготовка даних
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("apple", 1, "en"));

        // Тестуємо
        List<Word> ukWords = repository.findByLanguage("uk");
        assertEquals(1, ukWords.size(), "Має бути 1 слово українською.");
        assertEquals("яблуко", ukWords.get(0).getText());
    }

    @Test
    void testFindByDifficulty() throws Exception { // <--- ДОДАНО throws Exception
        // Підготовка даних
        repository.save(new Word("яблуко", 1, "uk"));
        repository.save(new Word("сонце", 2, "uk"));

        // Тестуємо
        List<Word> easyWords = repository.findByDifficulty(1);
        assertEquals(1, easyWords.size(), "Має бути 1 слово зі складністю 1.");
        assertEquals("яблуко", easyWords.get(0).getText());
    }

    @Test
    void testUpdateWord() throws Exception { // <--- ДОДАНО throws Exception
        // Створюємо та зберігаємо слово
        Word word = new Word("яблуко", 1, "uk");
        Word saved = repository.save(word);
        assertNotNull(saved.getId(), "Збережене слово повинно мати ID для оновлення.");

        // Оновлюємо дані
        saved.setText("яблуко золоте");
        saved.setDifficulty(2);
        saved.setDescription("Смачний фрукт"); // Додамо опис для тесту
        repository.update(Math.toIntExact(saved.getId()), saved);

        // Перевіряємо оновлення
        Optional<Word> updated = repository.findById(Math.toIntExact(saved.getId()));
        assertTrue(updated.isPresent(), "Слово має існувати після оновлення.");
        assertEquals(saved.getId(), updated.get().getId());
        assertEquals("яблуко золоте", updated.get().getText());
        assertEquals(2, updated.get().getDifficulty());
        assertEquals("Смачний фрукт", updated.get().getDescription());
    }

    @Test
    void testDeleteWord() throws Exception { // <--- ДОДАНО throws Exception
        Word word = new Word("гравітація", 3, "uk");
        Word saved = repository.save(word);
        assertNotNull(saved.getId(), "Збережене слово повинно мати ID для видалення.");

        repository.delete(Math.toIntExact(saved.getId()));
        Optional<Word> found = repository.findById(Math.toIntExact(saved.getId()));
        assertFalse(found.isPresent(), "Слово має бути видалене.");
    }
}