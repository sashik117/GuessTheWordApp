package com.guessthewordapp.presentation.view.viewmodels;

import com.guessthewordapp.MainApp;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.springframework.stereotype.Component;

@Component
public class SettingsViewModel {

    private final BooleanProperty lightThemeSelected = new SimpleBooleanProperty();
    private final BooleanProperty darkThemeSelected = new SimpleBooleanProperty();
    // Додамо властивість, яку контролер може слухати для переходу
    private final StringProperty navigationRequest = new SimpleStringProperty(null);

    public SettingsViewModel() {
        // Ініціалізуємо стан вибору теми відповідно до поточної
        String currentTheme = MainApp.getCurrentTheme();
        if ("dark".equals(currentTheme)) {
            darkThemeSelected.set(true);
        } else {
            lightThemeSelected.set(true);
        }

        // Слухачі для зміни теми
        lightThemeSelected.addListener((obs, oldVal, newVal) -> {
            if (newVal) { // Якщо вибрана світла тема
                if (!MainApp.getCurrentTheme().equals("light")) {
                    MainApp.switchTheme("light");
                }
            }
        });

        darkThemeSelected.addListener((obs, oldVal, newVal) -> {
            if (newVal) { // Якщо вибрана темна тема
                if (!MainApp.getCurrentTheme().equals("dark")) {
                    MainApp.switchTheme("dark");
                }
            }
        });
    }

    public void logout() {
        navigationRequest.set("login");
    }

    public void openAdminPanel() {
        navigationRequest.set("admin");
    }

    public void goBack() {
        navigationRequest.set("mainMenu");
    }

    // Properties for binding
    public BooleanProperty lightThemeSelectedProperty() { return lightThemeSelected; }
    public BooleanProperty darkThemeSelectedProperty() { return darkThemeSelected; }
    public StringProperty navigationRequestProperty() { return navigationRequest; }

    // Метод для очищення запиту на навігацію після обробки контролером
    public void clearNavigationRequest() {
        navigationRequest.set(null);
    }
}