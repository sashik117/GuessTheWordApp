package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.config.SpringContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SettingsController {

    @FXML private Button logoutButton;
    @FXML private Button adminLoginButton;
    @FXML private Button backButton;
    @FXML private Button fullscreenButton;
    @FXML private Label titleLabel;

    @FXML
    private void initialize() {
        // Ініціалізація, якщо потрібно
    }

    @FXML
    private void onLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setTitle("Guess the Word — Вхід");
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAdminLoginClick() {
        // Тут буде логіка входу як адміністратор (поки просто повідомлення)
        titleLabel.setText("Функція 'Увійти як адміністратор' буде реалізована пізніше");
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            loader.setControllerFactory(SpringContextProvider.getApplicationContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 650);
            stage.setScene(scene);
            stage.setTitle("Guess the Word — Головне меню");
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}