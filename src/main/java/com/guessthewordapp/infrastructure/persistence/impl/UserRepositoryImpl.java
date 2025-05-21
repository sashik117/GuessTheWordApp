package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class UserRepositoryImpl implements UserRepository {

    private final ConnectionPool connectionPool;

    public UserRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by username", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user.getId(), user);
        }
    }

    private User create(User user) {
        String sql = "INSERT INTO User (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM User";
        List<User> users = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
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
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM User WHERE id = ?";
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
            throw new RuntimeException("Failed to find user by id", e);
        }
    }

    @Override
    public List<User> findByField(String fieldName, Object value) {
        return List.of();
    }

    @Override
    public List<User> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit, String baseSql) {
        return List.of();
    }

    @Override
    public List<User> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit) {
        return List.of();
    }

    @Override
    public List<User> findAll(int offset, int limit) {
        return List.of();
    }

    @Override
    public User update(Long id, User user) {
        String sql = "UPDATE User SET username = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setLong(5, id);
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public Map<Long, User> updateAll(Map<Long, User> entities) {
        return Map.of();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM User WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public void deleteAll(List<Long> longs) {

    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        return user;
    }

    // Інші методи інтерфейсу
    @Override
    public List<User> saveAll(List<User> entities) {
        List<User> savedUsers = new ArrayList<>();
        for (User user : entities) {
            savedUsers.add(save(user));
        }
        return savedUsers;
    }

    @Override
    public Object extractId(Object entity) {
        return entity instanceof User ? ((User) entity).getId() : null;
    }
}