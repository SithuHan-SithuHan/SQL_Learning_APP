package com.coffee_and_code.sql_learning.service;



import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing learning content and topics
 */
public class LearningContentService {
    private static final Logger logger = LoggerFactory.getLogger(LearningContentService.class);
    private Map<String, String> topicContent;

    public LearningContentService() {
        initializeContent();
    }

    /**
     * Initialize all learning content
     */
    private void initializeContent() {
        topicContent = new HashMap<>();

        // DDL Content
        topicContent.put("Introduction to DDL", createDDLIntroContent());
        topicContent.put("CREATE TABLE", createCreateTableContent());
        topicContent.put("ALTER TABLE", createAlterTableContent());
        topicContent.put("DROP TABLE", createDropTableContent());
        topicContent.put("Constraints", createConstraintsContent());

        // DML Content
        topicContent.put("Introduction to DML", createDMLIntroContent());
        topicContent.put("SELECT Statement", createSelectContent());
        topicContent.put("INSERT Statement", createInsertContent());
        topicContent.put("UPDATE Statement", createUpdateContent());
        topicContent.put("DELETE Statement", createDeleteContent());
        topicContent.put("Joins", createJoinsContent());
        topicContent.put("Subqueries", createSubqueriesContent());

        // DCL Content
        topicContent.put("Introduction to DCL", createDCLIntroContent());
        topicContent.put("GRANT Statement", createGrantContent());
        topicContent.put("REVOKE Statement", createRevokeContent());

        // TCL Content
        topicContent.put("Introduction to TCL", createTCLIntroContent());
        topicContent.put("COMMIT", createCommitContent());
        topicContent.put("ROLLBACK", createRollbackContent());
        topicContent.put("SAVEPOINT", createSavepointContent());

        // Normalization Content
        topicContent.put("Database Normalization", createNormalizationIntroContent());
        topicContent.put("First Normal Form (1NF)", create1NFContent());
        topicContent.put("Second Normal Form (2NF)", create2NFContent());
        topicContent.put("Third Normal Form (3NF)", create3NFContent());
        topicContent.put("BCNF", createBCNFContent());

        logger.info("Learning content initialized with {} topics", topicContent.size());
    }

    /**
     * Get the learning topics tree structure
     */
    public TreeItem<String> getLearningTopicsTree() {
        TreeItem<String> root = new TreeItem<>("SQL Learning");

        // DDL Section
        TreeItem<String> ddlNode = new TreeItem<>("Data Definition Language (DDL)");
        ddlNode.getChildren().addAll(
                new TreeItem<>("Introduction to DDL"),
                new TreeItem<>("CREATE TABLE"),
                new TreeItem<>("ALTER TABLE"),
                new TreeItem<>("DROP TABLE"),
                new TreeItem<>("Constraints")
        );

        // DML Section
        TreeItem<String> dmlNode = new TreeItem<>("Data Manipulation Language (DML)");
        dmlNode.getChildren().addAll(
                new TreeItem<>("Introduction to DML"),
                new TreeItem<>("SELECT Statement"),
                new TreeItem<>("INSERT Statement"),
                new TreeItem<>("UPDATE Statement"),
                new TreeItem<>("DELETE Statement"),
                new TreeItem<>("Joins"),
                new TreeItem<>("Subqueries")
        );

        // DCL Section
        TreeItem<String> dclNode = new TreeItem<>("Data Control Language (DCL)");
        dclNode.getChildren().addAll(
                new TreeItem<>("Introduction to DCL"),
                new TreeItem<>("GRANT Statement"),
                new TreeItem<>("REVOKE Statement")
        );

        // TCL Section
        TreeItem<String> tclNode = new TreeItem<>("Transaction Control Language (TCL)");
        tclNode.getChildren().addAll(
                new TreeItem<>("Introduction to TCL"),
                new TreeItem<>("COMMIT"),
                new TreeItem<>("ROLLBACK"),
                new TreeItem<>("SAVEPOINT")
        );

        // Normalization Section
        TreeItem<String> normalizationNode = new TreeItem<>("Database Normalization");
        normalizationNode.getChildren().addAll(
                new TreeItem<>("Database Normalization"),
                new TreeItem<>("First Normal Form (1NF)"),
                new TreeItem<>("Second Normal Form (2NF)"),
                new TreeItem<>("Third Normal Form (3NF)"),
                new TreeItem<>("BCNF")
        );

        root.getChildren().addAll(ddlNode, dmlNode, dclNode, tclNode, normalizationNode);

        // Expand all nodes by default
        expandTreeView(root);

        return root;
    }

