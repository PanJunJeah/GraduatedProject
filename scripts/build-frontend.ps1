# 前端构建脚本
# Vue3 + Vite

$ErrorActionPreference = "Stop"

$FRONTEND_DIR = "d:\springbootwork\GraduatedProject\translate-platform"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Transformer 翻译平台 - 前端构建" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/3] 安装依赖..." -ForegroundColor Green
Push-Location $FRONTEND_DIR
npm install

Write-Host "[2/3] 开发环境构建..." -ForegroundColor Green
npm run build

Write-Host "[3/3] 构建完成!" -ForegroundColor Green
Write-Host "  输出目录: $FRONTEND_DIR\dist"
Write-Host ""

Write-Host "启动开发服务器:" -ForegroundColor Yellow
Write-Host "  npm run dev"
Write-Host ""

Pop-Location
