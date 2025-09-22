# SQL Learning Desktop Application

A comprehensive desktop application built with Java and JavaFX for learning SQL through interactive theory lessons and hands-on practice exercises.

## Features

### 📚 Learning Section
- **DDL (Data Definition Language)**: CREATE, ALTER, DROP, Constraints
- **DML (Data Manipulation Language)**: SELECT, INSERT, UPDATE, DELETE, Joins, Subqueries
- **DCL (Data Control Language)**: GRANT, REVOKE
- **TCL (Transaction Control Language)**: COMMIT, ROLLBACK, SAVEPOINT
- **Database Normalization**: 1NF, 2NF, 3NF, BCNF

### 🎯 Practice Section
- **Multiple Difficulty Levels**: Easy, Medium, Hard, Pro
- **Interactive SQL Editor** with syntax highlighting
- **Real-time SQL validation** and execution
- **Database browser** for exploring table structures
- **Progress tracking** and completion status
- **Sample database** with realistic data for practice

### 🛠️ Technical Features
- **Modern JavaFX UI** with responsive design
- **Embedded H2 Database** for practice
- **SQL Syntax Highlighting** with RichTextFX
- **User Progress Persistence** with JSON
- **Comprehensive Error Handling**
- **Professional Styling** with CSS

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **IntelliJ IDEA Ultimate** (recommended)

## Installation & Setup

1. **Clone or download** the project to your local machine

2. **Open in IntelliJ IDEA Ultimate**:
   - File → Open → Select the project folder
   - IntelliJ will automatically detect the Maven project

3. **Install Dependencies**:
   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   - **Option 1**: Run `SQLLearningApp.java` directly
   - **Option 2**: Use Maven: `mvn javafx:run`
   - **Option 3**: Build JAR: `mvn clean package` then run the generated JAR

## Project Structure

```
src/
├── main/
│   ├── java/com/coffee_and_code/sql_learning/
│   │   ├── SQLLearningApp.java              # Main application class
│   │   ├── controller/
│   │   │   └── MainController.java          # Main UI controller
│   │   └── service/
│   │       ├── DatabaseService.java         # Database operations
│   │       ├── LearningContentService.java  # Learning content management
│   │       ├── PracticeService.java         # Practice questions & progress
│   │       ├── SqlSyntaxHighlighter.java    # SQL syntax highlighting
│   │       └── ResourceLoader.java          # Resource loading utilities
│   └── resources/
│       ├── fxml/
│       │   └── main.fxml                    # Main UI layout
│       ├── css/
│       │   └── styles.css                   # Application styling
│       └── images/
│           └── sql-icon.png                 # Application icon
└── test/
    └── java/                                # Unit tests (to be added)
```

## Usage

### Learning Mode
1. **Select a topic** from the left panel (DDL, DML, DCL, TCL, or Normalization)
2. **Read the content** in the main panel
3. **Navigate** using Previous/Next buttons
4. **Track progress** with the progress bar

### Practice Mode
1. **Choose a question** from the questions list
2. **Read the problem description** and requirements
3. **Write your SQL solution** in the editor
4. **Execute** to test your query
5. **Validate** syntax before submitting
6. **Submit** your answer for evaluation
7. **Reset database** if needed to start fresh

### Database Browser
- **View all tables** in the database
- **Examine table structures** with column details
- **Understand relationships** between tables

## Sample Database Schema

The application includes a comprehensive sample database with:

- **Departments**: Company departments with budgets and locations
- **Employees**: Staff information with salaries and relationships
- **Projects**: Company projects with budgets and timelines
- **Employee_Projects**: Many-to-many relationship table
- **Customers**: Customer information for order management
- **Orders**: Customer orders with status tracking

## Practice Questions

### Easy Level
- Basic SELECT queries
- Simple WHERE conditions
- COUNT functions

### Medium Level
- GROUP BY and aggregation
- ORDER BY and LIMIT
- Pattern matching with LIKE

### Hard Level
- Complex subqueries
- Advanced filtering
- Statistical queries

### Pro Level
- Complex JOINs with aggregation
- Window functions
- Self-joins and hierarchical queries

## Customization

### Adding New Learning Content
1. Edit `LearningContentService.java`
2. Add new content methods
3. Update the topics tree structure

### Adding New Practice Questions
1. Edit `PracticeService.java`
2. Add new question creation methods
3. Update difficulty levels as needed

### Modifying Database Schema
1. Edit `ResourceLoader.java`
2. Update the `loadSampleTables()` method
3. Add new tables and sample data

## Building for Distribution

### Create Executable JAR
```bash
mvn clean package
```
The JAR file will be created in the `target/` directory.

### Run the JAR
```bash
java -jar target/sql-learning-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Troubleshooting

### Common Issues

1. **JavaFX Runtime Not Found**
   - Ensure Java 17+ is installed
   - Check JavaFX modules are available

2. **Database Connection Issues**
   - The app uses embedded H2 database
   - No external database setup required

3. **UI Not Loading**
   - Check that FXML and CSS files are in the correct resources directory
   - Verify Maven resource filtering is working

4. **Compilation Errors**
   - Ensure all dependencies are downloaded: `mvn clean install`
   - Check Java version compatibility

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Future Enhancements

- [ ] User authentication and profiles
- [ ] More practice question categories
- [ ] SQL query performance analysis
- [ ] Export/import of user progress
- [ ] Dark mode theme
- [ ] Mobile-responsive design
- [ ] Integration with external databases
- [ ] Advanced SQL features (CTEs, Window Functions, etc.)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with JavaFX and Maven
- Uses H2 Database for embedded SQL practice
- RichTextFX for syntax highlighting
- Jackson for JSON processing

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the code comments
3. Create an issue in the project repository

---

**Happy SQL Learning! 🚀**
