package com.guessthewordapp.infrastructure.persistence;

import com.guessthewordapp.domain.enteties.*;
import com.guessthewordapp.infrastructure.persistence.contract.*;
import com.guessthewordapp.infrastructure.persistence.exception.DatabaseAccessException;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import jakarta.annotation.PostConstruct;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Реалізація патерну Unit of Work для управління транзакціями та змінами сутностей.
 */
 @Component //— анотацію видалено згідно з твоїм проханням
public class PersistenceContext {

    private final ConnectionPool connectionPool;

    private final GameSessionRepository gameSessionRepository;
    private final GuessRepository guessRepository;
    private final HintRepository hintRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final WordStatsRepository wordStatsRepository;

    private Connection connection;

    private final Map<Class<?>, Repository<?, ?>> repositories;
    private final List<Object> newEntities;
    private final Map<Object, Object> updatedEntities;
    private final List<Object> deletedEntities;

    public PersistenceContext(ConnectionPool connectionPool,
        GameSessionRepository gameSessionRepository,
        GuessRepository guessRepository,
        HintRepository hintRepository,
        UserRepository userRepository,
        WordRepository wordRepository,
        WordStatsRepository wordStatsRepository) {
        this.connectionPool = connectionPool;
        this.gameSessionRepository = gameSessionRepository;
        this.guessRepository = guessRepository;
        this.hintRepository = hintRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.wordStatsRepository = wordStatsRepository;

        this.repositories = new HashMap<>();
        this.newEntities = new ArrayList<>();
        this.updatedEntities = new HashMap<>();
        this.deletedEntities = new ArrayList<>();

        initializeConnection();

        // ВИКЛИК ІНІЦІАЛІЗАЦІЇ РЕПОЗИТОРІЇВ вручну
        init();
    }

    // Тепер цей метод можна залишити приватним і без @PostConstruct,
    // бо викликаємо його вручну у конструкторі
    private void init() {
        this.registerRepository(GameSession.class, gameSessionRepository);
        this.registerRepository(Guess.class, guessRepository);
        this.registerRepository(Hint.class, hintRepository);
        this.registerRepository(User.class, userRepository);
        this.registerRepository(Word.class, wordRepository);
        this.registerRepository(WordStats.class, wordStatsRepository);
    }

    public <T, ID> void registerRepository(Class<T> entityClass, Repository<T, ID> repository) {
        repositories.put(entityClass, repository);
    }

    public void registerNew(Object entity) {
        if (entity == null) throw new IllegalArgumentException("Сутність не може бути null");
        newEntities.add(entity);
    }

    public void registerUpdated(Object id, Object entity) {
        if (id == null || entity == null)
            throw new IllegalArgumentException("Ідентифікатор або сутність не можуть бути null");
        updatedEntities.put(id, entity);
    }

    public void registerDeleted(Object entity) {
        if (entity == null) throw new IllegalArgumentException("Сутність не може бути null");
        deletedEntities.add(entity);
    }

    public void commit() {
        try {
            for (Object entity : newEntities) {
                Repository<Object, Object> repository = getRepository(entity.getClass());
                System.out.println("Saving entity: " + entity);
                repository.save(entity);
            }
            for (Map.Entry<Object, Object> entry : updatedEntities.entrySet()) {
                Repository<Object, Object> repository = getRepository(entry.getValue().getClass());
                repository.update(entry.getKey(), entry.getValue());
            }
            for (Object entity : deletedEntities) {
                Repository<Object, Object> repository = getRepository(entity.getClass());
                Object id = repository.extractId(entity);
                repository.delete(id);
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new DatabaseAccessException("Помилка відкатування транзакції", rollbackEx);
            }
            throw new DatabaseAccessException("Помилка виконання транзакції", e);
        } finally {
            clear();
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseAccessException("Помилка закриття з'єднання", e);
            }
        }
    }

    private void clear() {
        newEntities.clear();
        updatedEntities.clear();
        deletedEntities.clear();
    }

    private void initializeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            this.connection = connectionPool.getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DatabaseAccessException("Помилка ініціалізації з'єднання", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID> Repository<T, ID> getRepository(Class<?> entityClass) {
        Repository<T, ID> repository = (Repository<T, ID>) repositories.get(entityClass);
        if (repository == null) {
            throw new IllegalStateException("Репозиторій для " + entityClass.getSimpleName() + " не зареєстровано");
        }
        return repository;
    }
}
