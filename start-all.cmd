@echo off
echo ========================================
echo   🚀 ZUGAREZ BACKEND - INICIO COMPLETO
echo ========================================
echo.

echo 📋 Iniciando stack de monitoreo...
docker-compose -f docker-compose.monitoring.yml up -d

echo.
echo ⏳ Esperando que los servicios estén listos...
timeout /t 10 /nobreak >nul

echo.
echo ✅ Verificando servicios de monitoreo:
echo 🔍 Prometheus: http://localhost:9090
echo 📊 Grafana: http://localhost:3001 (admin/admin123)
echo 📝 Loki: http://localhost:3100

echo.
echo 🎯 Iniciando aplicación Spring Boot...
echo ⚠️  La aplicación se iniciará en el puerto 8080
echo.

mvn spring-boot:run

REM Este archivo ya no es necesario - eliminar
