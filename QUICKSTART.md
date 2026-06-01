# Quick Start Guide - Docker Compose Deployment

## Prerequisites

Before you begin, ensure you have the following installed:
- **Docker Desktop** (includes Docker Compose)
  - Download from: https://www.docker.com/products/docker-desktop/
- **Git** (optional, for cloning)

## Step-by-Step Deployment

### 1. Navigate to Project Directory

```bash
cd D:\project\my\my-spring-ai\my-spring-ai
```

### 2. (Optional) Configure Environment Variables

If you want to customize API keys or other settings:

```powershell
# Copy example environment file
copy .env.example .env

# Edit .env file with your preferred editor
notepad .env
```

**Environment Variables:**
- `DASHSCOPE_API_KEY`: Your Alibaba DashScope API key
- `OLLAMA_BASE_URL`: URL for Ollama service (default uses host.docker.internal)

### 3. Start Services

#### Option A: Using PowerShell Script (Recommended for Windows)

```powershell
.\docker-manager.ps1 -Command start
```

#### Option B: Using Docker Compose Directly

```bash
docker-compose up -d
```

### 4. Verify Deployment

Check if services are running:

```powershell
.\docker-manager.ps1 -Command status
```

Or view logs:

```powershell
.\docker-manager.ps1 -Command logs
```

### 5. Access the Application

- **Web Interface**: http://localhost:8000
- **External PostgreSQL Database**: Configure via environment variables
  - Default: 192.168.1.130:5432
  - Database: my_ai
  - Username: postgres
  - Password: iot.2024

## Common Operations

### View Application Logs

```powershell
.\docker-manager.ps1 -Command logs -Service app
```

### Restart Services

```powershell
.\docker-manager.ps1 -Command restart
```

### Rebuild After Code Changes

```powershell
.\docker-manager.ps1 -Command rebuild
```

### Stop All Services

```powershell
.\docker-manager.ps1 -Command stop
```

### Connect to External Database

Configure your external database connection in `.env` file or docker-compose.yml:

```powershell
# Edit .env file
notepad .env
```

Set these variables:
- `SPRING_DATASOURCE_URL`: Your PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

### Clean Everything (Remove containers, volumes, images)

```powershell
.\docker-manager.ps1 -Command clean
```

## Troubleshooting

### Issue: Port 8000 already in use

**Solution**: Edit `docker-compose.yml` and change the port mapping:

```yaml
ports:
  - "8001:8000"  # Change 8000 to 8001 or another available port
```

### Issue: Application cannot connect to database

**Solution**: 
1. Verify your external PostgreSQL database is running and accessible
2. Check database connection URL in `.env` or docker-compose.yml
3. Ensure network connectivity between Docker container and database server
4. For remote databases, check firewall rules allow connections

### Issue: API Key errors

**Solution**: 
1. Verify your DASHSCOPE_API_KEY is valid
2. Update `.env` file with correct API key
3. Rebuild: `docker-compose up -d --build`

### Issue: Ollama connection issues

**Solution**:
1. Ensure Ollama is running on your host machine
2. For Windows/Mac, `host.docker.internal` should work
3. For Linux, you may need to add `--network=host` or configure networking

## Architecture Overview

```
┌─────────────────────────────────────┐
│         Docker Compose Network      │
│                                     │
│  ┌──────────────┐                   │
│  │              │                   │
│  │  Spring AI   │                   │
│  │  Application │                   │
│  │   (Port 8000)│                   │
│  │              │                   │
│  └──────┬───────┘                   │
│         │                           │
└─────────┼───────────────────────────┘
          │
          ▼
   http://localhost:8000
          
          │
          ▼ (External Connection)
   PostgreSQL Database
   (192.168.1.130:5432 or custom)
```

## Next Steps

1. **Configure Database**: Update external database connection in `.env` file
2. **Configure AI Models**: Update API keys in `.env` file
3. **Customize Configuration**: Modify `application.yml` as needed
4. **Scale Services**: Adjust resources in `docker-compose.yml`

## Support

For more detailed information, see:
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Complete deployment guide
- [README.md](README.md) - Project overview