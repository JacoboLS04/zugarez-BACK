@echo off
echo ========================================
echo   🚀 MIGRACIÓN COMPLETA A SUPABASE
echo ========================================
echo.

echo 📋 PASOS PARA MIGRAR A SUPABASE:
echo.
echo 1️⃣  Crear proyecto en https://supabase.com
echo 2️⃣  Exportar base de datos actual
echo 3️⃣  Importar en Supabase
echo 4️⃣  Actualizar configuración
echo 5️⃣  Probar conexión
echo.

set /p continuar="¿Continuar con la exportación? (s/n): "
if /i "%continuar%" neq "s" goto :end

echo.
echo 📦 Exportando base de datos actual...
echo ⚠️  Asegúrate de que PostgreSQL esté corriendo
echo.

pg_dump -h localhost -U postgres -d zugarez_db -f backup_zugarez.sql

if exist backup_zugarez.sql (
    echo ✅ Backup creado exitosamente: backup_zugarez.sql
    echo.
    echo 📋 PRÓXIMOS PASOS:
    echo.
    echo 1. Ve a tu proyecto Supabase
    echo 2. Database → SQL Editor
    echo 3. Copia y pega el contenido de backup_zugarez.sql
    echo 4. Ejecuta el script
    echo 5. Actualiza application-supabase.properties con tus credenciales
    echo 6. Ejecuta: mvn spring-boot:run -Dspring.profiles.active=supabase
    echo.
) else (
    echo ❌ Error al crear backup
    echo 💡 Verifica que PostgreSQL esté corriendo y las credenciales sean correctas
)

:end
pause
