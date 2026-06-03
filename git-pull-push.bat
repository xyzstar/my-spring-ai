@echo off
echo =========================================
echo   Git Pull and Push to GitHub
echo =========================================
echo.

cd /d D:\project\my\my-spring-ai\my-spring-ai

echo Step 1: Fetching remote changes...
git fetch origin

echo.
echo Step 2: Checking remote status...
git status

echo.
echo Step 3: Pulling remote changes (merging)...
git pull origin main --rebase

if %errorlevel% equ 0 (
    echo.
    echo Step 4: Adding all local changes...
    git add .
    
    echo.
    echo Step 5: Checking if there are changes to commit...
    git status
    
    echo.
    REM Check if there are staged changes
    git diff --cached --quiet
    if %errorlevel% neq 0 (
        echo Changes detected. Committing...
        git commit -m "Update: Docker Compose deployment and security improvements
        
- Add Docker Compose deployment with multi-stage build
- Remove PostgreSQL service, use external database
- Add health checks and environment variable support
- Replace real API keys with placeholders for security
- Add management scripts and documentation
- Configure .gitignore for Docker files"
        
        if %errorlevel% equ 0 (
            echo.
            echo Step 6: Pushing to GitHub...
            git push origin main
            
            if %errorlevel% equ 0 (
                echo.
                echo ========================================
                echo Successfully pushed to GitHub!
                echo Repository: https://github.com/xyzstar/my-spring-ai.git
                echo ========================================
            ) else (
                echo.
                echo Push failed. Please check the error above.
            )
        )
    ) else (
        echo No local changes to commit after merge.
        echo.
        echo Step 6: Pushing merged changes to GitHub...
        git push origin main
        
        if %errorlevel% equ 0 (
            echo.
            echo ========================================
            echo Successfully pushed to GitHub!
            echo Repository: https://github.com/xyzstar/my-spring-ai.git
            echo ========================================
        ) else (
            echo.
            echo Push failed. Please check the error above.
        )
    )
) else (
    echo.
    echo ========================================
    echo Pull failed! There might be conflicts.
    echo ========================================
    echo.
    echo Please resolve conflicts manually:
    echo 1. Check conflicting files with: git status
    echo 2. Edit files to resolve conflicts
    echo 3. Stage resolved files: git add .
    echo 4. Continue rebase: git rebase --continue
    echo 5. Then push: git push origin main
    echo.
)

echo.
pause
