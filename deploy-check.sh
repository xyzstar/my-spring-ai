#!/bin/bash

# Docker Compose 部署验证脚本

echo "========================================="
echo "Docker Compose 部署验证"
echo "========================================="
echo ""

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi
echo "✅ Docker 已安装: $(docker --version)"

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi
echo "✅ Docker Compose 已安装"

# 检查 JAR 文件是否存在
if [ ! -f "target/my-spring-ai-1.0-SNAPSHOT.jar" ]; then
    echo "⚠️  JAR 文件不存在，需要先构建项目"
    echo "   运行: mvn clean package -DskipTests"
    read -p "是否现在构建？(y/n): " build_choice
    if [ "$build_choice" = "y" ]; then
        mvn clean package -DskipTests
        if [ $? -ne 0 ]; then
            echo "❌ 构建失败"
            exit 1
        fi
    else
        exit 1
    fi
fi
echo "✅ JAR 文件存在"

# 检查 .env 文件
if [ ! -f ".env" ]; then
    echo "⚠️  .env 文件不存在，从 .env.example 复制"
    cp .env.example .env
    echo "   请编辑 .env 文件配置正确的参数"
    exit 1
fi
echo "✅ .env 文件存在"

# 检查 Docker 服务是否运行
if ! docker info &> /dev/null; then
    echo "❌ Docker 服务未运行"
    exit 1
fi
echo "✅ Docker 服务正在运行"

echo ""
echo "========================================="
echo "预检查完成，可以开始部署"
echo "========================================="
echo ""
echo "部署命令:"
echo "  docker-compose up -d --build"
echo ""
echo "查看日志:"
echo "  docker-compose logs -f app"
echo ""
echo "停止服务:"
echo "  docker-compose down"
echo ""
