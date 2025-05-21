package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.GameSession;
import com.guessthewordapp.infrastructure.persistence.contract.GameSessionRepository;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GameSessionRepositoryImpl implements GameSessionRepository {

    private final ConnectionPool connectionPool;

    public GameSessionRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public GameSession save(GameSession session) {
        String sql = "INSERT INTO GameSession (user_id, started_at, ended_at) VALUES (?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, session.getUserId());
            stmt.setTimestamp(2, session.getStartedAt());
            if (session.getEndedAt() != null) {
                stmt.setTimestamp(3, session.getEndedAt());
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getLong(1));
                }
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save game session", e);
        }
    }

    @Override
    public List<GameSession> saveAll(List<GameSession> entities) {
        List<GameSession> savedSessions = new ArrayList<>();
        for (GameSession session : entities) {
            savedSessions.add(save(session));
        }
        return savedSessions;
    }

    @Override
    public Optional<GameSession> findById(Long id) {
        String sql = "SELECT * FROM GameSession WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find session by id", e);
        }
    }

    @Override
    public List<GameSession> findByUserId(Long userId) {
        String sql = "SELECT * FROM GameSession WHERE user_id = ?";
        List<GameSession> sessions = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRow(rs));
                }
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sessions by user id", e);
        }
    }

    @Override
    public List<GameSession> findByField(String fieldName, Object value) {
        String sql = "SELECT * FROM GameSession WHERE " + fieldName + " = ?";
        List<GameSession> sessions = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRow(rs));
                }
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sessions by field: " + fieldName, e);
        }
    }

    @Override
    public List<GameSession> findAll(QueryFilter filter, String sortBy, boolean isAscending,
        int offset, int limit, String baseSql) {
        return List.of();
    }

    @Override
    public List<GameSession> findAll(QueryFilter filter, String sortBy, boolean isAscending,
        int offset, int limit) {
        return List.of();
    }

    @Override
    public List<GameSession> findAll() {
        String sql = "SELECT * FROM GameSession";
        List<GameSession> sessions = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(mapRow(rs));
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all sessions", e);
        }
    }

    @Override
    public long count(QueryFilter filter) {
        return 0;
    }

    @Override
    public GameSession update(Long id, GameSession entity) {
        String sql = "UPDATE GameSession SET user_id = ?, started_at = ?, ended_at = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getUserId());
            stmt.setTimestamp(2, entity.getStartedAt());
            stmt.setTimestamp(3, entity.getEndedAt());
            stmt.setLong(4, id);
            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update session", e);
        }
    }

    @Override
    public Map<Long, GameSession> updateAll(Map<Long, GameSession> entities) {
        return Map.of();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM GameSession WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete session", e);
        }
    }

    @Override
    public void deleteAll(List<Long> longs) {

    }

    private GameSession mapRow(ResultSet rs) throws SQLException {
        GameSession session = new GameSession();
        session.setId(rs.getLong("id"));
        session.setUserId(rs.getLong("user_id"));
        session.setStartedAt(rs.getTimestamp("started_at"));
        session.setEndedAt(rs.getTimestamp("ended_at"));
        return session;
    }

    // Інші методи інтерфейсу (якщо потрібно)
    @Override
    public List<GameSession> findAll(int offset, int limit) { return findAll(); }
    @Override
    public long count() { return findAll().size(); }

    @Override
    public <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper) {
        return List.of();
    }

    @Override
    public Object extractId(Object entity) {
        return entity instanceof GameSession ? ((GameSession) entity).getId() : null;
    }
}