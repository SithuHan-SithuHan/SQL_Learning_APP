package com.coffee_and_code.sql_learning.controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.coffee_and_code.sql_learning.service.DatabaseService;
import com.coffee_and_code.sql_learning.service.LearningContentService;
import com.coffee_and_code.sql_learning.service.PracticeService;
import com.coffee_and_code.sql_learning.service.SqlSyntaxHighlighter;
import com.coffee_and_code.sql_learning.service.PracticeService.PracticeQuestion;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main controller for the SQL Learning Application
 */
public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // Main tabs
    @FXML private TabPane mainTabPane;
    @FXML private Tab learningTab;
    @FXML private Tab practiceTab;

    // Learning section
    @FXML private TreeView<String> topicsTreeView;
    @FXML private WebView contentWebView;
    @FXML private Button nextTopicBtn;
    @FXML private Button prevTopicBtn;
    @FXML private ProgressBar learningProgressBar;
    @FXML private Label progressLabel;

    // Practice section
    @FXML private ListView<String> questionsList;
    @FXML private Label questionTitleLabel;
    @FXML private WebView questionContentWebView;
    @FXML private VBox sqlEditorContainer;
    @FXML private Button executeBtn;
    @FXML private Button validateBtn;
    @FXML private Button resetBtn;
    @FXML private Button submitBtn;
    @FXML private TableView<ObservableList<Object>> resultTable;
    @FXML private TextArea outputTextArea;
    @FXML private Label statusLabel;
    @FXML private ProgressBar practiceProgressBar;
    @FXML
    private Label progressStatusLabel;

    // Database browser
    @FXML private ListView<String> tablesListView;
    @FXML private TableView<DatabaseService.ColumnInfo> tableStructureView;
    @FXML private TableColumn<DatabaseService.ColumnInfo, String> columnNameCol;
    @FXML private TableColumn<DatabaseService.ColumnInfo, String> columnTypeCol;
    @FXML private TableColumn<DatabaseService.ColumnInfo, Integer> columnSizeCol;
    @FXML private TableColumn<DatabaseService.ColumnInfo, Boolean> columnNullableCol;

    // Services
    private DatabaseService databaseService;
    private LearningContentService learningContentService;
    private PracticeService practiceService;

    // UI Components
    private CodeArea sqlCodeArea;
    private SqlSyntaxHighlighter syntaxHighlighter;

    // Current state
    private PracticeQuestion currentQuestion;
    private int currentQuestionIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController");

        // Initialize services
        learningContentService = new LearningContentService();
        practiceService = new PracticeService();
        syntaxHighlighter = new SqlSyntaxHighlighter();

        // Setup UI components
        setupLearningSection();
        setupPracticeSection();
        setupDatabaseBrowser();
        setupSqlEditor();

        logger.info("MainController initialized successfully");
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        loadDatabaseTables();
    }

    private void setupLearningSection() {
        // Setup topics tree view
        TreeItem<String> rootItem = learningContentService.getLearningTopicsTree();
        topicsTreeView.setRoot(rootItem);
        topicsTreeView.setShowRoot(false);

        // Handle topic selection
        topicsTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadTopicContent(newValue.getValue());
                    }
                }
        );

        // Setup navigation buttons
        nextTopicBtn.setOnAction(e -> navigateToNextTopic());
        prevTopicBtn.setOnAction(e -> navigateToPreviousTopic());

        // Select first topic by default
        Platform.runLater(() -> {
            topicsTreeView.getSelectionModel().selectFirst();
            updateLearningProgress();
        });
    }

    private void setupPracticeSection() {
        // Setup questions list
        List<PracticeQuestion> questions = practiceService.getAllQuestions();
        ObservableList<String> questionTitles = FXCollections.observableArrayList();
        questions.forEach(q -> questionTitles.add(q.getTitle()));
        questionsList.setItems(questionTitles);

        // Handle question selection
        questionsList.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.intValue() >= 0) {
                        currentQuestionIndex = newValue.intValue();
                        loadQuestion(questions.get(newValue.intValue()));
                    }
                }
        );

        // Setup buttons
        executeBtn.setOnAction(e -> executeSql());
        validateBtn.setOnAction(e -> validateSql());
        resetBtn.setOnAction(e -> resetDatabase());
        submitBtn.setOnAction(e -> submitAnswer());

        // Setup result table
        setupResultTable();

        // Select first question by default
        Platform.runLater(() -> {
            if (!questions.isEmpty()) {
                questionsList.getSelectionModel().selectFirst();
                updatePracticeProgress();
            }
        });
    }

    private void setupDatabaseBrowser() {
        // Setup table structure columns
        columnNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnSizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        columnNullableCol.setCellValueFactory(new PropertyValueFactory<>("nullable"));

        // Handle table selection
        tablesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadTableStructure(newValue);
                    }
                }
        );
    }

    private void setupSqlEditor() {
        // Create code area for SQL editing
        sqlCodeArea = new CodeArea();
        sqlCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(sqlCodeArea));
        sqlCodeArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 14px;");

        // Apply syntax highlighting
        sqlCodeArea.textProperty().addListener((obs, oldText, newText) -> {
            syntaxHighlighter.applySyntaxHighlighting(sqlCodeArea);
        });

        // Add to container
        sqlEditorContainer.getChildren().add(sqlCodeArea);
        VBox.setVgrow(sqlCodeArea, javafx.scene.layout.Priority.ALWAYS);

        // Set default SQL
        sqlCodeArea.replaceText("-- Write your SQL query here\nSELECT * FROM employees LIMIT 10;");
    }

    private void setupResultTable() {
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadTopicContent(String topic) {
        String content = learningContentService.getTopicContent(topic);
        WebEngine webEngine = contentWebView.getEngine();
        webEngine.loadContent(content, "text/html");
        updateLearningProgress();
    }

    private void loadQuestion(PracticeQuestion question) {
        currentQuestion = question;
        questionTitleLabel.setText(question.getTitle());

        WebEngine webEngine = questionContentWebView.getEngine();

        // Convert plain text description to HTML with <pre>
        String html = "<html><body><pre>" + escapeHtml(question.getDescription()) + "</pre></body></html>";
        webEngine.loadContent(html);

        // Clear previous results
        resultTable.getColumns().clear();
        resultTable.getItems().clear();
        outputTextArea.clear();
        statusLabel.setText("Ready");

        // Set example SQL if available
        if (question.getExampleSql() != null && !question.getExampleSql().isEmpty()) {
            sqlCodeArea.replaceText(question.getExampleSql());
        }
    }

    /** Escape HTML special characters */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void loadDatabaseTables() {
        try {
            List<String> tables = databaseService.getTables();
            ObservableList<String> tableNames = FXCollections.observableArrayList(tables);
            tablesListView.setItems(tableNames);
        } catch (Exception e) {
            logger.error("Failed to load database tables", e);
            showError("Failed to load database tables", e.getMessage());
        }
    }

    private void loadTableStructure(String tableName) {
        try {
            List<DatabaseService.ColumnInfo> columns = databaseService.getTableColumns(tableName);
            ObservableList<DatabaseService.ColumnInfo> columnData = FXCollections.observableArrayList(columns);
            tableStructureView.setItems(columnData);
        } catch (Exception e) {
            logger.error("Failed to load table structure for: " + tableName, e);
            showError("Failed to load table structure", e.getMessage());
        }
    }

    @FXML
    private void executeSql() {
        String sql = sqlCodeArea.getText().trim();
        if (sql.isEmpty()) {
            showWarning("Empty Query", "Please enter a SQL query to execute.");
            return;
        }

        statusLabel.setText("Executing...");
        executeBtn.setDisable(true);

        Task<DatabaseService.ExecutionResult> task = new Task<DatabaseService.ExecutionResult>() {
            @Override
            protected DatabaseService.ExecutionResult call() throws Exception {
                return databaseService.executeSql(sql);
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                DatabaseService.ExecutionResult result = task.getValue();
                displayExecutionResult(result);
                executeBtn.setDisable(false);
                statusLabel.setText(result.isSuccess() ? "Executed successfully" : "Execution failed");
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                executeBtn.setDisable(false);
                statusLabel.setText("Execution failed");
                outputTextArea.setText("Error: " + task.getException().getMessage());
                logger.error("SQL execution task failed", task.getException());
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void validateSql() {
        String sql = sqlCodeArea.getText().trim();
        if (sql.isEmpty()) {
            showWarning("Empty Query", "Please enter a SQL query to validate.");
            return;
        }

        DatabaseService.ValidationResult result = databaseService.validateSql(sql);
        if (result.isValid()) {
            outputTextArea.setText("✓ SQL syntax is valid");
            outputTextArea.setStyle("-fx-text-fill: green;");
        } else {
            outputTextArea.setText("✗ " + result.getMessage());
            outputTextArea.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void resetDatabase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Database");
        alert.setHeaderText("Reset Database to Initial State");
        alert.setContentText("This will reset all tables to their original state. Continue?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                databaseService.resetDatabase();
                outputTextArea.setText("Database reset successfully");
                outputTextArea.setStyle("-fx-text-fill: green;");
                loadDatabaseTables();
                statusLabel.setText("Database reset");
            } catch (Exception e) {
                logger.error("Failed to reset database", e);
                showError("Reset Failed", "Failed to reset database: " + e.getMessage());
            }
        }
    }

    @FXML
    private void submitAnswer() {
        if (currentQuestion == null) {
            showWarning("No Question", "Please select a question first.");
            return;
        }

        String userSql = sqlCodeArea.getText().trim();
        if (userSql.isEmpty()) {
            showWarning("Empty Answer", "Please write your SQL solution before submitting.");
            return;
        }

        // Validate and execute user's solution
        DatabaseService.ExecutionResult result = databaseService.executeSql(userSql);

        if (!result.isSuccess()) {
            showError("SQL Error", "Your SQL has errors:\n" + result.getMessage());
            return;
        }

        // Check answer against expected result
        boolean isCorrect = practiceService.checkAnswer(currentQuestion, result.getQueryResult());

        if (isCorrect) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correct!");
            alert.setHeaderText("Great job!");
            alert.setContentText("Your answer is correct! You can move to the next question.");
            alert.showAndWait();

            // Mark question as completed
            practiceService.markQuestionCompleted(currentQuestion.getId());
            updatePracticeProgress();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect");
            alert.setHeaderText("Not quite right");
            alert.setContentText("Your answer doesn't match the expected result. Review the question and try again.\n\nHint: " + currentQuestion.getHint());
            alert.showAndWait();
        }
    }

    private void navigateToNextTopic() {
        TreeItem<String> selected = topicsTreeView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TreeItem<String> next = getNextTreeItem(selected);
            if (next != null) {
                topicsTreeView.getSelectionModel().select(next);
            }
        }
    }

    private void navigateToPreviousTopic() {
        TreeItem<String> selected = topicsTreeView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TreeItem<String> previous = getPreviousTreeItem(selected);
            if (previous != null) {
                topicsTreeView.getSelectionModel().select(previous);
            }
        }
    }

    private TreeItem<String> getNextTreeItem(TreeItem<String> current) {
        if (!current.getChildren().isEmpty()) {
            return current.getChildren().get(0);
        }

        TreeItem<String> parent = current.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(current);
            if (index < parent.getChildren().size() - 1) {
                return parent.getChildren().get(index + 1);
            } else {
                return getNextTreeItem(parent);
            }
        }
        return null;
    }

    private TreeItem<String> getPreviousTreeItem(TreeItem<String> current) {
        TreeItem<String> parent = current.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(current);
            if (index > 0) {
                TreeItem<String> previous = parent.getChildren().get(index - 1);
                // Get the last leaf of the previous sibling
                while (!previous.getChildren().isEmpty()) {
                    previous = previous.getChildren().get(previous.getChildren().size() - 1);
                }
                return previous;
            } else {
                return parent;
            }
        }
        return null;
    }

    private void displayExecutionResult(DatabaseService.ExecutionResult result) {
        if (result.isSuccess()) {
            outputTextArea.setText(result.getMessage());
            outputTextArea.setStyle("-fx-text-fill: green;");

            if (result.getQueryResult() != null) {
                displayQueryResult(result.getQueryResult());
            }
        } else {
            outputTextArea.setText(result.getMessage());
            outputTextArea.setStyle("-fx-text-fill: red;");
            resultTable.getColumns().clear();
            resultTable.getItems().clear();
        }
    }

    private void displayQueryResult(DatabaseService.QueryResult queryResult) {
        // Clear existing columns and data
        resultTable.getColumns().clear();
        resultTable.getItems().clear();

        if (queryResult.getColumnCount() == 0) {
            return;
        }

        // Create columns
        for (int i = 0; i < queryResult.getColumnCount(); i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(
                    queryResult.getColumnNames().get(i)
            );
            column.setCellValueFactory(param ->
                    new javafx.beans.property.SimpleObjectProperty<>(
                            param.getValue().get(columnIndex)
                    )
            );
            resultTable.getColumns().add(column);
        }

        // Add data
        for (List<Object> row : queryResult.getRows()) {
            ObservableList<Object> observableRow = FXCollections.observableArrayList(row);
            resultTable.getItems().add(observableRow);
        }

        // Update output with row count
        outputTextArea.appendText(String.format("\n%d row(s) returned.", queryResult.getRowCount()));
    }

    private void updateLearningProgress() {
        TreeItem<String> root = topicsTreeView.getRoot();
        int totalTopics = countLeafItems(root);
        TreeItem<String> selected = topicsTreeView.getSelectionModel().getSelectedItem();
        int currentPosition = getItemPosition(root, selected, 0);

        double progress = totalTopics > 0 ? (double) currentPosition / totalTopics : 0;
        learningProgressBar.setProgress(progress);
        progressLabel.setText(String.format("Topic %d of %d", currentPosition, totalTopics));
    }

    private void updatePracticeProgress() {
        int totalQuestions = practiceService.getTotalQuestions();
        int completedQuestions = practiceService.getCompletedQuestionsCount();

        double progress = totalQuestions > 0 ? (double) completedQuestions / totalQuestions : 0;
        practiceProgressBar.setProgress(progress);
        String progressText = String.format("Progress: %d/%d completed", completedQuestions, totalQuestions);
        progressStatusLabel.setText(progressText); // Corrected fx:id
    }

    private int countLeafItems(TreeItem<String> item) {
        if (item.getChildren().isEmpty()) {
            return 1;
        }

        int count = 0;
        for (TreeItem<String> child : item.getChildren()) {
            count += countLeafItems(child);
        }
        return count;
    }

    private int getItemPosition(TreeItem<String> root, TreeItem<String> target, int position) {
        if (root == target) {
            return position;
        }

        if (root.getChildren().isEmpty()) {
            return position + 1;
        }

        int currentPos = position;
        for (TreeItem<String> child : root.getChildren()) {
            int newPos = getItemPosition(child, target, currentPos);
            if (child == target || containsItem(child, target)) {
                return newPos;
            }
            currentPos = newPos;
        }
        return currentPos;
    }

    private boolean containsItem(TreeItem<String> parent, TreeItem<String> target) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (child == target || containsItem(child, target)) {
                return true;
            }
        }
        return false;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}