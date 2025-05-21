package com.guessthewordapp.config;

import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final ConnectionPool connectionPool;

    public DatabaseInitializer(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Метод ініціалізації бази даних за допомогою SQL-скрипту
    public void initialize() {
        try (
            Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            InputStream input = getClass().getClassLoader().getResourceAsStream("db/ddl_sqlite.sql")
        ) {
            // Перевіряємо, чи вже є записи в таблиці User
            var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM User");
            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("База даних вже ініціалізована");
                return;
            }

            if (input == null) {
                throw new RuntimeException("SQL скрипт для ініціалізації БД не знайдено");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                String sqlScript = sb.toString();
                String[] statements = sqlScript.split(";");
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                    }
                }
            }

            System.out.println("\u2705 База даних успішно ініціалізована");
        } catch (Exception e) {
            throw new RuntimeException("Помилка при ініціалізації БД", e);
        }
    }
}