    /**
     * Get content for a specific topic
     */
    public String getTopicContent(String topic) {
        return topicContent.getOrDefault(topic, createNotFoundContent(topic));
    }

    private void expandTreeView(TreeItem<String> item) {
        if (item != null) {
            item.setExpanded(true);
            for (TreeItem<String> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    // Content creation methods
    private String createDDLIntroContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; }
                    .highlight { background: #fff3cd; padding: 10px; border: 1px solid #ffeaa7; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>Introduction to DDL (Data Definition Language)</h1>
                
                <h2>What is DDL?</h2>
                <p>Data Definition Language (DDL) is a subset of SQL used to define and modify the structure of database objects such as tables, indexes, and schemas.</p>
                
                <h2>Main DDL Commands</h2>
                <ul>
                    <li><strong>CREATE</strong> - Creates new database objects</li>
                    <li><strong>ALTER</strong> - Modifies existing database objects</li>
                    <li><strong>DROP</strong> - Deletes database objects</li>
                    <li><strong>TRUNCATE</strong> - Removes all data from a table</li>
                </ul>
                
                <h2>Key Characteristics</h2>
                <div class="highlight">
                    <ul>
                        <li>DDL commands are auto-committed (changes are permanent immediately)</li>
                        <li>They affect the database schema/structure</li>
                        <li>They require appropriate privileges to execute</li>
                    </ul>
                </div>
                
                <h2>Example</h2>
                <div class="code">
                    CREATE TABLE employees (<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;id INT PRIMARY KEY,<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;name VARCHAR(100),<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;salary DECIMAL(10,2)<br>
                    );
                </div>
            </body>
            </html>
            """;
    }

    private String createCreateTableContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .syntax { background: #e8f5e8; padding: 10px; border: 1px solid #4CAF50; border-radius: 5px; font-family: monospace; }
                    table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    th { background-color: #f2f2f2; }
                </style>
            </head>
            <body>
                <h1>CREATE TABLE Statement</h1>
                
                <h2>Syntax</h2>
                <div class="syntax">
                CREATE TABLE table_name (
                    column1 datatype constraints,
                    column2 datatype constraints,
                    ...
                    table_constraints
                );
                </div>
                
                <h2>Common Data Types</h2>
                <table>
                    <tr><th>Data Type</th><th>Description</th><th>Example</th></tr>
                    <tr><td>INT</td><td>Integer numbers</td><td>age INT</td></tr>
                    <tr><td>VARCHAR(n)</td><td>Variable-length string</td><td>name VARCHAR(100)</td></tr>
                    <tr><td>DECIMAL(p,s)</td><td>Decimal numbers</td><td>salary DECIMAL(10,2)</td></tr>
                    <tr><td>DATE</td><td>Date values</td><td>birth_date DATE</td></tr>
                    <tr><td>BOOLEAN</td><td>True/False values</td><td>is_active BOOLEAN</td></tr>
                </table>
                
                <h2>Example</h2>
                <div class="code">CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    salary DECIMAL(10,2),
    hire_date DATE,
    department_id INT,
    is_active BOOLEAN DEFAULT TRUE
);</div>
                
                <h2>Key Points</h2>
                <ul>
                    <li>Table names must be unique within a database</li>
                    <li>Column names must be unique within a table</li>
                    <li>Choose appropriate data types for efficiency</li>
                    <li>Consider constraints for data integrity</li>
                </ul>
            </body>
            </html>
            """;
    }

    private String createSelectContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .syntax { background: #e8f5e8; padding: 10px; border: 1px solid #4CAF50; border-radius: 5px; font-family: monospace; }
                    .note { background: #fff3cd; padding: 10px; border: 1px solid #ffeaa7; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>SELECT Statement</h1>
                
                <h2>Basic Syntax</h2>
                <div class="syntax">
                SELECT column1, column2, ...
                FROM table_name
                WHERE condition
                ORDER BY column
                LIMIT number;
                </div>
                
                <h2>SELECT Variations</h2>
                
                <h3>1. Select All Columns</h3>
                <div class="code">SELECT * FROM employees;</div>
                
                <h3>2. Select Specific Columns</h3>
                <div class="code">SELECT first_name, last_name, salary FROM employees;</div>
                
                <h3>3. Using WHERE Clause</h3>
                <div class="code">SELECT * FROM employees WHERE salary > 50000;</div>
                
                <h3>4. Using ORDER BY</h3>
                <div class="code">SELECT * FROM employees ORDER BY salary DESC;</div>
                
                <h3>5. Using LIMIT</h3>
                <div class="code">SELECT * FROM employees LIMIT 10;</div>
                
                <h2>Common WHERE Operators</h2>
                <ul>
                    <li><strong>=</strong> - Equal to</li>
                    <li><strong>!=</strong> or <strong>&lt;&gt;</strong> - Not equal to</li>
                    <li><strong>&gt;</strong>, <strong>&lt;</strong> - Greater/Less than</li>
                    <li><strong>&gt;=</strong>, <strong>&lt;=</strong> - Greater/Less than or equal</li>
                    <li><strong>LIKE</strong> - Pattern matching</li>
                    <li><strong>IN</strong> - Match any value in a list</li>
                    <li><strong>BETWEEN</strong> - Within a range</li>
                </ul>
                
                <h2>Advanced Examples</h2>
                <div class="code">-- Using LIKE for pattern matching
SELECT * FROM employees WHERE first_name LIKE 'J%';

-- Using IN for multiple values
SELECT * FROM employees WHERE department_id IN (1, 2, 3);

-- Using BETWEEN for ranges
SELECT * FROM employees WHERE salary BETWEEN 40000 AND 80000;</div>
                
                <div class="note">
                    <strong>Note:</strong> The SELECT statement is the most commonly used SQL command for retrieving data from databases.
                </div>
            </body>
            </html>
            """;
    }

    private String createNormalizationIntroContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .highlight { background: #fff3cd; padding: 10px; border: 1px solid #ffeaa7; border-radius: 5px; margin: 15px 0; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; }
                    ul li { margin-bottom: 8px; }
                </style>
            </head>
            <body>
                <h1>Database Normalization</h1>
                
                <h2>What is Normalization?</h2>
                <p>Database normalization is the process of organizing data in a database to reduce redundancy and improve data integrity. It involves dividing large tables into smaller ones and defining relationships between them.</p>
                
                <h2>Why Normalize?</h2>
                <ul>
                    <li><strong>Reduce Data Redundancy</strong> - Eliminates duplicate data</li>
                    <li><strong>Improve Data Integrity</strong> - Reduces inconsistencies</li>
                    <li><strong>Save Storage Space</strong> - More efficient use of disk space</li>
                    <li><strong>Easier Maintenance</strong> - Updates need to be made in fewer places</li>
                </ul>
                
                <h2>Normal Forms</h2>
                <p>There are several normal forms, each building upon the previous one:</p>
                <ul>
                    <li><strong>First Normal Form (1NF)</strong> - Eliminates repeating groups</li>
                    <li><strong>Second Normal Form (2NF)</strong> - Eliminates partial dependencies</li>
                    <li><strong>Third Normal Form (3NF)</strong> - Eliminates transitive dependencies</li>
                    <li><strong>Boyce-Codd Normal Form (BCNF)</strong> - Stricter version of 3NF</li>
                </ul>
                
                <div class="highlight">
                    <strong>Key Concept:</strong> Each normal form addresses specific types of data anomalies and dependencies.
                </div>
                
                <h2>Example: Unnormalized Data</h2>
                <div class="code">
                Students Table:
                StudentID | Name    | Courses           | Instructors
                1         | John    | Math, Science     | Dr. Smith, Dr. Jones
                2         | Sarah   | English, History  | Prof. Brown, Dr. Wilson
                </div>
                
                <p>This table violates 1NF because it has repeating groups (multiple courses and instructors in single cells).</p>
                
                <h2>Benefits vs. Trade-offs</h2>
                <ul>
                    <li><strong>Benefits:</strong> Data integrity, reduced redundancy, easier maintenance</li>
                    <li><strong>Trade-offs:</strong> More complex queries, potentially slower performance for some operations</li>
                </ul>
            </body>
            </html>
            """;
    }

    // Additional content creation methods would continue here...
    // For brevity, I'm including a few more key ones:

    private String createAlterTableContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .syntax { background: #e8f5e8; padding: 10px; border: 1px solid #4CAF50; border-radius: 5px; font-family: monospace; }
                </style>
            </head>
            <body>
                <h1>ALTER TABLE Statement</h1>
                
                <h2>Purpose</h2>
                <p>The ALTER TABLE statement is used to modify an existing table structure without losing data.</p>
                
                <h2>Common Operations</h2>
                
                <h3>Add Column</h3>
                <div class="code">ALTER TABLE employees ADD COLUMN phone VARCHAR(20);</div>
                
                <h3>Drop Column</h3>
                <div class="code">ALTER TABLE employees DROP COLUMN phone;</div>
                
                <h3>Modify Column</h3>
                <div class="code">ALTER TABLE employees MODIFY COLUMN salary DECIMAL(12,2);</div>
                
                <h3>Add Constraint</h3>
                <div class="code">ALTER TABLE employees ADD CONSTRAINT fk_dept 
FOREIGN KEY (department_id) REFERENCES departments(id);</div>
                
                <h3>Drop Constraint</h3>
                <div class="code">ALTER TABLE employees DROP CONSTRAINT fk_dept;</div>
                
                <h2>Important Notes</h2>
                <ul>
                    <li>Some changes may require table to be empty</li>
                    <li>Always backup data before major alterations</li>
                    <li>Consider impact on existing applications</li>
                </ul>
            </body>
            </html>
            """;
    }

    private String createDropTableContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .warning { background: #ffebee; padding: 10px; border: 1px solid #f44336; border-radius: 5px; color: #c62828; }
                </style>
            </head>
            <body>
                <h1>DROP TABLE Statement</h1>
                
                <h2>Purpose</h2>
                <p>The DROP TABLE statement permanently removes a table and all its data from the database.</p>
                
                <h2>Syntax</h2>
                <div class="code">DROP TABLE table_name;</div>
                
                <h2>Safe Drop</h2>
                <div class="code">DROP TABLE IF EXISTS table_name;</div>
                
                <div class="warning">
                    <strong>⚠️ WARNING:</strong> DROP TABLE is irreversible! All data will be permanently lost.
                </div>
                
                <h2>Examples</h2>
                <div class="code">-- Drop a table
DROP TABLE old_employees;

-- Safe drop (no error if table doesn't exist)
DROP TABLE IF EXISTS temp_data;

-- Drop multiple tables
DROP TABLE table1, table2, table3;</div>
                
                <h2>Before Dropping Tables</h2>
                <ul>
                    <li>Backup important data</li>
                    <li>Check for foreign key constraints</li>
                    <li>Verify no applications depend on the table</li>
                    <li>Consider using TRUNCATE if you only want to remove data</li>
                </ul>
            </body>
            </html>
            """;
    }

    private String createConstraintsContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    th { background-color: #f2f2f2; }
                </style>
            </head>
            <body>
                <h1>Database Constraints</h1>
                
                <h2>What are Constraints?</h2>
                <p>Constraints are rules applied to table columns to ensure data integrity and validity.</p>
                
                <h2>Types of Constraints</h2>
                <table>
                    <tr><th>Constraint</th><th>Purpose</th><th>Example</th></tr>
                    <tr><td>PRIMARY KEY</td><td>Uniquely identifies each row</td><td>id INT PRIMARY KEY</td></tr>
                    <tr><td>FOREIGN KEY</td><td>Links to another table</td><td>dept_id INT REFERENCES departments(id)</td></tr>
                    <tr><td>UNIQUE</td><td>Ensures column values are unique</td><td>email VARCHAR(100) UNIQUE</td></tr>
                    <tr><td>NOT NULL</td><td>Column cannot be empty</td><td>name VARCHAR(50) NOT NULL</td></tr>
                    <tr><td>CHECK</td><td>Validates data against condition</td><td>age INT CHECK (age >= 18)</td></tr>
                    <tr><td>DEFAULT</td><td>Sets default value</td><td>status VARCHAR(10) DEFAULT 'active'</td></tr>
                </table>
                
                <h2>Example Table with Constraints</h2>
                <div class="code">CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    age INT CHECK (age >= 18 AND age <= 120),
    salary DECIMAL(10,2) DEFAULT 30000.00,
    department_id INT,
    status VARCHAR(10) DEFAULT 'active',
    FOREIGN KEY (department_id) REFERENCES departments(id)
);</div>
                
                <h2>Benefits of Constraints</h2>
                <ul>
                    <li>Prevent invalid data entry</li>
                    <li>Maintain data relationships</li>
                    <li>Ensure business rules compliance</li>
                    <li>Improve data quality and reliability</li>
                </ul>
            </body>
            </html>
            """;
    }

    // Additional content methods for DML, DCL, TCL, and Normalization
    private String createDMLIntroContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .highlight { background: #fff3cd; padding: 10px; border: 1px solid #ffeaa7; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>Introduction to DML (Data Manipulation Language)</h1>
                
                <h2>What is DML?</h2>
                <p>Data Manipulation Language (DML) is used to retrieve, insert, update, and delete data in database tables.</p>
                
                <h2>Main DML Commands</h2>
                <ul>
                    <li><strong>SELECT</strong> - Retrieves data from tables</li>
                    <li><strong>INSERT</strong> - Adds new records</li>
                    <li><strong>UPDATE</strong> - Modifies existing records</li>
                    <li><strong>DELETE</strong> - Removes records</li>
                </ul>
                
                <div class="highlight">
                    <strong>Key Point:</strong> DML operations can be rolled back (unlike DDL commands).
                </div>
            </body>
            </html>
            """;
    }

    private String createInsertContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>INSERT Statement</h1>
                
                <h2>Basic INSERT</h2>
                <div class="code">INSERT INTO employees (first_name, last_name, email, salary)
VALUES ('John', 'Doe', 'john.doe@email.com', 55000);</div>
                
                <h2>Multiple Row INSERT</h2>
                <div class="code">INSERT INTO employees (first_name, last_name, salary)
VALUES 
    ('Alice', 'Smith', 60000),
    ('Bob', 'Johnson', 52000),
    ('Carol', 'Williams', 58000);</div>
                
                <h2>INSERT with SELECT</h2>
                <div class="code">INSERT INTO employees_backup 
SELECT * FROM employees WHERE salary > 50000;</div>
            </body>
            </html>
            """;
    }

    private String createUpdateContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .warning { background: #ffebee; padding: 10px; border: 1px solid #f44336; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>UPDATE Statement</h1>
                
                <h2>Basic UPDATE</h2>
                <div class="code">UPDATE employees 
SET salary = 65000 
WHERE id = 1;</div>
                
                <h2>Update Multiple Columns</h2>
                <div class="code">UPDATE employees 
SET salary = 70000, department_id = 2 
WHERE first_name = 'John';</div>
                
                <div class="warning">
                    <strong>Warning:</strong> Always use WHERE clause to avoid updating all rows!
                </div>
            </body>
            </html>
            """;
    }

    private String createDeleteContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                    .warning { background: #ffebee; padding: 10px; border: 1px solid #f44336; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>DELETE Statement</h1>
                
                <h2>Basic DELETE</h2>
                <div class="code">DELETE FROM employees WHERE id = 1;</div>
                
                <h2>Conditional DELETE</h2>
                <div class="code">DELETE FROM employees WHERE salary < 40000;</div>
                
                <div class="warning">
                    <strong>Warning:</strong> DELETE without WHERE clause removes ALL rows!
                </div>
            </body>
            </html>
            """;
    }

    private String createJoinsContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>SQL Joins</h1>
                
                <h2>INNER JOIN</h2>
                <div class="code">SELECT e.first_name, d.department_name
FROM employees e
INNER JOIN departments d ON e.department_id = d.id;</div>
                
                <h2>LEFT JOIN</h2>
                <div class="code">SELECT e.first_name, d.department_name
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id;</div>
                
                <h2>RIGHT JOIN</h2>
                <div class="code">SELECT e.first_name, d.department_name
FROM employees e
RIGHT JOIN departments d ON e.department_id = d.id;</div>
            </body>
            </html>
            """;
    }

    private String createSubqueriesContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>Subqueries</h1>
                
                <h2>Subquery in WHERE</h2>
                <div class="code">SELECT * FROM employees 
WHERE salary > (SELECT AVG(salary) FROM employees);</div>
                
                <h2>Subquery with IN</h2>
                <div class="code">SELECT * FROM employees 
WHERE department_id IN (SELECT id FROM departments WHERE location = 'New York');</div>
            </body>
            </html>
            """;
    }

    // DCL, TCL, and additional normalization content methods would follow similar pattern...

    private String createDCLIntroContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                </style>
            </head>
            <body>
                <h1>Introduction to DCL (Data Control Language)</h1>
                
                <p>Data Control Language (DCL) is used to control access to data in the database.</p>
                
                <h2>Main DCL Commands</h2>
                <ul>
                    <li><strong>GRANT</strong> - Gives privileges to users</li>
                    <li><strong>REVOKE</strong> - Removes privileges from users</li>
                </ul>
            </body>
            </html>
            """;
    }

