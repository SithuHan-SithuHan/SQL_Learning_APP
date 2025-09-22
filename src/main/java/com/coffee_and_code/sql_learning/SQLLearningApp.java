package com.coffee_and_code.sql_learning;



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
            primaryStage.setTitle("SQL Learning Application");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.setMaximized(true);

            // Set application icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/images/sql-icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                logger.warn("Could not load application icon", e);
            }

            // Add CSS stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            primaryStage.show();
            logger.info("SQL Learning Application started successfully");

        } catch (IOException | java.sql.SQLException e) {
            logger.error("Failed to start application", e);
            System.exit(1);
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

    public static void main(String[] args) {
        launch(SQLLearningApp.class, args);
    }
}