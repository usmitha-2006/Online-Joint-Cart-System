package com.mycompany.app;


import java.sql.*;

public class StudentDatabaseExample {

 
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Student";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123";

    public static void main(String[] args) {
        try {
            // Step 1: Establishing a connection to the database
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                System.out.println("Connected to the database.");
                
                insertStudent(connection,"Smitha","U",34,"Information Science" );

                // Step 2: Inserting data into Student table
                insertStudent(connection, "Pankaj", "Kumar", 25, "Information Science and Engineering");

                // Step 3: Updating data in Student table
                updateStudent(connection, 1, "Kumar", "Pankaj", 26, "Computer Science and Engineering");

                // Step 4: Deleting data from Student table
                deleteStudent(connection, 1);

                // Step 5: Searching data in Student table
                searchStudent(connection, "Pankaj");

                // Step 6: Displaying all records in Student table
                displayAllStudents(connection);
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    // Method to insert data into Student table
    private static void insertStudent(Connection connection, String firstName, String lastName, int age, String departmenet) throws SQLException {
        String insertQuery = "INSERT INTO Student (first_name, last_name, age, departmenet) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, departmenet);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new student record was inserted successfully!");
            }
        }
    }

    // Method to update data in Student table
    private static void updateStudent(Connection connection, int studentId, String firstName, String lastName, int age, String departmenet) throws SQLException {
        String updateQuery = "UPDATE Student SET first_name=?, last_name=?, age=?, departmenet=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, departmenet);
            preparedStatement.setInt(5, studentId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Student record with ID " + studentId + " was updated successfully!");
            }
        }
    }

    // Method to delete data from Student table
    private static void deleteStudent(Connection connection, int studentId) throws SQLException {
        String deleteQuery = "DELETE FROM Student WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, studentId);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Student record with ID " + studentId + " was deleted successfully!");
            }
        }
    }

    // Method to search for data in Student table
    private static void searchStudent(Connection connection, String firstName) throws SQLException {
        String searchQuery = "SELECT * FROM Student WHERE first_name=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
            preparedStatement.setString(1, firstName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Student found:");
                    System.out.println("ID: " + resultSet.getInt("id"));
                    System.out.println("First Name: " + resultSet.getString("first_name"));
                    System.out.println("Last Name: " + resultSet.getString("last_name"));
                    System.out.println("Age: " + resultSet.getInt("age"));
                    System.out.println("Departmenet: " + resultSet.getString("departmenet"));
                } else {
                    System.out.println("No student found with first name: " + firstName);
                }
            }
        }
    }

    // Method to display all records in Student table
    private static void displayAllStudents(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM Student";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("First Name: " + resultSet.getString("first_name"));
                System.out.println("Last Name: " + resultSet.getString("last_name"));
                System.out.println("Age: " + resultSet.getInt("age"));
                System.out.println("Departmenet: " + resultSet.getString("departmenet"));
                System.out.println("----------------------");
            }
        }
    }
}
