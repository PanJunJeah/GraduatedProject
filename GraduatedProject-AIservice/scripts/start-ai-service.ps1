# AI 服务启动脚本
# Python FastAPI

param(
    [string]$Host = "0.0.0.0",
    [int]$Port = 8000
)

$ErrorActionPreference = "Stop"

$AI_SERVICE_DIR = "d:\pythonprogram\GranduatedProject"
$MODEL_PATH = "d:\pythonprogram\GranduatedProject\models\m2m100_418M"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Transformer 翻译平台 - AI 服务启动" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/4] 检查 Python 环境..." -ForegroundColor Green
python --version

Write-Host "[2/4] 检查模型文件..." -ForegroundColor Green
if (Test-Path $MODEL_PATH) {
    Write-Host "  模型路径: $MODEL_PATH" -ForegroundColor Green
} else {
    Write-Host "  [警告] 模型目录不存在: $MODEL_PATH" -ForegroundColor Yellow
}

Write-Host "[3/4] 检查依赖..." -ForegroundColor Green
Push-Location $AI_SERVICE_DIR
pip show fastapi | Select-String "Name:"
pip show transformers | Select-String "Name:"
Pop-Location

Write-Host "[4/4] 启动 FastAPI 服务..." -ForegroundColor Green
Write-Host "  地址: http://$Host`:$Port" -ForegroundColor Cyan
Write-Host "  文档: http://$Host`:$Port/docs" -ForegroundColor Cyan
Write-Host ""

Push-Location $AI_SERVICE_DIR
$env:MODEL_PATH = $MODEL_PATH
$env:HOST = $Host
$env:PORT = $Port

uvicorn src.translate_service:app --host $Host --port $Port --reload
