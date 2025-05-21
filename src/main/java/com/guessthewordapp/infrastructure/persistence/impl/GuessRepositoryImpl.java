package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.Guess;
import com.guessthewordapp.infrastructure.persistence.contract.GuessRepository;
import com.guessthewordapp.infrastructure.persistence.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class GuessRepositoryImpl implements GuessRepository {

    private final DataSource dataSource;

    public GuessRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Guess> findById(Long id) {
        String sql = "SELECT * FROM Guess WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find guess by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guess> findBySessionId(Long sessionId) {
        String sql = "SELECT * FROM Guess WHERE session_id = ?";
        List<Guess> guesses = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    guesses.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find guesses by sessionId", e);
        }
        return guesses;
    }

    @Override
    public List<Guess> findByField(String fieldName, Object value) {
        String sql = "SELECT * FROM Guess WHERE " + fieldName + " = ?";
        List<Guess> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find guesses by field: " + fieldName, e);
        }
        return results;
    }

    @Override
    public List<Guess> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit, String baseSql) {
        StringBuilder sql = new StringBuilder(baseSql != null ? baseSql : "SELECT * FROM Guess");
        List<Object> parameters = new ArrayList<>();
        StringJoiner whereClause = new StringJoiner(" AND ", " WHERE ", "");

        if (filter != null) {
            filter.apply(whereClause, parameters);
            sql.append(whereClause);
        }

        sql.append(" ORDER BY ").append(sortBy).append(isAscending ? " ASC" : " DESC");
        sql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);

        List<Guess> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch guesses with filter", e);
        }
        return results;
    }

    @Override
    public List<Guess> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit) {
        return findAll(filter, sortBy, isAscending, offset, limit, null);
    }

    @Override
    public List<Guess> findAll(int offset, int limit) {
        return findAll(null, "id", true, offset, limit, null);
    }

    @Override
    public List<Guess> findAll() {
        return findAll(0, Integer.MAX_VALUE);
    }

    @Override
    public long count(QueryFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Guess");
        List<Object> parameters = new ArrayList<>();
        StringJoiner whereClause = new StringJoiner(" AND ", " WHERE ", "");

        if (filter != null) {
            filter.apply(whereClause, parameters);
            sql.append(whereClause);
        }

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count guesses with filter", e);
        }
        return 0;
    }

    @Override
    public long count() {
        return count(null);
    }

    @Override
    public <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper) {
        StringJoiner selectClause = new StringJoiner(", ", "SELECT ", "");
        StringJoiner groupByClause = new StringJoiner(", ", " GROUP BY ", "");
        aggregation.apply(selectClause, groupByClause);

        String sql = selectClause + " FROM Guess" + groupByClause;
        List<R> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(resultMapper.apply(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to group guesses", e);
        }
        return results;
    }

    @Override
    public Guess save(Guess entity) {
        String sql = "INSERT INTO Guess (session_id, word_id, guess_text, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, entity.getSessionId());
            stmt.setLong(2, entity.getWordId());
            stmt.setString(3, entity.getGuessedText());
            stmt.setBoolean(4, entity.isCorrect());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save guess", e);
        }
        return entity;
    }

    @Override
    public List<Guess> saveAll(List<Guess> entities) {
        List<Guess> savedEntities = new ArrayList<>();
        for (Guess entity : entities) {
            savedEntities.add(save(entity));
        }
        return savedEntities;
    }

    @Override
    public Guess update(Long id, Guess entity) {
        String sql = "UPDATE Guess SET session_id = ?, word_id = ?, guess_text = ?, is_correct = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getSessionId());
            stmt.setLong(2, entity.getWordId());
            stmt.setString(3, entity.getGuessedText());
            stmt.setBoolean(4, entity.isCorrect());
            stmt.setLong(5, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update guess", e);
        }
        return entity;
    }

    @Override
    public Map<Long, Guess> updateAll(Map<Long, Guess> entities) {
        Map<Long, Guess> updatedEntities = new HashMap<>();
        for (Map.Entry<Long, Guess> entry : entities.entrySet()) {
            updatedEntities.put(entry.getKey(), update(entry.getKey(), entry.getValue()));
        }
        return updatedEntities;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM Guess WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete guess", e);
        }
    }

    @Override
    public void deleteAll(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public Object extractId(Object entity) {
        if (entity instanceof Guess) {
            return ((Guess) entity).getId();
        }
        throw new IllegalArgumentException("Invalid entity type for extracting ID");
    }

    private Guess mapRow(ResultSet rs) throws SQLException {
        Guess guess = new Guess();
        guess.setId(rs.getLong("id"));
        guess.setSessionId(rs.getLong("session_id"));
        guess.setWordId(rs.getLong("word_id"));
        guess.setGuessedText(rs.getString("guess_text"));
        guess.setCorrect(rs.getBoolean("is_correct"));
        return guess;
    }
}
