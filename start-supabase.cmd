@echo off
echo ================================
echo 🚀 INICIANDO ZUGAREZ CON SUPABASE
echo ================================
echo.

echo 📝 Configuración:
echo - Perfil: supabase
echo - Puerto: 8082
echo - Base de datos: Supabase Cloud
echo.

echo 🔄 Iniciando aplicación...
mvn spring-boot:run -Dspring.profiles.active=supabase -Dserver.port=8082

pause
