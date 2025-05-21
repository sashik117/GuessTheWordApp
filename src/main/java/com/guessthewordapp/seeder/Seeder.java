package com.guessthewordapp.seeder;

import com.guessthewordapp.config.Database;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Seeder {

    private static final Logger logger = Logger.getLogger(Seeder.class.getName());

    public Seeder() {
        // Конструктор може бути порожнім
    }

    public static void run() {
        try (
            Connection conn = Database.getDataSource().getConnection();
            Statement stmt = conn.createStatement();
        ) {
            String sql;

            // Зчитуємо SQL-скрипт із файлу resources/db/schema.sql
            try (InputStream input = Seeder.class.getClassLoader().getResourceAsStream("db/schema.sql")) {
                if (input == null) {
                    throw new FileNotFoundException("Файл db/schema.sql не знайдено у ресурсах.");
                }

                sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }

            // Виконуємо SQL-скрипт
            stmt.executeUpdate(sql);
            logger.info("База даних успішно ініціалізована.");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "SQL-файл не знайдено: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Помилка ініціалізації бази даних: " + e.getMessage(), e);
        }
    }
}
