package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankingDashboard extends JFrame {
    private String userEmail;
    private Connection connection;
    private long accountNumber;
    
    // GUI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel balanceLabel;
    private JLabel accountInfoLabel;
    
    public BankingDashboard(String email, Connection conn, Accounts acc, AccountManager accMgr) {
        this.userEmail = email;
        this.connection = conn;
        
        initializeGUI();
        checkAndCreateAccount();
    }
    
    private void initializeGUI() {
        setTitle("Banking Dashboard - " + userEmail);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        mainPanel.add(createDebitPanel(), "DEBIT");
        mainPanel.add(createCreditPanel(), "CREDIT");
        mainPanel.add(createTransferPanel(), "TRANSFER");
        mainPanel.add(createAccountCreationPanel(), "CREATE_ACCOUNT");
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void checkAndCreateAccount() {
        try {
            if (!accountExists(userEmail)) {
                cardLayout.show(mainPanel, "CREATE_ACCOUNT");
            } else {
                accountNumber = getAccountNumber(userEmail);
                updateAccountInfo();
                cardLayout.show(mainPanel, "DASHBOARD");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking account: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Banking Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        accountInfoLabel = new JLabel("Account: Loading...", SwingConstants.LEFT);
        accountInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        accountInfoLabel.setForeground(Color.WHITE);
        
        balanceLabel = new JLabel("Balance: Loading...", SwingConstants.RIGHT);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(accountInfoLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        buttonPanel.setOpaque(false);
        
        JButton debitBtn = createDashboardButton("Debit Money", new Color(220, 20, 60), "[-]");
        JButton creditBtn = createDashboardButton("Credit Money", new Color(34, 139, 34), "[+]");
        JButton transferBtn = createDashboardButton("Transfer Money", new Color(30, 144, 255), "[>]");
        JButton balanceBtn = createDashboardButton("Check Balance", new Color(255, 140, 0), "[$]");
        JButton refreshBtn = createDashboardButton("Refresh", new Color(128, 0, 128), "[R]");
        JButton logoutBtn = createDashboardButton("Logout", new Color(128, 128, 128), "[X]");
        
        debitBtn.addActionListener(e -> cardLayout.show(mainPanel, "DEBIT"));
        creditBtn.addActionListener(e -> cardLayout.show(mainPanel, "CREDIT"));
        transferBtn.addActionListener(e -> cardLayout.show(mainPanel, "TRANSFER"));
        balanceBtn.addActionListener(e -> checkBalance());
        refreshBtn.addActionListener(e -> {
            updateAccountInfo();
            JOptionPane.showMessageDialog(this, "Account information refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(debitBtn);
        buttonPanel.add(creditBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(balanceBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(logoutBtn);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createDashboardButton(String text, Color color, String emoji) {
        JButton button = new JButton(emoji + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 80));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
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
    
    private JPanel createDebitPanel() {
        return createTransactionPanel("Debit Money", "Enter amount to withdraw:", "DEBIT");
    }
    
    private JPanel createCreditPanel() {
        return createTransactionPanel("Credit Money", "Enter amount to deposit:", "CREDIT");
    }
    
    private JPanel createTransactionPanel(String title, String instruction, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel instructionLabel = new JLabel(instruction);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JTextField amountField = new JTextField(15);
        JPasswordField pinField = new JPasswordField(15);
        
        // Styling
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        amountField.setFont(fieldFont);
        pinField.setFont(fieldFont);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(instructionLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Security PIN:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pinField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        Color buttonColor = type.equals("DEBIT") ? new Color(220, 20, 60) : new Color(34, 139, 34);
        JButton submitBtn = createStyledButton(title, buttonColor);
        JButton backBtn = createStyledButton("Back to Dashboard", new Color(128, 128, 128));
        
        submitBtn.addActionListener(e -> {
            String amountStr = amountField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            
            if (amountStr.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (type.equals("DEBIT")) {
                    performDebit(amount, pin);
                } else {
                    performCredit(amount, pin);
                }
                
                amountField.setText("");
                pinField.setText("");
                updateAccountInfo();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> {
            amountField.setText("");
            pinField.setText("");
            cardLayout.show(mainPanel, "DASHBOARD");
        });
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("Transfer Money", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JTextField receiverField = new JTextField(15);
        JTextField amountField = new JTextField(15);
        JPasswordField pinField = new JPasswordField(15);
        
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        receiverField.setFont(fieldFont);
        amountField.setFont(fieldFont);
        pinField.setFont(fieldFont);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Receiver Account:"), gbc);
        gbc.gridx = 1;
        formPanel.add(receiverField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Security PIN:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pinField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton transferBtn = createStyledButton("Transfer Money", new Color(30, 144, 255));
        JButton backBtn = createStyledButton("Back to Dashboard", new Color(128, 128, 128));
        
        transferBtn.addActionListener(e -> {
            String receiverStr = receiverField.getText().trim();
            String amountStr = amountField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            
            if (receiverStr.isEmpty() || amountStr.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                long receiverAccount = Long.parseLong(receiverStr);
                double amount = Double.parseDouble(amountStr);
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (receiverAccount == accountNumber) {
                    JOptionPane.showMessageDialog(this, "Cannot transfer money to the same account!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                performTransfer(receiverAccount, amount, pin);
                
                receiverField.setText("");
                amountField.setText("");
                pinField.setText("");
                updateAccountInfo();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> {
            receiverField.setText("");
            amountField.setText("");
            pinField.setText("");
            cardLayout.show(mainPanel, "DASHBOARD");
        });
        
        buttonPanel.add(transferBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAccountCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("Create Bank Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        JLabel subtitleLabel = new JLabel("You need to create a bank account to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JTextField fullNameField = new JTextField(20);
        JTextField initialAmountField = new JTextField(20);
        JPasswordField pinField = new JPasswordField(20);
        
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        fullNameField.setFont(fieldFont);
        initialAmountField.setFont(fieldFont);
        pinField.setFont(fieldFont);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Initial Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(initialAmountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Security PIN:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pinField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton createBtn = createStyledButton("Create Account", new Color(34, 139, 34));
        JButton logoutBtn = createStyledButton("Logout", new Color(220, 20, 60));
        
        createBtn.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String amountStr = initialAmountField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            
            if (fullName.isEmpty() || amountStr.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double initialAmount = Double.parseDouble(amountStr);
                if (initialAmount < 0) {
                    JOptionPane.showMessageDialog(this, "Initial amount cannot be negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                accountNumber = createAccount(fullName, initialAmount, pin);
                JOptionPane.showMessageDialog(this, 
                    "Account created successfully!\nYour Account Number: " + accountNumber, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                updateAccountInfo();
                cardLayout.show(mainPanel, "DASHBOARD");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Account creation failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(createBtn);
        buttonPanel.add(logoutBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
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
    
    // Database operations
    private boolean accountExists(String email) throws SQLException {
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
    
    private long getAccountNumber(String email) throws SQLException {
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getLong("account_number");
        }
        throw new SQLException("Account not found!");
    }
    
    private long createAccount(String fullName, double initialAmount, String pin) throws SQLException {
        // Generate account number
        long newAccountNumber = generateAccountNumber();
        
        String query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setLong(1, newAccountNumber);
        stmt.setString(2, fullName);
        stmt.setString(3, userEmail);
        stmt.setDouble(4, initialAmount);
        stmt.setString(5, pin);
        
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            return newAccountNumber;
        } else {
            throw new SQLException("Account creation failed!");
        }
    }
    
    private long generateAccountNumber() throws SQLException {
        String query = "SELECT account_number FROM Accounts ORDER BY account_number DESC LIMIT 1";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getLong("account_number") + 1;
        } else {
            return 10000100;
        }
    }
    
    private void updateAccountInfo() {
        try {
            String query = "SELECT full_name, balance FROM Accounts WHERE account_number = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                double balance = rs.getDouble("balance");
                
                accountInfoLabel.setText("Account: " + accountNumber + " (" + fullName + ")");
                balanceLabel.setText("Balance: ₹" + String.format("%.2f", balance));
            }
        } catch (SQLException e) {
            accountInfoLabel.setText("Account: Error loading");
            balanceLabel.setText("Balance: Error");
        }
    }
    
    private void performDebit(double amount, String pin) {
        try {
            connection.setAutoCommit(false);
            
            // Verify PIN and get current balance
            String query = "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, accountNumber);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (amount <= currentBalance) {
                    // Update balance
                    String updateQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setLong(2, accountNumber);
                    
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        connection.commit();
                        JOptionPane.showMessageDialog(this, 
                            "₹" + amount + " debited successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "DASHBOARD");
                    } else {
                        connection.rollback();
                        JOptionPane.showMessageDialog(this, "Transaction failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid security PIN!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performCredit(double amount, String pin) {
        try {
            connection.setAutoCommit(false);
            
            // Verify PIN
            String query = "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, accountNumber);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Update balance
                String updateQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setDouble(1, amount);
                updateStmt.setLong(2, accountNumber);
                
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    JOptionPane.showMessageDialog(this, 
                        "₹" + amount + " credited successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainPanel, "DASHBOARD");
                } else {
                    connection.rollback();
                    JOptionPane.showMessageDialog(this, "Transaction failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid security PIN!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performTransfer(long receiverAccount, double amount, String pin) {
        try {
            connection.setAutoCommit(false);
            
            // Check if receiver account exists
            String receiverQuery = "SELECT account_number FROM Accounts WHERE account_number = ?";
            PreparedStatement receiverStmt = connection.prepareStatement(receiverQuery);
            receiverStmt.setLong(1, receiverAccount);
            ResultSet receiverRs = receiverStmt.executeQuery();
            
            if (!receiverRs.next()) {
                JOptionPane.showMessageDialog(this, "Receiver account does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                connection.setAutoCommit(true);
                return;
            }
            
            // Verify sender PIN and balance
            String senderQuery = "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement senderStmt = connection.prepareStatement(senderQuery);
            senderStmt.setLong(1, accountNumber);
            senderStmt.setString(2, pin);
            ResultSet senderRs = senderStmt.executeQuery();
            
            if (senderRs.next()) {
                double currentBalance = senderRs.getDouble("balance");
                if (amount <= currentBalance) {
                    // Perform transfer
                    String debitQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                    String creditQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    
                    PreparedStatement debitStmt = connection.prepareStatement(debitQuery);
                    PreparedStatement creditStmt = connection.prepareStatement(creditQuery);
                    
                    debitStmt.setDouble(1, amount);
                    debitStmt.setLong(2, accountNumber);
                    creditStmt.setDouble(1, amount);
                    creditStmt.setLong(2, receiverAccount);
                    
                    int debitRows = debitStmt.executeUpdate();
                    int creditRows = creditStmt.executeUpdate();
                    
                    if (debitRows > 0 && creditRows > 0) {
                        connection.commit();
                        JOptionPane.showMessageDialog(this, 
                            "₹" + amount + " transferred successfully to account " + receiverAccount + "!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "DASHBOARD");
                    } else {
                        connection.rollback();
                        JOptionPane.showMessageDialog(this, "Transfer failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid security PIN!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkBalance() {
        String pin = JOptionPane.showInputDialog(this, "Enter your security PIN:", "Security PIN", JOptionPane.QUESTION_MESSAGE);
        if (pin == null || pin.trim().isEmpty()) return;
        
        try {
            String query = "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, accountNumber);
            stmt.setString(2, pin.trim());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                JOptionPane.showMessageDialog(this, 
                    "Current Balance: ₹" + String.format("%.2f", balance), 
                    "Account Balance", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid security PIN!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking balance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            this.dispose();
            new BankingGUI();
        }
    }
}
