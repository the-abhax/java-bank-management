
@echo off
if not exist "bin\BankingManagementSystem\BankingApp.class" (
    echo Class files not found. Compiling first...
    call compile.bat
)

echo Running Banking Management System...
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingApp
pause
