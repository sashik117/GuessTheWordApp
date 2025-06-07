package com.guessthewordapp.presentation.view;

import com.guessthewordapp.config.SpringContextProvider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class ViewManager {
    private static Stage stage;
    private static ApplicationContext context;

    private ViewManager() {
        // Приватний конструктор для приховування публічного
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Вгадай Слово");
        stage.setResizable(false);

        // Отримання Spring контексту
        context = SpringContextProvider.getApplicationContext();
    }

    public static void showAdminWordsView() {
        loadView("/fxml/admin_words.fxml", "Guess The Word — Адмін-панель слів");
    }

    private static void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxmlPath));

            // Використання Spring для створення контролерів
            if (context != null) {
                loader.setControllerFactory(context::getBean);
            }

            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Додавання CSS стилів
            if (ViewManager.class.getResource("/css/styles.css") != null) {
                scene.getStylesheets().add(ViewManager.class.getResource("/css/styles.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            handleError("Помилка завантаження представлення: " + fxmlPath, e);
        }
    }

    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}