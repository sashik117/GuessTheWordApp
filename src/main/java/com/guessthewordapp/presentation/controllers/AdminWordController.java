package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.application.contract.WordService;
import com.guessthewordapp.application.contract.dto.WordDTO;
import com.guessthewordapp.presentation.view.viewmodels.AdminWordViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AdminWordController {

    private final AdminWordViewModel viewModel;
    private final ApplicationContext applicationContext; // Хоча не використовується в цьому файлі, залишаємо, якщо є залежності

    @Autowired
    public AdminWordController(WordService wordService, ApplicationContext applicationContext) {
        this.viewModel = new AdminWordViewModel(wordService);
        this.applicationContext = applicationContext;
    }

    @FXML private TableView<WordDTO> wordsTable;
    @FXML private TableColumn<WordDTO, String> textColumn;
    @FXML private TableColumn<WordDTO, String> languageColumn;
    @FXML private TableColumn<WordDTO, Integer> difficultyColumn;
    @FXML private TableColumn<WordDTO, String> descriptionColumn;

    @FXML private TextField searchField;
    @FXML private CheckBox filterToggle;

    @FXML private TextField textField;
    @FXML private TextField languageField;
    @FXML private TextField difficultyField;
    @FXML private TextField descriptionField;

    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private Button clearFormButton; // Додано FXML елемент для нової кнопки

    @FXML
    public void initialize() {
        // Розширена перевірка на null для всіх @FXML елементів
        if (wordsTable == null || textColumn == null || languageColumn == null || difficultyColumn == null || descriptionColumn == null ||
            searchField == null || filterToggle == null || textField == null || languageField == null ||
            difficultyField == null || descriptionField == null || saveButton == null || deleteButton == null || backButton == null ||
            clearFormButton == null) {
            System.err.println("ПОМИЛКА: Не всі @FXML елементи були успішно ініціалізовані в FXML файлі!");
            // Додайте тут логування, щоб точно знати, який елемент null
            if (wordsTable == null) System.err.println("wordsTable is null");
            if (textColumn == null) System.err.println("textColumn is null");
            // ... і так далі для кожного елемента, якщо це необхідно для налагодження
            return;
        }

        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        wordsTable.setItems(viewModel.wordsProperty());

        // Слухач для вибору слова в таблиці
        wordsTable.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, newVal) -> {
                viewModel.selectedWordProperty().set(newVal); // ViewModel заповнює поля
                if (newVal != null) {
                    System.out.println("Вибрано слово: " + newVal.getText() + ", ID: " + newVal.getId());
                } else {
                    System.out.println("Вибір слова знято.");
                }
            });

        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
        filterToggle.selectedProperty().bindBidirectional(viewModel.filterHardOnlyProperty());

        textField.textProperty().bindBidirectional(viewModel.textInputProperty());
        languageField.textProperty().bindBidirectional(viewModel.languageInputProperty());

        // Конвертер для поля складності
        StringConverter<Number> integerStringConverter = new StringConverter<>() {
            @Override
            public String toString(Number object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public Number fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    // Якщо порожньо, встановлюємо 1 або null залежно від твоїх вимог
                    // Для intProperty краще повертати значення за замовчуванням, якщо введення невалідне
                    showAlert("Складність не може бути порожньою. Встановлено 1."); // Повідомлення про автоматичне встановлення
                    return 1;
                }
                try {
                    int value = Integer.parseInt(string);
                    if (value >= 1 && value <= 5) {
                        return value;
                    } else {
                        showAlert("Складність має бути числом від 1 до 5. Поточне значення збережено.");
                        // Повертаємо поточне значення, щоб уникнути скидання на 0
                        return viewModel.difficultyInputProperty().get();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Складність має бути числом. Поточне значення збережено.");
                    return viewModel.difficultyInputProperty().get();
                }
            }
        };

        difficultyField.textProperty().bindBidirectional(viewModel.difficultyInputProperty(), integerStringConverter);
        descriptionField.textProperty().bindBidirectional(viewModel.descriptionInputProperty());

        // Обробник для кнопки "Зберегти" (додавання або оновлення)
        saveButton.setOnAction(e -> {
            if (validateInputs()) { // Перевірка введених даних
                viewModel.saveWord();
                showAlert("Слово успішно збережено!");
                // Після збереження (додавання або оновлення) очищаємо вибір в таблиці
                wordsTable.getSelectionModel().clearSelection();
            }
        });

        // Обробник для кнопки "Видалити"
        deleteButton.setOnAction(e -> {
            if (viewModel.selectedWordProperty().get() != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Ви впевнені, що хочете видалити це слово?", ButtonType.YES, ButtonType.NO);
                confirmation.setHeaderText(null);
                confirmation.setTitle("Підтвердження видалення");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        viewModel.deleteWord();
                        showAlert("Слово успішно видалено!");
                        // Після видалення очищаємо вибір в таблиці
                        wordsTable.getSelectionModel().clearSelection();
                    }
                });
            } else {
                showAlert("Будь ласка, оберіть слово для видалення.");
            }
        });

        // Обробник для кнопки "Вийти" (назад до налаштувань)
        backButton.setOnAction(e -> goBackToSettings());

        // Обробник для нової кнопки "Нове слово" (очищення форми)
        clearFormButton.setOnAction(e -> onClearFormClick());

        // Слухач для оновлення таблиці
        viewModel.wordsProperty().addListener((javafx.collections.ListChangeListener<WordDTO>) change -> {
            System.out.println("--- ОНОВЛЕННЯ СПИСКУ СЛІВ У ТАБЛИЦІ ---");
            System.out.println("Кількість слів: " + viewModel.wordsProperty().size());
            if (viewModel.wordsProperty().isEmpty()) {
                System.out.println("Список слів порожній.");
            } else {
                viewModel.wordsProperty().forEach(word ->
                    System.out.println("  - " + word.getText() + " (" + word.getLanguage() + ", " + word.getDifficulty() + ")"));
            }
            System.out.println("-------------------------------------");
        });

        // Початкове завантаження слів при ініціалізації контролера
        viewModel.loadWords();
    }

    // Метод для переходу назад до налаштувань
    @FXML
    private void goBackToSettings() {
        MainApp.getInstance().showSettingsScene();
    }

    // Метод для очищення полів форми та зняття виділення з таблиці
    @FXML
    private void onClearFormClick() {
        wordsTable.getSelectionModel().clearSelection(); // Зняти вибір з таблиці
        viewModel.clearForm(); // Очистити поля введення через ViewModel
        showAlert("Форма очищена. Тепер ви можете додати нове слово.");
    }

    // Допоміжний метод для відображення сповіщень
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Повідомлення");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для валідації введених даних перед збереженням
    private boolean validateInputs() {
        if (textField.getText().trim().isEmpty()) {
            showAlert("Поле 'Слово' не може бути порожнім.");
            return false;
        }
        if (languageField.getText().trim().isEmpty()) {
            showAlert("Поле 'Мова' не може бути порожньою.");
            return false;
        }
        try {
            Integer difficulty = viewModel.difficultyInputProperty().get();
            if (difficulty == null || difficulty < 1 || difficulty > 5) {
                showAlert("Складність має бути числом від 1 до 5.");
                return false;
            }
        } catch (Exception e) { // Ловимо загальне виключення на випадок, якщо get() поверне щось несподіване
            showAlert("Складність має бути числом.");
            return false;
        }
        return true;
    }
}