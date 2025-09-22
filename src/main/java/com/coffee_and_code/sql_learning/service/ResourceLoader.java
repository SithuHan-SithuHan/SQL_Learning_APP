package com.coffee_and_code.sql_learning.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading resources from classpath
 */
public class ResourceLoader {
    private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    /**
     * Load sample database tables SQL from resources
     */
    public static String[] loadSampleTables() {
        List<String> sqlStatements = new ArrayList<>();
        
        // Create departments table
        sqlStatements.add("""
            CREATE TABLE departments (
                id INT PRIMARY KEY AUTO_INCREMENT,
                department_name VARCHAR(100) NOT NULL,
                location VARCHAR(100),
                budget DECIMAL(15,2)
            );
            """);

        // Create employees table
        sqlStatements.add("""
            CREATE TABLE employees (
                id INT PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                email VARCHAR(100) UNIQUE,
                salary DECIMAL(10,2),
                hire_date DATE,
                department_id INT,
                manager_id INT,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (department_id) REFERENCES departments(id),
                FOREIGN KEY (manager_id) REFERENCES employees(id)
            );
            """);

        // Create projects table
        sqlStatements.add("""
            CREATE TABLE projects (
                id INT PRIMARY KEY AUTO_INCREMENT,
                project_name VARCHAR(100) NOT NULL,
                description TEXT,
                start_date DATE,
                end_date DATE,
                budget DECIMAL(12,2),
                department_id INT,
                FOREIGN KEY (department_id) REFERENCES departments(id)
            );
            """);

        // Create employee_projects table (many-to-many relationship)
        sqlStatements.add("""
            CREATE TABLE employee_projects (
                employee_id INT,
                project_id INT,
                role VARCHAR(50),
                start_date DATE,
                end_date DATE,
                PRIMARY KEY (employee_id, project_id),
                FOREIGN KEY (employee_id) REFERENCES employees(id),
                FOREIGN KEY (project_id) REFERENCES projects(id)
            );
            """);

        // Create customers table
        sqlStatements.add("""
            CREATE TABLE customers (
                id INT PRIMARY KEY AUTO_INCREMENT,
                company_name VARCHAR(100) NOT NULL,
                contact_person VARCHAR(100),
                email VARCHAR(100),
                phone VARCHAR(20),
                address TEXT,
                city VARCHAR(50),
                country VARCHAR(50)
            );
            """);

        // Create orders table
        sqlStatements.add("""
            CREATE TABLE orders (
                id INT PRIMARY KEY AUTO_INCREMENT,
                customer_id INT,
                order_date DATE,
                total_amount DECIMAL(10,2),
                status VARCHAR(20) DEFAULT 'PENDING',
                FOREIGN KEY (customer_id) REFERENCES customers(id)
            );
            """);

        // Insert sample data for departments
        sqlStatements.add("""
            INSERT INTO departments (department_name, location, budget) VALUES
            ('Engineering', 'San Francisco', 2000000.00),
            ('Marketing', 'New York', 800000.00),
            ('Sales', 'Chicago', 1200000.00),
            ('HR', 'Boston', 500000.00),
            ('Finance', 'Seattle', 600000.00);
            """);

        // Insert sample data for employees
        sqlStatements.add("""
            INSERT INTO employees (first_name, last_name, email, salary, hire_date, department_id, manager_id) VALUES
            ('John', 'Doe', 'john.doe@company.com', 75000.00, '2020-01-15', 1, NULL),
            ('Jane', 'Smith', 'jane.smith@company.com', 82000.00, '2019-03-20', 1, 1),
            ('Mike', 'Johnson', 'mike.johnson@company.com', 68000.00, '2021-06-10', 1, 1),
            ('Sarah', 'Williams', 'sarah.williams@company.com', 71000.00, '2020-09-05', 2, NULL),
            ('David', 'Brown', 'david.brown@company.com', 65000.00, '2021-02-14', 2, 4),
            ('Lisa', 'Davis', 'lisa.davis@company.com', 78000.00, '2018-11-30', 3, NULL),
            ('Tom', 'Wilson', 'tom.wilson@company.com', 72000.00, '2020-04-22', 3, 6),
            ('Amy', 'Garcia', 'amy.garcia@company.com', 69000.00, '2021-08-18', 4, NULL),
            ('Chris', 'Martinez', 'chris.martinez@company.com', 63000.00, '2022-01-12', 4, 8),
            ('Emma', 'Anderson', 'emma.anderson@company.com', 74000.00, '2019-07-08', 5, NULL);
            """);

        // Insert sample data for projects
        sqlStatements.add("""
            INSERT INTO projects (project_name, description, start_date, end_date, budget, department_id) VALUES
            ('Website Redesign', 'Complete redesign of company website', '2023-01-01', '2023-06-30', 150000.00, 1),
            ('Mobile App Development', 'New mobile application for customers', '2023-02-15', '2023-12-31', 300000.00, 1),
            ('Marketing Campaign 2023', 'Annual marketing campaign', '2023-01-01', '2023-12-31', 200000.00, 2),
            ('Sales Training Program', 'Training program for sales team', '2023-03-01', '2023-08-31', 75000.00, 3),
            ('HR System Upgrade', 'Upgrade of HR management system', '2023-04-01', '2023-10-31', 100000.00, 4);
            """);

        // Insert sample data for employee_projects
        sqlStatements.add("""
            INSERT INTO employee_projects (employee_id, project_id, role, start_date, end_date) VALUES
            (1, 1, 'Project Manager', '2023-01-01', '2023-06-30'),
            (2, 1, 'Frontend Developer', '2023-01-01', '2023-06-30'),
            (3, 1, 'Backend Developer', '2023-01-01', '2023-06-30'),
            (1, 2, 'Technical Lead', '2023-02-15', '2023-12-31'),
            (2, 2, 'Mobile Developer', '2023-02-15', '2023-12-31'),
            (4, 3, 'Campaign Manager', '2023-01-01', '2023-12-31'),
            (5, 3, 'Content Creator', '2023-01-01', '2023-12-31'),
            (6, 4, 'Training Coordinator', '2023-03-01', '2023-08-31'),
            (7, 4, 'Sales Trainer', '2023-03-01', '2023-08-31'),
            (8, 5, 'Project Manager', '2023-04-01', '2023-10-31'),
            (9, 5, 'System Analyst', '2023-04-01', '2023-10-31');
            """);

        // Insert sample data for customers
        sqlStatements.add("""
            INSERT INTO customers (company_name, contact_person, email, phone, address, city, country) VALUES
            ('TechCorp Inc', 'Alice Johnson', 'alice@techcorp.com', '+1-555-0101', '123 Tech Street', 'San Francisco', 'USA'),
            ('Global Solutions Ltd', 'Bob Smith', 'bob@globalsolutions.com', '+1-555-0102', '456 Business Ave', 'New York', 'USA'),
            ('Innovation Co', 'Carol Davis', 'carol@innovation.com', '+1-555-0103', '789 Innovation Blvd', 'Chicago', 'USA'),
            ('Future Systems', 'David Wilson', 'david@futuresystems.com', '+1-555-0104', '321 Future Lane', 'Boston', 'USA'),
            ('Digital Enterprises', 'Eva Brown', 'eva@digitalenterprises.com', '+1-555-0105', '654 Digital Drive', 'Seattle', 'USA');
            """);

        // Insert sample data for orders
        sqlStatements.add("""
            INSERT INTO orders (customer_id, order_date, total_amount, status) VALUES
            (1, '2023-01-15', 25000.00, 'COMPLETED'),
            (1, '2023-02-20', 18000.00, 'PENDING'),
            (2, '2023-01-25', 32000.00, 'COMPLETED'),
            (2, '2023-03-10', 15000.00, 'SHIPPED'),
            (3, '2023-02-05', 28000.00, 'COMPLETED'),
            (3, '2023-03-15', 22000.00, 'PENDING'),
            (4, '2023-01-30', 19000.00, 'COMPLETED'),
            (4, '2023-02-28', 35000.00, 'SHIPPED'),
            (5, '2023-02-12', 26000.00, 'COMPLETED'),
            (5, '2023-03-20', 14000.00, 'PENDING');
            """);

        return sqlStatements.toArray(new String[0]);
    }

    /**
     * Load resource file as string
     */
    public static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = ResourceLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            logger.error("Failed to load resource: " + resourcePath, e);
            return "";
        }
    }

    /**
     * Load resource file as list of strings
     */
    public static List<String> loadResourceAsList(String resourcePath) {
        List<String> lines = new ArrayList<>();
        try (InputStream inputStream = ResourceLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.error("Failed to load resource: " + resourcePath, e);
        }
        return lines;
    }
}