    private String createGrantContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>GRANT Statement</h1>
                
                <h2>Grant SELECT Permission</h2>
                <div class="code">GRANT SELECT ON employees TO user1;</div>
                
                <h2>Grant Multiple Permissions</h2>
                <div class="code">GRANT SELECT, INSERT, UPDATE ON employees TO user1;</div>
                
                <h2>Grant All Permissions</h2>
                <div class="code">GRANT ALL PRIVILEGES ON database_name.* TO user1;</div>
            </body>
            </html>
            """;
    }

    private String createRevokeContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>REVOKE Statement</h1>
                
                <h2>Revoke Specific Permission</h2>
                <div class="code">REVOKE INSERT ON employees FROM user1;</div>
                
                <h2>Revoke All Permissions</h2>
                <div class="code">REVOKE ALL PRIVILEGES ON database_name.* FROM user1;</div>
            </body>
            </html>
            """;
    }

    private String createTCLIntroContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                </style>
            </head>
            <body>
                <h1>Introduction to TCL (Transaction Control Language)</h1>
                
                <p>Transaction Control Language (TCL) is used to manage transactions in the database.</p>
                
                <h2>Main TCL Commands</h2>
                <ul>
                    <li><strong>COMMIT</strong> - Saves all changes permanently</li>
                    <li><strong>ROLLBACK</strong> - Undoes all changes</li>
                    <li><strong>SAVEPOINT</strong> - Creates a point to rollback to</li>
                </ul>
                
                <h2>What is a Transaction?</h2>
                <p>A transaction is a sequence of database operations that are treated as a single unit of work.</p>
            </body>
            </html>
            """;
    }

    private String createCommitContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>COMMIT Statement</h1>
                
                <p>COMMIT makes all changes in the current transaction permanent.</p>
                
                <h2>Example</h2>
                <div class="code">BEGIN TRANSACTION;
UPDATE employees SET salary = salary * 1.1;
COMMIT;</div>
                
                <p>After COMMIT, changes cannot be rolled back.</p>
            </body>
            </html>
            """;
    }

    private String createRollbackContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>ROLLBACK Statement</h1>
                
                <p>ROLLBACK undoes all changes in the current transaction.</p>
                
                <h2>Example</h2>
                <div class="code">BEGIN TRANSACTION;
