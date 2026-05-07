# Spring Boot 后端启动脚本
# Windows PowerShell 版本

param(
    [string]$Profile = "dev",
    [string]$JvmMem = "512m"
)

$ErrorActionPreference = "Stop"

$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot
$JAR_FILE = "$PROJECT_ROOT\target\GraduatedProject-0.0.1-SNAPSHOT.jar"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Transformer 翻译平台 - 后端启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path $JAR_FILE)) {
    Write-Host "[警告] JAR文件不存在，正在构建项目..." -ForegroundColor Yellow
    Push-Location $PROJECT_ROOT
    mvn clean package -DskipTests
    Pop-Location
}

Write-Host "[1/3] 检查 Java 环境..." -ForegroundColor Green
java -version

Write-Host "[2/3] 检查数据库连接..." -ForegroundColor Green
$DB_HOST = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$DB_PORT = if ($env:DB_PORT) { $env:DB_PORT } else { "5432" }
Write-Host "  数据库: $DB_HOST`:$DB_PORT/translatedb"

Write-Host "[3/3] 启动 Spring Boot 应用..." -ForegroundColor Green
Write-Host ""

$env:SPRING_PROFILES_ACTIVE = $Profile

java -jar -Xmx$JvmMem -Xms256m `
    -Dserver.port=8080 `
    -Dspring.profiles.active=$Profile `
    $JAR_FILE
