package com.guessthewordapp.infrastructure.persistence.impl;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final ConnectionPool connectionPool;

    public UserRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        logger.debug("UserRepositoryImpl initialized");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        String sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapRow(rs);
                    logger.debug("User found: id={}, username={}", user.getId(), user.getUsername());
                    return Optional.of(user);
                }
            }
            logger.debug("No user found by username: {}", username);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Failed to find user by username: {}", username, e);
            throw new RuntimeException("Failed to find user by username", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        String sql = "SELECT * FROM User WHERE email = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapRow(rs);
                    logger.debug("User found: id={}, email={}", user.getId(), user.getEmail());
                    return Optional.of(user);
                }
            }
            logger.debug("No user found by email: {}", email);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Failed to find user by email: {}", email, e);
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    @Override
    public User save(User user) {
        logger.debug("Saving user: email={}", user.getEmail());
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user.getId(), user);
        }
    }

    private User create(User user) {
        logger.debug("Creating new user: email={}", user.getEmail());
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
                    logger.info("User created: id={}, email={}", user.getId(), user.getEmail());
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Failed to create user: email={}", user.getEmail(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public List<User> findAll() {
        logger.debug("Finding all users");
        String sql = "SELECT * FROM User";
        List<User> users = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            logger.debug("Found {} users", users.size());
            return users;
        } catch (SQLException e) {
            logger.error("Failed to find all users", e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public long count(QueryFilter filter) {
        logger.warn("count(QueryFilter) method not implemented due to missing QueryFilter.toSql()");
        return 0; // Тимчасова заглушка
    }

    @Override
    public long count() {
        logger.debug("Counting all users");
        String sql = "SELECT COUNT(*) FROM User";
        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("Counted users: {}", count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Failed to count all users", e);
            throw new RuntimeException("Failed to count all users", e);
        }
    }

    @Override
    public <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper) {
        logger.warn("groupBy method not fully implemented");
        return List.of(); // Реалізуй при потребі
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by id: {}", id);
        // Повертаємо до використання id, оскільки в базі даних стовпець називається id
        String sql = "SELECT * FROM User WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapRow(rs);
                    logger.debug("User found: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
                    return Optional.of(user);
                }
            }
            logger.debug("No user found by id: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Failed to find user by id: {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to find user by id", e);
        }
    }

    @Override
    public List<User> findByField(String fieldName, Object value) {
        logger.debug("Finding users by field: {}={}", fieldName, value);
        String sql = "SELECT * FROM User WHERE " + fieldName + " = ?";
        List<User> users = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            logger.debug("Found {} users by field: {}={}", users.size(), fieldName, value);
            return users;
        } catch (SQLException e) {
            logger.error("Failed to find users by field: {}={}", fieldName, value, e);
            throw new RuntimeException("Failed to find users by field", e);
        }
    }

    @Override
    public List<User> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit, String baseSql) {
        logger.warn("findAll(QueryFilter) method not implemented due to missing QueryFilter.toSql()");
        return List.of(); // Тимчасова заглушка
    }

    @Override
    public List<User> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit) {
        return findAll(filter, sortBy, isAscending, offset, limit, "SELECT * FROM User");
    }

    @Override
    public List<User> findAll(int offset, int limit) {
        logger.debug("Finding users with offset={}, limit={}", offset, limit);
        String sql = "SELECT * FROM User LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            logger.debug("Found {} users", users.size());
            return users;
        } catch (SQLException e) {
            logger.error("Failed to find users with offset and limit", e);
            throw new RuntimeException("Failed to find users with offset and limit", e);
        }
    }

    @Override
    public User update(Long id, User user) {
        logger.debug("Updating user: id={}", id);
        String sql = "UPDATE User SET username = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setLong(5, id);
            stmt.executeUpdate();
            logger.info("User updated: id={}, email={}", id, user.getEmail());
            return user;
        } catch (SQLException e) {
            logger.error("Failed to update user: id={}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public Map<Long, User> updateAll(Map<Long, User> entities) {
        logger.debug("Updating {} users", entities.size());
        Map<Long, User> updatedUsers = new java.util.HashMap<>();
        for (Map.Entry<Long, User> entry : entities.entrySet()) {
            updatedUsers.put(entry.getKey(), update(entry.getKey(), entry.getValue()));
        }
        logger.debug("Updated {} users", updatedUsers.size());
        return updatedUsers;
    }

    @Override
    public void delete(Long id) {
        logger.debug("Deleting user: id={}", id);
        String sql = "DELETE FROM User WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            logger.info("User deleted: id={}", id);
        } catch (SQLException e) {
            logger.error("Failed to delete user: id={}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public void deleteAll(List<Long> ids) {
        logger.debug("Deleting {} users", ids.size());
        String sql = "DELETE FROM User WHERE id IN (" + ids.stream().map(id -> "?").collect(
            Collectors.joining(",")) + ")";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setLong(i + 1, ids.get(i));
            }
            stmt.executeUpdate();
            logger.info("Deleted {} users", ids.size());
        } catch (SQLException e) {
            logger.error("Failed to delete users", e);
            throw new RuntimeException("Failed to delete users", e);
        }
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

    @Override
    public List<User> saveAll(List<User> entities) {
        logger.debug("Saving {} users", entities.size());
        List<User> savedUsers = new ArrayList<>();
        for (User user : entities) {
            savedUsers.add(save(user));
        }
        logger.debug("Saved {} users", savedUsers.size());
        return savedUsers;
    }

    @Override
    public Object extractId(Object entity) {
        return entity instanceof User ? ((User) entity).getId() : null;
    }
}