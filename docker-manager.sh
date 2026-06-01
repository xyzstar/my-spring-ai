#!/bin/bash

# Docker Compose Management Script for My Spring AI

set -e

echo "========================================="
echo "  My Spring AI - Docker Compose Manager"
echo "========================================="
echo ""

case "${1:-help}" in
  start)
    echo "Starting services..."
    docker-compose up -d
    echo ""
    echo "Services started! Waiting for application to be ready..."
    sleep 5
    echo "Check logs with: docker-compose logs -f app"
    echo "Access application at: http://localhost:8000"
    ;;
  
  stop)
    echo "Stopping services..."
    docker-compose down
    echo "Services stopped."
    ;;
  
  restart)
    echo "Restarting services..."
    docker-compose restart
    echo "Services restarted."
    ;;
  
  rebuild)
    echo "Rebuilding and restarting services..."
    docker-compose up -d --build
    echo "Services rebuilt and restarted."
    ;;
  
  logs)
    echo "Showing logs (Ctrl+C to exit)..."
    docker-compose logs -f ${2:-app}
    ;;
  
  status)
    echo "Service status:"
    docker-compose ps
    ;;
  
  clean)
    echo "Cleaning up containers, volumes, and images..."
    docker-compose down -v --rmi all
    echo "Cleanup complete."
    ;;
  
  help|*)
    echo "Usage: ./docker-manager.sh <command> [options]"
    echo ""
    echo "Commands:"
    echo "  start       - Start all services"
    echo "  stop        - Stop all services"
    echo "  restart     - Restart all services"
    echo "  rebuild     - Rebuild and restart services"
    echo "  logs [svc]  - View logs (default: app)"
    echo "  status      - Show service status"
    echo "  clean       - Remove all containers, volumes, and images"
    echo "  help        - Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./docker-manager.sh start"
    echo "  ./docker-manager.sh logs app"
    ;;
esac