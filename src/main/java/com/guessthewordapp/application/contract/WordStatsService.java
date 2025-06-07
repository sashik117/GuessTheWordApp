package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import java.util.List;
import java.util.Optional;

public interface WordStatsService {
    WordStatsDTO saveWordStats(WordStatsDTO wordStatsDTO, Long userId);
    Optional<WordStatsDTO> getWordStatsByWordId(Long wordId, Long userId);

    // Оновлений метод для інкременту статистики
    void updateStats(Long userId, boolean correct);

    // Видаляємо старий метод incrementStats
    // void incrementStats(Long wordId, boolean isCorrect, Long userId);

    // Оновлений метод для загальної статистики
    void updateOverallGameStats(Long userId, boolean isGameWon);

    List<WordStatsDTO> getWordStatsByUserId(Long currentUserId);
}