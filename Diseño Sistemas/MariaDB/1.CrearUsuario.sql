-- =========================================================
-- PORTAL ESCOLAR - MARIADB
-- Creación de Base de Datos y Usuario
-- =========================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS PORTAL_ESCOLAR
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE PORTAL_ESCOLAR;

-- Crear usuario (si no existe)
-- Nota: Cambia 'localhost' por '%' si necesitas acceso remoto
CREATE USER IF NOT EXISTS 'portal_escolar'@'localhost' 
IDENTIFIED BY 'portal123';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON PORTAL_ESCOLAR.* TO 'portal_escolar'@'localhost';

-- Aplicar cambios
FLUSH PRIVILEGES;

-- Verificar
SELECT user, host FROM mysql.user WHERE user = 'portal_escolar';
SHOW GRANTS FOR 'portal_escolar'@'localhost';
