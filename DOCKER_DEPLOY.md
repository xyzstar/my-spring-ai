# Docker Compose 部署指南

## 前置要求

1. **Docker 和 Docker Compose** 已安装
2. **PostgreSQL 数据库** 正在运行（本地或远程）
3. **Ollama 服务** 正在运行（用于本地 AI 模型）
4. **阿里百炼 API Key**（用于云端 AI 模型）

## 快速开始

### 1. 构建项目

```bash
# 使用 Maven 打包项目
mvn clean package -DskipTests
```

这将在 `target/` 目录下生成 `my-spring-ai-1.0-SNAPSHOT.jar` 文件。

### 2. 配置环境变量

复制 `.env.example` 为 `.env` 并根据实际情况修改：

```bash
cp .env.example .env
```

编辑 `.env` 文件，配置以下内容：

```env
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/iot_platform?currentSchema=public
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# AI 服务配置
SPRING_AI_DASHSCOPE_API_KEY=your_dashscope_api_key
SPRING_AI_OLLAMA_BASE_URL=http://host.docker.internal:11434
```

**重要提示：**
- `host.docker.internal` 是 Docker 访问宿主机服务的特殊域名
- Windows/Mac: Docker Desktop 自动支持 `host.docker.internal`
- Linux: 需要在 docker-compose.yml 中添加 `extra_hosts` 配置（已包含）

### 3. 启动服务

```bash
# 构建并启动容器
docker-compose up -d --build

# 查看日志
docker-compose logs -f app

# 检查服务状态
docker-compose ps
```

### 4. 验证部署

访问以下地址验证服务是否正常运行：

- **应用首页**: http://localhost:8000
- **健康检查**: http://localhost:8000/actuator/health

### 5. 停止服务

```bash
# 停止容器
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v
```

## 常见问题

### 1. 无法连接到数据库

**问题**: 容器无法连接到 PostgreSQL 数据库

**解决方案**:
- 确保 PostgreSQL 正在运行
- 检查 `.env` 中的数据库 URL、用户名和密码
- 如果使用本地数据库，确保使用 `host.docker.internal` 而不是 `localhost`
- 检查防火墙设置，确保端口可访问

### 2. 无法连接到 Ollama

**问题**: 容器无法连接到 Ollama 服务

**解决方案**:
- 确保 Ollama 正在运行：`ollama list`
- 确保已拉取所需的模型：`ollama pull qwen2.5-coder:7b-instruct-q4_K_M`
- 检查 `.env` 中的 `OLLAMA_BASE_URL` 配置
- 在 Linux 上，确保 docker-compose.yml 中包含 `extra_hosts` 配置

### 3. 超时错误

**问题**: AI 请求超时

**解决方案**:
- 已在 `application.yml` 中配置 10 分钟超时
- 对于首次加载大模型，可能需要更长时间
- 可以预热模型：`curl http://localhost:11434/api/generate -d '{"model":"qwen2.5-coder:7b-instruct-q4_K_M","prompt":"test"}'`

### 4. JAR 文件未找到

**问题**: Docker 构建时找不到 JAR 文件

**解决方案**:
- 确保已执行 `mvn clean package`
- 检查 `target/` 目录下是否存在 `my-spring-ai-1.0-SNAPSHOT.jar`
- 如果版本号不同，请更新 `Dockerfile` 中的文件名

## 生产环境部署建议

### 1. 使用外部配置文件

创建 `application-prod.yml` 并在 docker-compose.yml 中挂载：

```yaml
volumes:
  - ./application-prod.yml:/app/config/application-prod.yml
environment:
  SPRING_PROFILES_ACTIVE: prod
```

### 2. 资源限制

在 docker-compose.yml 中添加资源限制：

```yaml
deploy:
  resources:
    limits:
      memory: 2G
      cpus: '2.0'
```

### 3. 日志管理

配置日志轮转：

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

### 4. 网络安全

- 不要将 `.env` 文件提交到版本控制
- 使用 Docker secrets 管理敏感信息
- 配置网络策略限制访问

## 监控和维护

### 查看日志

```bash
# 实时查看日志
docker-compose logs -f app

# 查看最近 100 行日志
docker-compose logs --tail=100 app
```

### 进入容器

```bash
docker exec -it my-spring-ai sh
```

### 重启服务

```bash
docker-compose restart app
```

### 更新应用

```bash
# 重新构建并启动
mvn clean package -DskipTests
docker-compose up -d --build
```

## 技术栈

- **Java**: 21 (Eclipse Temurin)
- **Spring Boot**: 3.3.8
- **Spring AI**: 1.0.0-M6
- **Spring AI Alibaba**: 1.0.0-M6.1
- **PostgreSQL**: 外部数据库
- **Ollama**: 本地 AI 模型服务
