package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.WordDTO;
import java.util.List;
import java.util.Optional;

public interface WordService {
    WordDTO saveWord(WordDTO wordDTO);
    Optional<WordDTO> getWordById(Integer wordId);
    List<WordDTO> getWordsByLanguage(String language);
    void deleteWord(Integer wordId);
}