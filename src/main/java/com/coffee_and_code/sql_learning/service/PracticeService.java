package com.coffee_and_code.sql_learning.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced service for managing practice questions and user progress
 */
public class PracticeService {
    private static final Logger logger = LoggerFactory.getLogger(PracticeService.class);
    private List<PracticeQuestion> questions;
    private Set<String> completedQuestions;
    private ObjectMapper objectMapper;
    private Map<String, Integer> userStats;

    public PracticeService() {
        this.objectMapper = new ObjectMapper();
        this.completedQuestions = new HashSet<>();
        this.userStats = new HashMap<>();
        initializeStats();
        loadQuestions();
        loadUserProgress();
    }

    /**
     * Initialize user statistics
     */
    private void initializeStats() {
        userStats.put("totalQueriesExecuted", 0);
        userStats.put("successfulQueries", 0);
        userStats.put("bestStreak", 0);
        userStats.put("currentStreak", 0);
    }

    /**
     * Load practice questions from resources
     */
    private void loadQuestions() {
        questions = new ArrayList<>();

        // Easy Questions
        questions.add(createEasyQuestionCombineTwoTables());
        questions.add(createEasyQuestion2());
        questions.add(createEasyQuestion3());
        questions.add(createEasyQuestion4());
        questions.add(createEasyQuestion5());

        // Medium Questions
        questions.add(createMediumQuestion1());
        questions.add(createMediumQuestion2());
        questions.add(createMediumQuestion3());
        questions.add(createMediumQuestion4());
        questions.add(createMediumQuestion5());

        // Hard Questions
        questions.add(createHardQuestion1());
        questions.add(createHardQuestion2());
        questions.add(createHardQuestion3());
        questions.add(createHardQuestion4());

        // Pro Questions
        questions.add(createProQuestion1());
        questions.add(createProQuestion2());
        questions.add(createProQuestion3());
        questions.add(createProQuestion4());

        logger.info("Loaded {} practice questions", questions.size());
    }

    /**
     * Load user progress from file
     */
    private void loadUserProgress() {
        try {
            // Try to load from user_progress.json
            InputStream inputStream = getClass().getResourceAsStream("/user_progress.json");
            if (inputStream != null) {
                Map<String, Object> progressData = objectMapper.readValue(inputStream, Map.class);

                if (progressData.containsKey("completedQuestions")) {
                    List<String> completed = (List<String>) progressData.get("completedQuestions");
                    completedQuestions.addAll(completed);
                }

                if (progressData.containsKey("userStats")) {
                    Map<String, Object> stats = (Map<String, Object>) progressData.get("userStats");
                    stats.forEach((key, value) -> {
                        if (value instanceof Number) {
                            userStats.put(key, ((Number) value).intValue());
                        }
                    });
                }
            }
        } catch (IOException e) {
            logger.warn("Could not load user progress, starting fresh", e);
        }
    }

