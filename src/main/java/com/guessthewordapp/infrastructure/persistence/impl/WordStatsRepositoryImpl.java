package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;
import com.guessthewordapp.infrastructure.persistence.Repository.QueryFilter; // Додано імпорт
import com.guessthewordapp.infrastructure.persistence.Repository.DataAggregation; // Додано імпорт
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool; // <-- Важливо: використовуємо ConnectionPool

import javax.sql.DataSource; // Можливо, вам знадобиться його видалити або перетворити ConnectionPool на DataSource
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class WordStatsRepositoryImpl implements WordStatsRepository {

    // Змінимо dataSource на ConnectionPool, оскільки в тесті ви його так ініціалізуєте.
    // Або ж ConnectionPool має імплементувати DataSource.
    // Припускаємо, що ConnectionPool - це те, що ви хочете використовувати.
    private final ConnectionPool connectionPool;

    // Конструктор приймає ConnectionPool
    public WordStatsRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public WordStats save(WordStats stats) {
        if (stats.getId() == null) {
            // INSERT
            String sql = "INSERT INTO WordStats (word_id, user_id, correct_count, total_count) VALUES (?, ?, ?, ?)";
            try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                if (stats.getWordId() == null || stats.getUserId() == null) {
                    throw new IllegalArgumentException("WordId and UserId cannot be null");
                }

                stmt.setLong(1, stats.getWordId());
                stmt.setLong(2, stats.getUserId());
                stmt.setInt(3, stats.getCorrectCount());
                stmt.setInt(4, stats.getTotalCount());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        stats.setId(rs.getLong(1));
                    }
                }
                return stats;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save WordStats", e);
            }
        } else {
            // UPDATE
            return update(stats.getId(), stats);
        }
    }

    @Override
    public List<WordStats> saveAll(List<WordStats> entities) {
        List<WordStats> savedEntities = new ArrayList<>();
        for (WordStats entity : entities) {
            savedEntities.add(save(entity));
        }
        return savedEntities;
    }

    @Override
    public Optional<WordStats> findById(Long id) {
        String sql = "SELECT * FROM WordStats WHERE stat_id = ?";
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find WordStats by id", e);
        }
        return Optional.empty();
    }

    // Методи, які не мають реалізації у вашій поточній версії, залишаємо як заглушки
    @Override
    public List<WordStats> findByField(String fieldName, Object value) {
        return List.of();
    }

    @Override
    public List<WordStats> findAll(QueryFilter filter, String sortBy, boolean isAscending,
        int offset, int limit, String baseSql) {
        return List.of();
    }

    @Override
    public List<WordStats> findAll(QueryFilter filter, String sortBy, boolean isAscending,
        int offset, int limit) {
        return List.of();
    }

    @Override
    public List<WordStats> findAll(int offset, int limit) {
        String sql = "SELECT * FROM WordStats LIMIT ? OFFSET ?";
        List<WordStats> statsList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all WordStats", e);
        }
        return statsList;
    }

    @Override
    public List<WordStats> findAll() {
        String sql = "SELECT * FROM WordStats";
        List<WordStats> statsList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                statsList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all WordStats", e);
        }
        return statsList;
    }

    @Override
    public long count(QueryFilter filter) {
        return 0;
    }

    @Override
    public Optional<WordStats> findByWordId(Long wordId) { // <--- ОСЬ ТУТ БУЛА ПРОБЛЕМА! ДОДАНО РЕАЛІЗАЦІЮ!
        String sql = "SELECT * FROM WordStats WHERE word_id = ?";
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, wordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find WordStats by wordId", e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<WordStats> findByWordIdAndUserId(Long wordId, Long userId) {
        String sql = "SELECT * FROM WordStats WHERE word_id = ? AND user_id = ?";
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (wordId == null || userId == null) {
                throw new IllegalArgumentException("wordId and userId cannot be null");
            }

            stmt.setLong(1, wordId);
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find WordStats by wordId and userId", e);
        }
        return Optional.empty();
    }

    @Override
    public List<WordStats> findByUserId(Long userId) {
        String sql = "SELECT * FROM WordStats WHERE user_id = ?";
        List<WordStats> statsList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find WordStats by userId", e);
        }
        return statsList;
    }

    @Override
    public WordStats update(Long id, WordStats entity) {
        String sql = "UPDATE WordStats SET word_id = ?, user_id = ?, correct_count = ?, total_count = ? WHERE stat_id = ?";
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getWordId());
            stmt.setLong(2, entity.getUserId());
            stmt.setInt(3, entity.getCorrectCount());
            stmt.setInt(4, entity.getTotalCount());
            stmt.setLong(5, id);
            int updatedRows = stmt.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Updating WordStats failed, no rows affected.");
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update WordStats", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM WordStats WHERE stat_id = ?";
        try (Connection conn = connectionPool.getConnection(); // Використовуємо connectionPool
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete WordStats", e);
        }
    }

    // Невикористовувані методи (залишаємо, як є)
    @Override
    public long count() { return 0; }

    @Override
    public <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper) {
        return List.of();
    }

    @Override
    public Object extractId(Object entity) { return null; }

    @Override
    public long count(Object filter) {
        return 0;
    }

    // Ви прибрали параметр QueryFilter. Якщо він вам не потрібен, то добре.
    // Якщо ж у вас є два методи count з різними параметрами, то потрібно їх розрізняти.
    // @Override
    // public long count(Object filter) { return 0; } // Оригінальний метод з Object filter
    // Якщо ви хочете використовувати QueryFilter, то метод має бути:
    // @Override
    // public long count(QueryFilter filter) { return 0; } // Як вище вже є

    @Override
    public <R> List<R> groupBy(Object aggregation, Function<ResultSet, R> resultMapper) { return List.of(); }

    @Override
    public Map<Long, WordStats> updateAll(Map<Long, WordStats> entities) { return Map.of(); }

    @Override
    public void deleteAll(List<Long> ids) {}

    private WordStats mapRow(ResultSet rs) throws SQLException {
        WordStats stats = new WordStats();
        stats.setId(rs.getLong("stat_id"));
        stats.setWordId(rs.getLong("word_id"));
        stats.setUserId(rs.getLong("user_id"));
        stats.setCorrectCount(rs.getInt("correct_count"));
        stats.setTotalCount(rs.getInt("total_count"));
        return stats;
    }
}