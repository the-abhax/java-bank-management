# Banking Management System PowerShell Runner
Write-Host "Banking Management System" -ForegroundColor Green

# Check if compiled classes exist
if (-Not (Test-Path "bin\BankingManagementSystem\BankingApp.class")) {
    Write-Host "Class files not found. Compiling first..." -ForegroundColor Yellow
    javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit
    }
    Write-Host "Compilation successful!" -ForegroundColor Green
}

Write-Host "Running Banking Management System..." -ForegroundColor Blue
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingApp
