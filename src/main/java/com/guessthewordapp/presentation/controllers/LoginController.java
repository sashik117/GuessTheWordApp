package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.infrastructure.AuthenticationService;
import com.guessthewordapp.presentation.view.viewmodels.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button fullscreenButton;

    private final AuthenticationService authenticationService;
    private final LoginViewModel loginViewModel;

    @Autowired
    public LoginController(AuthenticationService authenticationService, LoginViewModel loginViewModel) {
        this.authenticationService = authenticationService;
        this.loginViewModel = loginViewModel;
        logger.debug("LoginController initialized");
    }

    @FXML
    private void initialize() {
        logger.debug("LoginController: initialize() called");
        emailField.textProperty().bindBidirectional(loginViewModel.emailInputProperty());
        passwordField.textProperty().bindBidirectional(loginViewModel.passwordInputProperty());
        errorLabel.textProperty().bind(loginViewModel.errorLabelTextProperty());
        errorLabel.styleProperty().bind(loginViewModel.errorLabelStyleProperty());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            logger.warn("Login attempt with empty fields");
            return;
        }

        try {
            logger.debug("Attempting login for email: {}", email);
            if (loginViewModel.login()) {
                User user = authenticationService.login(email, password);
                Long userId = user.getId();
                logger.info("Login successful for userId: {}", userId);
                showSuccessAlert("Успішний вхід", "Ви успішно увійшли в систему!", () -> {
                    MainApp.getInstance().setCurrentUserId(userId);
                    emailField.clear();
                    passwordField.clear();
                    MainApp.getInstance().showMainMenuScene();
                });
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Login failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
        }
    }

    @FXML
    private void handleRegisterLink() {
        logger.debug("Switching to register scene");
        MainApp.getInstance().showRegisterScene();
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
        logger.debug("Toggled fullscreen mode: {}", stage.isFullScreen());
    }

    private void showSuccessAlert(String title, String message, Runnable onConfirm) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(new javafx.scene.control.ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE));
        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                onConfirm.run();
            }
        });
    }
}