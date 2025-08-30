@echo off
echo ========================================
echo   📦 EXPORTAR BASE DE DATOS ACTUAL
echo ========================================
echo.

echo 🔍 Creando backup de la base de datos...
pg_dump -h localhost -U postgres -d zugarezDB -f backup_zugarez.sql

echo.
echo ✅ Backup creado: backup_zugarez.sql
echo 💡 Ahora puedes importar este archivo en Supabase

pause
