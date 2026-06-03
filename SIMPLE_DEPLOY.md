# 简化版 Docker Compose 部署指南

## 部署架构

```
本地开发机                    服务器
┌─────────────┐              ┌──────────────────┐
│             │              │                  │
│  Maven      │              │  Docker Compose  │
│  Build JAR  │──上传JAR──▶  │  + JRE Image     │
│             │              │  + Mount JAR     │
└─────────────┘              └──────────────────┘
```

## 本地部署（Windows）

### 一键部署

双击运行 `deploy.bat`，它会自动：
1. 使用 Maven 打包 JAR
2. 启动 Docker Compose

### 手动部署

```powershell
# 1. 打包 JAR
mvn clean package -DskipTests

# 2. 启动 Docker Compose
docker-compose up -d

# 3. 查看日志
docker-compose logs -f
```

## 服务器部署（Linux）

### 步骤 1: 本地打包

在本地 Windows 机器上：
```powershell
mvn clean package -DskipTests
```

### 步骤 2: 上传 JAR 到服务器

使用 SCP 或其他工具上传 JAR 文件：
```bash
scp target/my-spring-ai-1.0-SNAPSHOT.jar root@your-server:/home/docker/my-spring-ai/target/
```

### 步骤 3: 在服务器上部署

```bash
# SSH 登录服务器
ssh root@your-server

# 进入项目目录
cd /home/docker/my-spring-ai

# 运行部署脚本
chmod +x deploy-server.sh
./deploy-server.sh
```

或者手动执行：
```bash
docker-compose down
docker-compose up -d
docker-compose logs -f
```

## 配置文件

### .env（可选）

创建 `.env` 文件自定义配置：
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://144.24.12.178:35432/iot_platform?currentSchema=public
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=pg112233
DASHSCOPE_API_KEY=sk-cbc5df1475b642b98d007c98dbf95855
OLLAMA_BASE_URL=http://144.24.12.178:11434
```

## 常用命令

```bash
# 查看应用状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 重启应用
docker-compose restart

# 停止应用
docker-compose down

# 更新部署（上传新 JAR 后）
docker-compose restart
```

## 优势

✅ **简单快速** - 无需在 Docker 中编译  
✅ **节省资源** - 服务器不需要 Maven 和 JDK  
✅ **易于更新** - 只需替换 JAR 文件并重启  
✅ **调试方便** - 本地打包，问题易排查  

## 注意事项

1. 确保 `target/my-spring-ai-1.0-SNAPSHOT.jar` 文件存在
2. 首次部署需要下载 JRE 镜像（约 200MB）
3. 修改代码后需要重新打包并重启容器
4. JAR 文件以只读方式挂载，容器内无法修改
