package com.guessthewordapp;

import com.guessthewordapp.config.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;
    private static MainApp instance;

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            setupStage();
            showLoginScene();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setupStage() {
        primaryStage.setTitle("Guess The Word");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
    }

    public void loadScene(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }

    public void showLoginScene() {
        try {
            loadScene("/fxml/login.fxml", "Guess The Word — Вхід");
        } catch (Exception e) {
            handleError("Помилка завантаження входу", e);
        }
    }

    public void showRegisterScene() {
        try {
            loadScene("/fxml/register.fxml", "Guess The Word — Реєстрація");
        } catch (Exception e) {
            handleError("Помилка завантаження реєстрації", e);
        }
    }

    public void showMainMenuScene() {
        try {
            loadScene("/fxml/main_menu.fxml", "Guess The Word — Головне меню");
        } catch (Exception e) {
            handleError("Помилка завантаження меню", e);
        }
    }

    public void showSettingsScene() {
        try {
            loadScene("/fxml/settings.fxml", "Guess The Word — Налаштування");
        } catch (Exception e) {
            handleError("Помилка завантаження налаштувань", e);
        }
    }

    public void showGameScene() {
        try {
            loadScene("/fxml/game.fxml", "Guess The Word — Гра");
        } catch (Exception e) {
            handleError("Помилка завантаження гри", e);
        }
    }

    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    public static MainApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}