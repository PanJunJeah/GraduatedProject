# Transformer 交互式翻译平台 - 部署指南

## 目录

1. [环境要求](#环境要求)
2. [快速部署 (Docker)](#快速部署-docker)
3. [手动部署](#手动部署)
4. [云服务部署](#云服务部署)
5. [验证部署](#验证部署)
6. [常见问题](#常见问题)

---

## 环境要求

### 最低配置

| 组件 | CPU | 内存 | 存储 |
|------|-----|------|------|
| 后端服务 | 2核 | 4GB | 10GB |
| AI 服务 | 4核 | 8GB | 20GB |
| PostgreSQL | 1核 | 2GB | 10GB |
| Redis | 1核 | 1GB | 5GB |
| Qdrant | 2核 | 4GB | 10GB |

### 推荐配置 (生产环境)

| 组件 | CPU | 内存 | 存储 |
|------|-----|------|------|
| 后端服务 | 4核 | 8GB | 20GB |
| AI 服务 | 8核+GPU | 16GB+ | 50GB |
| PostgreSQL | 2核 | 4GB | 50GB |
| Redis | 2核 | 4GB | 10GB |
| Qdrant | 4核 | 8GB | 50GB |

### 软件依赖

- Java 17+
- Node.js 18+
- Python 3.10+
- PostgreSQL 15+
- Redis 7+
- Docker & Docker Compose (可选)
- Nginx (可选，用于反向代理)

---

## 快速部署 (Docker)

### 步骤 1: 准备环境

```bash
# 安装 Docker
# Windows: https://docs.docker.com/desktop/install/windows-install/
# macOS: https://docs.docker.com/desktop/install/mac-install/
# Linux: https://docs.docker.com/engine/install/

# 验证安装
docker --version
docker-compose --version
```

### 步骤 2: 下载项目

```bash
git clone <repository-url>
cd GraduatedProject
```

### 步骤 3: 配置环境变量

```bash
cp deploy/.env.docker .env
# 编辑 .env 文件，修改 JWT_SECRET 等敏感配置
```

### 步骤 4: 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 步骤 5: 访问应用

- 前端界面: http://localhost
- 后端 API: http://localhost:8080
- AI 服务: http://localhost:8000
- API 文档: http://localhost:8000/docs

---

## 手动部署

### 方式 A: 本地开发环境

#### 1. 数据库准备

```bash
# 安装 PostgreSQL
# https://www.postgresql.org/download/

# 创建数据库
psql -U postgres -c "CREATE DATABASE translatedb;"

# 执行初始化脚本
psql -U postgres -d translatedb -f scripts/init-db.sql
```

#### 2. Redis 准备

```bash
# 安装 Redis
# https://redis.io/download/

# 启动 Redis
redis-server
```

#### 3. Qdrant 准备

```bash
# 安装 Qdrant
# https://github.com/qdrant/qdrant/releases

# 启动 Qdrant
qdrant
```

#### 4. 启动 AI 服务

```bash
cd d:\pythonprogram\GranduatedProject

# 安装 Python 依赖
pip install -r requirements.txt

# 启动服务
python src/translate_service.py
```

#### 5. 启动后端服务

```bash
cd d:\springbootwork\GraduatedProject

# 构建项目
mvn clean package -DskipTests

# 启动服务
java -jar target/GraduatedProject-0.0.1-SNAPSHOT.jar
```

#### 6. 启动前端开发服务器

```bash
cd d:\springbootwork\GraduatedProject\translate-platform

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 方式 B: 生产环境 (Nginx)

#### 1. 安装 Nginx

```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

#### 2. 配置 Nginx

```bash
# 复制配置文件
sudo cp deploy/nginx/translation.conf /etc/nginx/sites-available/translation
sudo ln -s /etc/nginx/sites-available/translation /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重载 Nginx
sudo systemctl reload nginx
```

#### 3. 配置 SSL (可选但推荐)

```bash
# 使用 Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d translation.yourdomain.com
```

---

## 云服务部署

### Railway 部署 (推荐)

1. 创建 Railway 账号: https://railway.app
2. 连接 GitHub 仓库
3. 添加环境变量
4. 部署服务

**后端配置:**
```
PORT=8080
DB_HOST=<Supabase PostgreSQL Host>
DB_PORT=5432
DB_NAME=translatedb
DB_USER=postgres
DB_PASSWORD=<Your Password>
JWT_SECRET=<Generate Random String>
AI_SERVICE_URL=<Your AI Service URL>
```

### Supabase (数据库)

1. 创建 Supabase 项目
2. 获取连接信息
3. 配置环境变量

### Vercel (前端)

```bash
cd translate-platform
vercel --prod
```

### Qdrant Cloud

1. 注册 Qdrant Cloud: https://cloud.qdrant.io
2. 创建 Cluster
3. 获取 API Key 和 URL
4. 配置环境变量

---

## 验证部署

### 健康检查

```bash
# 后端健康检查
curl http://localhost:8080/api/public/health

# AI 服务健康检查
curl http://localhost:8000/health
```

### 功能测试

1. 访问 http://localhost
2. 注册新用户
3. 登录
4. 进行翻译测试
5. 查看翻译历史
6. 测试批量翻译

### 性能测试

```bash
# 翻译接口响应时间
time curl -X POST http://localhost:8080/api/translation \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"sourceText":"Hello","sourceLang":"en","targetLang":"zh"}'
```

---

## 常见问题

### Q1: Docker 部署内存不足

```yaml
# docker-compose.yml 中增加内存限制
services:
  ai-service:
    deploy:
      resources:
        limits:
          memory: 8G
```

### Q2: AI 服务启动失败

```bash
# 检查模型文件
ls -la d:/pythonprogram/GranduatedProject/models/m2m100_418M/

# 重新下载模型
python -c "from transformers import M2M100Tokenizer; M2M100Tokenizer.from_pretrained('facebook/m2m100_418M')"
```

### Q3: 数据库连接失败

```bash
# 检查 PostgreSQL 服务状态
pg_isready -h localhost -p 5432

# 测试连接
psql -h localhost -U postgres -d translatedb
```

### Q4: 前端无法访问后端 API

检查 Nginx 配置中的 proxy_pass 是否正确指向后端地址。

### Q5: JWT Token 失效

确保 `JWT_SECRET` 在所有环境中都是相同的值，且足够长 (建议 64+ 字符)。

---

## 维护

### 备份数据库

```bash
pg_dump -U postgres translatedb > backup_$(date +%Y%m%d).sql
```

### 更新服务

```bash
# Docker 部署
docker-compose pull
docker-compose up -d

# 手动部署
git pull
mvn clean package -DskipTests
# 重启服务
```

### 监控

建议使用以下工具监控服务:
- Prometheus + Grafana (指标监控)
- ELK Stack (日志分析)
- Uptime Robot (在线监控)

---

## 联系支持

如有问题，请提交 Issue 或联系开发者。
