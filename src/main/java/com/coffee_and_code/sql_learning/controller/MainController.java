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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main controller for the SQL Learning Application
 */
public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // ===== MENU BAR COMPONENTS =====
    @FXML private MenuBar menuBar;
    @FXML private MenuItem newQueryMenuItem;
    @FXML private MenuItem openQueryMenuItem;
    @FXML private MenuItem saveQueryMenuItem;
    @FXML private MenuItem exportProgressMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private MenuItem cutMenuItem;
    @FXML private MenuItem copyMenuItem;
    @FXML private MenuItem pasteMenuItem;
    @FXML private MenuItem findMenuItem;
    @FXML private MenuItem replaceMenuItem;
    @FXML private MenuItem dbBrowserMenuItem;
    @FXML private MenuItem queryHistoryMenuItem;
    @FXML private CheckMenuItem lineNumbersMenuItem;
    @FXML private CheckMenuItem wordWrapMenuItem;
    @FXML private MenuItem zoomInMenuItem;
    @FXML private MenuItem zoomOutMenuItem;
    @FXML private MenuItem resetZoomMenuItem;
    @FXML private MenuItem fullScreenMenuItem;
    @FXML private MenuItem formatMenuItem;
    @FXML private MenuItem analyzeMenuItem;
    @FXML private MenuItem performanceMenuItem;
    @FXML private MenuItem settingsMenuItem;
    @FXML private MenuItem gettingStartedMenuItem;
    @FXML private MenuItem sqlReferenceMenuItem;
    @FXML private MenuItem shortcutsMenuItem;
    @FXML private MenuItem updatesMenuItem;
    @FXML private MenuItem aboutMenuItem;

    // ===== MAIN TABS =====
    @FXML private TabPane mainTabPane;
    @FXML private Tab learningTab;
    @FXML private Tab practiceTab;
    @FXML private Tab explorerTab;
    @FXML private Tab analyticsTab;

    // ===== LEARNING TAB COMPONENTS =====
    @FXML private TreeView<String> topicsTreeView;
    @FXML private WebView contentWebView;
    @FXML private Button nextTopicBtn;
    @FXML private Button prevTopicBtn;
    @FXML private Button bookmarkBtn;
    @FXML private Button noteBtn;
    @FXML private Button shareBtn;
    @FXML private Button searchBtn;
    @FXML private Button printBtn;
    @FXML private ProgressBar learningProgressBar;
    @FXML private Label progressLabel;
    @FXML private Label overallProgressLabel;
    @FXML private Label topicTitleLabel;

    // ===== PRACTICE TAB COMPONENTS =====
    // Difficulty filters
    @FXML private RadioButton allLevelsRadio;
    @FXML private RadioButton easyRadio;
    @FXML private RadioButton mediumRadio;
    @FXML private RadioButton hardRadio;
    @FXML private RadioButton proRadio;
    private ToggleGroup difficultyGroup;

    // Questions and content
    @FXML private ListView<String> questionsList;
    @FXML private Label currentQuestionLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label questionTitleLabel;
    @FXML private Label questionDescriptionLabel;
    @FXML private Button hintBtn;
    @FXML private Button solutionBtn;

    // SQL Editor
    @FXML private Button formatCodeBtn;
    @FXML private Button clearCodeBtn;
    @FXML private Button executeBtn;
    @FXML private Button validateBtn;
    @FXML private TextArea sqlEditor;

    // Results
    @FXML private Label executionTimeLabel;
    @FXML private Button exportResultsBtn;
    @FXML private TableView<ObservableList<Object>> resultsTable;

    // Database Schema Browser
    @FXML private TreeView<String> schemaTreeView;
    @FXML private Button showTableBtn;
    @FXML private Button describeTableBtn;
    @FXML private Button sampleDataBtn;

    // Progress and Statistics
    @FXML private ProgressBar practiceProgressBar;
    @FXML private Label practiceProgressLabel;
    @FXML private Label totalQueriesLabel;
    @FXML private Label successRateLabel;
    @FXML private Label bestStreakLabel;

    // ===== STATUS BAR =====
    @FXML private Label statusLabel;
    @FXML private Label connectionStatusLabel;
    @FXML private Label userProgressStatusLabel;
    @FXML private Label timeLabel;

    // ===== SERVICES =====
    private DatabaseService databaseService;
    private LearningContentService learningContentService;
    private PracticeService practiceService;

    // ===== UI COMPONENTS =====
    private CodeArea sqlCodeArea;
    private SqlSyntaxHighlighter syntaxHighlighter;
    private Timer clockTimer;

    // ===== STATE VARIABLES =====
    private PracticeQuestion currentQuestion;
    private int currentQuestionIndex = 0;
    private long queryStartTime;
    private int totalQueriesExecuted = 0;
    private int successfulQueries = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController with modern UI");

        // Initialize services
        learningContentService = new LearningContentService();
        practiceService = new PracticeService();
        syntaxHighlighter = new SqlSyntaxHighlighter();

        // Setup all UI sections
        setupMenuBar();
        setupLearningSection();
        setupPracticeSection();
        setupStatusBar();
        setupClock();

        logger.info("MainController initialized successfully");
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        loadDatabaseSchema();
        updateConnectionStatus();
    }

    // ===== SETUP METHODS =====

    private void setupMenuBar() {
        // File menu actions
        newQueryMenuItem.setOnAction(e -> newQuery());
        openQueryMenuItem.setOnAction(e -> openQuery());
        saveQueryMenuItem.setOnAction(e -> saveQuery());
        exportProgressMenuItem.setOnAction(e -> exportProgress());
        exitMenuItem.setOnAction(e -> exitApplication());

        // Edit menu actions
        undoMenuItem.setOnAction(e -> undo());
        redoMenuItem.setOnAction(e -> redo());
        cutMenuItem.setOnAction(e -> cut());
        copyMenuItem.setOnAction(e -> copy());
        pasteMenuItem.setOnAction(e -> paste());
        findMenuItem.setOnAction(e -> find());
        replaceMenuItem.setOnAction(e -> replace());

        // View menu actions
        dbBrowserMenuItem.setOnAction(e -> showDatabaseBrowser());
        queryHistoryMenuItem.setOnAction(e -> showQueryHistory());
        lineNumbersMenuItem.setOnAction(e -> toggleLineNumbers());
        wordWrapMenuItem.setOnAction(e -> toggleWordWrap());
        zoomInMenuItem.setOnAction(e -> zoomIn());
        zoomOutMenuItem.setOnAction(e -> zoomOut());
        resetZoomMenuItem.setOnAction(e -> resetZoom());
        fullScreenMenuItem.setOnAction(e -> toggleFullScreen());

        // Tools menu actions
        formatMenuItem.setOnAction(e -> formatSql());
        analyzeMenuItem.setOnAction(e -> analyzeSql());
        performanceMenuItem.setOnAction(e -> showPerformanceTips());
        settingsMenuItem.setOnAction(e -> showSettings());

        // Help menu actions
        gettingStartedMenuItem.setOnAction(e -> showGettingStarted());
        sqlReferenceMenuItem.setOnAction(e -> showSqlReference());
        shortcutsMenuItem.setOnAction(e -> showKeyboardShortcuts());
        updatesMenuItem.setOnAction(e -> checkForUpdates());
        aboutMenuItem.setOnAction(e -> showAbout());
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
                        updateTopicTitle(newValue.getValue());
                    }
                }
        );

        // Setup navigation buttons
        nextTopicBtn.setOnAction(e -> navigateToNextTopic());
        prevTopicBtn.setOnAction(e -> navigateToPreviousTopic());

        // Setup action buttons
        bookmarkBtn.setOnAction(e -> bookmarkTopic());
        noteBtn.setOnAction(e -> addNote());
        shareBtn.setOnAction(e -> shareTopic());
        searchBtn.setOnAction(e -> searchContent());
        printBtn.setOnAction(e -> printContent());

        // Select first topic by default
        Platform.runLater(() -> {
            topicsTreeView.getSelectionModel().selectFirst();
            updateLearningProgress();
        });
    }

    private void setupPracticeSection() {
        // Setup difficulty filter radio buttons
        difficultyGroup = new ToggleGroup();
        allLevelsRadio.setToggleGroup(difficultyGroup);
        easyRadio.setToggleGroup(difficultyGroup);
        mediumRadio.setToggleGroup(difficultyGroup);
        hardRadio.setToggleGroup(difficultyGroup);
        proRadio.setToggleGroup(difficultyGroup);

        // Handle difficulty filter changes
        difficultyGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                filterQuestionsByDifficulty();
            }
        });

        // Setup questions list
        loadAllQuestions();
        questionsList.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.intValue() >= 0) {
                        currentQuestionIndex = newValue.intValue();
                        List<PracticeQuestion> questions = practiceService.getAllQuestions();
                        if (newValue.intValue() < questions.size()) {
                            loadQuestion(questions.get(newValue.intValue()));
                        }
                    }
                }
        );

        // Setup action buttons
        hintBtn.setOnAction(e -> showHint());
        solutionBtn.setOnAction(e -> showSolution());
        formatCodeBtn.setOnAction(e -> formatSql());
        clearCodeBtn.setOnAction(e -> clearEditor());
        executeBtn.setOnAction(e -> executeSql());
        validateBtn.setOnAction(e -> validateSql());
        exportResultsBtn.setOnAction(e -> exportResults());

        // Setup database schema browser
        setupDatabaseSchemaBrowser();

        // Setup quick action buttons
        showTableBtn.setOnAction(e -> showSelectedTable());
        describeTableBtn.setOnAction(e -> describeSelectedTable());
        sampleDataBtn.setOnAction(e -> showSampleData());

        // Setup SQL editor (replace TextArea with CodeArea)
        setupAdvancedSqlEditor();

        // Setup results table
        setupResultsTable();

        // Load first question
        Platform.runLater(() -> {
            List<PracticeQuestion> questions = practiceService.getAllQuestions();
            if (!questions.isEmpty()) {
                questionsList.getSelectionModel().selectFirst();
                updatePracticeProgress();
                updateStatistics();
            }
        });
        showTablesBtn.setOnAction(e -> showTables());
        resetQuestionBtn.setOnAction(e -> resetQuestion());
    }

    private void setupAdvancedSqlEditor() {
        // Replace the TextArea with CodeArea for better SQL editing
        if (sqlCodeArea == null) {
            sqlCodeArea = new CodeArea();
            sqlCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(sqlCodeArea));
            sqlCodeArea.setStyle("-fx-font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', 'Consolas', monospace; -fx-font-size: 14px;");

            // Apply syntax highlighting
            sqlCodeArea.textProperty().addListener((obs, oldText, newText) -> {
                syntaxHighlighter.applySyntaxHighlighting(sqlCodeArea);
            });

            // Replace TextArea with CodeArea in parent
            if (sqlEditor.getParent() instanceof VBox) {
                VBox parent = (VBox) sqlEditor.getParent();
                int index = parent.getChildren().indexOf(sqlEditor);
                parent.getChildren().remove(sqlEditor);
                parent.getChildren().add(index, sqlCodeArea);
                VBox.setVgrow(sqlCodeArea, javafx.scene.layout.Priority.ALWAYS);
            }
        }
    }

    private void setupDatabaseSchemaBrowser() {
        // Setup schema tree view
        TreeItem<String> schemaRoot = new TreeItem<>("Database Schema");
        schemaRoot.setExpanded(true);
        schemaTreeView.setRoot(schemaRoot);
        schemaTreeView.setShowRoot(false);
    }

    private void setupResultsTable() {
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupStatusBar() {
        statusLabel.setText("Ready");
        updateConnectionStatus();
        updateUserProgressStatus();
    }

    private void setupClock() {
        clockTimer = new Timer(true);
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateClock());
            }
        }, 0, 1000);
    }

    // ===== LEARNING SECTION METHODS =====

    private void loadTopicContent(String topic) {
        String content = learningContentService.getTopicContent(topic);
        WebEngine webEngine = contentWebView.getEngine();
        webEngine.loadContent(content, "text/html");
        updateLearningProgress();
    }

    private void updateTopicTitle(String topic) {
        topicTitleLabel.setText(topic);
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

    // ===== PRACTICE SECTION METHODS =====

    private void loadAllQuestions() {
        List<PracticeQuestion> questions = practiceService.getAllQuestions();
        ObservableList<String> questionTitles = FXCollections.observableArrayList();
        questions.forEach(q -> questionTitles.add(String.format("[%s] %s",
                getDifficultyEmoji(q.getDifficulty()), q.getTitle())));
        questionsList.setItems(questionTitles);
    }

    private void filterQuestionsByDifficulty() {
        RadioButton selected = (RadioButton) difficultyGroup.getSelectedToggle();
        if (selected == null) return;

        String difficulty = selected.getText().toLowerCase();
        List<PracticeQuestion> allQuestions = practiceService.getAllQuestions();
        ObservableList<String> filteredTitles = FXCollections.observableArrayList();

        for (PracticeQuestion q : allQuestions) {
            boolean shouldInclude = false;
            if (selected == allLevelsRadio) {
                shouldInclude = true;
            } else if (selected == easyRadio && q.getDifficulty().equalsIgnoreCase("easy")) {
                shouldInclude = true;
            } else if (selected == mediumRadio && q.getDifficulty().equalsIgnoreCase("medium")) {
                shouldInclude = true;
            } else if (selected == hardRadio && q.getDifficulty().equalsIgnoreCase("hard")) {
                shouldInclude = true;
            } else if (selected == proRadio && q.getDifficulty().equalsIgnoreCase("pro")) {
                shouldInclude = true;
            }

            if (shouldInclude) {
                filteredTitles.add(String.format("[%s] %s",
                        getDifficultyEmoji(q.getDifficulty()), q.getTitle()));
            }
        }

        questionsList.setItems(filteredTitles);
    }

    private void loadQuestion(PracticeQuestion question) {
        currentQuestion = question;

        // Update question info
        currentQuestionLabel.setText(String.format("Question %d of %d",
                currentQuestionIndex + 1, practiceService.getAllQuestions().size()));
        difficultyLabel.setText(getDifficultyEmoji(question.getDifficulty()) + " " +
                question.getDifficulty().substring(0, 1).toUpperCase() +
                question.getDifficulty().substring(1));
        questionTitleLabel.setText(question.getTitle());

        // Load HTML content in a WebView for better formatting
        // Check if we have a WebView for question description
        WebView questionWebView = createQuestionWebView();

        // Replace the label with WebView for rich HTML content
        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    margin: 0; 
                    padding: 0;
                    background-color: white;
                    color: #0f172a;
                }
            </style>
        </head>
        <body>
        %s
        </body>
        </html>
        """.formatted(question.getDescription());

        questionWebView.getEngine().loadContent(htmlContent, "text/html");

        // Clear previous results
        resultsTable.getColumns().clear();
        resultsTable.getItems().clear();
        executionTimeLabel.setText("Execution time: 0ms");

        // Set example SQL if available
        if (question.getExampleSql() != null && !question.getExampleSql().isEmpty()) {
            if (sqlCodeArea != null) {
                sqlCodeArea.replaceText(question.getExampleSql());
            } else {
                sqlEditor.setText(question.getExampleSql());
            }
        }
    }


    private WebView createQuestionWebView() {
        WebView webView = new WebView();
        webView.setPrefHeight(300);
        webView.setMaxHeight(Double.MAX_VALUE);
        return webView;
    }

    // ===== SQL EXECUTION METHODS =====

    @FXML
    private void executeSql() {
        String sql = sqlCodeArea != null ? sqlCodeArea.getText().trim() : sqlEditor.getText().trim();
        if (sql.isEmpty()) {
            showWarning("Empty Query", "Please enter a SQL query to execute.");
            return;
        }

        statusLabel.setText("Executing...");
        executeBtn.setDisable(true);
        queryStartTime = System.currentTimeMillis();
        totalQueriesExecuted++;

        Task<DatabaseService.ExecutionResult> task = new Task<DatabaseService.ExecutionResult>() {
            @Override
            protected DatabaseService.ExecutionResult call() throws Exception {
                return databaseService.executeSql(sql);
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                DatabaseService.ExecutionResult result = task.getValue();
                long executionTime = System.currentTimeMillis() - queryStartTime;
                displayExecutionResult(result, executionTime);
                executeBtn.setDisable(false);
                statusLabel.setText(result.isSuccess() ? "Executed successfully" : "Execution failed");

                if (result.isSuccess()) {
                    successfulQueries++;
                }
                updateStatistics();
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                executeBtn.setDisable(false);
                statusLabel.setText("Execution failed");
                long executionTime = System.currentTimeMillis() - queryStartTime;
                executionTimeLabel.setText(String.format("Execution time: %dms", executionTime));
                logger.error("SQL execution task failed", task.getException());
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void validateSql() {
        String sql = sqlCodeArea != null ? sqlCodeArea.getText().trim() : sqlEditor.getText().trim();
        if (sql.isEmpty()) {
            showWarning("Empty Query", "Please enter a SQL query to validate.");
            return;
        }

        DatabaseService.ValidationResult result = databaseService.validateSql(sql);
        if (result.isValid()) {
            statusLabel.setText("✓ SQL syntax is valid");
            statusLabel.setStyle("-fx-text-fill: #059669;");
        } else {
            statusLabel.setText("✗ " + result.getMessage());
            statusLabel.setStyle("-fx-text-fill: #dc2626;");
        }
    }

    private void displayExecutionResult(DatabaseService.ExecutionResult result, long executionTime) {
        executionTimeLabel.setText(String.format("Execution time: %dms", executionTime));

        if (result.isSuccess()) {
            statusLabel.setText(result.getMessage());
            statusLabel.setStyle("-fx-text-fill: #059669;");

            if (result.getQueryResult() != null) {
                displayQueryResult(result.getQueryResult());
            }
        } else {
            statusLabel.setText(result.getMessage());
            statusLabel.setStyle("-fx-text-fill: #dc2626;");
            resultsTable.getColumns().clear();
            resultsTable.getItems().clear();
        }
    }

    private void displayQueryResult(DatabaseService.QueryResult queryResult) {
        // Clear existing columns and data
        resultsTable.getColumns().clear();
        resultsTable.getItems().clear();

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
            resultsTable.getColumns().add(column);
        }

        // Add data
        for (List<Object> row : queryResult.getRows()) {
            ObservableList<Object> observableRow = FXCollections.observableArrayList(row);
            resultsTable.getItems().add(observableRow);
        }
    }

    // ===== DATABASE SCHEMA METHODS =====

    private void loadDatabaseSchema() {
        if (databaseService == null) return;

        try {
            List<String> tables = databaseService.getTables();
            TreeItem<String> root = schemaTreeView.getRoot();
            root.getChildren().clear();

            for (String tableName : tables) {
                TreeItem<String> tableItem = new TreeItem<>("📊 " + tableName);

                // Add columns as children
                try {
                    List<DatabaseService.ColumnInfo> columns = databaseService.getTableColumns(tableName);
                    for (DatabaseService.ColumnInfo column : columns) {
                        TreeItem<String> columnItem = new TreeItem<>(
                                String.format("🔹 %s (%s)", column.getName(), column.getType())
                        );
                        tableItem.getChildren().add(columnItem);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to load columns for table: " + tableName, e);
                }

                root.getChildren().add(tableItem);
            }
        } catch (Exception e) {
            logger.error("Failed to load database schema", e);
        }
    }

    // ===== ACTION BUTTON METHODS =====

    private void showHint() {
        if (currentQuestion != null && currentQuestion.getHint() != null) {
            showInfo("Hint", currentQuestion.getHint());
        } else {
            showInfo("No Hint", "No hint available for this question.");
        }
    }

    private void showSolution() {
        if (currentQuestion != null && currentQuestion.getSolution() != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Solution");
            alert.setHeaderText("Correct Solution");
            alert.setContentText(currentQuestion.getSolution());
            alert.getDialogPane().setPrefWidth(600);
            alert.showAndWait();
        } else {
            showInfo("No Solution", "No solution available for this question.");
        }
    }

    private void clearEditor() {
        if (sqlCodeArea != null) {
            sqlCodeArea.clear();
        } else {
            sqlEditor.clear();
        }
    }

    private void showSelectedTable() {
        TreeItem<String> selected = schemaTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().startsWith("📊")) {
            String tableName = selected.getValue().substring(2).trim();
            String sql = "SELECT * FROM " + tableName + " LIMIT 100;";
            if (sqlCodeArea != null) {
                sqlCodeArea.replaceText(sql);
            } else {
                sqlEditor.setText(sql);
            }
        }
    }

    private void describeSelectedTable() {
        TreeItem<String> selected = schemaTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().startsWith("📊")) {
            String tableName = selected.getValue().substring(2).trim();
            String sql = "DESCRIBE " + tableName + ";";
            if (sqlCodeArea != null) {
                sqlCodeArea.replaceText(sql);
            } else {
                sqlEditor.setText(sql);
            }
        }
    }

    private void showSampleData() {
        TreeItem<String> selected = schemaTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().startsWith("📊")) {
            String tableName = selected.getValue().substring(2).trim();
            String sql = "SELECT * FROM " + tableName + " LIMIT 5;";
            if (sqlCodeArea != null) {
                sqlCodeArea.replaceText(sql);
            } else {
                sqlEditor.setText(sql);
            }
        }
    }

    // ===== PROGRESS AND STATISTICS METHODS =====

    private void updateLearningProgress() {
        TreeItem<String> root = topicsTreeView.getRoot();
        int totalTopics = countLeafItems(root);
        TreeItem<String> selected = topicsTreeView.getSelectionModel().getSelectedItem();
        int currentPosition = getItemPosition(root, selected, 0);

        double progress = totalTopics > 0 ? (double) currentPosition / totalTopics : 0;
        learningProgressBar.setProgress(progress);
        progressLabel.setText(String.format("Topic %d of %d", currentPosition, totalTopics));
        overallProgressLabel.setText(String.format("%.1f%%", progress * 100));

        updateUserProgressStatus();
    }

    private void updatePracticeProgress() {
        int totalQuestions = practiceService.getTotalQuestions();
        int completedQuestions = practiceService.getCompletedQuestionsCount();

        double progress = totalQuestions > 0 ? (double) completedQuestions / totalQuestions : 0;
        practiceProgressBar.setProgress(progress);
        practiceProgressLabel.setText(String.format("%d/%d", completedQuestions, totalQuestions));

        updateUserProgressStatus();
    }

    private void updateStatistics() {
        totalQueriesLabel.setText(String.valueOf(totalQueriesExecuted));

        double successRate = totalQueriesExecuted > 0 ?
                (double) successfulQueries / totalQueriesExecuted * 100 : 0;
        successRateLabel.setText(String.format("%.1f%%", successRate));

        // You can implement streak tracking logic here
        bestStreakLabel.setText("5"); // Placeholder
    }

    private void updateConnectionStatus() {
        if (databaseService != null) {
            connectionStatusLabel.setText("Database: Connected");
            connectionStatusLabel.setStyle("-fx-text-fill: #059669;");
        } else {
            connectionStatusLabel.setText("Database: Disconnected");
            connectionStatusLabel.setStyle("-fx-text-fill: #dc2626;");
        }
    }

    private void updateUserProgressStatus() {
        // Calculate overall progress combining learning and practice
        double learningProgress = learningProgressBar.getProgress();
        double practiceProgress = practiceProgressBar.getProgress();
        double overallProgress = (learningProgress + practiceProgress) / 2;

        userProgressStatusLabel.setText(String.format("Progress: %.1f%%", overallProgress * 100));
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        timeLabel.setText(timeString);
    }

    // ===== MENU ACTION METHODS (Stubs - implement as needed) =====

    private void newQuery() {
        clearEditor();
        statusLabel.setText("New query created");
    }

    private void openQuery() {
        // TODO: Implement file chooser to open SQL files
        statusLabel.setText("Open query not implemented yet");
    }

    private void saveQuery() {
        // TODO: Implement file chooser to save SQL files
        statusLabel.setText("Save query not implemented yet");
    }

    private void exportProgress() {
        // TODO: Implement progress export
        statusLabel.setText("Export progress not implemented yet");
    }

    private void exitApplication() {
        Platform.exit();
    }

    private void undo() {
        if (sqlCodeArea != null) {
            sqlCodeArea.undo();
        }
    }

    private void redo() {
        if (sqlCodeArea != null) {
            sqlCodeArea.redo();
        }
    }

    private void cut() {
        if (sqlCodeArea != null) {
            sqlCodeArea.cut();
        }
    }

    private void copy() {
        if (sqlCodeArea != null) {
            sqlCodeArea.copy();
        }
    }

    private void paste() {
        if (sqlCodeArea != null) {
            sqlCodeArea.paste();
        }
    }

    private void find() {
        statusLabel.setText("Find not implemented yet");
    }

    private void replace() {
        statusLabel.setText("Replace not implemented yet");
    }

    private void showDatabaseBrowser() {
        mainTabPane.getSelectionModel().select(explorerTab);
    }

    private void showQueryHistory() {
        statusLabel.setText("Query history not implemented yet");
    }

    private void toggleLineNumbers() {
        // Line numbers are already implemented in CodeArea
        statusLabel.setText("Line numbers toggled");
    }

    private void toggleWordWrap() {
        if (sqlCodeArea != null) {
            sqlCodeArea.setWrapText(!sqlCodeArea.isWrapText());
        }
    }

    private void zoomIn() {
        statusLabel.setText("Zoom in not implemented yet");
    }

    private void zoomOut() {
        statusLabel.setText("Zoom out not implemented yet");
    }

    private void resetZoom() {
        statusLabel.setText("Reset zoom not implemented yet");
    }

    private void toggleFullScreen() {
        statusLabel.setText("Full screen toggle not implemented yet");
    }

    private void formatSql() {
        statusLabel.setText("SQL formatter not implemented yet");
    }

    private void analyzeSql() {
        statusLabel.setText("SQL analyzer not implemented yet");
    }

    private void showPerformanceTips() {
        statusLabel.setText("Performance tips not implemented yet");
    }

    private void showSettings() {
        statusLabel.setText("Settings not implemented yet");
    }

    private void showGettingStarted() {
        statusLabel.setText("Getting started guide not implemented yet");
    }

    private void showSqlReference() {
        statusLabel.setText("SQL reference not implemented yet");
    }

    private void showKeyboardShortcuts() {
        statusLabel.setText("Keyboard shortcuts not implemented yet");
    }

    private void checkForUpdates() {
        statusLabel.setText("Update check not implemented yet");
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("SQL Learning Application");
        alert.setContentText("A comprehensive desktop application for learning SQL.\n\nVersion: 2.0\nDeveloped with JavaFX");
        alert.showAndWait();
    }

    private void bookmarkTopic() {
        statusLabel.setText("Topic bookmarked");
    }

    private void addNote() {
        statusLabel.setText("Add note feature not implemented yet");
    }

    private void shareTopic() {
        statusLabel.setText("Share topic feature not implemented yet");
    }

    private void searchContent() {
        statusLabel.setText("Search content feature not implemented yet");
    }

    private void printContent() {
        statusLabel.setText("Print content feature not implemented yet");
    }

    private void exportResults() {
        statusLabel.setText("Export results feature not implemented yet");
    }

    // ===== UTILITY METHODS =====

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

    // Update the getDifficultyEmoji method in MainController:
    private String getDifficultyEmoji(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "easy" -> "🟢";
            case "medium" -> "🟡";
            case "hard" -> "🔴";
            case "pro" -> "⭐";
            default -> "⚪";
        };
    }

    // Add this method to get difficulty counts for UI:
    private void updateDifficultyCountsInUI() {
        Map<String, Integer> totalCounts = practiceService.getQuestionCountsByDifficulty();
        Map<String, Integer> completedCounts = practiceService.getCompletedCountsByDifficulty();

        // Update UI labels (you can add these to your FXML if needed)
        Platform.runLater(() -> {
            // Example: update difficulty filter labels with counts
            easyRadio.setText(String.format("🟢 Easy (%d/%d)",
                    completedCounts.get("easy"), totalCounts.get("easy")));
            mediumRadio.setText(String.format("🟡 Medium (%d/%d)",
                    completedCounts.get("medium"), totalCounts.get("medium")));
            hardRadio.setText(String.format("🔴 Hard (%d/%d)",
                    completedCounts.get("hard"), totalCounts.get("hard")));
            proRadio.setText(String.format("⭐ Pro (%d/%d)",
                    completedCounts.get("pro"), totalCounts.get("pro")));
        });
    }

    // Add these methods to your MainController class

    @FXML
    private Button showTablesBtn;
    @FXML
    private Button resetQuestionBtn;

    // Method implementations
    private void showTables() {
        if (currentQuestion != null) {
            // Extract table information from question description and show in a popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Tables");
            alert.setHeaderText("Tables used in this question:");

            // You can enhance this to parse actual table schemas from the question
            String tableInfo = """
        Available Tables:
        
        📊 employees
        📊 departments
        📊 projects
        📊 customers
        📊 orders
        
        Use the SQL Editor to query these tables.
        """;

            alert.setContentText(tableInfo);
            alert.getDialogPane().setPrefWidth(400);
            alert.showAndWait();
        }
    }

    private void resetQuestion() {
        if (currentQuestion != null) {
            // Clear the SQL editor
            clearEditor();
            // Reset results
            resultsTable.getColumns().clear();
            resultsTable.getItems().clear();
            executionTimeLabel.setText("Execution time: 0ms");
            statusLabel.setText("Question reset - ready to start");
        }
    }

    // ===== CLEANUP =====

    public void shutdown() {
        if (clockTimer != null) {
            clockTimer.cancel();
        }
    }

}