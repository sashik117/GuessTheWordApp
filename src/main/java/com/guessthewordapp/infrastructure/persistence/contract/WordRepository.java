package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.List;
import java.util.Optional;

public interface WordRepository extends Repository<Word, Integer> {
    List<Word> findAll();
    List<Word> findByLanguage(String language);
    List<Word> findByDifficulty(int difficulty);
    Optional<Word> findById(Integer id);
    Word save(Word word);
    void delete(Integer id);
}