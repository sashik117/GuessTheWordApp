package com.guessthewordapp.presentation.view.viewmodels;

import com.guessthewordapp.application.contract.WordStatsService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameViewModel {

    private static final Logger logger = LoggerFactory.getLogger(GameViewModel.class);

    private final StringProperty wordDisplayProperty = new SimpleStringProperty();
    private final StringProperty attemptsTextProperty = new SimpleStringProperty();
    private final DoubleProperty progressBarProperty = new SimpleDoubleProperty();
    private final BooleanProperty hintButtonDisabledProperty = new SimpleBooleanProperty(false);
    private final ObservableList<String> hintsList = FXCollections.observableArrayList();
    private final BooleanProperty gameWonProperty = new SimpleBooleanProperty(false);

    private final ObservableMap<Character, CharacterState> keyboardLetterStates = FXCollections.observableHashMap();

    private final List<GameWord> wordList = Arrays.asList(
        new GameWord(1L, "яблуко", "це їжа", "солодкий червоний або жовтий фрукт"),
        new GameWord(2L, "молоко", "це напій", "білий поживний напій"),
        new GameWord(3L, "стілець", "це предмет", "пристосування для сидіння"),
        new GameWord(4L, "банан", "це їжа", "довгий жовтий фрукт"),
        new GameWord(5L, "вікно", "це предмет", "відкритий отвір у стіні зі склом"),
        new GameWord(6L, "сік", "це напій", "свіжовичавлений фруктовий сік"),
        new GameWord(7L, "місто", "це місце", "велике скупчення людей"),
        new GameWord(8L, "рюкзак", "це предмет", "сумка, яку носять на спині"),
        new GameWord(9L, "літак", "це транспорт", "машина, що літає в небі"),
        new GameWord(10L, "сонце", "це небесне тіло", "зірка, що освітлює Землю"),
        new GameWord(11L, "кіт", "це тварина", "маленький пухнастий улюбленець"),
        new GameWord(12L, "собака", "це тварина", "вірний друг людини"),
        new GameWord(13L, "дерево", "це природа", "має стовбур і листя"),
        new GameWord(14L, "книга", "це предмет", "читання на паперових сторінках"),
        new GameWord(15L, "телефон", "це предмет", "використовується для дзвінків"),
        new GameWord(16L, "машина", "це транспорт", "перевозить людей і вантажі"),
        new GameWord(17L, "озеро", "це природа", "велике водоймище прісної води"),
        new GameWord(18L, "гора", "це природа", "високе підняття землі"),
        new GameWord(19L, "комп'ютер", "це технологія", "електронний пристрій для роботи"),
        new GameWord(20L, "кава", "це напій", "міцний чорний напій"),
        new GameWord(21L, "чай", "це напій", "гарячий напій з листя"),
        new GameWord(22L, "поїзд", "це транспорт", "рухається по рейках"),
        new GameWord(23L, "парасолька", "це предмет", "захищає від дощу")
    );

    private final Set<Long> usedWordIds = new HashSet<>();
    private GameWord currentGameWord;
    private char[] displayedWord;
    private int attemptsLeft;
    private final Set<Character> guessedLetters = new HashSet<>();
    private int hintsUsed;
    private final String[] hintLevels = new String[2];
    private Long userId;
    private final WordStatsService statsService;

    public GameViewModel(WordStatsService statsService) {
        this.statsService = Objects.requireNonNull(statsService, "WordStatsService cannot be null");
        this.userId = null;
        logger.info("Initializing GameViewModel");
        initializeKeyboardStates();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        logger.info("GameViewModel: Set userId: {}", userId);
        initializeGame();
    }

    public void initializeGame() {
        startNewGame();
    }

    private void initializeKeyboardStates() {
        String ukrainianAlphabet = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
        for (char c : ukrainianAlphabet.toCharArray()) {
            keyboardLetterStates.put(Character.toLowerCase(c), CharacterState.DEFAULT);
        }
    }

    public void startNewGame() {
        try {
            List<GameWord> availableWords = new ArrayList<>();
            for (GameWord word : wordList) {
                if (!usedWordIds.contains(word.getId())) {
                    availableWords.add(word);
                }
            }
            if (availableWords.isEmpty()) {
                usedWordIds.clear();
                availableWords.addAll(wordList);
            }

            Random random = new Random();
            currentGameWord = availableWords.get(random.nextInt(availableWords.size()));
            usedWordIds.add(currentGameWord.getId());
            String currentWord = currentGameWord.getWord();

            displayedWord = new char[currentWord.length()];
            Arrays.fill(displayedWord, '_');

            updateWordDisplay();

            attemptsLeft = 10;
            updateAttemptsText();
            progressBarProperty.set(1.0);

            guessedLetters.clear();
            hintsUsed = 0;

            hintLevels[0] = currentGameWord.getHint1();
            hintLevels[1] = currentGameWord.getHint2();

            hintsList.clear();
            hintButtonDisabledProperty.set(false);
            gameWonProperty.set(false);

            resetKeyboardStates();
            logger.debug("New game started with word: {}", currentWord);

        } catch (Exception e) {
            logger.error("Error starting new game", e);
        }
    }

    public void resetKeyboardStates() {
        for (Character key : new ArrayList<>(keyboardLetterStates.keySet())) {
            keyboardLetterStates.put(key, CharacterState.DEFAULT);
        }
    }

    public boolean processGuess(String guess) {
        if (guess.isEmpty()) {
            return false;
        }

        boolean isCorrect;
        if (guess.length() == 1) {
            char guessedChar = guess.toLowerCase().charAt(0);
            if (guessedLetters.contains(guessedChar)) {
                return false;
            }
            isCorrect = checkLetter(guessedChar);
        } else {
            isCorrect = checkWord(guess);
        }

        return isCorrect;
    }

    public boolean processLetterGuess(char letter) {
        char lowerCaseLetter = Character.toLowerCase(letter);
        if (guessedLetters.contains(lowerCaseLetter)) {
            return false;
        }

        guessedLetters.add(lowerCaseLetter);
        boolean isCorrect = checkLetter(lowerCaseLetter);

        return isCorrect;
    }

    private boolean checkLetter(char letter) {
        boolean found = false;
        String currentWord = currentGameWord.getWord();

        for (int i = 0; i < currentWord.length(); i++) {
            if (Character.toLowerCase(currentWord.charAt(i)) == letter) {
                displayedWord[i] = currentWord.charAt(i);
                found = true;
            }
        }
        updateWordDisplay();

        if (found) {
            updateLetterState(letter, CharacterState.CORRECT);
        } else {
            attemptsLeft--;
            updateAttemptsText();
            updateLetterState(letter, CharacterState.INCORRECT);
        }

        if (isWordGuessed()) {
            logger.debug("Word guessed correctly: {}", currentWord);
            endGame(true);
        } else if (attemptsLeft <= 0) {
            logger.debug("No attempts left, game over. Word was: {}", currentWord);
            endGame(false);
        }

        return found;
    }

    private boolean checkWord(String word) {
        String currentWord = currentGameWord.getWord();
        boolean isCorrect = word.equalsIgnoreCase(currentWord);

        if (isCorrect) {
            for (int i = 0; i < currentWord.length(); i++) {
                displayedWord[i] = currentWord.charAt(i);
            }
            updateWordDisplay();
            logger.debug("Word guessed correctly via full word input: {}", word);
        } else {
            attemptsLeft--;
            updateAttemptsText();
            logger.debug("Incorrect word guess: {}. Attempts left: {}", word, attemptsLeft);
        }

        if (isCorrect) {
            endGame(true);
        } else if (attemptsLeft <= 0) {
            logger.debug("No attempts left after incorrect word guess. Word was: {}", currentWord);
            endGame(false);
        }
        return isCorrect;
    }

    private void updateStats(boolean correct) {
        // Цей метод більше не викликається для літер, лише для endGame
    }

    private void updateWordDisplay() {
        StringBuilder wordDisplayText = new StringBuilder();
        for (char c : displayedWord) {
            wordDisplayText.append(c == '_' ? "_ " : c + " ");
        }
        wordDisplayProperty.set(wordDisplayText.toString().trim());
    }

    private void updateAttemptsText() {
        attemptsTextProperty.set("Спроби: " + attemptsLeft + "/10");
        progressBarProperty.set((double) attemptsLeft / 10);
    }

    public void showHint() {
        if (hintsUsed < 3) {
            if (hintsUsed < 2) {
                hintsList.add(hintLevels[hintsUsed]);
                logger.debug("Hint shown: {}", hintLevels[hintsUsed]);
            } else {
                String hintResult = revealRandomLetters();
                hintsList.add("Додаткова підказка: " + hintResult);
                logger.debug("Additional hint shown: {}", hintResult);
            }
            hintsUsed++;
            if (hintsUsed >= 3) {
                hintButtonDisabledProperty.set(true);
                logger.debug("Hint button disabled after 3 hints");
            }
        }
    }

    private String revealRandomLetters() {
        String currentWord = currentGameWord.getWord();
        List<Integer> closedIndices = new ArrayList<>();

        for (int i = 0; i < currentWord.length(); i++) {
            if (displayedWord[i] == '_') {
                closedIndices.add(i);
            }
        }

        if (closedIndices.isEmpty()) {
            return "Всі літери вже відкриті!";
        }

        int lettersToReveal = Math.min(2, closedIndices.size());
        Collections.shuffle(closedIndices);
        List<Integer> indicesToReveal = closedIndices.subList(0, lettersToReveal);

        List<Character> revealedLetters = new ArrayList<>();
        for (int index : indicesToReveal) {
            char letter = currentWord.charAt(index);
            displayedWord[index] = letter;
            revealedLetters.add(letter);
        }
        updateWordDisplay();

        Collections.sort(revealedLetters);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < revealedLetters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(revealedLetters.get(i));
        }

        return "відкриті літери: " + sb.toString();
    }

    public boolean isWordGuessed() {
        for (char c : displayedWord) {
            if (c == '_') return false;
        }
        return true;
    }

    public void endGame(boolean win) {
        logger.debug("Ending game: win={}", win);
        if (userId != null) {
            statsService.updateOverallGameStats(userId, win);
            logger.debug("Updated overall game stats for user: {}, win: {}", userId, win);
        } else {
            logger.warn("No user ID, overall stats not updated");
        }
        gameWonProperty.set(win);
    }

    public StringProperty wordDisplayProperty() { return wordDisplayProperty; }
    public StringProperty attemptsTextProperty() { return attemptsTextProperty; }
    public DoubleProperty progressBarProperty() { return progressBarProperty; }
    public BooleanProperty hintButtonDisabledProperty() { return hintButtonDisabledProperty; }
    public ObservableList<String> hintsListProperty() { return hintsList; }
    public ObservableMap<Character, CharacterState> getKeyboardLetterStates() { return keyboardLetterStates; }
    public BooleanProperty gameWonProperty() { return gameWonProperty; }
    public int getAttemptsLeft() { return attemptsLeft; }
    public String getCurrentWord() { return currentGameWord != null ? currentGameWord.getWord() : ""; }

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

        public Long getId() { return id; }
        public String getWord() { return word; }
        public String getHint1() { return hint1; }
        public String getHint2() { return hint2; }
    }

    public enum CharacterState {
        DEFAULT, CORRECT, INCORRECT
    }

    private void updateLetterState(char letter, CharacterState newState) {
        keyboardLetterStates.put(Character.toLowerCase(letter), newState);
    }
}