package codSoft;

import java.sql.*;
import java.util.Scanner;

public class StudentGradeCalculator {

    // Check Database connection
    static final String URL = "jdbc:mysql://localhost:3306/codSoft_task";
    static final String USER = "root";
    static final String PASS = "Sunil@1319";

    static Connection conn = null;

    public static void main(String[] args) {
        try {
            // Check connection Establish or not
            conn = DriverManager.getConnection(URL, USER, PASS);
            createDatabaseTable();

            calculateGrade();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create the table already exist or not in database
    public static void createDatabaseTable() throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS students_grades (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "student_name VARCHAR(255), " +
                "total_marks INT, " +
                "average_percentage FLOAT, " +
                "grade CHAR(1))";
        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);
    }

    //calculate the grade based on input marks
    public static void calculateGrade() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter student name: ");
        String studentName = scanner.nextLine();

        System.out.print("Enter number of subjects: ");
        int numSubjects = scanner.nextInt();

        int[] marks = new int[numSubjects];
        int totalMarks = 0;

        for (int i = 0; i < numSubjects; i++) {
            System.out.print("Enter marks for subject " + (i + 1) + ": ");
            marks[i] = scanner.nextInt();
            totalMarks += marks[i];
        }

        // Cal average percentage
        float averagePercentage = (float) totalMarks / numSubjects;

        // Cal grade based on average percentage
        char grade = calculateGradeFromPercentage(averagePercentage);

        // Display results
        System.out.println("Total Marks: " + totalMarks);
        System.out.println("Average Percentage: " + averagePercentage + "%");
        System.out.println("Grade: " + grade);

        // Save the result to the database
        saveGrade(studentName, totalMarks, averagePercentage, grade);
    }

    // Method to assign grade based on percentage
    public static char calculateGradeFromPercentage(float averagePercentage) {
        if (averagePercentage >= 90) {
            return 'A';
        } else if (averagePercentage >= 80) {
            return 'B';
        } else if (averagePercentage >= 70) {
            return 'C';
        } else if (averagePercentage >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }

    // Method to save student grade details to MySQL database
    public static void saveGrade(String studentName, int totalMarks, float averagePercentage, char grade) throws SQLException {
        String sqlInsert = "INSERT INTO students_grades (student_name, total_marks, average_percentage, grade) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sqlInsert);
        pstmt.setString(1, studentName);
        pstmt.setInt(2, totalMarks);
        pstmt.setFloat(3, averagePercentage);
        pstmt.setString(4, String.valueOf(grade));
        pstmt.executeUpdate();
        System.out.println("Grade details saved for " + studentName);
    }
}
