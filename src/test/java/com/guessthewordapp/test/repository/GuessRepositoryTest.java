package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.Guess;
import com.guessthewordapp.infrastructure.persistence.contract.GuessRepository;
import com.guessthewordapp.infrastructure.persistence.impl.GuessRepositoryImpl;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuessRepositoryTest {

    private GuessRepository repository;
    private DataSource dataSource;

    @BeforeAll
    void setupDatabase() throws Exception {
        dataSource = Database.getTestDataSource();
        try (var conn = dataSource.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Guess (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id INTEGER NOT NULL,
                    word_id INTEGER NOT NULL,
                    guess_text TEXT NOT NULL,
                    is_correct BOOLEAN NOT NULL
                )
            """);
        }
        repository = new GuessRepositoryImpl(dataSource);
    }

    @AfterEach
    void cleanupDatabase() throws Exception {
        try (var conn = dataSource.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Guess");
        }
    }

    @Test
    void testSaveAndFindById() {
        Guess guess = new Guess();
        guess.setSessionId(1L);
        guess.setWordId(2L);
        guess.setGuessedText("test");
        guess.setCorrect(true);

        Guess saved = repository.save(guess);
        assertNotNull(saved.getId());

        Optional<Guess> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("test", found.get().getGuessedText());
    }

    @Test
    void testFindBySessionId() {
        Guess guess1 = new Guess();
        guess1.setSessionId(10L);
        guess1.setWordId(1L);
        guess1.setGuessedText("guess1");
        guess1.setCorrect(false);
        repository.save(guess1);

        Guess guess2 = new Guess();
        guess2.setSessionId(10L);
        guess2.setWordId(2L);
        guess2.setGuessedText("guess2");
        guess2.setCorrect(true);
        repository.save(guess2);

        List<Guess> guesses = repository.findBySessionId(10L);
        assertEquals(2, guesses.size());
    }

    @Test
    void testUpdateGuess() {
        Guess guess = new Guess();
        guess.setSessionId(3L);
        guess.setWordId(4L);
        guess.setGuessedText("original");
        guess.setCorrect(false);
        Guess saved = repository.save(guess);

        saved.setGuessedText("updated");
        saved.setCorrect(true);
        repository.update(saved.getId(), saved);

        Optional<Guess> updated = repository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("updated", updated.get().getGuessedText());
        assertTrue(updated.get().isCorrect());
    }

    @Test
    void testDeleteGuess() {
        Guess guess = new Guess();
        guess.setSessionId(5L);
        guess.setWordId(6L);
        guess.setGuessedText("to delete");
        guess.setCorrect(true);
        Guess saved = repository.save(guess);

        repository.delete(saved.getId());
        Optional<Guess> deleted = repository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}