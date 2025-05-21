package com.guessthewordapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
            config.setJdbcUrl("jdbc:sqlite:./guesstheword.db"); // Повний шлях до файлу БД
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000); // 30 секунд на з'єднання
            config.setIdleTimeout(600000); // 10 хвилин простою
            config.setMaxLifetime(1800000); // 30 хвилин для живучості з'єднання

            dataSource = new HikariDataSource(config);

            // Створення таблиці game_sessions
            try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS game_sessions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "started_at TIMESTAMP NOT NULL," +
                    "ended_at TIMESTAMP NOT NULL)";
                stmt.execute(createTableSQL);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize main database pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static DataSource getTestDataSource() {
        if (testDataSource == null) {
            HikariConfig testConfig = new HikariConfig();
            try {
                // Конфігурація для тестової бази
                testConfig.setJdbcUrl("jdbc:sqlite::memory:"); // in-memory база
                testConfig.setDriverClassName("org.sqlite.JDBC");
                testConfig.setMaximumPoolSize(5);
                testDataSource = new HikariDataSource(testConfig);
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
