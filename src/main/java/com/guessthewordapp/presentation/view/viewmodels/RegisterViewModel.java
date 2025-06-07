package com.guessthewordapp.presentation.view.viewmodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegisterViewModel {

    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty emailInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty confirmPasswordInput = new SimpleStringProperty("");
    private final StringProperty errorLabelText = new SimpleStringProperty("");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    public RegisterViewModel() {
        // Конструктор без залежностей, якщо сервіс реєстрації не потрібен тут безпосередньо
        // Якщо буде сервіс реєстрації, його треба буде інжектувати
    }

    public boolean register() {
        errorLabelText.set(""); // Очищаємо помилку при кожній спробі
        if (!validateInput()) {
            return false;
        }

        // Тут мала б бути логіка виклику сервісу реєстрації
        // Наприклад: authService.registerUser(usernameInput.get(), emailInput.get(), passwordInput.get());
        // Наразі просто симулюємо успіх
        errorLabelText.set("Реєстрація пройшла успішно!");
        return true;
    }

    private boolean validateInput() {
        String username = usernameInput.get().trim();
        String email = emailInput.get().trim();
        String password = passwordInput.get();
        String confirmPassword = confirmPasswordInput.get();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabelText.set("Будь ласка, заповніть всі поля");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errorLabelText.set("Будь ласка, введіть коректну email адресу");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errorLabelText.set("Пароль має містити мінімум 8 символів (літери та цифри)");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            errorLabelText.set("Паролі не співпадають");
            return false;
        }

        return true;
    }

    // Properties for binding
    public StringProperty usernameInputProperty() { return usernameInput; }
    public StringProperty emailInputProperty() { return emailInput; }
    public StringProperty passwordInputProperty() { return passwordInput; }
    public StringProperty confirmPasswordInputProperty() { return confirmPasswordInput; }
    public StringProperty errorLabelTextProperty() { return errorLabelText; }
}