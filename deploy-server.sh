#!/bin/bash

echo "========================================"
echo "Deploy My Spring AI with Docker Compose"
echo "========================================"
echo ""

# Configuration
PROJECT_DIR="/home/docker/my-spring-ai"
JAR_FILE="my-spring-ai-1.0.jar"

cd $PROJECT_DIR

echo "Step 1: Checking JAR file..."
if [ -f "target/$JAR_FILE" ]; then
    echo "✓ JAR file found: target/$JAR_FILE"
else
    echo "✗ ERROR: JAR file not found!"
    echo "Please upload the JAR file to target/ directory first."
    exit 1
fi

echo ""
echo "Step 2: Stopping old container..."
docker-compose down

echo ""
echo "Step 3: Starting new container..."
docker-compose up -d

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "✓ Deployment SUCCESSFUL!"
    echo "========================================"
    echo ""
    echo "Application is running at: http://localhost:8000"
    echo ""
    echo "Useful commands:"
    echo "  docker-compose logs -f     - View logs"
    echo "  docker-compose down        - Stop application"
    echo "  docker-compose restart     - Restart application"
    echo ""
    
    echo "Waiting for application to start..."
    sleep 5
    docker-compose logs --tail=20
else
    echo ""
    echo "========================================"
    echo "✗ Deployment FAILED!"
    echo "========================================"
    echo "Please check Docker Compose configuration."
    exit 1
fi
