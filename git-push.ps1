# Git Push Script for My Spring AI Project

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Git Push to GitHub" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$projectPath = "D:\project\my\my-spring-ai\my-spring-ai"

Set-Location $projectPath

Write-Host "Step 1: Checking Git status..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "Step 2: Adding all changes..." -ForegroundColor Yellow
git add .

Write-Host ""
Write-Host "Step 3: Checking what will be committed..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "Step 4: Committing changes..." -ForegroundColor Yellow
$commitMessage = "Update: Docker Compose deployment configuration and security improvements

- Add Docker Compose deployment with multi-stage build
- Remove PostgreSQL service, use external database
- Add health checks and environment variable support
- Replace real API keys with placeholders for security
- Add management scripts (PowerShell and Bash)
- Update documentation for Docker deployment
- Configure .gitignore for Docker files"

git commit -m $commitMessage

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Step 5: Pushing to GitHub..." -ForegroundColor Yellow
    git push origin main
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Successfully pushed to GitHub!" -ForegroundColor Green
        Write-Host "Repository: https://github.com/xyzstar/my-spring-ai.git" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "❌ Push failed. Please check the error message above." -ForegroundColor Red
        Write-Host ""
        Write-Host "Common issues:" -ForegroundColor Yellow
        Write-Host "1. Check your internet connection" -ForegroundColor White
        Write-Host "2. Verify GitHub credentials" -ForegroundColor White
        Write-Host "3. Check if remote repository exists" -ForegroundColor White
    }
} else {
    Write-Host ""
    Write-Host "⚠️  No changes to commit or commit failed." -ForegroundColor Yellow
    Write-Host "If there are no changes, this is normal." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
