-- Script para agregar columnas de código de login a la tabla users
-- Ejecutar este script en PostgreSQL

ALTER TABLE users ADD COLUMN IF NOT EXISTS login_code VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS login_code_expiry TIMESTAMP;

-- Opcional: Ver la estructura actualizada de la tabla
-- \d users
