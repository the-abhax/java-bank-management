package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        try {
            scanner.nextLine(); // consume any leftover newline
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            // Basic validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required!");
                return;
            }
            
            if (!email.contains("@")) {
                System.out.println("Please enter a valid email address!");
                return;
            }
            
            if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters long!");
                return;
            }

            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);

            stmt.executeUpdate();
            System.out.println("User Registered Successfully!");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                System.out.println("Email already exists! Please use a different email.");
            } else {
                System.out.println("Registration failed: " + e.getMessage());
            }
        }
    }

    public String login() {
        try {
            System.out.print("Enter Email: ");
            String email = scanner.next();
            System.out.print("Enter Password: ");
            String password = scanner.next();

            String query = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return email; // login success
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login failed
    }
}


