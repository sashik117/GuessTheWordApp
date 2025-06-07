package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button fullscreenButton;

    private final AuthenticationService authenticationService;

    @Autowired
    public RegisterController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @FXML
    private void initialize() {
        // Обробники в FXML
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Заповніть усі поля");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Паролі не збігаються");
            return;
        }

        try {
            authenticationService.register(username, password, email,
                String.valueOf(UserRole.PLAYER));
            errorLabel.setText("Реєстрація успішна");
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            MainApp.getInstance().showLoginScene();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Помилка при реєстрації");
        }
    }

    @FXML
    private void handleLoginLink() {
        MainApp.getInstance().showLoginScene();
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}