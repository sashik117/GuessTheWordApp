package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.application.contract.WordStatsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    // FXML elements
    @FXML private Label wordLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label hintLabel1;
    @FXML private Label hintLabel2;
    @FXML private Label hintLabel3;
    @FXML private TextField guessField;
    @FXML private Button submitButton;
    @FXML private Button hintButton;
    @FXML private VBox lettersContainer;
    @FXML private ProgressBar progressBar;
    @FXML private Button fullscreenButton;
    @FXML private VBox hintBox;

    // Game data
    private final List<GameWord> wordList = Arrays.asList(
        new GameWord(1L, "яблуко", "це їжа", "солодкий червоний або жовтий фрукт"),
        new GameWord(2L, "молоко", "це напій", "білий поживний напій"),
        new GameWord(3L, "стілець", "це предмет", "пристосування для сидіння"),
        new GameWord(4L, "банан", "це їжа", "довгий жовтий фрукт"),
        new GameWord(5L, "вікно", "це предмет", "відкритий отвір у стіні зі склом"),
        new GameWord(6L, "сік", "це напій", "свіжовичавлений фруктовий сік"),
        new GameWord(7L, "місто", "це місце", "велике скупчення людей"),
        new GameWord(8L, "рюкзак", "це предмет", "сумка, яку носять на спині")
    );

    private GameWord currentGameWord;
    private char[] displayedWord;
    private int attemptsLeft;
    private final Set<Character> guessedLetters = new HashSet<>();
    private int hintsUsed;
    private final String[] hintLevels = new String[3];
    private final WordStatsService statsService;
    private static final Long currentUserId = 1L;

    public GameController(WordStatsService statsService) {
        this.statsService = Objects.requireNonNull(statsService, "WordStatsService cannot be null");
    }

    @FXML
    public void initialize() {
        try {
            if (lettersContainer == null) {
                throw new IllegalStateException("lettersContainer is not injected properly");
            }

            setupUkrainianKeyboard();
            startNewGame();

            if (fullscreenButton != null) {
                fullscreenButton.setOnAction(e -> toggleFullscreen());
            }

            if (guessField != null) {
                guessField.setOnAction(e -> handleGuess());
            }

            if (submitButton != null) {
                submitButton.setOnAction(e -> handleGuess());
            }

            if (hintButton != null) {
                hintButton.setOnAction(e -> showHint());
            }
        } catch (Exception e) {
            logger.error("Initialization error", e);
            showAlert("Помилка ініціалізації", "Не вдалося завантажити гру: " + e.getMessage());
        }
    }

    private void toggleFullscreen() {
        try {
            Stage stage = (Stage) fullscreenButton.getScene().getWindow();
            boolean isFullscreen = !stage.isFullScreen();
            stage.setFullScreen(isFullscreen);

            if (wordLabel != null) {
                wordLabel.setStyle(isFullscreen ? "-fx-font-size: 48px;" : "-fx-font-size: 42px;");
            }

            if (lettersContainer != null) {
                lettersContainer.setStyle(isFullscreen ? "-fx-spacing: 10px;" : "-fx-spacing: 8px;");
            }
        } catch (Exception e) {
            logger.error("Fullscreen toggle error", e);
        }
    }

    @FXML
    private void returnToMainMenu() {
        try {
            MainApp.getInstance().showMainMenuScene();
        } catch (Exception e) {
            logger.error("Error returning to main menu", e);
            showAlert("Помилка", "Не вдалося повернутися до головного меню");
        }
    }

    private void startNewGame() {
        try {
            Random random = new Random();
            currentGameWord = wordList.get(random.nextInt(wordList.size()));
            String currentWord = currentGameWord.getWord();

            displayedWord = new char[currentWord.length()];
            Arrays.fill(displayedWord, '_');

            // Initialize labels
            setLabelText(hintLabel1, currentGameWord.getHint1());
            setLabelText(hintLabel2, currentGameWord.getHint2());
            setLabelText(hintLabel3, "Підказка: ");

            updateWordDisplay();

            attemptsLeft = 10;
            setLabelText(attemptsLabel, "Спроби: " + attemptsLeft + "/10");

            if (progressBar != null) {
                progressBar.setProgress(1.0);
            }

            guessedLetters.clear();
            hintsUsed = 0;

            hintLevels[0] = currentGameWord.getHint1();
            hintLevels[1] = currentGameWord.getHint2();
            hintLevels[2] = "Перша літера: " + currentWord.charAt(0);

            if (hintBox != null) {
                hintBox.getChildren().clear();
            }

            if (hintButton != null) {
                hintButton.setDisable(false);
            }

            enableKeyboard();
        } catch (Exception e) {
            logger.error("Error starting new game", e);
            showAlert("Помилка", "Не вдалося розпочати нову гру");
        }
    }

    private void setLabelText(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    @FXML
    private void handleGuess() {
        try {
            if (guessField == null) return;

            String guess = guessField.getText().toLowerCase().trim();
            guessField.clear();

            if (guess.isEmpty()) {
                showAlert("Помилка", "Будь ласка, введіть букву або слово");
                return;
            }

            boolean isCorrect;
            if (guess.length() == 1) {
                char guessedChar = guess.charAt(0);
                if (guessedLetters.contains(guessedChar)) {
                    showAlert("Увага", "Ви вже вводили цю букву");
                    return;
                }
                guessedLetters.add(guessedChar);
                isCorrect = checkLetter(guessedChar);
            } else {
                isCorrect = checkWord(guess);
            }

            updateWordStats(isCorrect);

            if (isWordGuessed()) {
                endGame(true);
            } else if (attemptsLeft <= 0) {
                endGame(false);
            }
        } catch (Exception e) {
            logger.error("Error handling guess", e);
        }
    }

    @FXML
    private void handleLetterGuess(ActionEvent event) {
        try {
            Button letterButton = (Button) event.getSource();
            String letterStr = letterButton.getText().toLowerCase();
            char letter = letterStr.charAt(0);

            if (guessedLetters.contains(letter)) return;

            guessedLetters.add(letter);
            boolean isCorrect = checkLetter(letter);
            updateWordStats(isCorrect);

            letterButton.setDisable(true);
            letterButton.setStyle(isCorrect ?
                "-fx-background-color: lightgreen; -fx-text-fill: black;" :
                "-fx-background-color: #FF8888; -fx-text-fill: black;");

            if (isWordGuessed()) {
                endGame(true);
            } else if (attemptsLeft <= 0) {
                endGame(false);
            }
        } catch (Exception e) {
            logger.error("Error handling letter guess", e);
        }
    }

    private boolean checkLetter(char letter) {
        boolean found = false;
        String currentWord = currentGameWord.getWord();

        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
        }

        updateWordDisplay();

        if (!found) {
            attemptsLeft--;
            updateAttempts();
        }
        return found;
    }

    private boolean checkWord(String word) {
        String currentWord = currentGameWord.getWord();
        if (word.equals(currentWord)) {
            for (int i = 0; i < currentWord.length(); i++) {
                displayedWord[i] = currentWord.charAt(i);
            }
            updateWordDisplay();
            return true;
        } else {
            attemptsLeft--;
            updateAttempts();
            showAlert("Невірно", "Це не те слово. Спробуйте ще!");
            return false;
        }
    }

    private void updateWordDisplay() {
        if (wordLabel == null) return;

        StringBuilder wordDisplayText = new StringBuilder();
        for (char c : displayedWord) {
            wordDisplayText.append(c == '_' ? "_ " : c + " ");
        }
        wordLabel.setText(wordDisplayText.toString().trim());

        if (hintLabel3 != null) {
            hintLabel3.setText("Підказка: вгадано літер - " + guessedLetters.size());
        }
    }

    private void updateAttempts() {
        setLabelText(attemptsLabel, "Спроби: " + attemptsLeft + "/10");

        if (progressBar != null) {
            progressBar.setProgress((double) attemptsLeft / 10);
        }
    }

    @FXML
    private void showHint() {
        try {
            if (hintBox == null || hintButton == null) return;

            if (hintsUsed < hintLevels.length) {
                Label hint = new Label(hintLevels[hintsUsed]);
                hint.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");
                hintBox.getChildren().add(hint);
                hintsUsed++;

                if (hintsUsed == hintLevels.length) {
                    hintButton.setDisable(true);
                }
            }
        } catch (Exception e) {
            logger.error("Error showing hint", e);
        }
    }

    private boolean isWordGuessed() {
        for (char c : displayedWord) {
            if (c == '_') return false;
        }
        return true;
    }

    private void endGame(boolean win) {
        try {
            statsService.incrementStats(currentGameWord.getId(), win, currentUserId);

            if (wordLabel == null) return;

            if (win) {
                wordLabel.setStyle("-fx-text-fill: #388e3c;");
                showAlert("Перемога!", "Вітаємо! Ви вгадали слово: " + currentGameWord.getWord());
            } else {
                wordLabel.setText(currentGameWord.getWord());
                wordLabel.setStyle("-fx-text-fill: #d32f2f;");
                showAlert("Гра закінчена", "На жаль, ви програли. Загадане слово: " + currentGameWord.getWord());
            }

            startNewGame();
        } catch (Exception e) {
            logger.error("Error ending game", e);
        }
    }

    private void updateWordStats(boolean isCorrect) {
        try {
            statsService.incrementStats(currentGameWord.getId(), isCorrect, currentUserId);
        } catch (Exception e) {
            logger.error("Error updating word stats", e);
        }
    }

    private void enableKeyboard() {
        if (lettersContainer == null) return;

        for (Node node : lettersContainer.getChildren()) {
            if (node instanceof HBox) {
                for (Node btnNode : ((HBox) node).getChildren()) {
                    if (btnNode instanceof Button) {
                        Button button = (Button) btnNode;
                        button.setDisable(false);
                        button.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black;");
                    }
                }
            }
        }
    }

    private void setupUkrainianKeyboard() {
        lettersContainer.getChildren().clear();
        lettersContainer.setSpacing(8);

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
                letterBtn.setPrefSize(36, 36);
                letterBtn.setStyle("-fx-font-size: 16px; -fx-background-color: #f0f0f0; -fx-text-fill: black;");
                letterBtn.setOnAction(this::handleLetterGuess);
                hbox.getChildren().add(letterBtn);
            }
            lettersContainer.getChildren().add(hbox);
        }
    }

    private void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error("Error showing alert", e);
        }
    }

    private static class GameWord {
        private final Long id;
        private final String word;
        private final String hint1;
        private final String hint2;

        public GameWord(Long id, String word, String hint1, String hint2) {
            this.id = id;
            this.word = word;
            this.hint1 = hint1;
            this.hint2 = hint2;
        }

        public Long getId() {
            return id;
        }

        public String getWord() {
            return word;
        }

        public String getHint1() {
            return hint1;
        }

        public String getHint2() {
            return hint2;
        }
    }
}