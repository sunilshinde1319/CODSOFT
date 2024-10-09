package codSoft;

import java.sql.*;
import java.util.Scanner;

class BankAccount {
    private int accountNumber;
    private String userName;
    private double balance;
    private Connection conn;

    // Constructor to initialize the account from the database
    public BankAccount(int accountNumber) throws SQLException {
        this.accountNumber = accountNumber;

        // Establish connection to MySQL
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/codSoft_task", "root", "Sunil@1319");

        // Retrieve the account details from the database
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE account_number = ?");
        stmt.setInt(1, accountNumber);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            this.userName = rs.getString("user_name");
            this.balance = rs.getDouble("balance");
        } else {
            System.out.println("Account not found.");
            System.exit(0);
        }
    }

    // Method to withdraw amount
    public boolean withdraw(double amount) throws SQLException {
        if (amount > balance) {
            System.out.println("Insufficient balance for the withdrawal.");
            return false;
        } else {
            balance -= amount;
            updateBalance();
            System.out.println("Successfully withdrawn: RS." + amount);
            return true;
        }
    }

    // Method to deposit amount
    public void deposit(double amount) throws SQLException {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount.");
        } else {
            balance += amount;
            updateBalance();
            System.out.println("Successfully deposited: RS." + amount);
        }
    }

    // Method to check the current balance
    public double checkBalance() {
        return balance;
    }

    // Method to get user account details
    public void displayAccountDetails() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("User Name: " + userName);
        System.out.println("Current Balance: RS." + balance);
    }

    // Method to update the balance in the database
    private void updateBalance() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET balance = ? WHERE account_number = ?");
        stmt.setDouble(1, balance);
        stmt.setInt(2, accountNumber);
        stmt.executeUpdate();
    }

    // Close the database connection
    public void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}

class ATM {
    private BankAccount account;

    // Constructor to initialize ATM with a user's bank account
    public ATM(BankAccount account) {
        this.account = account;
    }

    // Method to display ATM menu
    public void displayMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nATM Menu:");
            System.out.println("1. Display Account Details");
            System.out.println("2. Check Balance");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Deposit Money");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Display Account Details
                    account.displayAccountDetails();
                    break;

                case 2:
                    // Check Balance
                    System.out.println("Your current balance is: RS." + account.checkBalance());
                    break;

                case 3:
                    // Withdraw Money
                    System.out.print("Enter the amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    account.withdraw(withdrawAmount);
                    break;

                case 4:
                    // Deposit Money
                    System.out.print("Enter the amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    account.deposit(depositAmount);
                    break;

                case 5:
                    // Exit
                    System.out.println("Thank you for Visiting!. Have a Good Day!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
        account.closeConnection();
    }
}

public class ATMInterface {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Get user account number
            System.out.print("Enter your account number: ");
            int accountNumber = scanner.nextInt();

            // Initialize the bank account from the database
            BankAccount userAccount = new BankAccount(accountNumber);

            // Create an ATM and connect it to the user's bank account
            ATM atm = new ATM(userAccount);

            // Display the ATM menu to the user
            atm.displayMenu();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
