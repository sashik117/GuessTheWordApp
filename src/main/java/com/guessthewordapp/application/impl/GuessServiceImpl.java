package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.GuessService;
import com.guessthewordapp.application.contract.dto.GuessDTO;
import com.guessthewordapp.domain.enteties.Guess;
import com.guessthewordapp.infrastructure.persistence.contract.GuessRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuessServiceImpl implements GuessService {

    private final GuessRepository guessRepository;

    public GuessServiceImpl(GuessRepository guessRepository) {
        this.guessRepository = guessRepository;
    }

    @Override
    public GuessDTO saveGuess(GuessDTO guessDTO) {
        Guess guess = mapToEntity(guessDTO);
        Guess savedGuess = guessRepository.save(guess);
        return mapToDTO(savedGuess);
    }

    @Override
    public List<GuessDTO> getGuessesForSession(Long sessionId) {
        return guessRepository.findBySessionId(sessionId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    private Guess mapToEntity(GuessDTO dto) {
        return new Guess(
            dto.id(),
            dto.sessionId(),
            dto.wordId(),
            dto.guessedText(),
            dto.correct()
        );
    }

    private GuessDTO mapToDTO(Guess entity) {
        return new GuessDTO(
            entity.getId(),
            entity.getSessionId(),
            entity.getWordId(),
            entity.getGuessedText(),
            entity.isCorrect()
        );
    }
}