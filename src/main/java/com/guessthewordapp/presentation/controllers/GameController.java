package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.presentation.view.viewmodels.GameViewModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @FXML private Label wordLabel;
    @FXML private Label attemptsLabel;
    @FXML private TextField guessField;
    @FXML private Button submitButton;
    @FXML private Button hintButton;
    @FXML private VBox lettersContainer;
    @FXML private ProgressBar progressBar;
    @FXML private Button fullscreenButton;
    @FXML private VBox hintBox;
    @FXML private Label hintsTitleLabel;

    private final GameViewModel gameViewModel;
    private final Map<Character, Button> keyboardButtons = new HashMap<>();
    private Stage primaryStage;
    private Long userId;
    private boolean gameResultShown = false;

    public GameController(GameViewModel gameViewModel) {
        this.gameViewModel = Objects.requireNonNull(gameViewModel, "GameViewModel cannot be null");
    }

    public void initialize(Long userId) {
        this.userId = userId;
        gameViewModel.setUserId(userId);
        logger.debug("GameController initialized with userId: {}", userId);
        try {
            wordLabel.textProperty().bind(gameViewModel.wordDisplayProperty());
            attemptsLabel.textProperty().bind(gameViewModel.attemptsTextProperty());
            progressBar.progressProperty().bind(gameViewModel.progressBarProperty());
            hintButton.disableProperty().bind(gameViewModel.hintButtonDisabledProperty());

            gameViewModel.hintsListProperty().addListener((ListChangeListener<String>) c -> {
                if (hintBox != null) {
                    hintBox.getChildren().clear();
                    for (String hintText : gameViewModel.hintsListProperty()) {
                        Label hint = new Label(hintText);
                        hint.getStyleClass().add("hint-text");
                        hintBox.getChildren().add(hint);
                    }
                }
            });

            setupUkrainianKeyboard();

            gameViewModel.getKeyboardLetterStates().addListener((MapChangeListener<Character, GameViewModel.CharacterState>) change -> {
                if (change.getValueAdded() != null) {
                    updateKeyboardButtonUI(change.getKey(), change.getValueAdded());
                }
            });

            gameViewModel.initializeGame();

            if (fullscreenButton != null) fullscreenButton.setOnAction(e -> toggleFullscreen());
            if (guessField != null) guessField.setOnAction(e -> handleGuess());
            if (submitButton != null) submitButton.setOnAction(e -> handleGuess());
            if (hintButton != null) hintButton.setOnAction(e -> showHint());
            if (attemptsLabel != null) attemptsLabel.getStyleClass().add("bold-text");
            if (hintsTitleLabel != null) hintsTitleLabel.getStyleClass().add("bold-text");

            // Додаємо періодичну перевірку стану гри
            new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (gameViewModel.getAttemptsLeft() <= 0 && !gameResultShown) {
                        Platform.runLater(() -> {
                            logger.debug("Game lost detected via periodic check");
                            showGameResult(false);
                        });
                    }
                }
            }, 0, 100); // Перевіряємо кожні 100 мс

            gameViewModel.gameWonProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && !gameResultShown) {
                    logger.debug("Game won detected via gameWonProperty listener");
                    showGameResult(true);
                }
            });

        } catch (Exception e) {
            logger.error("Initialization error", e);
            showAlert("Помилка ініціалізації", "Не вдалося завантажити гру: " + e.getMessage());
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void toggleFullscreen() {
        try {
            Stage stage = (Stage) fullscreenButton.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        } catch (Exception e) {
            logger.error("Fullscreen toggle error", e);
        }
    }

    @FXML
    private void returnToMainMenu() {
        try {
            logger.debug("Returning to main menu");
            MainApp mainApp = MainApp.getInstance();
            mainApp.showMainMenuScene();
        } catch (Exception e) {
            logger.error("Error returning to main menu", e);
            showAlert("Помилка", "Не вдалося повернутися до головного меню");
        }
    }

    @FXML
    private void handleGuess() {
        if (guessField == null || gameViewModel.getAttemptsLeft() <= 0) return;

        String guess = guessField.getText().trim();
        if (guess.isEmpty()) {
            showAlert("Помилка", "Будь ласка, введіть букву або слово");
            return;
        }

        logger.debug("Processing guess: {}", guess);
        boolean processed = gameViewModel.processGuess(guess.toLowerCase());
        guessField.clear();

        if (!processed && guess.length() == 1) {
            showAlert("Увага", "Ви вже вводили цю букву або вона неправильна");
        }
    }

    @FXML
    private void handleLetterGuess(ActionEvent event) {
        if (gameViewModel.getAttemptsLeft() <= 0) return;

        Button letterButton = (Button) event.getSource();
        String letterStr = letterButton.getText().toLowerCase();
        char letter = letterStr.charAt(0);
        logger.debug("Processing letter guess: {}", letter);
        gameViewModel.processLetterGuess(letter);
    }

    @FXML
    private void showHint() {
        if (gameViewModel.getAttemptsLeft() <= 0) return;
        logger.debug("Showing hint");
        gameViewModel.showHint();
    }

    private void setupUkrainianKeyboard() {
        if (lettersContainer == null) return;

        lettersContainer.getChildren().clear();
        lettersContainer.setSpacing(8);
        keyboardButtons.clear();

        String[] rows = {
            "А Б В Г Ґ Д Е Є Ж З",
            "И І Ї Й К Л М Н О П",
            "Р С Т У Ф Х Ц Ч Ш Щ",
            "Ь Ю Я"
        };

        for (String row : rows) {
            HBox hbox = new HBox();
            hbox.setSpacing(6);
            hbox.setAlignment(Pos.CENTER);
            for (char c : row.replace(" ", "").toCharArray()) {
                Button letterBtn = new Button(String.valueOf(c).toUpperCase());
                letterBtn.setPrefSize(42, 42);
                letterBtn.setAlignment(Pos.CENTER);
                letterBtn.getStyleClass().add("keyboard-button");
                letterBtn.setOnAction(this::handleLetterGuess);
                hbox.getChildren().add(letterBtn);
                keyboardButtons.put(Character.toLowerCase(c), letterBtn);
            }
            lettersContainer.getChildren().add(hbox);
        }
    }

    private void updateKeyboardButtonUI(char letter, GameViewModel.CharacterState state) {
        Button button = keyboardButtons.get(letter);
        if (button != null) {
            button.getStyleClass().removeAll("keyboard-button-correct", "keyboard-button-incorrect");
            button.setDisable(false);

            switch (state) {
                case CORRECT:
                    button.getStyleClass().add("keyboard-button-correct");
                    button.setDisable(true);
                    break;
                case INCORRECT:
                    button.getStyleClass().add("keyboard-button-incorrect");
                    button.setDisable(true);
                    break;
                case DEFAULT:
                    break;
            }
        }
    }

    private void showGameResult(boolean win) {
        if (gameResultShown) return;
        logger.debug("Showing game result: win={}", win);
        gameResultShown = true;
        String message;
        if (win) {
            message = "Вітаємо! Ви вгадали слово: " + gameViewModel.getCurrentWord();
        } else {
            message = "На жаль, ви не вгадали. Слово було: " + gameViewModel.getCurrentWord();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Результат гри");
        alert.setHeaderText(null);
        alert.setContentText(message + "\nБажаєте почати нову гру?");
        ButtonType yesButton = new ButtonType("Так", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Ні", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }

        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                gameViewModel.startNewGame();
                gameResultShown = false;
            } else {
                MainApp mainApp = MainApp.getInstance();
                mainApp.showMainMenuScene();
            }
        });

        MainApp.getInstance().refreshMainMenuStatistics();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }

        alert.showAndWait();
    }
}