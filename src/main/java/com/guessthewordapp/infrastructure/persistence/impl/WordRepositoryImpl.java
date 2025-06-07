package com.guessthewordapp.infrastructure.persistence.impl;

import static com.guessthewordapp.config.Database.columnExists;

import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import com.guessthewordapp.infrastructure.persistence.Repository; // Додано для QueryFilter, DataAggregation
import com.guessthewordapp.infrastructure.persistence.Repository.QueryFilter; // Імпортуємо вкладений клас QueryFilter
import com.guessthewordapp.infrastructure.persistence.Repository.DataAggregation; // Імпортуємо вкладений клас DataAggregation

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class WordRepositoryImpl implements WordRepository {

    private final ConnectionPool connectionPool;

    public WordRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Word> findAll() {
        final String sql = "SELECT * FROM Word";
        List<Word> words = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                words.add(mapRowToWord(rs));
            }
            return words;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка завантаження слів", e);
        }
    }

    // Ці методи, схоже, не мають реалізації у вашому репозиторії,
    // але вони є в інтерфейсі Repository. Якщо вони не потрібні,
    // можливо, варто переглянути успадкування від Repository.
    // Або ви їх ігноруєте для конкретної імплементації WordRepositoryImpl.
    // Залишаємо їх як є, якщо це задумано.
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
    // Кінець ігнорованих методів

    @Override
    public List<Word> findByLanguage(String language) {
        final String sql = "SELECT * FROM Word WHERE language = ?";
        List<Word> words = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, language);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    words.add(mapRowToWord(rs));
                }
            }
            return words;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку слів за мовою", e);
        }
    }

    @Override
    public List<Word> findByDifficulty(int difficulty) {
        final String sql = "SELECT * FROM Word WHERE difficulty = ?";
        List<Word> words = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, difficulty);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    words.add(mapRowToWord(rs));
                }
            }
            return words;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку слів за складністю", e);
        }
    }

    @Override
    public Optional<Word> findById(Integer id) { // <--- ID тепер Integer
        final String sql = "SELECT * FROM Word WHERE word_id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // <--- setInt для Integer
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWord(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку слова за ID", e);
        }
    }

    // Ці методи, схоже, не мають реалізації у вашому репозиторії
    @Override
    public List<Word> findByField(String fieldName, Object value) {
        return List.of();
    }

    @Override
    public List<Word> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit, String baseSql) {
        return List.of();
    }

    @Override
    public List<Word> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset,
        int limit) {
        return List.of();
    }

    @Override
    public List<Word> findAll(int offset, int limit) {
        return List.of();
    }
    // Кінець ігнорованих методів

    @Override
    public Word save(Word word) {
        if (word.getId() == null) {
            return create(word);
        } else {
            return update(word.getId(), word); // <--- getId() тепер Integer
        }
    }

    @Override
    public List<Word> saveAll(List<Word> entities) {
        return List.of();
    }

    @Override
    public void delete(Integer id) { // <--- ID тепер Integer
        final String sql = "DELETE FROM Word WHERE word_id = ?";
        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // <--- setInt для Integer
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Не вдалося видалити слово з ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення слова", e);
        }
    }

    @Override
    public void deleteAll(List<Integer> integers) {
        // Заглушка, якщо не реалізовано
    }

    @Override
    public Object extractId(Object entity) {
        // Заглушка, якщо не реалізовано
        if (entity instanceof Word) {
            return ((Word) entity).getId();
        }
        return null;
    }

    private Word create(Word word) {
        final String sql = "INSERT INTO Word (text, difficulty, language, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, word.getText());
            stmt.setInt(2, word.getDifficulty());
            stmt.setString(3, word.getLanguage());
            stmt.setString(4, word.getDescription());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    word.setId(rs.getInt(1)); // <--- getId() тепер Integer
                }
            }
            return word;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання слова", e);
        }
    }

    @Override
    public Word update(Integer id, Word word) { // <--- ID тепер Integer
        final String sql = "UPDATE Word SET text = ?, difficulty = ?, language = ?, description = ? WHERE word_id = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, word.getText());
            stmt.setInt(2, word.getDifficulty());
            stmt.setString(3, word.getLanguage());
            stmt.setString(4, word.getDescription());
            stmt.setInt(5, id); // <--- setInt для Integer
            stmt.executeUpdate();

            return word;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення слова", e);
        }
    }

    @Override
    public Map<Integer, Word> updateAll(Map<Integer, Word> entities) {
        return Map.of();
    }

    private Word mapRowToWord(ResultSet rs) throws SQLException {
        Word word = new Word();
        word.setId(rs.getInt("word_id")); // <--- getInt для Integer
        word.setText(rs.getString("text"));
        word.setDifficulty(rs.getInt("difficulty"));
        word.setLanguage(rs.getString("language"));

        // Безпечна перевірка наявності стовпця
        // Тут ми припустимо, що columnExists знаходиться в com.guessthewordapp.config.Database
        // або є допоміжним методом у цьому класі
        try {
            if (columnExists(rs, "description")) { // Використання вашого columnExists
                word.setDescription(rs.getString("description"));
            }
        } catch (SQLException e) {
            word.setDescription(""); // Fallback, якщо стовпець не знайдено
            System.err.println("Увага: стовпець 'description' не знайдено. Встановлено порожній опис.");
        }

        return word;
    }

    // Допоміжний метод columnExists, якщо він не в Database.java або ви хочете його тут
    // Якщо він вже є у Database.java, то не додавайте його сюди.
    private boolean columnExists(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();
        for (int i = 1; i <= columns; i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }
}