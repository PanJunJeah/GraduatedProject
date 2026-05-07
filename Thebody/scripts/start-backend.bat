@echo off
REM Spring Boot 后端启动脚本 (Windows CMD)

echo ========================================
echo   Transformer 翻译平台 - 后端启动
echo ========================================
echo.

REM 检查 Java
java -version

REM 构建项目（如果需要）
if not exist "target\GraduatedProject-0.0.1-SNAPSHOT.jar" (
    echo [INFO] JAR not found, building...
    call mvn clean package -DskipTests
)

echo.
echo [INFO] Starting Spring Boot...
echo.

java -jar -Xmx512m -Xms256m target\GraduatedProject-0.0.1-SNAPSHOT.jar
