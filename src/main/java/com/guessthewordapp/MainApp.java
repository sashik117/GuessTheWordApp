package com.guessthewordapp;

import com.guessthewordapp.config.AppConfig;
import com.guessthewordapp.presentation.controllers.GameController;
import com.guessthewordapp.presentation.controllers.MainMenuController;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;

public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;
    private static MainApp instance;
    private static String currentTheme = "light";
    private MainMenuController mainMenuController;
    private Long currentUserId;

    @Override
    public void init() {
        logger.debug("MainApp: init() started.");
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
        instance = this;
        logger.debug("MainApp: init() finished.");
    }

    @Override
    public void start(Stage primaryStage) {
        logger.debug("MainApp: start() started.");
        this.primaryStage = primaryStage;

        primaryStage.initStyle(StageStyle.DECORATED);

        try {
            setupStage();
            applyTheme();
            showLoginScene();
        } catch (Exception e) {
            handleError("Помилка запуску додатку", e);
        }
        logger.debug("MainApp: start() finished.");
    }

    private void applyTheme() {
        logger.debug("MainApp: Applying theme: {}", currentTheme);
        Application.setUserAgentStylesheet(null);
        if ("dark".equals(currentTheme)) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
        if (primaryStage != null && primaryStage.getScene() != null) {
            applyCss(primaryStage.getScene().getRoot());
        }
    }

    public static void switchTheme(String theme) {
        if (!currentTheme.equals(theme)) {
            logger.debug("MainApp: Switching theme from {} to {}", currentTheme, theme);
            currentTheme = theme;
            if (instance != null) {
                instance.applyTheme();
            }
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    private void setupStage() {
        logger.debug("MainApp: setupStage() called.");
        primaryStage.setTitle("Guess The Word");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        logger.debug("MainApp: Set min width/height to 800/600.");
    }

    public void loadScene(String fxmlPath, String title) throws Exception {
        logger.debug("--- MainApp: loadScene() started for {} ---", fxmlPath);
        logger.debug("Current userId before loading scene: {}", currentUserId);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        logger.debug("MainApp: FXML loaded successfully: {}", fxmlPath);

        if ("/fxml/main_menu.fxml".equals(fxmlPath)) {
            mainMenuController = loader.getController();
            if (mainMenuController != null && currentUserId != null) {
                mainMenuController.setUserId(currentUserId);
            }
            logger.info("MainApp: MainMenuController saved for userId: {}", currentUserId);
        }

        applyCss(root);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        logger.debug("MainApp: Scene set for Stage. New title: {}", title);

        if (primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
            logger.debug("MainApp: Exited full-screen mode in loadScene.");
        }

        if (!primaryStage.isShowing()) {
            primaryStage.show();
            logger.debug("MainApp: Stage shown for the first time.");
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            logger.debug("MainApp: Timeline executed for loadScene maximized operation.");
            logger.debug("Stage state BEFORE maximize - isShowing: {}, isFullScreen: {}, isMaximized: {}",
                primaryStage.isShowing(), primaryStage.isFullScreen(), primaryStage.isMaximized());
            logger.debug("Stage dimensions BEFORE maximize - Width: {}, Height: {}",
                primaryStage.getWidth(), primaryStage.getHeight());

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setMaximized(true);
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
            logger.debug("MainApp: primaryStage.setMaximized(true) and set dimensions (Width: {}, Height: {}) called in loadScene.",
                screenBounds.getWidth(), screenBounds.getHeight());

            logger.debug("Stage state AFTER maximize - isShowing: {}, isFullScreen: {}, isMaximized: {}",
                primaryStage.isShowing(), primaryStage.isFullScreen(), primaryStage.isMaximized());
            logger.debug("Stage dimensions AFTER maximize - Width: {}, Height: {}",
                primaryStage.getWidth(), primaryStage.getHeight());
            logger.debug("--- MainApp: Timeline finished for loadScene ---");
        }));
        timeline.setCycleCount(1);
        timeline.play();

        logger.debug("--- MainApp: loadScene() finished for {} ---", fxmlPath);
    }

    public void switchScene(String fxmlPath, String title) {
        try {
            logger.debug("--- MainApp: switchScene() started for {} ---", fxmlPath);
            logger.debug("Current userId before switching scene: {}", currentUserId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            logger.debug("MainApp: Loaded FXML: {}", fxmlPath);

            if ("/fxml/main_menu.fxml".equals(fxmlPath)) {
                mainMenuController = loader.getController();
                if (mainMenuController != null && currentUserId != null) {
                    mainMenuController.setUserId(currentUserId);
                }
            } else if ("/fxml/game.fxml".equals(fxmlPath)) {
                GameController controller = loader.getController();
                if (controller != null && currentUserId != null) {
                    controller.initialize(currentUserId);
                }
            }

            applyTheme();
            applyCss(root);

            Scene currentScene = primaryStage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            primaryStage.setTitle(title);
            logger.debug("MainApp: Scene switched. New title: {}", title);

            if (primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
                logger.debug("MainApp: Exited full-screen mode in switchScene.");
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                logger.debug("MainApp: Timeline executed for switchScene maximized operation.");
                logger.debug("Stage state BEFORE maximize - isShowing: {}, isFullScreen: {}, isMaximized: {}",
                    primaryStage.isShowing(), primaryStage.isFullScreen(), primaryStage.isMaximized());
                logger.debug("Stage dimensions BEFORE maximize - Width: {}, Height: {}",
                    primaryStage.getWidth(), primaryStage.getHeight());

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setMaximized(true);
                primaryStage.setWidth(screenBounds.getWidth());
                primaryStage.setHeight(screenBounds.getHeight());
                logger.debug("MainApp: primaryStage.setMaximized(true) and set dimensions (Width: {}, Height: {}) called in switchScene.",
                    screenBounds.getWidth(), screenBounds.getHeight());

                logger.debug("Stage state AFTER maximize - isShowing: {}, isFullScreen: {}, isMaximized: {}",
                    primaryStage.isShowing(), primaryStage.isFullScreen(), primaryStage.isMaximized());
                logger.debug("Stage dimensions AFTER maximize - Width: {}, Height: {}",
                    primaryStage.getWidth(), primaryStage.getHeight());
                logger.debug("--- MainApp: Timeline finished for switchScene ---");
            }));
            timeline.setCycleCount(1);
            timeline.play();

            if ("/fxml/main_menu.fxml".equals(fxmlPath) && mainMenuController != null && currentUserId != null) {
                mainMenuController.onRefreshStatistics();
            }

            logger.debug("--- MainApp: switchScene() finished for {} ---", fxmlPath);
        } catch (Exception e) {
            handleError("Помилка переключення сцени", e);
        }
    }

    private void applyCss(Parent root) {
        logger.debug("MainApp: Applying CSS for theme: {}", currentTheme);
        root.getStylesheets().clear();
        String customCssPath = "dark".equals(currentTheme) ? "/css/dark-theme.css" : "/css/styles.css";
        URL cssResource = getClass().getResource(customCssPath);
        if (cssResource != null) {
            root.getStylesheets().add(cssResource.toExternalForm());
            logger.debug("MainApp: Applied custom CSS: {}", customCssPath);
        } else {
            logger.error("CSS resource not found: {}", customCssPath);
        }
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
        logger.debug("MainApp: Showing main menu scene with userId: {}", currentUserId);
        if (mainMenuController != null && currentUserId != null) {
            mainMenuController.setUserId(currentUserId);
        }
        switchScene("/fxml/main_menu.fxml", "Guess The Word — Головне меню");
    }

    public void showSettingsScene() {
        logger.debug("MainApp: Showing settings scene");
        switchScene("/fxml/settings.fxml", "Guess The Word — Налаштування");
    }

    public void showGameScene() {
        logger.debug("MainApp: Showing game scene with userId: {}", currentUserId);
        switchScene("/fxml/game.fxml", "Guess The Word — Гра");
    }

    public void showAdminWordsScene() {
        logger.debug("MainApp: Showing admin words scene");
        switchScene("/fxml/admin-words.fxml", "Guess The Word — Адмін-панель слів");
    }

    public void refreshMainMenuStatistics() {
        logger.debug("Refreshing main menu statistics");
        if (mainMenuController != null) {
            mainMenuController.onRefreshStatistics();
        }
    }

    private void handleError(String message, Exception e) {
        logger.error("{}: {}", message, e.getMessage(), e);
        System.exit(1);
    }

    @Override
    public void stop() {
        logger.debug("MainApp: stop() called. Closing Spring context.");
        if (springContext != null) {
            springContext.close();
        }
    }

    public static MainApp getInstance() {
        return instance;
    }

    public MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
        logger.info("MainApp: Set current user ID: {}", userId);
    }

    public Long getCurrentUserId() {
        logger.debug("MainApp: Get current user ID: {}", currentUserId);
        return currentUserId;
    }

    public static void main(String[] args) {
        launch(args);
    }
}