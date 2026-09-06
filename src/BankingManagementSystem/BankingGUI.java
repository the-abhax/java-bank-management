package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankingGUI extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String username = "root";
    private static final String password = System.getenv().getOrDefault("DB_PASSWORD", "");
    
    private Connection connection;
    private Accounts accounts;
    private AccountManager accountManager;
    
    // GUI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public BankingGUI() {
        initializeDatabase();
        initializeGUI();
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            
            accounts = new Accounts(connection, null);
            accountManager = new AccountManager(connection, null);
            
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Database connection failed: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initializeGUI() {
        setTitle("Banking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Use CardLayout to switch between different screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different panels
        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createRegistrationPanel(), "REGISTER");
        mainPanel.add(createLoginPanel(), "LOGIN");
        
        add(mainPanel);
        
        // Show welcome screen first
        cardLayout.show(mainPanel, "WELCOME");
        
        setVisible(true);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("Banking Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 200, 100, 200));
        buttonPanel.setOpaque(false);
        
        JButton registerBtn = createStyledButton("Register New User", new Color(34, 139, 34));
        JButton loginBtn = createStyledButton("Login", new Color(30, 144, 255));
        JButton exitBtn = createStyledButton("Exit", new Color(220, 20, 60));
        
        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        exitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                try {
                    if (connection != null) connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
        
        buttonPanel.add(registerBtn);
        buttonPanel.add(loginBtn);
        buttonPanel.add(exitBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Form fields
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        // Styling for text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        nameField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton registerBtn = createStyledButton("Register", new Color(34, 139, 34));
        JButton backBtn = createStyledButton("Back", new Color(128, 128, 128));
        
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                registerUser(name, email, pass);
                JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                cardLayout.show(mainPanel, "WELCOME");
            } catch (Exception ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "Email already exists! Please use a different email.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        backBtn.addActionListener(e -> {
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });
        
        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Form fields
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        // Styling for text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        emailField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton loginBtn = createStyledButton("Login", new Color(30, 144, 255));
        JButton backBtn = createStyledButton("Back", new Color(128, 128, 128));
        
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            
            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (loginUser(email, pass)) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    emailField.setText("");
                    passwordField.setText("");
                    openBankingDashboard(email);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> {
            emailField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Database operations
    private void registerUser(String name, String email, String password) throws SQLException {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.executeUpdate();
    }
    
    private boolean loginUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email=? AND password=?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
    
    private void openBankingDashboard(String email) {
        // Close current window and open banking dashboard
        this.dispose();
        new BankingDashboard(email, connection, accounts, accountManager);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingGUI());
    }
}
