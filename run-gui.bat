@echo off
echo Banking Management System - GUI Version
echo.

if not exist "bin\BankingManagementSystem\BankingGUI.class" (
    echo GUI classes not found. Compiling first...
    javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java
    if %errorlevel% neq 0 (
        echo Compilation failed!
        pause
        exit /b
    )
    echo Compilation successful!
) else (
    echo Classes found. Recompiling for updates...
    javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java
    if %errorlevel% equ 0 (
        echo Compilation successful!
    )
)

echo.
echo Launching GUI Banking System...
java -cp "bin;lib\mysql-connector-j-9.4.0.jar" BankingManagementSystem.BankingGUI
pause
