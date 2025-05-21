/*
package com.guessthewordapp;

import com.spaceexplorer.infrastructure.database.ConnectionPool;
import com.spaceexplorer.ui.SceneManager;
import com.spaceexplorer.ui.viewmodel.LoginViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.java.Log;

@Log
public class Main extends Application {

    private static final String APP_TITLE = "Space Explorer Game";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    private SceneManager sceneManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up primary stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Create initial empty scene (will be replaced by SceneManager)
            Scene scene = new Scene(new javafx.scene.layout.Pane(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

            // Load CSS styles
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

            // Set the scene
            primaryStage.setScene(scene);

            // Initialize scene manager
            sceneManager = new SceneManager(primaryStage);

            // Navigate to login screen
            sceneManager.navigateTo(LoginViewModel.class);

            // Show the stage
            primaryStage.show();

            log.info("Application started successfully");
        } catch (Exception e) {
            log.severe("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Clean up resources when the application closes
        try {
            // Close database connection pool
            ConnectionPool.getInstance().close();
            log.info("Application resources released");
        } catch (Exception e) {
            log.severe("Error closing resources: " + e.getMessage());
        }
    }
}
*/