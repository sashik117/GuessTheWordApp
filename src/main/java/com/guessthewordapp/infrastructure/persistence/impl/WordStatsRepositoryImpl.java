package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;

import java.util.Map;
import java.util.function.Function;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordStatsRepositoryImpl implements WordStatsRepository {

    private final DataSource dataSource;

    public WordStatsRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public WordStats save(WordStats stats) {
        String sql = "INSERT INTO WordStats (word_id, correct_count, total_count) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (stats.getWordId() == null || stats.getCorrectCount() < 0 || stats.getTotalCount() < 0) {
                throw new IllegalArgumentException("Invalid data for WordStats");
            }

            stmt.setLong(1, stats.getWordId());
            stmt.setInt(2, stats.getCorrectCount());
            stmt.setInt(3, stats.getTotalCount());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    stats.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save WordStats", e);
        }
        return stats;
    }

    @Override
    public List<WordStats> saveAll(List<WordStats> entities) {
        return List.of();
    }

    @Override
    public Optional<WordStats> findById(Long id) {
        String sql = "SELECT * FROM WordStats WHERE stat_id = ?";
        try (Connection conn = dataSource.getConnection();
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
    public Optional<WordStats> findByWordId(Long wordId) {
        String sql = "SELECT * FROM WordStats WHERE word_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (wordId == null) {
                throw new IllegalArgumentException("wordId cannot be null");
            }

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
    public List<WordStats> findAll(int offset, int limit) {
        String sql = "SELECT * FROM WordStats LIMIT ? OFFSET ?";
        List<WordStats> statsList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
    public long count() {
        return 0;
    }

    @Override
    public <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper) {
        return List.of();
    }

    @Override
    public WordStats update(Long id, WordStats entity) {
        String sql = "UPDATE WordStats SET correct_count = ?, total_count = ? WHERE stat_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getCorrectCount());
            stmt.setInt(2, entity.getTotalCount());
            stmt.setLong(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update WordStats", e);
        }
        return entity;
    }

    @Override
    public Map<Long, WordStats> updateAll(Map<Long, WordStats> entities) {
        return Map.of();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM WordStats WHERE stat_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete WordStats", e);
        }
    }

    @Override
    public void deleteAll(List<Long> longs) {

    }

    @Override
    public Object extractId(Object entity) {
        if (entity instanceof WordStats) {
            return ((WordStats) entity).getId();
        }
        throw new IllegalArgumentException("Entity is not of type WordStats");
    }

    private WordStats mapRow(ResultSet rs) throws SQLException {
        WordStats stats = new WordStats();
        stats.setId(rs.getLong("stat_id"));
        stats.setWordId(rs.getLong("word_id"));
        stats.setCorrectCount(rs.getInt("correct_count"));
        stats.setTotalCount(rs.getInt("total_count"));
        return stats;
    }
}
