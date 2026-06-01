# Docker Compose Management Script for My Spring AI (PowerShell)

param(
    [string]$Command = "help",
    [string]$Service = "app"
)

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  My Spring AI - Docker Compose Manager" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

switch ($Command) {
    "start" {
        Write-Host "Starting services..." -ForegroundColor Green
        docker-compose up -d
        Write-Host ""
        Write-Host "Services started! Waiting for application to be ready..." -ForegroundColor Green
        Start-Sleep -Seconds 5
        Write-Host "Check logs with: docker-compose logs -f app" -ForegroundColor Yellow
        Write-Host "Access application at: http://localhost:8000" -ForegroundColor Yellow
    }
    
    "stop" {
        Write-Host "Stopping services..." -ForegroundColor Green
        docker-compose down
        Write-Host "Services stopped." -ForegroundColor Green
    }
    
    "restart" {
        Write-Host "Restarting services..." -ForegroundColor Green
        docker-compose restart
        Write-Host "Services restarted." -ForegroundColor Green
    }
    
    "rebuild" {
        Write-Host "Rebuilding and restarting services..." -ForegroundColor Green
        docker-compose up -d --build
        Write-Host "Services rebuilt and restarted." -ForegroundColor Green
    }
    
    "logs" {
        Write-Host "Showing logs (Ctrl+C to exit)..." -ForegroundColor Green
        docker-compose logs -f $Service
    }
    
    "status" {
        Write-Host "Service status:" -ForegroundColor Green
        docker-compose ps
    }
    
    "clean" {
        Write-Host "Cleaning up containers, volumes, and images..." -ForegroundColor Green
        docker-compose down -v --rmi all
        Write-Host "Cleanup complete." -ForegroundColor Green
    }
    
    "help" {
        Write-Host "Usage: .\docker-manager.ps1 [-Command <command>] [-Service <service>]" -ForegroundColor White
        Write-Host ""
        Write-Host "Commands:" -ForegroundColor Cyan
        Write-Host "  start       - Start all services" -ForegroundColor White
        Write-Host "  stop        - Stop all services" -ForegroundColor White
        Write-Host "  restart     - Restart all services" -ForegroundColor White
        Write-Host "  rebuild     - Rebuild and restart services" -ForegroundColor White
        Write-Host "  logs        - View logs (use -Service to specify service)" -ForegroundColor White
        Write-Host "  status      - Show service status" -ForegroundColor White
        Write-Host "  clean       - Remove all containers, volumes, and images" -ForegroundColor White
        Write-Host "  help        - Show this help message" -ForegroundColor White
        Write-Host ""
        Write-Host "Examples:" -ForegroundColor Cyan
        Write-Host "  .\docker-manager.ps1 -Command start" -ForegroundColor White
        Write-Host "  .\docker-manager.ps1 -Command logs -Service app" -ForegroundColor White
    }
    
    default {
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Write-Host "Use 'help' to see available commands." -ForegroundColor Yellow
    }
}