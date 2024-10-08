package codSoft;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class NumberGame {

    // MySQL database connection details
    static final String URL = "jdbc:mysql://localhost:3306/codSoft_task";
    static final String USER = "root";
    static final String PASS = "Sunil@1319";

    static Connection conn = null;

    public static void main(String[] args) {
        try {
            // Establish MySQL connection
            conn = DriverManager.getConnection(URL, USER, PASS);

            createDatabaseTable();
            playGame();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDatabaseTable() throws SQLException {
        // Create table if not exists
        String sqlCreate = "CREATE TABLE IF NOT EXISTS NumberGame_scores (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255), " +
                "score INT)";
        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);
    }

    public static void playGame() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String playAgain;

        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();

        do {
            int randomNumber = generateRandomNumber(1, 100);
            int attempts = 0;
            boolean correctGuess = false;

            System.out.println("Guess a number between 1 and 100!");

            while (!correctGuess && attempts < 10) {
                attempts++;
                System.out.print("Attempt " + attempts + ": Enter your guess: ");
                int userGuess = scanner.nextInt();

                correctGuess = checkGuess(userGuess, randomNumber);
            }

            if (!correctGuess) {
                System.out.println("Sorry! You've reached the maximum attempts. The number was " + randomNumber);
            }

            saveScore(playerName, attempts);
            System.out.println("Do you want to play another round? (yes/no)");
            playAgain = scanner.next();

        } while (playAgain.equalsIgnoreCase("yes"));

        displayScores();
    }

    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean checkGuess(int userGuess, int randomNumber) {
        if (userGuess > randomNumber) {
            System.out.println("Too high!");
            return false;
        } else if (userGuess < randomNumber) {
            System.out.println("Too low!");
            return false;
        } else {
            System.out.println("Correct! You guessed the number.");
            return true;
        }
    }

    public static void saveScore(String playerName, int attempts) throws SQLException {
        String sqlInsert = "INSERT INTO NumberGame_scores (username, score) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sqlInsert);
        pstmt.setString(1, playerName);
        pstmt.setInt(2, attempts);
        pstmt.executeUpdate();
        System.out.println("Score saved for " + playerName);
    }

    public static void displayScores() throws SQLException {
        System.out.println("Scoreboard:");
        String sqlSelect = "SELECT * FROM NumberGame_scores ORDER BY score ASC";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sqlSelect);

        while (rs.next()) {
            System.out.println("Player: " + rs.getString("username") + " | Attempts: " + rs.getInt("score"));
        }
    }
}

