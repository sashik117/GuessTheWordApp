package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.WordService;
import com.guessthewordapp.application.contract.dto.WordDTO;
import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;

    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public WordDTO saveWord(WordDTO wordDTO) {
        Word word = mapToEntity(wordDTO);
        Word savedWord = wordRepository.save(word);
        return mapToDTO(savedWord);
    }

    @Override
    public Optional<WordDTO> getWordById(Integer wordId) { // <--- wordId тепер Integer
        return wordRepository.findById(wordId) // findById тепер очікує Integer
            .map(this::mapToDTO);
    }

    @Override
    public List<WordDTO> getWordsByLanguage(String language) {
        return wordRepository.findByLanguage(language)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteWord(Integer wordId) { // <--- wordId тепер Integer
        wordRepository.delete(wordId); // delete тепер очікує Integer
    }

    // Додайте інші методи з WordService.java, якщо вони є
    // Наприклад, для updateWord:
    // @Override
    // public Optional<WordDTO> updateWord(Integer id, WordDTO updatedWord) {
    //     Optional<Word> existingWord = wordRepository.findById(id);
    //     if (existingWord.isPresent()) {
    //         Word wordToUpdate = existingWord.get();
    //         wordToUpdate.setText(updatedWord.getText());
    //         wordToUpdate.setDifficulty(updatedWord.getDifficulty());
    //         wordToUpdate.setLanguage(updatedWord.getLanguage());
    //         wordToUpdate.setDescription(updatedWord.getDescription());
    //         return Optional.of(mapToDTO(wordRepository.update(id, wordToUpdate))); // update тепер очікує Integer
    //     }
    //     return Optional.empty();
    // }

    private Word mapToEntity(WordDTO dto) {
        return new Word(
            dto.getId(), // <--- getId() повертає Integer
            dto.getText(),
            dto.getDifficulty(),
            dto.getLanguage(),
            dto.getDescription()
        );
    }

    private WordDTO mapToDTO(Word entity) {
        return new WordDTO(
            entity.getId(), // <--- getId() повертає Integer
            entity.getText(),
            entity.getDifficulty(),
            entity.getLanguage(),
            entity.getDescription()
        );
    }
}