package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.WordStatsService;
import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.domain.exception.ForbiddenOperationException;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WordStatsServiceImpl implements WordStatsService {
    private static final Logger logger = LoggerFactory.getLogger(WordStatsServiceImpl.class);

    private final WordStatsRepository wordStatsRepository;
    private final UserRepository userRepository;

    // Константа для загальної статистики
    private static final Long OVERALL_STATS_WORD_ID = 0L;

    public WordStatsServiceImpl(WordStatsRepository wordStatsRepository, UserRepository userRepository) {
        this.wordStatsRepository = wordStatsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WordStatsDTO saveWordStats(WordStatsDTO wordStatsDTO, Long userId) {
        logger.debug("Saving word stats for user: {}, wordId: {}", userId, wordStatsDTO.wordId());
        requireUserRole(userId, UserRole.EDITOR, "Збереження статистики доступне лише редакторам та адміністраторам");

        WordStats stats = mapToEntity(wordStatsDTO);
        stats.setUserId(userId);
        WordStats savedStats = wordStatsRepository.save(stats);
        return mapToDTO(savedStats);
    }

    @Override
    public Optional<WordStatsDTO> getWordStatsByWordId(Long wordId, Long userId) {
        logger.debug("Fetching word stats for wordId: {}, userId: {}", wordId, userId);
        requireUserRole(userId, UserRole.PLAYER, "Перегляд статистики доступний лише авторизованим користувачам");

        return wordStatsRepository.findByWordIdAndUserId(wordId, userId)
            .map(this::mapToDTO);
    }

    @Override
    public void updateStats(Long userId, boolean correct) {
        logger.debug("Updating stats for userId: {}, correct: {}", userId, correct);
        try {
            requireUserRole(userId, UserRole.PLAYER, "Оновлення статистики доступне лише гравцям");

            Optional<WordStats> existingStats = wordStatsRepository.findByWordIdAndUserId(
                OVERALL_STATS_WORD_ID, userId);

            WordStats statsToUpdate;
            if (existingStats.isPresent()) {
                statsToUpdate = existingStats.get();
                logger.debug("Found existing stats for userId: {}", userId);
            } else {
                statsToUpdate = new WordStats();
                statsToUpdate.setWordId(OVERALL_STATS_WORD_ID);
                statsToUpdate.setUserId(userId);
                statsToUpdate.setCorrectCount(0);
                statsToUpdate.setTotalCount(0);
                logger.debug("Creating new stats for userId: {}", userId);
            }

            statsToUpdate.setTotalCount(statsToUpdate.getTotalCount() + 1);
            if (correct) {
                statsToUpdate.setCorrectCount(statsToUpdate.getCorrectCount() + 1);
            }

            wordStatsRepository.save(statsToUpdate);
            logger.info("Updated stats for userId: {} - correct: {}, total: {}",
                userId, statsToUpdate.getCorrectCount(), statsToUpdate.getTotalCount());

        } catch (ForbiddenOperationException e) {
            logger.warn("User {} doesn't have permission to update stats: {}", userId, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating stats for userId: {}", userId, e);
        }
    }

    @Override
    public void updateOverallGameStats(Long userId, boolean isGameWon) {
        logger.debug("Updating overall game stats for userId: {}, isGameWon: {}", userId, isGameWon);
        try {
            if (userId == null) {
                logger.error("User ID is null in updateOverallGameStats");
                return;
            }

            requireUserRole(userId, UserRole.PLAYER, "Оновлення загальної статистики доступне лише гравцям");

            Optional<WordStats> overallStatsEntry = wordStatsRepository.findByWordIdAndUserId(
                OVERALL_STATS_WORD_ID, userId);

            WordStats statsToUpdate;
            if (overallStatsEntry.isPresent()) {
                statsToUpdate = overallStatsEntry.get();
                logger.debug("Found existing overall stats for userId: {}", userId);
            } else {
                statsToUpdate = new WordStats();
                statsToUpdate.setWordId(OVERALL_STATS_WORD_ID);
                statsToUpdate.setUserId(userId);
                statsToUpdate.setCorrectCount(0);
                statsToUpdate.setTotalCount(0);
                logger.debug("Creating new overall stats for userId: {}", userId);
            }

            statsToUpdate.setTotalCount(statsToUpdate.getTotalCount() + 1);
            if (isGameWon) {
                statsToUpdate.setCorrectCount(statsToUpdate.getCorrectCount() + 1);
            }

            wordStatsRepository.save(statsToUpdate);
            logger.info("Saved overall game stats for userId: {} - correct games: {}, total games: {}",
                userId, statsToUpdate.getCorrectCount(), statsToUpdate.getTotalCount());

        } catch (ForbiddenOperationException e) {
            logger.error("User {} doesn't have permission to update overall stats: {}", userId, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating overall game stats for userId: {}", userId, e);
        }
    }

    @Override
    public List<WordStatsDTO> getWordStatsByUserId(Long userId) {
        logger.debug("Fetching overall stats for userId: {}", userId);
        if (userId == null) {
            logger.error("User ID is null in getWordStatsByUserId");
            return List.of();
        }

        try {
            requireUserRole(userId, UserRole.PLAYER, "Перегляд статистики доступний лише авторизованим користувачам");
        } catch (ForbiddenOperationException e) {
            logger.warn("User {} doesn't have permission to view stats: {}", userId, e.getMessage());
            return List.of();
        }

        return wordStatsRepository.findByUserId(userId).stream()
            .map(this::mapToDTO)
            .toList();
    }

    private void requireUserRole(Long userId, UserRole minimalRole, String errorMessage) {
        logger.debug("Checking user role for userId: {}, minimalRole: {}", userId, minimalRole);
        // Використовуємо orElseThrow без лямбди, щоб уникнути проблем з проксі
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found: {}", userId);
                return new ForbiddenOperationException("Користувач не знайдений");
            });

        if (!user.hasPermission(minimalRole)) {
            logger.warn("User {} doesn't have required role: {}", userId, minimalRole);
            throw new ForbiddenOperationException(errorMessage);
        }
    }

    private WordStats mapToEntity(WordStatsDTO dto) {
        WordStats stats = new WordStats();
        stats.setId(dto.id());
        stats.setWordId(dto.wordId());
        stats.setUserId(dto.userId()); // Тепер коректно, оскільки userId додано до WordStatsDTO
        stats.setCorrectCount(dto.correctCount());
        stats.setTotalCount(dto.totalCount());
        return stats;
    }

    private WordStatsDTO mapToDTO(WordStats entity) {
        return new WordStatsDTO(
            entity.getId(),
            entity.getWordId(),
            entity.getUserId(), // Тепер коректно, оскільки userId додано до конструктора
            entity.getCorrectCount(),
            entity.getTotalCount()
        );
    }
}