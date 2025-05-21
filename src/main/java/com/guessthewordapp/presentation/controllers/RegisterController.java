package com.guessthewordapp.presentation.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button fullscreenButton;
    @FXML private Label errorLabel;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private void handleRegister() {
        errorLabel.setText("");
        if (!validateInput()) {
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Успіх", "Реєстрація пройшла успішно!");
        redirectToLogin(); // Переходимо на логін після успішної реєстрації
    }

    private boolean validateInput() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Будь ласка, заповніть всі поля");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errorLabel.setText("Будь ласка, введіть коректну email адресу");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errorLabel.setText("Пароль має містити мінімум 8 символів (літери та цифри)");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Паролі не співпадають");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
            stage.setTitle("Guess the Word — Вхід");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося завантажити форму входу.");
        }
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}
