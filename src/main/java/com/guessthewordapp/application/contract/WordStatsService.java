package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import java.util.List;
import java.util.Optional;

public interface WordStatsService {
    WordStatsDTO saveWordStats(WordStatsDTO wordStatsDTO, Long userId);
    Optional<WordStatsDTO> getWordStatsByWordId(Long wordId, Long userId);
    void incrementStats(Long wordId, boolean isCorrect, Long userId);

    List<WordStatsDTO> getWordStatsByUserId(Long currentUserId);
}