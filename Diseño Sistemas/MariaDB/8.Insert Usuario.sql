-- =========================================================
-- PORTAL ESCOLAR - USUARIO ADMINISTRADOR
-- Crea el primer usuario del sistema con credenciales
-- =========================================================

USE PORTAL_ESCOLAR;

-- Variables para IDs
SET @id_usuario = NULL;
SET @id_correo = NULL;
SET @id_credencial = NULL;

-- Password hash para: Admin123!
-- Generado con BCrypt (fuerza 10)
SET @password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMye1JRvWmZQVpEQ8YQHG0J.HQZIqKWXjXC';

-- =======================
-- 1. INSERTAR USUARIO
-- =======================
CALL USUARIOS_INSERTAR(
    'Admin',                -- nombre
    'Sistema',             -- primer_apellido
    'Portal',              -- segundo_apellido
    1,                     -- id_tipousuario_fk (Administrador)
    1                      -- id_estado_fk (ACTIVO)
);
SET @id_usuario = LAST_INSERT_ID();

-- =======================
-- 2. INSERTAR TELÉFONO
-- =======================
CALL TELEFONO_INSERTAR(
    '8888-8888',           -- numero
    @id_usuario,           -- id_usuario_fk
    1                      -- id_estado_fk (ACTIVO)
);

-- =======================
-- 3. INSERTAR CORREO (ES_LOGIN = 'S')
-- =======================
CALL CORREO_INSERTAR(
    'admin@escuela.com',   -- correo (CAMBIAR POR TU EMAIL)
    'S',                   -- es_login ('S' = se usa para login)
    @id_usuario,           -- id_usuario_fk
    1                      -- id_estado_fk (ACTIVO)
);
SET @id_correo = LAST_INSERT_ID();

-- =======================
-- 4. INSERTAR CREDENCIALES
-- =======================
CALL CREDENCIALES_INSERTAR(
    @password_hash,        -- password_hash (Admin123!)
    NULL,                  -- ultimo_login (aún no ha hecho login)
    0,                     -- intentos_fallidos
    NULL,                  -- bloqueado_hasta
    @id_usuario,           -- id_usuario_fk
    @id_correo,            -- id_correo_fk
    1                      -- id_estado_fk (ACTIVO)
);
SET @id_credencial = LAST_INSERT_ID();

-- =======================
-- VERIFICACIÓN Y RESUMEN
-- =======================
SELECT 
    '✓ Usuario administrador creado exitosamente' AS resultado,
    'admin@escuela.com' AS email,
    'Admin123!' AS password_temporal,
    @id_usuario AS id_usuario,
    @id_correo AS id_correo,
    @id_credencial AS id_credencial;

SELECT 
    u.ID_USUARIO,
    u.NOMBRE,
    u.PRIMER_APELLIDO,
    u.SEGUNDO_APELLIDO,
    tu.NOMBRE AS tipo_usuario,
    c.CORREO,
    c.ES_LOGIN,
    cr.ID_CREDENCIAL,
    e.DESCRIPCION AS estado
FROM USUARIOS_TB u
JOIN TIPOUSUARIO_TB tu ON u.ID_TIPOUSUARIO_FK = tu.ID_TIPOUSUARIO
JOIN ESTADOS_TB e ON u.ID_ESTADO_FK = e.ID_ESTADO
JOIN CORREO_TB c ON c.ID_USUARIO_FK = u.ID_USUARIO
JOIN CREDENCIALES_TB cr ON cr.ID_USUARIO_FK = u.ID_USUARIO
WHERE u.ID_USUARIO = @id_usuario;

-- =======================
-- INSTRUCCIONES
-- =======================
SELECT '
╔════════════════════════════════════════════════════════════╗
║  USUARIO ADMINISTRADOR CREADO                              ║
╠════════════════════════════════════════════════════════════╣
║  Email:    admin@escuela.com                               ║
║  Password: Admin123!                                       ║
╠════════════════════════════════════════════════════════════╣
║  ⚠️  IMPORTANTE:                                           ║
║  1. Cambia el password después del primer login            ║
║  2. Actualiza el email si es necesario                     ║
║  3. Este usuario tiene permisos de Administrador           ║
╚════════════════════════════════════════════════════════════╝
' AS INSTRUCCIONES;
