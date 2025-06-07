package com.guessthewordapp.presentation.controllers;

import com.guessthewordapp.MainApp;
import com.guessthewordapp.presentation.view.viewmodels.SettingsViewModel; // Імпортуємо ViewModel
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

    @FXML private ToggleButton lightThemeToggle;
    @FXML private ToggleButton darkThemeToggle;
    @FXML private Button logoutButton;
    @FXML private Button adminLoginButton;
    @FXML private Button backButton;
    @FXML private Button fullscreenButton;

    private ToggleGroup themeToggleGroup; // Declare here, initialize in `initialize`

    private final SettingsViewModel settingsViewModel; // Інжектуємо ViewModel

    public SettingsController(SettingsViewModel settingsViewModel) {
        this.settingsViewModel = settingsViewModel;
    }

    @FXML
    private void initialize() {
        themeToggleGroup = new ToggleGroup();

        lightThemeToggle.setToggleGroup(themeToggleGroup);
        darkThemeToggle.setToggleGroup(themeToggleGroup);

        // Bind toggle buttons' selected state to ViewModel properties
        lightThemeToggle.selectedProperty().bindBidirectional(settingsViewModel.lightThemeSelectedProperty());
        darkThemeToggle.selectedProperty().bindBidirectional(settingsViewModel.darkThemeSelectedProperty());

        // Listen for navigation requests from ViewModel
        settingsViewModel.navigationRequestProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case "login":
                        MainApp.getInstance().showLoginScene();
                        break;
                    case "admin":
                        MainApp.getInstance().showAdminWordsScene();
                        break;
                    case "mainMenu":
                        MainApp.getInstance().showMainMenuScene();
                        break;
                }
                settingsViewModel.clearNavigationRequest(); // Очищаємо запит після обробки
            }
        });
    }

    @FXML
    private void onLogoutClick() {
        settingsViewModel.logout();
    }

    @FXML
    private void onAdminLoginClick() {
        settingsViewModel.openAdminPanel();
    }

    @FXML
    private void onBackClick() {
        settingsViewModel.goBack();
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}