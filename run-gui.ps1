# Banking Management System GUI Runner
Write-Host "Banking Management System - GUI Version" -ForegroundColor Green

# Check if compiled classes exist
if (-Not (Test-Path "bin\BankingManagementSystem\BankingGUI.class")) {
    Write-Host "GUI classes not found. Compiling first..." -ForegroundColor Yellow
    javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\BankingGUI.java src\BankingManagementSystem\BankingDashboard.java src\BankingManagementSystem\User.java src\BankingManagementSystem\Accounts.java src\BankingManagementSystem\AccountManager.java
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit
    }
    Write-Host "Compilation successful!" -ForegroundColor Green
} else {
    Write-Host "Classes found. Checking for updates..." -ForegroundColor Blue
    javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation successful!" -ForegroundColor Green
    }
}

Write-Host "Launching GUI Banking System..." -ForegroundColor Blue
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingGUI
