package com.guessthewordapp.config;

import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseInitializer {

    private final ConnectionPool connectionPool;

    public DatabaseInitializer(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void initialize() {
        System.out.println("Початок ініціалізації БД...");

        try (Connection conn = connectionPool.getConnection()) {
            // Перевірка чи таблиця Word вже існує
            if (!tableExists(conn, "Word")) {
                System.out.println("Створення таблиць...");
                executeSqlScript(conn, "db/ddl_sqlite.sql");
            } else {
                // Перевірка чи є стовпець description
                if (!columnExists(conn, "Word", "description")) {
                    System.out.println("Додавання стовпця description...");
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE Word ADD COLUMN description TEXT");
                    }
                }
            }

            // Перевірка чи потрібно заповнювати тестові дані
            if (shouldInitializeData(conn)) {
                System.out.println("Заповнення тестовими даними...");
                executeSqlScript(conn, "db/dml_sqlite.sql");
            }

            System.out.println("\u2705 База даних успішно ініціалізована");
        } catch (Exception e) {
            System.err.println("\u274c Помилка при ініціалізації БД: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Помилка при ініціалізації БД", e);
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException, SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    private boolean shouldInitializeData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM Word")) {
            return rs.next() && rs.getInt("count") == 0;
        }
    }

    private void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        List<String> statements = new ArrayList<>();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(scriptPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            if (input == null) {
                throw new RuntimeException("SQL скрипт не знайдено: " + scriptPath);
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Пропускаємо коментарі та порожні рядки
                if (line.trim().startsWith("--") || line.trim().isEmpty()) continue;
                sb.append(line).append("\n");
            }

            // Розділяємо на окремі запити
            for (String sql : sb.toString().split(";")) {
                if (!sql.trim().isEmpty()) {
                    statements.add(sql.trim());
                }
            }
        }

        try (Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            for (String sql : statements) {
                stmt.execute(sql);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}