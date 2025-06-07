package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.presentation.view.viewmodels.MainMenuViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainMenuController {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);

    // UI elements
    @FXML private Label guessedLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label successRateLabel;
    @FXML private Button exitButton;
    @FXML private Button startGameButton;
    @FXML private Button settingsButton;
    @FXML private Button fullscreenButton;
    @FXML private Button refreshButton;

    private final MainMenuViewModel viewModel;

    public MainMenuController(MainMenuViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        // Bind ViewModel properties to UI
        guessedLabel.textProperty().bind(viewModel.guessedWordsProperty().asString());
        attemptsLabel.textProperty().bind(viewModel.totalAttemptsProperty().asString());
        successRateLabel.textProperty().bind(viewModel.successRateProperty());

        Long userId = MainApp.getInstance().getCurrentUserId();
        setUserId(userId); // Виклик методу setUserId
    }

    public void setUserId(Long userId) {
        this.viewModel.setUserId(userId);
        logger.debug("MainMenuController: Set userId: {} and initialized statistics", userId);
    }

    @FXML
    public void onRefreshStatistics() {
        viewModel.loadStatistics();
    }

    @FXML
    private void startGame() {
        try {
            MainApp.getInstance().showGameScene();
        } catch (Exception e) {
            logger.error("Error starting game", e);
        }
    }

    @FXML
    private void openSettings() {
        try {
            MainApp.getInstance().showSettingsScene();
        } catch (Exception e) {
            logger.error("Error opening settings", e);
        }
    }

    @FXML
    private void exitGame() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        viewModel.exitGame(stage);
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        viewModel.toggleFullscreen(stage);
    }
}