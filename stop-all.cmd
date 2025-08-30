@echo off
echo ========================================
echo   🛑 ZUGAREZ BACKEND - PARADA COMPLETA
echo ========================================
echo.

echo 📋 Deteniendo stack de monitoreo...
docker-compose -f docker-compose.monitoring.yml down

echo.
echo ✅ Todos los servicios han sido detenidos.
echo 💡 Para detener Spring Boot usa Ctrl+C en la terminal donde se ejecuta.

pause
