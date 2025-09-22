package com.coffee_and_code.sql_learning.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing database connections and operations
 */
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private static DatabaseService instance;
    private Connection connection;
    private final String DB_URL = "jdbc:h2:mem:sqllearning;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private final String DB_USER = "sa";
    private final String DB_PASSWORD = "";

    private DatabaseService() {}

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    /**
     * Initialize the database connection and create sample tables
     */
    public void initializeDatabase() throws SQLException {
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");

            // Create connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create sample database schema for practice
            createSampleTables();

            logger.info("Database initialized successfully");
        } catch (ClassNotFoundException e) {
            logger.error("H2 driver not found", e);
            throw new SQLException("Database driver not found", e);
        }
    }

    /**
     * Create sample tables with data for SQL practice
     */
    private void createSampleTables() throws SQLException {
        String[] sampleTables = ResourceLoader.loadSampleTables();

        try (Statement stmt = connection.createStatement()) {
            for (String sql : sampleTables) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }

        logger.info("Sample tables created successfully");
    }

    /**
     * Execute a SELECT query and return results
     */
    public QueryResult executeQuery(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return processResultSet(rs);
        }
    }

    /**
     * Execute a DDL/DML query and return affected rows count
     */
    public int executeUpdate(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    /**
     * Execute any SQL statement and return appropriate result
     */
    public ExecutionResult executeSql(String sql) {
        try {
            sql = sql.trim();
            if (sql.isEmpty()) {
                return new ExecutionResult(false, "Empty query", null, 0);
            }

            // Check if it's a SELECT query
            if (sql.toUpperCase().startsWith("SELECT") ||
                    sql.toUpperCase().startsWith("SHOW") ||
                    sql.toUpperCase().startsWith("DESCRIBE") ||
                    sql.toUpperCase().startsWith("EXPLAIN")) {

                QueryResult result = executeQuery(sql);
                return new ExecutionResult(true, "Query executed successfully", result, 0);

            } else {
                // DDL/DML query
                int affectedRows = executeUpdate(sql);
                return new ExecutionResult(true,
                        String.format("Query executed successfully. %d row(s) affected.", affectedRows),
                        null, affectedRows);
            }
        } catch (SQLException e) {
            logger.warn("SQL execution failed: {}", e.getMessage());
            return new ExecutionResult(false, "SQL Error: " + e.getMessage(), null, 0);
        } catch (Exception e) {
            logger.error("Unexpected error during SQL execution", e);
            return new ExecutionResult(false, "Unexpected error: " + e.getMessage(), null, 0);
        }
    }

    /**
     * Validate SQL syntax without executing
     */
    public ValidationResult validateSql(String sql) {
        try {
            // Try to prepare the statement to check syntax
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                return new ValidationResult(true, "SQL syntax is valid");
            }
        } catch (SQLException e) {
            return new ValidationResult(false, "SQL syntax error: " + e.getMessage());
        }
    }

    /**
     * Get list of all tables in the database
     */
    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }

        return tables;
    }

    /**
     * Get table structure information
     */
    public List<ColumnInfo> getTableColumns(String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getColumns(null, null, tableName, "%")) {
            while (rs.next()) {
                ColumnInfo column = new ColumnInfo(
                        rs.getString("COLUMN_NAME"),
                        rs.getString("TYPE_NAME"),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getBoolean("NULLABLE")
                );
                columns.add(column);
            }
        }

        return columns;
    }

    /**
     * Reset database to initial state
     */
    public void resetDatabase() throws SQLException {
        // Drop all tables and recreate
        List<String> tables = getTables();
        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                stmt.execute("DROP TABLE IF EXISTS " + table);
            }
        }

        // Recreate sample tables
        createSampleTables();

        logger.info("Database reset successfully");
    }

    /**
     * Process ResultSet into QueryResult object
     */
    private QueryResult processResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Get column names
        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnLabel(i));
        }

        // Get rows data
        List<List<Object>> rows = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            rows.add(row);
        }

        return new QueryResult(columnNames, rows);
    }

    /**
     * Close database connection
     */
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }

    // Inner classes for data transfer objects
    public static class QueryResult {
        private final List<String> columnNames;
        private final List<List<Object>> rows;

        public QueryResult(List<String> columnNames, List<List<Object>> rows) {
            this.columnNames = columnNames;
            this.rows = rows;
        }

        public List<String> getColumnNames() { return columnNames; }
        public List<List<Object>> getRows() { return rows; }
        public int getRowCount() { return rows.size(); }
        public int getColumnCount() { return columnNames.size(); }
    }

    public static class ExecutionResult {
        private final boolean success;
        private final String message;
        private final QueryResult queryResult;
        private final int affectedRows;

        public ExecutionResult(boolean success, String message, QueryResult queryResult, int affectedRows) {
            this.success = success;
            this.message = message;
            this.queryResult = queryResult;
            this.affectedRows = affectedRows;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public QueryResult getQueryResult() { return queryResult; }
        public int getAffectedRows() { return affectedRows; }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    public static class ColumnInfo {
        private final String name;
        private final String type;
        private final int size;
        private final boolean nullable;

        public ColumnInfo(String name, String type, int size, boolean nullable) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.nullable = nullable;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public int getSize() { return size; }
        public boolean isNullable() { return nullable; }

        @Override
        public String toString() {
            return String.format("%s %s(%d) %s",
                    name, type, size, nullable ? "NULL" : "NOT NULL");
        }
    }
}