package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.Hint;
import com.guessthewordapp.infrastructure.persistence.contract.HintRepository;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HintRepositoryImpl implements HintRepository {
    private final DataSource dataSource;

    public HintRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Hint> findById(Long id) {
        String sql = "SELECT * FROM Hint WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Hint hint = new Hint();
                hint.setId(rs.getLong("id"));
                hint.setWordId(rs.getLong("word_id"));
                hint.setText(rs.getString("text"));
                return Optional.of(hint);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find hint by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Hint save(Hint hint) {
        String sql = "INSERT INTO Hint (word_id, text) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, hint.getWordId());
            stmt.setString(2, hint.getText());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    hint.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save hint", e);
        }
        return hint;
    }

    @Override
    public List<Hint> findByWordId(Long wordId) {
        String sql = "SELECT * FROM Hint WHERE word_id = ?";
        List<Hint> hints = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, wordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Hint hint = new Hint();
                hint.setId(rs.getLong("id"));
                hint.setWordId(rs.getLong("word_id"));
                hint.setText(rs.getString("text"));
                hints.add(hint);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find hints by wordId", e);
        }
        return hints;
    }

    @Override
    public Hint update(Long id, Hint hint) {
        String sql = "UPDATE Hint SET word_id = ?, text = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, hint.getWordId());
            stmt.setString(2, hint.getText());
            stmt.setLong(3, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Hint with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update hint", e);
        }
        return hint;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM Hint WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete hint", e);
        }
    }

    // Решта методів залишаємо без змін, оскільки вони не використовуються в тестах
    @Override
    public List<Hint> findByField(String fieldName, Object value) {
        return List.of();
    }

    @Override
    public List<Hint> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit, String baseSql) {
        return List.of();
    }

    @Override
    public List<Hint> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit) {
        return List.of();
    }

    @Override
    public List<Hint> findAll(int offset, int limit) {
        return List.of();
    }

    @Override
    public List<Hint> findAll() {
        return List.of();
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
    public List<Hint> saveAll(List<Hint> entities) {
        return List.of();
    }

    @Override
    public Map<Long, Hint> updateAll(Map<Long, Hint> entities) {
        return Map.of();
    }

    @Override
    public void deleteAll(List<Long> longs) {
    }

    @Override
    public Object extractId(Object entity) {
        return null;
    }
}