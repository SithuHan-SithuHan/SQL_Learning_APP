package com.coffee_and_code.sql_learning.service;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL Syntax Highlighter for CodeArea
 */
public class SqlSyntaxHighlighter {
    
    // SQL Keywords
    private static final String[] KEYWORDS = {
        "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP",
        "TABLE", "DATABASE", "INDEX", "VIEW", "TRIGGER", "PROCEDURE", "FUNCTION",
        "JOIN", "INNER", "LEFT", "RIGHT", "OUTER", "ON", "AS", "AND", "OR", "NOT",
        "IN", "EXISTS", "BETWEEN", "LIKE", "IS", "NULL", "ORDER", "BY", "GROUP",
        "HAVING", "LIMIT", "OFFSET", "UNION", "ALL", "DISTINCT", "TOP", "CASE",
        "WHEN", "THEN", "ELSE", "END", "IF", "WHILE", "FOR", "LOOP", "BEGIN",
        "COMMIT", "ROLLBACK", "SAVEPOINT", "GRANT", "REVOKE", "PRIVILEGES",
        "INT", "VARCHAR", "CHAR", "TEXT", "DECIMAL", "FLOAT", "DOUBLE", "DATE",
        "TIME", "DATETIME", "TIMESTAMP", "BOOLEAN", "BLOB", "CLOB", "JSON",
        "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "UNIQUE", "CHECK", "DEFAULT",
        "NOT", "NULL", "AUTO_INCREMENT", "IDENTITY", "SEQUENCE", "CONSTRAINT",
        "ASC", "DESC", "COUNT", "SUM", "AVG", "MIN", "MAX", "FIRST", "LAST",
        "ROW_NUMBER", "RANK", "DENSE_RANK", "LEAD", "LAG", "OVER", "PARTITION",
        "WINDOW", "ROWS", "RANGE", "UNBOUNDED", "PRECEDING", "FOLLOWING", "CURRENT"
    };

    // SQL Functions
    private static final String[] FUNCTIONS = {
        "ABS", "ACOS", "ASIN", "ATAN", "ATAN2", "CEIL", "COS", "COT", "DEGREES",
        "EXP", "FLOOR", "LOG", "LOG10", "MOD", "PI", "POWER", "RADIANS", "RAND",
        "ROUND", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE", "ASCII", "CHAR",
        "CHAR_LENGTH", "CONCAT", "CONCAT_WS", "ELT", "FIELD", "FIND_IN_SET",
        "FORMAT", "INSERT", "INSTR", "LCASE", "LEFT", "LENGTH", "LOCATE", "LOWER",
        "LPAD", "LTRIM", "MID", "POSITION", "REPEAT", "REPLACE", "REVERSE",
        "RIGHT", "RPAD", "RTRIM", "SPACE", "STRCMP", "SUBSTRING", "SUBSTRING_INDEX",
        "TRIM", "UCASE", "UPPER", "ADDDATE", "ADDTIME", "CONVERT_TZ", "CURDATE",
        "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURTIME", "DATE",
        "DATE_ADD", "DATE_FORMAT", "DATE_SUB", "DATEDIFF", "DAY", "DAYNAME",
        "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "EXTRACT", "FROM_DAYS", "FROM_UNIXTIME",
        "GET_FORMAT", "HOUR", "LAST_DAY", "LOCALTIME", "LOCALTIMESTAMP", "MAKEDATE",
        "MAKETIME", "MICROSECOND", "MINUTE", "MONTH", "MONTHNAME", "NOW", "PERIOD_ADD",
        "PERIOD_DIFF", "QUARTER", "SECOND", "SEC_TO_TIME", "STR_TO_DATE", "SUBDATE",
        "SUBTIME", "SYSDATE", "TIME", "TIME_FORMAT", "TIME_TO_SEC", "TIMEDIFF",
        "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TO_DAYS", "TO_SECONDS",
        "UNIX_TIMESTAMP", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "WEEK", "WEEKDAY",
        "WEEKOFYEAR", "YEAR", "YEARWEEK"
    };

    // Pattern definitions
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String FUNCTION_PATTERN = "\\b(" + String.join("|", FUNCTIONS) + ")\\s*\\(";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String NUMBER_PATTERN = "\\b\\d+\\.?\\d*\\b";
    private static final String COMMENT_PATTERN = "--.*$";
    private static final String MULTILINE_COMMENT_PATTERN = "/\\*[\\s\\S]*?\\*/";
    private static final String OPERATOR_PATTERN = "[=<>!]+|[+\\-*/%]";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?:--.*$)"                     // Single-line comment (non-capturing)
                    + "|(?:/\\*[\\s\\S]*?\\*/)"       // Multi-line comment (non-capturing)
                    + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
            , Pattern.MULTILINE);
    /**
     * Apply syntax highlighting to the given CodeArea
     */
    public void applySyntaxHighlighting(CodeArea codeArea) {
        String text = codeArea.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        StyleSpans<Collection<String>> styleSpans = computeHighlighting(text);
        codeArea.setStyleSpans(0, styleSpans);
    }

    /**
     * Compute highlighting spans for the given text
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass = getStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Get the CSS style class for the matched group
     */
    private String getStyleClass(Matcher matcher) {
        if (matcher.group("KEYWORD") != null) {
            return "sql-keyword";
        } else if (matcher.group("FUNCTION") != null) {
            return "sql-function";
        } else if (matcher.group("STRING") != null) {
            return "sql-string";
        } else if (matcher.group("NUMBER") != null) {
            return "sql-number";
        } else {
            String matched = matcher.group();
            if (matched.startsWith("--") || matched.startsWith("/*")) {
                return "sql-comment";
            } else if (matcher.group("OPERATOR") != null) {
                return "sql-operator";
            }
        }
        return "";
    }

    /**
     * Get CSS styles for SQL syntax highlighting
     */
    public static String getCssStyles() {
        return """
            .sql-keyword {
                -fx-fill: #0000FF;
                -fx-font-weight: bold;
            }
            .sql-function {
                -fx-fill: #800080;
                -fx-font-weight: bold;
            }
            .sql-string {
                -fx-fill: #008000;
            }
            .sql-number {
                -fx-fill: #FF0000;
            }
            .sql-comment {
                -fx-fill: #808080;
                -fx-font-style: italic;
            }
            .sql-operator {
                -fx-fill: #FF8000;
                -fx-font-weight: bold;
            }
            """;
    }
}
