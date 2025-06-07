package com.guessthewordapp.presentation.view.viewmodels;

import com.guessthewordapp.application.contract.WordService;
import com.guessthewordapp.application.contract.dto.WordDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdminWordViewModel {

    private final WordService wordService;
    private final ObservableList<WordDTO> words = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final BooleanProperty filterHardOnly = new SimpleBooleanProperty(false);

    private final ObjectProperty<WordDTO> selectedWord = new SimpleObjectProperty<>();
    private final StringProperty textInput = new SimpleStringProperty("");
    private final StringProperty languageInput = new SimpleStringProperty("uk");
    private final IntegerProperty difficultyInput = new SimpleIntegerProperty(1);
    private final StringProperty descriptionInput = new SimpleStringProperty("");

    public AdminWordViewModel(WordService wordService) {
        this.wordService = wordService;
        searchQuery.addListener((obs, old, newVal) -> filterWords());
        filterHardOnly.addListener((obs, old, newVal) -> filterWords());

        selectedWord.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // *** ЗМІНЕНО: використовуємо геттери WordDTO ***
                textInput.set(newVal.getText());
                languageInput.set(newVal.getLanguage());
                difficultyInput.set(newVal.getDifficulty());
                descriptionInput.set(newVal.getDescription() != null ? newVal.getDescription() : "");
            } else {
                clearForm();
            }
        });
    }

    public void loadWords() {
        try {
            List<WordDTO> fetchedWords = wordService.getWordsByLanguage("uk");
            if (fetchedWords != null) {
                words.setAll(fetchedWords);
                System.out.println("ViewModel: Завантажено " + fetchedWords.size() + " слів.");
                // *** ЗМІНЕНО: використовуємо геттери WordDTO для логування ***
                fetchedWords.forEach(w -> System.out.println("  - ViewModel Loaded: " + w.getText() + ", Difficulty: " + w.getDifficulty()));
            } else {
                System.out.println("ViewModel: wordService.getWordsByLanguage(\"uk\") повернув null.");
            }
        } catch (Exception e) {
            System.err.println("ViewModel: Помилка при завантаженні слів: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterWords() {
        String query = searchQuery.get().toLowerCase();
        boolean hardOnly = filterHardOnly.get();

        List<WordDTO> allWords;
        try {
            allWords = wordService.getWordsByLanguage("uk");
            if (allWords == null) {
                allWords = List.of();
                System.out.println("ViewModel: filterWords: wordService.getWordsByLanguage(\"uk\") повернув null.");
            }
        } catch (Exception e) {
            System.err.println("ViewModel: Помилка при отриманні слів для фільтрації: " + e.getMessage());
            e.printStackTrace();
            allWords = List.of();
        }

        List<WordDTO> filtered = allWords.stream()
            .filter(word ->
                // *** ЗМІНЕНО: використовуємо геттери WordDTO для фільтрації ***
                word.getText().toLowerCase().contains(query) ||
                    (word.getDescription() != null && word.getDescription().toLowerCase().contains(query)))
            .filter(word -> !hardOnly || word.getDifficulty() > 2)
            .toList();

        words.setAll(filtered);
        System.out.println("ViewModel: Відфільтровано " + filtered.size() + " слів.");
    }

    public void saveWord() {
        if (textInput.get().isEmpty() || languageInput.get().isEmpty()) {
            System.err.println("ViewModel: Поля 'Слово' або 'Мова' порожні при збереженні.");
            return;
        }

        Integer difficulty = difficultyInput.get();
        if (difficulty == null) {
            difficulty = 1;
            System.out.println("ViewModel: Difficulty було null, встановлено 1.");
        }

        WordDTO wordDTO = new WordDTO(
            // *** ЗМІНЕНО: використовуємо геттер WordDTO для ID, якщо слово вибране ***
            selectedWord.get() != null ? selectedWord.get().getId() : null,
            textInput.get(),
            difficulty,
            languageInput.get(),
            descriptionInput.get()
        );

        try {
            wordService.saveWord(wordDTO);
            // *** ЗМІНЕНО: використовуємо геттер WordDTO для логування ***
            System.out.println("ViewModel: Слово збережено: " + wordDTO.getText());
            loadWords();
            clearForm();
        } catch (Exception e) {
            System.err.println("ViewModel: Помилка при збереженні слова: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteWord() {
        if (selectedWord.get() != null) {
            try {
                // *** ЗМІНЕНО: використовуємо геттер WordDTO для ID ***
                wordService.deleteWord(selectedWord.get().getId());
                // *** ЗМІНЕНО: використовуємо геттер WordDTO для логування ***
                System.out.println("ViewModel: Слово видалено: " + selectedWord.get().getText());
                loadWords();
                clearForm();
            } catch (Exception e) {
                System.err.println("ViewModel: Помилка при видаленні слова: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("ViewModel: Не обрано слово для видалення.");
        }
    }

    public void clearForm() {
        selectedWord.set(null);
        textInput.set("");
        languageInput.set("uk");
        difficultyInput.set(1);
        descriptionInput.set("");
        System.out.println("ViewModel: Форма очищена.");
    }

    public ObservableList<WordDTO> wordsProperty() { return words; }
    public StringProperty searchQueryProperty() { return searchQuery; }
    public BooleanProperty filterHardOnlyProperty() { return filterHardOnly; }
    public ObjectProperty<WordDTO> selectedWordProperty() { return selectedWord; }
    public StringProperty textInputProperty() { return textInput; }
    public StringProperty languageInputProperty() { return languageInput; }
    public IntegerProperty difficultyInputProperty() { return difficultyInput; }
    public StringProperty descriptionInputProperty() { return descriptionInput; }
}