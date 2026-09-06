# Banking Management System Setup Guide

## Prerequisites
1. **Java 8 or higher** installed
2. **MySQL Server** running on localhost:3306
3. **MySQL Root user** with password (update in BankingApp.java if different)

## Database Setup
1. Run the provided SQL script to create the database:
   ```sql
   mysql -u root -p < banking_system.sql
   ```
   Or run the SQL commands from `banking_system.sql` in your MySQL client.

## Running the Application

### Method 1: Using PowerShell (Recommended)
```powershell
.\run.ps1
```

### Method 2: Using Batch File
```cmd
run.bat
```

### Method 3: Manual Compilation and Execution
```bash
# Compile
javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java

# Run
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingApp
```

## Configuration
- **Database URL**: `jdbc:mysql://localhost:3306/banking_system`
- **Username**: `root`
- **Password**: set the `DB_PASSWORD` environment variable before running

## Features
-  User Registration and Login
-  Account Management (Open/Close)
-  Money Transfer between accounts
-  Balance Inquiry
-  Transaction Security (PIN-based)
-  Input Validation
-  Error Handling



## Troubleshooting
- If MySQL connection fails, ensure MySQL is running and credentials are correct
- If compilation fails, ensure Java and MySQL connector JAR are in the correct paths
- For permission issues with PowerShell, run: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser`
