@echo off
echo =========================================
echo   Git Push to GitHub
echo =========================================
echo.

cd /d D:\project\my\my-spring-ai\my-spring-ai

echo Step 1: Checking Git status...
git status

echo.
echo Step 2: Adding all changes...
git add .

echo.
echo Step 3: Committing changes...
git commit -m "Update: Docker Compose deployment and security improvements

- Add Docker Compose deployment with multi-stage build
- Remove PostgreSQL service, use external database
- Add health checks and environment variable support
- Replace real API keys with placeholders for security
- Add management scripts and documentation
- Configure .gitignore for Docker files"

if %errorlevel% equ 0 (
    echo.
    echo Step 4: Pushing to GitHub...
    git push origin main
    
    if %errorlevel% equ 0 (
        echo.
        echo Successfully pushed to GitHub!
        echo Repository: https://github.com/xyzstar/my-spring-ai.git
    ) else (
        echo.
        echo Push failed. Please check the error message above.
    )
) else (
    echo.
    echo No changes to commit or commit failed.
)

echo.
pause
