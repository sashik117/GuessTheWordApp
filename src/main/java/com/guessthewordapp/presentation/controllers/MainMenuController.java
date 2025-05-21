package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.application.contract.WordStatsService;
import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import com.guessthewordapp.config.SpringContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MainMenuController {

    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);

    @FXML private Label guessedLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label successRateLabel;

    @FXML private Button exitButton;
    @FXML private Button startGameButton;
    @FXML private Button settingsButton;
    @FXML private Button fullscreenButton;
    @FXML private Button leftArrowButton;
    @FXML private Button rightArrowButton;

    private final WordStatsService wordStatsService;
    private Long currentUserId; // Заміни на ID реального користувача

    // Індекс сторінки статистики (0 - загальна, 1 - наприклад за останній місяць)
    private int statsPage = 0;

    public MainMenuController(WordStatsService wordStatsService) {
        this.wordStatsService = wordStatsService;
        this.currentUserId = 1L; // Приклад, треба отримувати реальний ID
    }

    @FXML
    public void initialize() {
        updateStatsForPage(statsPage);
    }

    @FXML
    private void onLeftArrow() {
        if (statsPage > 0) {
            statsPage--;
        } else {
            statsPage = 1;
        }
        updateStatsForPage(statsPage);
    }

    @FXML
    private void onRightArrow() {
        if (statsPage < 1) {
            statsPage++;
        } else {
            statsPage = 0;
        }
        updateStatsForPage(statsPage);
    }

    private void updateStatsForPage(int page) {
        try {
            List<WordStatsDTO> stats;
            if (page == 0) {
                // Загальна статистика
                stats = wordStatsService.getWordStatsByUserId(currentUserId);
            } else {
                // Імітація іншої статистики — наприклад, за останній місяць
                // Якщо немає методу, просто візьми ту ж статистику
                stats = wordStatsService.getWordStatsByUserId(currentUserId);
            }

            int totalCorrect = stats.stream()
                .mapToInt(WordStatsDTO::correctCount)
                .sum();

            int totalAttempts = stats.stream()
                .mapToInt(WordStatsDTO::totalCount)
                .sum();

            double successRate = totalAttempts > 0 ? (double) totalCorrect / totalAttempts * 100 : 0;

            guessedLabel.setText(String.valueOf(totalCorrect));
            attemptsLabel.setText(String.valueOf(totalAttempts));
            successRateLabel.setText(String.format("%.1f%%", successRate));
        } catch (Exception e) {
            logger.error("Помилка при оновленні статистики", e);
            showError("Помилка при завантаженні статистики", e);
            setDefaultStats();
        }
    }

    private void setDefaultStats() {
        guessedLabel.setText("0");
        attemptsLabel.setText("0");
        successRateLabel.setText("0%");
    }

    @FXML
    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) startGameButton.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (IOException e) {
            logger.error("Помилка при запуску гри", e);
            showError("Не вдалося запустити гру", e);
        }
    }

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) settingsButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setTitle("Guess the Word — Налаштування");
            stage.setFullScreen(true);
        } catch (IOException e) {
            logger.error("Помилка при відкритті налаштувань", e);
            showError("Не вдалося відкрити налаштування", e);
        }
    }

    @FXML
    private void exitGame() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
