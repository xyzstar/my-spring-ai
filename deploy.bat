@echo off
echo ========================================
echo Build and Deploy with Docker Compose
echo ========================================
echo.

cd /d D:\project\my\my-spring-ai\my-spring-ai

echo Step 1: Building JAR file...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Checking JAR file...
if exist target\my-spring-ai-1.0-SNAPSHOT.jar (
    echo JAR file found: target\my-spring-ai-1.0-SNAPSHOT.jar
) else (
    echo ERROR: JAR file not found!
    pause
    exit /b 1
)

echo.
echo Step 3: Starting Docker Compose...
docker-compose up -d

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Deployment SUCCESSFUL!
    echo ========================================
    echo.
    echo Application is running at: http://localhost:8000
    echo.
    echo Useful commands:
    echo   docker-compose logs -f     - View logs
    echo   docker-compose down        - Stop application
    echo   docker-compose restart     - Restart application
) else (
    echo.
    echo ========================================
    echo Deployment FAILED!
    echo ========================================
    echo Please check Docker Compose configuration.
)

echo.
pause
