package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.config.SpringContextProvider;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.infrastructure.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private javafx.scene.control.Button fullscreenButton;

    private final AuthenticationService authService;

    public LoginController(AuthenticationService authService) {
        this.authService = authService;
    }

    @FXML
    private void initialize() {
        // Можна додати логіку ініціалізації, якщо треба
    }

    @FXML
    private void onLoginClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(email, password);
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Успішний вхід! Вітаємо, " + user.getUsername());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setTitle("Guess the Word — Головне меню");
            stage.setFullScreen(true);  // Відкриваємо в повноекранному режимі
        } catch (Exception e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void onRegisterLinkClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setTitle("Guess the Word — Реєстрація");
            stage.setFullScreen(true);  // Відкриваємо в повноекранному режимі
        } catch (IOException e) {
            errorLabel.setText("Не вдалося відкрити форму реєстрації.");
        }
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}
