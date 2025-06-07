package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.List;
import java.util.Optional;

// Змінено Repository<Word, Integer> відповідно до вашого надання
public interface WordRepository extends Repository<Word, Integer> {
    List<Word> findAll();
    List<Word> findByLanguage(String language);
    List<Word> findByDifficulty(int difficulty);
    // Optional<Word> findById(Integer id); - вже є від Repository<Word, Integer>
    // Word save(Word word); - вже є від Repository<Word, Integer>
    // void delete(Integer id); - вже є від Repository<Word, Integer>
    // Додайте, якщо є в імплементації, наприклад:
    // Word update(Integer id, Word word);
    // Optional<Word> findByText(String text);
}