package com.coffee_and_code.sql_learning;

import com.coffee_and_code.sql_learning.utils.IconGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.coffee_and_code.sql_learning.service.DatabaseService;
import com.coffee_and_code.sql_learning.controller.MainController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Main Application class for SQL Learning Desktop App
 */
public class SQLLearningApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(SQLLearningApp.class);
    private DatabaseService databaseService;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database service
            databaseService = DatabaseService.getInstance();
            databaseService.initializeDatabase();

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load());

            // Get controller and set database service
            MainController controller = loader.getController();
            controller.setDatabaseService(databaseService);

            // Set up primary stage
            primaryStage.setTitle("SQL Learning Application - Professional Edition");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            primaryStage.setMaximized(true);

            // Set application icon (with fallback)
            try {
                InputStream iconStream = getClass().getResourceAsStream("/images/sql-icon.png");
                Image icon;
                if (iconStream != null) {
                    icon = new Image(iconStream);
                    logger.info("Application icon loaded successfully");
                } else {
                    // Use generated default icon
                    icon = IconGenerator.createDefaultIcon();
                    logger.warn("Using generated default icon");
                }
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                logger.warn("Could not load application icon: {}", e.getMessage());
                // Try generated icon as fallback
                try {
                    primaryStage.getIcons().add(IconGenerator.createDefaultIcon());
                } catch (Exception ex) {
                    logger.warn("Could not create default icon either");
                }
            }

            // Add CSS stylesheet (with error handling)
            try {
                String cssFile = getClass().getResource("/css/styles.css").toExternalForm();
                scene.getStylesheets().add(cssFile);
                logger.info("CSS stylesheet loaded successfully");
            } catch (Exception e) {
                logger.error("Could not load CSS stylesheet: {}", e.getMessage());
                // Continue without custom styles
            }

            primaryStage.show();
            logger.info("SQL Learning Application started successfully");

        } catch (IOException | java.sql.SQLException e) {
            logger.error("Failed to start application", e);
            // Show error dialog instead of just exiting
            showErrorAndExit("Application Startup Error",
                    "Failed to start the application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            if (databaseService != null) {
                databaseService.shutdown();
            }
            logger.info("Application shutdown completed");
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }

    private void showErrorAndExit(String title, String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Startup Failed");
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            // If even the alert fails, just print to console
            System.err.println(title + ": " + message);
        }
        System.exit(1);
    }

    public static void main(String[] args) {
        launch(SQLLearningApp.class, args);
    }
}