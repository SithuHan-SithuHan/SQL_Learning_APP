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
 * Service for managing practice questions and user progress
 */
public class PracticeService {
    private static final Logger logger = LoggerFactory.getLogger(PracticeService.class);
    private List<PracticeQuestion> questions;
    private Set<String> completedQuestions;
    private ObjectMapper objectMapper;

    public PracticeService() {
        this.objectMapper = new ObjectMapper();
        this.completedQuestions = new HashSet<>();
        loadQuestions();
        loadUserProgress();
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
        
        // Medium Questions
        questions.add(createMediumQuestion1());
        questions.add(createMediumQuestion2());
        questions.add(createMediumQuestion3());
        
        // Hard Questions
        questions.add(createHardQuestion1());
        questions.add(createHardQuestion2());
        questions.add(createHardQuestion3());
        
        // Pro Questions
        questions.add(createProQuestion1());
        questions.add(createProQuestion2());
        questions.add(createProQuestion3());
        
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
     * Get questions by difficulty level
     */
    public List<PracticeQuestion> getQuestionsByDifficulty(DifficultyLevel level) {
        return questions.stream()
                .filter(q -> q.getDifficulty() == level)
                .collect(Collectors.toList());
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
     * Mark a question as completed
     */
    public void markQuestionCompleted(String questionId) {
        completedQuestions.add(questionId);
        saveUserProgress();
    }

    /**
     * Check if a question is completed
     */
    public boolean isQuestionCompleted(String questionId) {
        return completedQuestions.contains(questionId);
    }

    /**
     * Check if user's answer is correct
     */
    public boolean checkAnswer(PracticeQuestion question, DatabaseService.QueryResult userResult) {
        if (question.getExpectedResult() == null) {
            return true; // No expected result to compare
        }
        
        // Simple comparison - in a real app, you'd want more sophisticated comparison
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
        
        // Compare column names
        if (!expected.getColumnNames().equals(actual.getColumnNames())) {
            return false;
        }
        
        // Compare data rows (simplified comparison)
        for (int i = 0; i < expected.getRowCount(); i++) {
            List<Object> expectedRow = expected.getRows().get(i);
            List<Object> actualRow = actual.getRows().get(i);
            
            if (!expectedRow.equals(actualRow)) {
                return false;
            }
        }
        
        return true;
    }

    // Question creation methods
    private PracticeQuestion createEasyQuestionCombineTwoTables() {
        return new PracticeQuestion(
                "Easy-175",
                "Combine Two Tables",
                """
                175, Combine Two Tables, Easy, 
                Topics, premium lock icon, Companies, SQL Schema, Pandas Schema, 
                Table: Person
        
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | personId    | int     |
                | lastName    | varchar |
                | firstName   | varchar |
                +-------------+---------+
                personId is the primary key (column with unique values) for this table.
                This table contains information about the ID of some persons and their first and last names.
        
                Table: Address
        
                +-------------+---------+
                | Column Name | Type    |
                +-------------+---------+
                | addressId   | int     |
                | personId    | int     |
                | city        | varchar |
                | state       | varchar |
                +-------------+---------+
                addressId is the primary key (column with unique values) for this table.
                Each row of this table contains information about the city and state of one person with ID = PersonId.
        
                Write a solution to report the first name, last name, city, and state of each person in the Person table. 
                If the address of a personId is not present in the Address table, report null instead.
        
                Return the result table in any order.
        
                Example 1:
        
                Input: 
                Person table:
                +----------+----------+-----------+
                | personId | lastName | firstName |
                +----------+----------+-----------+
                | 1        | Wang     | Allen     |
                | 2        | Alice    | Bob       |
                +----------+----------+-----------+
                Address table:
                +-----------+----------+---------------+------------+
                | addressId | personId | city          | state      |
                +-----------+----------+---------------+------------+
                | 1         | 2        | New York City | New York   |
                | 2         | 3        | Leetcode      | California |
                +-----------+----------+---------------+------------+
                Output: 
                +-----------+----------+---------------+----------+
                | firstName | lastName | city          | state    |
                +-----------+----------+---------------+----------+
                | Allen     | Wang     | Null          | Null     |
                | Bob       | Alice    | New York City | New York |
                +-----------+----------+---------------+----------+
                Explanation: 
                There is no address in the address table for the personId = 1 so we return null in their city and state.
                addressId = 1 contains information about the address of personId = 2.
                """,
                "SELECT p.firstName, p.lastName, a.city, a.state FROM Person p LEFT JOIN Address a ON p.personId = a.personId;",
                DifficultyLevel.EASY,
                "Use LEFT JOIN to include all persons and get null for missing addresses.",
                null
        );
    }

    private PracticeQuestion createEasyQuestion2() {
        return new PracticeQuestion(
            "easy_2",
            "Find High Salary Employees",
            "Write a SQL query to find all employees with salary greater than 50000.",
            "SELECT * FROM employees WHERE salary > 50000;",
            DifficultyLevel.EASY,
            "Use WHERE clause with > operator to filter by salary.",
            null
        );
    }

    private PracticeQuestion createEasyQuestion3() {
        return new PracticeQuestion(
            "easy_3",
            "Count Total Employees",
            "Write a SQL query to count the total number of employees.",
            "SELECT COUNT(*) FROM employees;",
            DifficultyLevel.EASY,
            "Use COUNT(*) function to count all rows.",
            null
        );
    }

    private PracticeQuestion createMediumQuestion1() {
        return new PracticeQuestion(
            "medium_1",
            "Average Salary by Department",
            "Write a SQL query to find the average salary for each department.",
            "SELECT department_id, AVG(salary) as avg_salary FROM employees GROUP BY department_id;",
            DifficultyLevel.MEDIUM,
            "Use GROUP BY to group by department and AVG() to calculate average salary.",
            null
        );
    }

    private PracticeQuestion createMediumQuestion2() {
        return new PracticeQuestion(
            "medium_2",
            "Top 5 Highest Paid Employees",
            "Write a SQL query to find the top 5 highest paid employees.",
            "SELECT * FROM employees ORDER BY salary DESC LIMIT 5;",
            DifficultyLevel.MEDIUM,
            "Use ORDER BY DESC to sort by salary in descending order and LIMIT to get top 5.",
            null
        );
    }

    private PracticeQuestion createMediumQuestion3() {
        return new PracticeQuestion(
            "medium_3",
            "Employees with Specific Names",
            "Write a SQL query to find all employees whose first name starts with 'J'.",
            "SELECT * FROM employees WHERE first_name LIKE 'J%';",
            DifficultyLevel.MEDIUM,
            "Use LIKE operator with wildcard % to match names starting with 'J'.",
            null
        );
    }

    private PracticeQuestion createHardQuestion1() {
        return new PracticeQuestion(
            "hard_1",
            "Department with Most Employees",
            "Write a SQL query to find which department has the most employees.",
            "SELECT department_id, COUNT(*) as employee_count FROM employees GROUP BY department_id ORDER BY employee_count DESC LIMIT 1;",
            DifficultyLevel.HARD,
            "Group by department, count employees, order by count descending, and take the first result.",
            null
        );
    }

    private PracticeQuestion createHardQuestion2() {
        return new PracticeQuestion(
            "hard_2",
            "Employees Earning More Than Average",
            "Write a SQL query to find employees who earn more than the average salary.",
            "SELECT * FROM employees WHERE salary > (SELECT AVG(salary) FROM employees);",
            DifficultyLevel.HARD,
            "Use a subquery to calculate the average salary and compare it in the WHERE clause.",
            null
        );
    }

    private PracticeQuestion createHardQuestion3() {
        return new PracticeQuestion(
            "hard_3",
            "Second Highest Salary",
            "Write a SQL query to find the second highest salary.",
            "SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees);",
            DifficultyLevel.HARD,
            "Find the maximum salary that is less than the overall maximum salary.",
            null
        );
    }

    private PracticeQuestion createProQuestion1() {
        return new PracticeQuestion(
            "pro_1",
            "Complex Join with Aggregation",
            "Write a SQL query to find departments with their employee count and average salary, only for departments with more than 2 employees.",
            "SELECT d.department_name, COUNT(e.id) as employee_count, AVG(e.salary) as avg_salary FROM departments d JOIN employees e ON d.id = e.department_id GROUP BY d.id, d.department_name HAVING COUNT(e.id) > 2;",
            DifficultyLevel.PRO,
            "Use JOIN to connect tables, GROUP BY for aggregation, and HAVING to filter groups.",
            null
        );
    }

    private PracticeQuestion createProQuestion2() {
        return new PracticeQuestion(
            "pro_2",
            "Window Function - Rank Employees",
            "Write a SQL query to rank employees by salary within their department.",
            "SELECT first_name, last_name, salary, department_id, RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as salary_rank FROM employees;",
            DifficultyLevel.PRO,
            "Use RANK() window function with PARTITION BY and ORDER BY clauses.",
            null
        );
    }

    private PracticeQuestion createProQuestion3() {
        return new PracticeQuestion(
            "pro_3",
            "Self Join - Find Manager Hierarchy",
            "Write a SQL query to find all employees and their managers (assuming employees table has a manager_id column).",
            "SELECT e.first_name as employee_name, m.first_name as manager_name FROM employees e LEFT JOIN employees m ON e.manager_id = m.id;",
            DifficultyLevel.PRO,
            "Use self-join with LEFT JOIN to include employees without managers.",
            null
        );
    }

    /**
     * Practice Question data class
     */
    public static class PracticeQuestion {
        private String id;
        private String title;
        private String description;
        private String exampleSql;
        private DifficultyLevel difficulty;
        private String hint;
        private DatabaseService.QueryResult expectedResult;

        public PracticeQuestion(String id, String title, String description, String exampleSql, 
                              DifficultyLevel difficulty, String hint, DatabaseService.QueryResult expectedResult) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.exampleSql = exampleSql;
            this.difficulty = difficulty;
            this.hint = hint;
            this.expectedResult = expectedResult;
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getExampleSql() { return exampleSql; }
        public DifficultyLevel getDifficulty() { return difficulty; }
        public String getHint() { return hint; }
        public DatabaseService.QueryResult getExpectedResult() { return expectedResult; }
    }

    /**
     * Difficulty levels for practice questions
     */
    public enum DifficultyLevel {
        EASY("Easy", "#4CAF50"),
        MEDIUM("Medium", "#FF9800"),
        HARD("Hard", "#F44336"),
        PRO("Pro", "#9C27B0");

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
