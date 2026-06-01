# Docker Compose Deployment Guide

## Prerequisites

- Docker Desktop installed
- Docker Compose v2+ installed

## Quick Start

1. **Copy environment file** (optional, if you want to customize API keys):
   ```bash
   cp .env.example .env
   ```

2. **Start all services**:
   ```bash
   docker-compose up -d
   ```

3. **View logs**:
   ```bash
   docker-compose logs -f app
   ```

4. **Access the application**:
   - Web UI: http://localhost:8000

## Services

- **app**: Spring AI Application (port 8000)
- **External PostgreSQL**: Configure via environment variables

## Configuration

### Environment Variables

Edit `.env` file or modify `docker-compose.yml`:

- `SPRING_DATASOURCE_URL`: External PostgreSQL connection URL (default: jdbc:postgresql://192.168.1.130:5432/my_ai?currentSchema=public)
- `SPRING_DATASOURCE_USERNAME`: Database username (default: postgres)
- `SPRING_DATASOURCE_PASSWORD`: Database password (default: iot.2024)
- `DASHSCOPE_API_KEY`: Your Alibaba DashScope API key
- `OLLAMA_BASE_URL`: Ollama service URL (default: http://host.docker.internal:11434)

## Common Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and restart
docker-compose up -d --build
```

## Troubleshooting

1. **Port conflicts**: Change port in `docker-compose.yml` if 8000 is in use
2. **Database connection**: Ensure external PostgreSQL is accessible from Docker container
3. **API Key issues**: Verify your DASHSCOPE_API_KEY is valid
4. **Network issues**: For remote databases, check firewall rules and network connectivity