package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.WordStatsService;
import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.domain.exception.ForbiddenOperationException;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WordStatsServiceImpl implements WordStatsService {

    private final WordStatsRepository wordStatsRepository;
    private final UserRepository userRepository;

    public WordStatsServiceImpl(WordStatsRepository wordStatsRepository,
        UserRepository userRepository) {
        this.wordStatsRepository = wordStatsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WordStatsDTO saveWordStats(WordStatsDTO wordStatsDTO, Long userId) {
        requireUserRole(userId, UserRole.EDITOR, "Збереження статистики доступне лише редакторам та адміністраторам");

        WordStats stats = mapToEntity(wordStatsDTO);
        WordStats savedStats = wordStatsRepository.save(stats);
        return mapToDTO(savedStats);
    }

    @Override
    public Optional<WordStatsDTO> getWordStatsByWordId(Long wordId, Long userId) {
        requireUserRole(userId, UserRole.PLAYER, "Перегляд статистики доступний лише авторизованим користувачам");

        return wordStatsRepository.findByWordId(wordId)
            .map(this::mapToDTO);
    }

    @Override
    public void incrementStats(Long wordId, boolean isCorrect, Long userId) {
        requireUserRole(userId, UserRole.PLAYER, "Оновлення статистики доступне лише гравцям");

        WordStats stats = wordStatsRepository.findByWordId(wordId)
            .orElseGet(() -> {
                WordStats newStats = new WordStats();
                newStats.setWordId(wordId);
                return newStats;
            });

        stats.setTotalCount(stats.getTotalCount() + 1);
        if (isCorrect) {
            stats.setCorrectCount(stats.getCorrectCount() + 1);
        }

        wordStatsRepository.save(stats);
    }

    @Override
    public List<WordStatsDTO> getWordStatsByUserId(Long currentUserId) {
        return List.of();
    }

    private void requireUserRole(Long userId, UserRole minimalRole, String errorMessage) {
        userRepository.findById(userId)
            .filter(user -> user.hasPermission(minimalRole))
            .orElseThrow(() -> new ForbiddenOperationException(errorMessage));
    }

    private WordStats mapToEntity(WordStatsDTO dto) {
        WordStats stats = new WordStats();
        stats.setId(dto.id());
        stats.setWordId(dto.wordId());
        stats.setCorrectCount(dto.correctCount());
        stats.setTotalCount(dto.totalCount());
        return stats;
    }

    private WordStatsDTO mapToDTO(WordStats entity) {
        return new WordStatsDTO(
            entity.getId(),
            entity.getWordId(),
            entity.getCorrectCount(),
            entity.getTotalCount()
        );
    }
}