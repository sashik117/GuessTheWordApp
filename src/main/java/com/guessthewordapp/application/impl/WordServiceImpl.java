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
    public Optional<WordDTO> getWordById(Integer wordId) {
        return wordRepository.findById(wordId)
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
    public void deleteWord(Integer wordId) {
        wordRepository.delete(wordId);
    }

    private Word mapToEntity(WordDTO dto) {
        return new Word(
            dto.id(),
            dto.text(),
            dto.difficulty(),
            dto.language(),
            dto.description()
        );
    }

    private WordDTO mapToDTO(Word entity) {
        return new WordDTO(
            entity.getId(),
            entity.getText(),
            entity.getDifficulty(),
            entity.getLanguage(),
            entity.getDescription()
        );
    }
}