    /**
     * Save user progress to file
     */
    public void saveUserProgress() {
        try {
            Map<String, Object> progressData = new HashMap<>();
            progressData.put("completedQuestions", new ArrayList<>(completedQuestions));
            progressData.put("userStats", userStats);
            progressData.put("lastUpdated", new Date().toString());

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new java.io.File("user_progress.json"), progressData);
        } catch (IOException e) {
            logger.error("Failed to save user progress", e);
        }
    }

    /**
     * Get all practice questions
     */
    public List<PracticeQuestion> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * Get questions by difficulty level (string-based for UI compatibility)
     */
    public List<PracticeQuestion> getQuestionsByDifficulty(String difficulty) {
        if ("all".equalsIgnoreCase(difficulty)) {
            return getAllQuestions();
        }

        return questions.stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());
    }

    /**
     * Get questions by difficulty level (enum-based)
     */
    public List<PracticeQuestion> getQuestionsByDifficulty(DifficultyLevel level) {
        return questions.stream()
                .filter(q -> q.getDifficultyEnum() == level)
                .collect(Collectors.toList());
    }

    /**
     * Get questions count by difficulty
     */
    public Map<String, Integer> getQuestionCountsByDifficulty() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("easy", (int) questions.stream().filter(q -> "easy".equals(q.getDifficulty())).count());
        counts.put("medium", (int) questions.stream().filter(q -> "medium".equals(q.getDifficulty())).count());
        counts.put("hard", (int) questions.stream().filter(q -> "hard".equals(q.getDifficulty())).count());
        counts.put("pro", (int) questions.stream().filter(q -> "pro".equals(q.getDifficulty())).count());
        return counts;
    }

    /**
     * Get total number of questions
     */
    public int getTotalQuestions() {
        return questions.size();
    }

    /**
     * Get number of completed questions
     */
    public int getCompletedQuestionsCount() {
        return completedQuestions.size();
    }

    /**
     * Get completed questions count by difficulty
     */
    public Map<String, Integer> getCompletedCountsByDifficulty() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("easy", 0);
        counts.put("medium", 0);
        counts.put("hard", 0);
        counts.put("pro", 0);

        for (String questionId : completedQuestions) {
            PracticeQuestion question = getQuestionById(questionId);
            if (question != null) {
                String difficulty = question.getDifficulty();
                counts.put(difficulty, counts.get(difficulty) + 1);
            }
        }

        return counts;
    }

    /**
     * Get question by ID
     */
    public PracticeQuestion getQuestionById(String questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Mark a question as completed
     */
    public void markQuestionCompleted(String questionId) {
        if (!completedQuestions.contains(questionId)) {
            completedQuestions.add(questionId);
            incrementCurrentStreak();
            saveUserProgress();
            logger.info("Question {} marked as completed", questionId);
        }
    }

    /**
     * Check if a question is completed
     */
    public boolean isQuestionCompleted(String questionId) {
        return completedQuestions.contains(questionId);
    }

    /**
     * Update user statistics
     */
    public void updateStats(String statName, int value) {
        userStats.put(statName, value);
        saveUserProgress();
    }

    /**
     * Get user statistic
     */
    public int getStat(String statName) {
        return userStats.getOrDefault(statName, 0);
    }

    /**
     * Increment current streak
     */
    private void incrementCurrentStreak() {
        int currentStreak = userStats.get("currentStreak") + 1;
        userStats.put("currentStreak", currentStreak);

        int bestStreak = userStats.get("bestStreak");
        if (currentStreak > bestStreak) {
            userStats.put("bestStreak", currentStreak);
        }
    }

    /**
     * Reset current streak
     */
    public void resetCurrentStreak() {
        userStats.put("currentStreak", 0);
        saveUserProgress();
    }

    /**
     * Check if user's answer is correct
     */
    public boolean checkAnswer(PracticeQuestion question, DatabaseService.QueryResult userResult) {
        if (question.getExpectedResult() == null) {
            return true; // No expected result to compare
        }

        return compareResults(question.getExpectedResult(), userResult);
    }

    /**
     * Compare two query results
     */
    private boolean compareResults(DatabaseService.QueryResult expected, DatabaseService.QueryResult actual) {
        if (expected.getRowCount() != actual.getRowCount()) {
            return false;
        }

        if (expected.getColumnCount() != actual.getColumnCount()) {
            return false;
        }

        // Compare column names (case-insensitive)
        List<String> expectedColumns = expected.getColumnNames().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<String> actualColumns = actual.getColumnNames().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        if (!expectedColumns.equals(actualColumns)) {
            return false;
        }

        // Compare data rows (simplified comparison)
        for (int i = 0; i < expected.getRowCount(); i++) {
            List<Object> expectedRow = expected.getRows().get(i);
            List<Object> actualRow = actual.getRows().get(i);

            if (!compareRows(expectedRow, actualRow)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare two rows with type flexibility
     */
    private boolean compareRows(List<Object> expected, List<Object> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            Object expectedValue = expected.get(i);
            Object actualValue = actual.get(i);

            // Handle nulls
            if (expectedValue == null && actualValue == null) {
                continue;
            }
            if (expectedValue == null || actualValue == null) {
                return false;
            }

            // Convert to strings for comparison (handles different number types)
            if (!expectedValue.toString().equals(actualValue.toString())) {
                return false;
            }
        }

        return true;
    }

    // ===== QUESTION CREATION METHODS =====

// Update the createEasyQuestionCombineTwoTables method with better HTML formatting:

    private PracticeQuestion createEasyQuestionCombineTwoTables() {
        return new PracticeQuestion(
                "Easy-175",
                "Combine Two Tables",
                // Enhanced HTML-formatted description
                """
                <div style='font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;'>
                    <h3 style='color: #2563eb; margin-bottom: 20px;'>Problem Description</h3>
                    <p>Write a solution to report the <strong>first name</strong>, <strong>last name</strong>, <strong>city</strong>, and <strong>state</strong> of each person in the Person table.</p>
                    <p>If the address of a personId is not present in the Address table, report <code>null</code> instead.</p>
                    
                    <h4 style='color: #059669; margin-top: 30px; margin-bottom: 15px;'>📊 Table: Person</h4>
                    <table style='border-collapse: collapse; width: 100%; margin-bottom: 20px; border: 2px solid #e2e8f0;'>
                        <thead>
                            <tr style='background-color: #f8fafc;'>
                                <th style='border: 1px solid #cbd5e1; padding: 12px; text-align: left; font-weight: 600;'>Column Name</th>
                                <th style='border: 1px solid #cbd5e1; padding: 12px; text-align: left; font-weight: 600;'>Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>personId</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>int</td>
                            </tr>
                            <tr style='background-color: #f8fafc;'>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>lastName</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>varchar</td>
                            </tr>
                            <tr>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>firstName</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>varchar</td>
                            </tr>
                        </tbody>
                    </table>
                    <p><small><em>personId is the primary key (column with unique values) for this table.</em></small></p>
                    
                    <h4 style='color: #059669; margin-top: 30px; margin-bottom: 15px;'>📊 Table: Address</h4>
                    <table style='border-collapse: collapse; width: 100%; margin-bottom: 20px; border: 2px solid #e2e8f0;'>
                        <thead>
                            <tr style='background-color: #f8fafc;'>
                                <th style='border: 1px solid #cbd5e1; padding: 12px; text-align: left; font-weight: 600;'>Column Name</th>
                                <th style='border: 1px solid #cbd5e1; padding: 12px; text-align: left; font-weight: 600;'>Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>addressId</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>int</td>
                            </tr>
                            <tr style='background-color: #f8fafc;'>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>personId</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>int</td>
                            </tr>
                            <tr>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>city</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>varchar</td>
                            </tr>
                            <tr style='background-color: #f8fafc;'>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'><code>state</code></td>
                                <td style='border: 1px solid #cbd5e1; padding: 10px;'>varchar</td>
                            </tr>
                        </tbody>
                    </table>
                    <p><small><em>addressId is the primary key (column with unique values) for this table.</em></small></p>
                    
                    <h4 style='color: #d97706; margin-top: 30px; margin-bottom: 15px;'>📝 Example</h4>
                    
                    <h5 style='margin-bottom: 10px;'>Input - Person table:</h5>
                    <table style='border-collapse: collapse; width: 100%; margin-bottom: 15px; border: 2px solid #fbbf24;'>
                        <thead>
                            <tr style='background-color: #fef3c7;'>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>personId</th>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>lastName</th>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>firstName</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>1</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>Wang</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>Allen</td>
                            </tr>
                            <tr style='background-color: #fef3c7;'>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>2</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>Alice</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>Bob</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <h5 style='margin-bottom: 10px;'>Input - Address table:</h5>
                    <table style='border-collapse: collapse; width: 100%; margin-bottom: 15px; border: 2px solid #fbbf24;'>
                        <thead>
                            <tr style='background-color: #fef3c7;'>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>addressId</th>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>personId</th>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>city</th>
                                <th style='border: 1px solid #f59e0b; padding: 10px; text-align: center;'>state</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>1</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>2</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>New York City</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>New York</td>
                            </tr>
                            <tr style='background-color: #fef3c7;'>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>2</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>3</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>Leetcode</td>
                                <td style='border: 1px solid #f59e0b; padding: 8px; text-align: center;'>California</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <h5 style='margin-bottom: 10px;'>Expected Output:</h5>
                    <table style='border-collapse: collapse; width: 100%; margin-bottom: 20px; border: 2px solid #10b981;'>
                        <thead>
                            <tr style='background-color: #d1fae5;'>
                                <th style='border: 1px solid #10b981; padding: 10px; text-align: center;'>firstName</th>
                                <th style='border: 1px solid #10b981; padding: 10px; text-align: center;'>lastName</th>
                                <th style='border: 1px solid #10b981; padding: 10px; text-align: center;'>city</th>
                                <th style='border: 1px solid #10b981; padding: 10px; text-align: center;'>state</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>Allen</td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>Wang</td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'><em>null</em></td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'><em>null</em></td>
                            </tr>
                            <tr style='background-color: #d1fae5;'>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>Bob</td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>Alice</td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>New York City</td>
                                <td style='border: 1px solid #10b981; padding: 8px; text-align: center;'>New York</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <div style='background-color: #f0f9ff; padding: 15px; border-left: 4px solid #2563eb; margin-top: 20px;'>
                        <h5 style='color: #1e40af; margin-bottom: 10px;'>💡 Key Concepts:</h5>
                        <ul style='margin: 0; padding-left: 20px;'>
                            <li>Use <strong>LEFT JOIN</strong> to include all persons</li>
                            <li>Join tables using the <code>personId</code> foreign key</li>
                            <li>Missing addresses will show as <em>null</em> values</li>
                        </ul>
                    </div>
                </div>
                """,
                "-- Write your SQL query here\nSELECT p.firstName, p.lastName, a.city, a.state \nFROM Person p \nLEFT JOIN Address a ON p.personId = a.personId;",
                "easy",
                "💡 Use LEFT JOIN to include all persons and get null for missing addresses.",
                "SELECT p.firstName, p.lastName, a.city, a.state FROM Person p LEFT JOIN Address a ON p.personId = a.personId;",
                null
        );
    }

    private PracticeQuestion createEasyQuestion2() {
        return new PracticeQuestion(
                "easy_2",
                "Find High Salary Employees",
                """
                Write a SQL query to find all employees with salary greater than 50000.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | salary      | decimal |
                +-------------+---------+
                
                Expected output: All employees with salary > 50000
                """,
                "-- Write your SQL query here\nSELECT * FROM employees WHERE salary > 50000;",
                "easy",
                "💡 Use WHERE clause with > operator to filter by salary.",
                "SELECT * FROM employees WHERE salary > 50000;",
                null
        );
    }

    private PracticeQuestion createEasyQuestion3() {
        return new PracticeQuestion(
                "easy_3",
                "Count Total Employees",
                """
                Write a SQL query to count the total number of employees.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                +-------------+---------+
                
                Expected output: A single number showing total count
                """,
                "-- Write your SQL query here\nSELECT COUNT(*) as total_employees FROM employees;",
                "easy",
                "💡 Use COUNT(*) function to count all rows.",
                "SELECT COUNT(*) FROM employees;",
                null
        );
    }

    private PracticeQuestion createEasyQuestion4() {
        return new PracticeQuestion(
                "easy_4",
                "Select Employees from Engineering",
                """
                Write a SQL query to find all employees who work in the 'Engineering' department.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | first_name    | varchar |
                | last_name     | varchar |
                | department_id | int     |
                +---------------+---------+
                
                Table: departments
                +-----------------+---------+
                | Column Name     | Type    |
                +-----------------+---------+
                | id              | int     |
                | department_name | varchar |
                +-----------------+---------+
                
                Expected output: All employees in Engineering department
                """,
                "-- Write your SQL query here\nSELECT e.* FROM employees e \nJOIN departments d ON e.department_id = d.id \nWHERE d.department_name = 'Engineering';",
                "easy",
                "💡 Use JOIN to connect tables and WHERE to filter by department name.",
                "SELECT e.* FROM employees e JOIN departments d ON e.department_id = d.id WHERE d.department_name = 'Engineering';",
                null
        );
    }

    private PracticeQuestion createEasyQuestion5() {
        return new PracticeQuestion(
                "easy_5",
                "Find Employees Hired After 2020",
                """
                Write a SQL query to find all employees hired after January 1, 2020.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | hire_date   | date    |
                +-------------+---------+
                
                Expected output: All employees hired after 2020-01-01
                """,
                "-- Write your SQL query here\nSELECT * FROM employees WHERE hire_date > '2020-01-01';",
                "easy",
                "💡 Use WHERE clause with date comparison.",
                "SELECT * FROM employees WHERE hire_date > '2020-01-01';",
                null
        );
    }

    private PracticeQuestion createMediumQuestion1() {
        return new PracticeQuestion(
                "medium_1",
                "Average Salary by Department",
                """
                Write a SQL query to find the average salary for each department.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | salary        | decimal |
                | department_id | int     |
                +---------------+---------+
                
                Table: departments
                +-----------------+---------+
                | Column Name     | Type    |
                +-----------------+---------+
                | id              | int     |
                | department_name | varchar |
                +-----------------+---------+
                
                Expected output: Department name and average salary
                """,
                "-- Write your SQL query here\nSELECT d.department_name, AVG(e.salary) as avg_salary \nFROM employees e \nJOIN departments d ON e.department_id = d.id \nGROUP BY d.id, d.department_name;",
                "medium",
                "💡 Use GROUP BY to group by department and AVG() to calculate average salary.",
                "SELECT d.department_name, AVG(e.salary) as avg_salary FROM employees e JOIN departments d ON e.department_id = d.id GROUP BY d.id, d.department_name;",
                null
        );
    }

    private PracticeQuestion createMediumQuestion2() {
        return new PracticeQuestion(
                "medium_2",
                "Top 5 Highest Paid Employees",
                """
                Write a SQL query to find the top 5 highest paid employees with their names and salaries.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | salary      | decimal |
                +-------------+---------+
                
                Expected output: Top 5 employees sorted by salary (highest first)
                """,
                "-- Write your SQL query here\nSELECT first_name, last_name, salary \nFROM employees \nORDER BY salary DESC \nLIMIT 5;",
                "medium",
                "💡 Use ORDER BY DESC to sort by salary in descending order and LIMIT to get top 5.",
                "SELECT first_name, last_name, salary FROM employees ORDER BY salary DESC LIMIT 5;",
                null
        );
    }

    private PracticeQuestion createMediumQuestion3() {
        return new PracticeQuestion(
                "medium_3",
                "Employees with Names Starting with 'J'",
                """
                Write a SQL query to find all employees whose first name starts with 'J'.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                +-------------+---------+
                
                Expected output: All employees with first name starting with 'J'
                """,
                "-- Write your SQL query here\nSELECT * FROM employees WHERE first_name LIKE 'J%';",
                "medium",
                "💡 Use LIKE operator with wildcard % to match names starting with 'J'.",
                "SELECT * FROM employees WHERE first_name LIKE 'J%';",
                null
        );
    }

    private PracticeQuestion createMediumQuestion4() {
        return new PracticeQuestion(
                "medium_4",
                "Department Employee Count",
                """
                Write a SQL query to show each department with the number of employees in it.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | department_id | int     |
                +---------------+---------+
                
                Table: departments
                +-----------------+---------+
                | Column Name     | Type    |
                +-----------------+---------+
                | id              | int     |
                | department_name | varchar |
                +-----------------+---------+
                
                Expected output: Department name and employee count
                """,
                "-- Write your SQL query here\nSELECT d.department_name, COUNT(e.id) as employee_count \nFROM departments d \nLEFT JOIN employees e ON d.id = e.department_id \nGROUP BY d.id, d.department_name;",
                "medium",
                "💡 Use LEFT JOIN to include departments with 0 employees and COUNT() to count employees.",
                "SELECT d.department_name, COUNT(e.id) as employee_count FROM departments d LEFT JOIN employees e ON d.id = e.department_id GROUP BY d.id, d.department_name;",
                null
        );
    }

    private PracticeQuestion createMediumQuestion5() {
        return new PracticeQuestion(
                "medium_5",
                "Employees Hired This Year",
                """
                Write a SQL query to find employees hired in the current year.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | hire_date   | date    |
                +-------------+---------+
                
                Expected output: All employees hired in the current year
                """,
                "-- Write your SQL query here\nSELECT * FROM employees WHERE YEAR(hire_date) = YEAR(CURDATE());",
                "medium",
                "💡 Use YEAR() function to extract year from dates and CURDATE() for current date.",
                "SELECT * FROM employees WHERE YEAR(hire_date) = YEAR(CURDATE());",
                null
        );
    }

    private PracticeQuestion createHardQuestion1() {
        return new PracticeQuestion(
                "hard_1",
                "Department with Most Employees",
                """
                Write a SQL query to find which department has the most employees.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | department_id | int     |
                +---------------+---------+
                
                Table: departments
                +-----------------+---------+
                | Column Name     | Type    |
                +-----------------+---------+
                | id              | int     |
                | department_name | varchar |
                +-----------------+---------+
                
                Expected output: Department name with highest employee count
                """,
                "-- Write your SQL query here\nSELECT d.department_name, COUNT(e.id) as employee_count \nFROM departments d \nJOIN employees e ON d.id = e.department_id \nGROUP BY d.id, d.department_name \nORDER BY employee_count DESC \nLIMIT 1;",
                "hard",
                "🔥 Group by department, count employees, order by count descending, and take the first result.",
                "SELECT d.department_name, COUNT(e.id) as employee_count FROM departments d JOIN employees e ON d.id = e.department_id GROUP BY d.id, d.department_name ORDER BY employee_count DESC LIMIT 1;",
                null
        );
    }

    private PracticeQuestion createHardQuestion2() {
        return new PracticeQuestion(
                "hard_2",
                "Employees Earning More Than Average",
                """
                Write a SQL query to find employees who earn more than the average salary.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | salary      | decimal |
                +-------------+---------+
                
                Expected output: All employees with salary above average
                """,
                "-- Write your SQL query here\nSELECT * FROM employees \nWHERE salary > (SELECT AVG(salary) FROM employees);",
                "hard",
                "🔥 Use a subquery to calculate the average salary and compare it in the WHERE clause.",
                "SELECT * FROM employees WHERE salary > (SELECT AVG(salary) FROM employees);",
                null
        );
    }

    private PracticeQuestion createHardQuestion3() {
        return new PracticeQuestion(
                "hard_3",
                "Second Highest Salary",
                """
                Write a SQL query to find the second highest salary.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | salary      | decimal |
                +-------------+---------+
                
                Expected output: The second highest salary value
                """,
                "-- Write your SQL query here\nSELECT MAX(salary) as second_highest \nFROM employees \nWHERE salary < (SELECT MAX(salary) FROM employees);",
                "hard",
                "🔥 Find the maximum salary that is less than the overall maximum salary.",
                "SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees);",
                null
        );
    }

    private PracticeQuestion createHardQuestion4() {
        return new PracticeQuestion(
                "hard_4",
                "Employees with No Manager",
                """
                Write a SQL query to find all employees who don't have a manager.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | manager_id  | int     |
                +-------------+---------+
                
                Expected output: All employees where manager_id is NULL
                """,
                "-- Write your SQL query here\nSELECT * FROM employees WHERE manager_id IS NULL;",
                "hard",
                "🔥 Use IS NULL to find employees without managers.",
                "SELECT * FROM employees WHERE manager_id IS NULL;",
                null
        );
    }

    private PracticeQuestion createProQuestion1() {
        return new PracticeQuestion(
                "pro_1",
                "Complex Join with Aggregation",
                """
                Write a SQL query to find departments with their employee count and average salary, 
                only for departments with more than 2 employees.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | salary        | decimal |
                | department_id | int     |
                +---------------+---------+
                
                Table: departments
                +-----------------+---------+
                | Column Name     | Type    |
                +-----------------+---------+
                | id              | int     |
                | department_name | varchar |
                +-----------------+---------+
                
                Expected output: Department name, employee count, and average salary (only departments with >2 employees)
                """,
                "-- Write your SQL query here\nSELECT d.department_name, \n       COUNT(e.id) as employee_count, \n       AVG(e.salary) as avg_salary \nFROM departments d \nJOIN employees e ON d.id = e.department_id \nGROUP BY d.id, d.department_name \nHAVING COUNT(e.id) > 2;",
                "pro",
                "⭐ Use JOIN to connect tables, GROUP BY for aggregation, and HAVING to filter groups.",
                "SELECT d.department_name, COUNT(e.id) as employee_count, AVG(e.salary) as avg_salary FROM departments d JOIN employees e ON d.id = e.department_id GROUP BY d.id, d.department_name HAVING COUNT(e.id) > 2;",
                null
        );
    }

    private PracticeQuestion createProQuestion2() {
        return new PracticeQuestion(
                "pro_2",
                "Window Function - Rank Employees",
                """
                Write a SQL query to rank employees by salary within their department.
                
                Table: employees
                +---------------+---------+
                | Column Name   | Type    |
                +---------------+---------+
                | id            | int     |
                | first_name    | varchar |
                | last_name     | varchar |
                | salary        | decimal |
                | department_id | int     |
                +---------------+---------+
                
                Expected output: Employee details with salary rank within department
                """,
                "-- Write your SQL query here\nSELECT first_name, \n       last_name, \n       salary, \n       department_id, \n       RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as salary_rank \nFROM employees;",
                "pro",
                "⭐ Use RANK() window function with PARTITION BY and ORDER BY clauses.",
                "SELECT first_name, last_name, salary, department_id, RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as salary_rank FROM employees;",
                null
        );
    }

    private PracticeQuestion createProQuestion3() {
        return new PracticeQuestion(
                "pro_3",
                "Self Join - Find Manager Hierarchy",
                """
                Write a SQL query to find all employees and their managers.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | manager_id  | int     |
                +-------------+---------+
                
                Expected output: Employee name and their manager's name
                """,
                "-- Write your SQL query here\nSELECT CONCAT(e.first_name, ' ', e.last_name) as employee_name, \n       CONCAT(m.first_name, ' ', m.last_name) as manager_name \nFROM employees e \nLEFT JOIN employees m ON e.manager_id = m.id;",
                "pro",
                "⭐ Use self-join with LEFT JOIN to include employees without managers.",
                "SELECT CONCAT(e.first_name, ' ', e.last_name) as employee_name, CONCAT(m.first_name, ' ', m.last_name) as manager_name FROM employees e LEFT JOIN employees m ON e.manager_id = m.id;",
                null
        );
    }

    private PracticeQuestion createProQuestion4() {
        return new PracticeQuestion(
                "pro_4",
                "Running Total of Salaries",
                """
                Write a SQL query to calculate running total of salaries ordered by employee ID.
                
                Table: employees
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | id          | int     |
                | first_name  | varchar |
                | last_name   | varchar |
                | salary      | decimal |
                +-------------+---------+
                
                Expected output: Employee details with running total of salaries
                """,
                "-- Write your SQL query here\nSELECT id, \n       first_name, \n       last_name, \n       salary, \n       SUM(salary) OVER (ORDER BY id) as running_total \nFROM employees \nORDER BY id;",
                "pro",
                "⭐ Use SUM() window function with ORDER BY to calculate running total.",
                "SELECT id, first_name, last_name, salary, SUM(salary) OVER (ORDER BY id) as running_total FROM employees ORDER BY id;",
                null
        );
    }

    /**
     * Enhanced Practice Question data class
     */
    public static class PracticeQuestion {
        private String id;
        private String title;
        private String description;
        private String exampleSql;
        private String difficulty; // String for UI compatibility
        private String hint;
        private String solution;
        private DatabaseService.QueryResult expectedResult;

        public PracticeQuestion(String id, String title, String description, String exampleSql,
                                String difficulty, String hint, String solution, DatabaseService.QueryResult expectedResult) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.exampleSql = exampleSql;
            this.difficulty = difficulty.toLowerCase();
            this.hint = hint;
            this.solution = solution;
            this.expectedResult = expectedResult;
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getExampleSql() { return exampleSql; }
        public String getDifficulty() { return difficulty; }
        public String getHint() { return hint; }
        public String getSolution() { return solution; }
        public DatabaseService.QueryResult getExpectedResult() { return expectedResult; }

        // Get difficulty as enum for backward compatibility
        public DifficultyLevel getDifficultyEnum() {
            return switch (difficulty.toLowerCase()) {
                case "easy" -> DifficultyLevel.EASY;
                case "medium" -> DifficultyLevel.MEDIUM;
                case "hard" -> DifficultyLevel.HARD;
                case "pro" -> DifficultyLevel.PRO;
                default -> DifficultyLevel.EASY;
            };
        }
    }

    /**
     * Difficulty levels for practice questions
     */
    public enum DifficultyLevel {
        EASY("Easy", "#059669"),     // Green
        MEDIUM("Medium", "#d97706"), // Orange
        HARD("Hard", "#dc2626"),     // Red
        PRO("Pro", "#7c3aed");       // Purple

        private final String displayName;
        private final String color;

        DifficultyLevel(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
}