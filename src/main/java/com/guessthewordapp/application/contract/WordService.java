package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.WordDTO;
import java.util.List;
import java.util.Optional;

public interface WordService {
    WordDTO saveWord(WordDTO wordDTO);
    Optional<WordDTO> getWordById(Integer wordId); // <--- wordId тепер Integer
    List<WordDTO> getWordsByLanguage(String language);
    void deleteWord(Integer wordId); // <--- wordId тепер Integer
    // Додайте інші методи, якщо вони є у вашій реалізації WordServiceImpl
    // Наприклад, Optional<WordDTO> updateWord(Integer id, WordDTO updatedWord);
    // List<WordDTO> getAllWords();
    // Optional<WordDTO> getWordByText(String text);
    // List<WordDTO> getWordsByDifficulty(int difficulty);
}