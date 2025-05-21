package com.guessthewordapp.test.repository;

import com.guessthewordapp.config.Database;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.impl.UserRepositoryImpl;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    private UserRepository repository;
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
                CREATE TABLE IF NOT EXISTS User (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            """);
        }
        repository = new UserRepositoryImpl(connectionPool);
    }

    @AfterEach
    void cleanupDatabase() throws Exception {
        try (var conn = connectionPool.getConnection();
            var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM User");
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        connectionPool.shutdown();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpass");
        user.setRole(UserRole.PLAYER);

        User saved = repository.save(user);
        assertNotNull(saved.getId());

        Optional<User> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals(UserRole.PLAYER, found.get().getRole());
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setUsername("emailuser");
        user.setEmail("email@example.com");
        user.setPasswordHash("passhash");
        user.setRole(UserRole.PLAYER);

        repository.save(user);

        Optional<User> found = repository.findByEmail("email@example.com");
        assertTrue(found.isPresent());
        assertEquals("emailuser", found.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("uniqueuser");
        user.setEmail("unique@example.com");
        user.setPasswordHash("passhash");
        user.setRole(UserRole.PLAYER);

        repository.save(user);

        Optional<User> found = repository.findByUsername("uniqueuser");
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUsername("oldname");
        user.setEmail("update@example.com");
        user.setPasswordHash("passhash");
        user.setRole(UserRole.PLAYER);
        User saved = repository.save(user);

        saved.setUsername("newname");
        saved.setRole(UserRole.ADMIN);
        repository.update(saved.getId(), saved);

        Optional<User> updated = repository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("newname", updated.get().getUsername());
        assertEquals(UserRole.ADMIN, updated.get().getRole());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setUsername("todelete");
        user.setEmail("delete@example.com");
        user.setPasswordHash("passhash");
        user.setRole(UserRole.PLAYER);
        User saved = repository.save(user);

        repository.delete(saved.getId());
        Optional<User> deleted = repository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}