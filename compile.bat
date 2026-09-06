@echo off
echo Compiling Banking Management System...
javac -cp "lib\mysql-connector-j-9.4.0.jar" -d bin src\BankingManagementSystem\*.java
if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    pause
)
