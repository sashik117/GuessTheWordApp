package com.guessthewordapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static final HikariDataSource dataSource;
    private static HikariDataSource testDataSource;

    static {
        HikariConfig config = new HikariConfig();
        try {
            // Конфігурація для основної бази
            config.setJdbcUrl("jdbc:sqlite:./guesstheword.db");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("foreign_keys", "true");

            dataSource = new HikariDataSource(config);

            // Перевірка та оновлення схеми
            checkAndUpdateSchema(dataSource);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize main database pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void checkAndUpdateSchema(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement()) {

            // Перевірка наявності стовпця description
            if (!columnExists(conn, "Word", "description")) {
                LOGGER.info("Adding description column to Word table...");
                stmt.execute("ALTER TABLE Word ADD COLUMN description TEXT");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Schema update failed", e);
        }
    }

    public static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, table, column)) {
            return rs.next();
        }
    }

    // Інші методи без змін...
    public static DataSource getDataSource() {
        return dataSource;
    }

    public static DataSource getTestDataSource() {
        if (testDataSource == null) {
            HikariConfig testConfig = new HikariConfig();
            try {
                testConfig.setJdbcUrl("jdbc:sqlite::memory:");
                testConfig.setDriverClassName("org.sqlite.JDBC");
                testConfig.setMaximumPoolSize(5);
                testDataSource = new HikariDataSource(testConfig);

                // Ініціалізація тестової БД
                try (Connection conn = testDataSource.getConnection();
                    Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Word ("
                        + "word_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "text TEXT NOT NULL,"
                        + "difficulty INTEGER NOT NULL,"
                        + "language TEXT NOT NULL,"
                        + "description TEXT)");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize test database pool", e);
                throw new RuntimeException(e);
            }
        }
        return testDataSource;
    }

    public static void close() {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
            if (testDataSource != null) {
                testDataSource.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to close database pools", e);
        }
    }
}