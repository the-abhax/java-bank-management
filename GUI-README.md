# Banking Management System - GUI Version


This is a complete **Swing-based GUI** application for the Banking Management System that provides an intuitive and user-friendly interface for all banking operations.

##  Features

###  **Welcome Screen**
- **Register New User** - Create a new user account
- **Login** - Access existing user account  
- **Exit** - Close the application safely

###  **User Registration**
- Full name input with validation
- Email validation (must contain @)
- Password validation (minimum 6 characters)
- Duplicate email detection
- Clean, responsive form design

###  **User Login**
- Secure email/password authentication
- Error handling for invalid credentials
- Automatic redirect to banking dashboard

###  **Banking Dashboard**
- **Real-time account information display**
- **Current balance shown in header**
- **Six main operations:**
  -  **Debit Money** - Withdraw funds
  -  **Credit Money** - Deposit funds
  -  **Transfer Money** - Send money to other accounts
  -  **Check Balance** - View current balance with PIN verification
  -  **Refresh** - Update account information
  -  **Logout** - Return to welcome screen

###  **Account Creation**
- Automatic detection if user needs to create bank account
- Full name input for account holder
- Initial deposit amount (can be zero)
- Security PIN setup
- Auto-generated unique account numbers

###  **Transaction Operations**
- **Debit Operations:**
  - Amount validation (positive numbers only)
  - Insufficient balance detection
  - PIN verification
  - Real-time balance updates

- **Credit Operations:**
  - Amount validation
  - PIN verification
  - Instant balance updates

- **Transfer Operations:**
  - Receiver account validation
  - Self-transfer prevention
  - Insufficient balance checks
  - PIN verification
  - Atomic transactions (both debit and credit succeed or both fail)

## How to Run the GUI Application

### **Method 1: PowerShell Script (Recommended)**
```powershell
.\run-gui.ps1
```

### **Method 2: Batch File**
```cmd
run-gui.bat
```

### **Method 3: Manual Command**
```bash
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingGUI
```

##  User Journey

### **First-Time User:**
1. Launch application → Welcome screen appears
2. Click "Register New User" → Fill registration form
3. Click "Login" → Enter credentials
4. Automatically redirected to account creation
5. Fill account details → Account created with unique number
6. Access full banking dashboard

### **Returning User:**
1. Launch application → Welcome screen appears
2. Click "Login" → Enter credentials
3. Directly access banking dashboard
4. Perform banking operations

##  Design Features

### **Visual Design:**
- **Clean, professional color scheme**
- **Intuitive button layouts with hover effects**
- **Consistent typography (Arial font family)**
- **Responsive form layouts**
- **Card-based navigation system**

### **User Experience:**
- **Input validation with user-friendly error messages**
- **Progress feedback for database operations**
- **Confirmation dialogs for critical actions**
- **Automatic form clearing after successful operations**
- **Real-time balance updates**

### **Security Features:**
- **Password field masking**
- **PIN verification for all transactions**
- **SQL injection prevention with prepared statements**
- **Transaction atomicity (all-or-nothing operations)**

##  Technical Implementation

### **Architecture:**
- **MVC Pattern** - Separation of GUI, business logic, and data access
- **CardLayout** - Smooth screen transitions
- **Event-driven programming** - Responsive user interactions
- **Database connection pooling** - Efficient resource management

### **Key Components:**
- `BankingGUI.java` - Main application window with registration/login
- `BankingDashboard.java` - Banking operations interface
- Integration with existing `User.java`, `Accounts.java`, `AccountManager.java`

### **Database Integration:**
- **MySQL 8.0 compatibility**
- **JDBC connection management**
- **Prepared statements for security**
- **Transaction management**
- **Error handling and rollback**

##  Screenshots Description

The application features:
- **Welcome Screen**: Clean title with three prominent action buttons
- **Registration Form**: Organized input fields with validation
- **Login Form**: Simple email/password entry
- **Dashboard**: Account info header with six operation buttons in grid layout
- **Transaction Forms**: Focused single-purpose interfaces
- **Account Creation**: Guided setup for new bank accounts

##  Configuration

### **Database Settings** (in BankingGUI.java):
- URL: `jdbc:mysql://localhost:3306/banking_system`
- Username: `root`
- Password: set with the `DB_PASSWORD` environment variable

### **GUI Settings:**
- Main window size: 800x600 pixels
- Dashboard size: 900x700 pixels
- Professional color scheme with consistent styling

##  Error Handling

The application provides comprehensive error handling for:
- **Database connection failures**
- **Invalid user input**
- **Network connectivity issues**
- **Transaction failures**
- **Insufficient funds**
- **Invalid PINs**
- **Account not found errors**

##  Benefits of GUI Version

### **Over Console Application:**
-  **Intuitive visual interface**
-  **Better user experience**
-  **Professional appearance**
-  **Easier navigation**
-  **Real-time visual feedback**
-  **Modern application feel**

### **Production Ready:**
-  **Complete input validation**
-  **Security measures implemented**
-  **Error handling throughout**
-  **Professional UI/UX design**
-  **Scalable architecture**

##  Requirements

- **Java 8 or higher**
- **MySQL Server running on localhost:3306**
- **MySQL JDBC Driver** (included in lib/ directory)
- **Windows/Linux/macOS** (cross-platform compatible)

---

