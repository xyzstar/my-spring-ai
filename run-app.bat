@echo off
echo ========================================
echo Clean, Compile and Run Application
echo ========================================
echo.

cd /d D:\project\my\my-spring-ai\my-spring-ai

echo Step 1: Cleaning project...
call mvn clean

echo.
echo Step 2: Compiling project...
call mvn compile -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Starting application...
echo.
call mvn spring-boot:run

pause
