#!/bin/bash
# Docker Compose 启动脚本 (Linux/macOS)

echo "========================================"
echo "  Transformer 翻译平台 - Docker 部署"
echo "========================================"
echo ""

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "[错误] Docker 未安装"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "[错误] Docker Compose 未安装"
    exit 1
fi

echo "[1/5] 检查 Docker 版本..."
docker --version
docker-compose --version

echo ""
echo "[2/5] 创建网络..."
docker network create translation-network 2>/dev/null || true

echo "[3/5] 拉取基础镜像..."
docker pull postgres:15-alpine
docker pull redis:7-alpine
docker pull qdrant/qdrant:latest

echo ""
echo "[4/5] 构建自定义镜像..."
docker-compose build

echo ""
echo "[5/5] 启动所有服务..."
docker-compose up -d

echo ""
echo "========================================"
echo "  服务已启动!"
echo "========================================"
echo ""
echo "前端:     http://localhost"
echo "后端API:  http://localhost:8080"
echo "AI服务:   http://localhost:8000"
echo "Qdrant:   http://localhost:6333"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
