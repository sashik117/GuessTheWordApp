package com.guessthewordapp.presentation.view.viewmodels;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.infrastructure.AuthenticationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LoginViewModel {

    private final AuthenticationService authService;

    private final StringProperty emailInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorLabelText = new SimpleStringProperty("");
    private final StringProperty errorLabelStyle = new SimpleStringProperty("");

    public LoginViewModel(AuthenticationService authService) {
        this.authService = Objects.requireNonNull(authService, "AuthenticationService cannot be null");
    }

    public boolean login() {
        String email = emailInput.get();
        String password = passwordInput.get();

        try {
            User user = authService.login(email, password);
            errorLabelStyle.set("-fx-text-fill: green;");
            errorLabelText.set("Успішний вхід! Вітаємо, " + user.getUsername());
            MainApp.getInstance().setCurrentUserId(user.getId()); // Оновлюємо userId
            return true; // Успішний вхід
        } catch (Exception e) {
            errorLabelStyle.set("-fx-text-fill: red;");
            errorLabelText.set("Помилка: " + e.getMessage());
            return false; // Помилка входу
        }
    }

    // Properties for binding
    public StringProperty emailInputProperty() { return emailInput; }
    public StringProperty passwordInputProperty() { return passwordInput; }
    public StringProperty errorLabelTextProperty() { return errorLabelText; }
    public StringProperty errorLabelStyleProperty() { return errorLabelStyle; }
}