DELETE FROM employees WHERE salary < 30000;
ROLLBACK;</div>
                
                <p>After ROLLBACK, all changes are undone.</p>
            </body>
            </html>
            """;
    }

    private String createSavepointContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>SAVEPOINT Statement</h1>
                
                <p>SAVEPOINT creates a point within a transaction to which you can later rollback.</p>
                
                <h2>Example</h2>
                <div class="code">BEGIN TRANSACTION;
UPDATE employees SET salary = salary * 1.1;
SAVEPOINT before_delete;
DELETE FROM employees WHERE performance = 'poor';
ROLLBACK TO before_delete;
COMMIT;</div>
            </body>
            </html>
            """;
    }

    private String create1NFContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .code { background: #f4f4f4; padding: 15px; border-left: 4px solid #3498db; font-family: monospace; white-space: pre-line; }
                </style>
            </head>
            <body>
                <h1>First Normal Form (1NF)</h1>
                
                <h2>Rules for 1NF</h2>
                <ul>
                    <li>Each cell contains only atomic (indivisible) values</li>
                    <li>No repeating groups or arrays</li>
                    <li>Each record is unique</li>
                </ul>
                
                <h2>Before 1NF (Violates 1NF)</h2>
                <div class="code">Student Table:
ID | Name  | Subjects
1  | John  | Math, Science, English
2  | Mary  | History, Art</div>
                
                <h2>After 1NF</h2>
                <div class="code">Student_Subjects Table:
ID | Name  | Subject
1  | John  | Math
1  | John  | Science  
1  | John  | English
2  | Mary  | History
2  | Mary  | Art</div>
            </body>
            </html>
            """;
    }

    private String create2NFContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                </style>
            </head>
            <body>
                <h1>Second Normal Form (2NF)</h1>
                
                <h2>Rules for 2NF</h2>
                <ul>
                    <li>Must be in 1NF</li>
                    <li>No partial dependencies on composite primary keys</li>
                    <li>All non-key attributes must depend on the entire primary key</li>
                </ul>
                
                <p>2NF eliminates partial dependencies by creating separate tables.</p>
            </body>
            </html>
            """;
    }

    private String create3NFContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                </style>
            </head>
            <body>
                <h1>Third Normal Form (3NF)</h1>
                
                <h2>Rules for 3NF</h2>
                <ul>
                    <li>Must be in 2NF</li>
                    <li>No transitive dependencies</li>
                    <li>Non-key attributes should not depend on other non-key attributes</li>
                </ul>
                
                <p>3NF eliminates transitive dependencies for better data integrity.</p>
            </body>
            </html>
            """;
    }

    private String createBCNFContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                </style>
            </head>
            <body>
                <h1>Boyce-Codd Normal Form (BCNF)</h1>
                
                <h2>Rules for BCNF</h2>
                <ul>
                    <li>Must be in 3NF</li>
                    <li>For every functional dependency A → B, A must be a superkey</li>
                </ul>
                
                <p>BCNF is a stricter version of 3NF that handles certain anomalies that 3NF doesn't address.</p>
            </body>
            </html>
            """;
    }

    private String createNotFoundContent(String topic) {
        return String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; text-align: center; }
                    h1 { color: #e74c3c; }
                </style>
            </head>
            <body>
                <h1>Content Not Found</h1>
                <p>Content for topic "%s" is not available yet.</p>
                <p>This content is under development.</p>
            </body>
            </html>
            """, topic);
    }
}