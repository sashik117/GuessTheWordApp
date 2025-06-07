package com.guessthewordapp.presentation.view.viewmodels;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.application.contract.WordStatsService;
import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import java.util.Optional;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainMenuViewModel {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuViewModel.class);

    private final WordStatsService wordStatsService;
    private Long currentUserId;

    // Properties for UI binding
    private final IntegerProperty guessedWords = new SimpleIntegerProperty(0);
    private final IntegerProperty totalAttempts = new SimpleIntegerProperty(0);
    private final StringProperty successRate = new SimpleStringProperty("0%");

    public MainMenuViewModel(WordStatsService wordStatsService) {
        this.wordStatsService = wordStatsService;
        this.currentUserId = null;
        logger.info("Initializing MainMenuViewModel");
    }

    public void setUserId(Long userId) {
        this.currentUserId = userId;
        initializeStatistics();
    }

    public void initializeStatistics() {
        if (currentUserId != null) {
            loadStatistics();
        } else {
            logger.warn("No user ID provided, resetting statistics");
            resetStatistics();
        }
    }

    public void loadStatistics() {
        try {
            logger.debug("Loading statistics for user: {}", currentUserId);
            if (currentUserId == null) {
                logger.warn("Cannot load statistics: userId is null");
                resetStatistics();
                return;
            }
            List<WordStatsDTO> stats = wordStatsService.getWordStatsByUserId(currentUserId);

            // Шукаємо запис з wordId = 0 (загальна статистика)
            Optional<WordStatsDTO> overall = stats.stream()
                .filter(s -> s.wordId() != null && s.wordId() == 0L)
                .findFirst();

            if (overall.isPresent()) {
                WordStatsDTO statsDto = overall.get();
                int totalCorrect = statsDto.correctCount();
                int totalAttempts = statsDto.totalCount();
                double rate = totalAttempts > 0 ? (double) totalCorrect / totalAttempts * 100 : 0;

                logger.info("Loaded statistics for user: {}, correct={}, total={}, rate={}%",
                    currentUserId, totalCorrect, totalAttempts, rate);

                guessedWords.set(totalCorrect);
                this.totalAttempts.set(totalAttempts);
                successRate.set(String.format("%.1f%%", rate));
            } else {
                logger.info("No overall statistics found for user: {}", currentUserId);
                resetStatistics();
            }
        } catch (Exception e) {
            logger.error("Error loading statistics for user: {}", currentUserId, e);
            resetStatistics();
        }
    }

    private void resetStatistics() {
        logger.debug("Resetting statistics for user: {}", currentUserId);
        guessedWords.set(0);
        totalAttempts.set(0);
        successRate.set("0%");
    }

    // Getters for properties
    public IntegerProperty guessedWordsProperty() { return guessedWords; }
    public IntegerProperty totalAttemptsProperty() { return totalAttempts; }
    public StringProperty successRateProperty() { return successRate; }

    // Stage-related methods
    public void exitGame(Stage stage) {
        stage.close();
    }

    public void toggleFullscreen(Stage stage) {
        stage.setFullScreen(!stage.isFullScreen());
    }
}