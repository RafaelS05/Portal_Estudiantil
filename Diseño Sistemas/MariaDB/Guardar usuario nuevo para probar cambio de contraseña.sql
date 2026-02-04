-- =========================================================
-- PORTAL ESCOLAR - USUARIO ESTUDIANTE
-- Crea un usuario estudiante con credenciales
-- =========================================================

USE PORTAL_ESCOLAR;

-- Variables para IDs
SET @id_usuario = NULL;
SET @id_correo = NULL;
SET @id_credencial = NULL;

-- Password hash para: Estudiante123!
-- Generado con BCrypt (fuerza 10)
SET @password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMye1JRvWmZQVpEQ8YQHG0J.HQZIqKWXjXC';

-- =======================
-- 1. INSERTAR USUARIO
-- =======================
CALL USUARIOS_INSERTAR(
    'Alex Leandro',        -- nombre
    'Coto',                -- primer_apellido
    'Quesada',             -- segundo_apellido
    3,                     -- id_tipousuario_fk (ESTUDIANTE)
    1                      -- id_estado_fk (ACTIVO)
);
SET @id_usuario = LAST_INSERT_ID();

-- =======================
-- 2. INSERTAR TELÉFONO
-- =======================
CALL TELEFONO_INSERTAR(
    '8888-9999',           -- numero
    @id_usuario,           -- id_usuario_fk
    1                      -- id_estado_fk (ACTIVO)
);

-- =======================
-- 3. INSERTAR CORREO (ES_LOGIN = 'S')
-- =======================
CALL CORREO_INSERTAR(
    'alexleandro846@gmail.com', -- correo  cambiar por el de ustedes para validar el cambio de contraseña
    'S',                        -- es_login
    @id_usuario,                -- id_usuario_fk
    1                           -- id_estado_fk (ACTIVO)
);
SET @id_correo = LAST_INSERT_ID();

-- =======================
-- 4. INSERTAR CREDENCIALES
-- =======================
CALL CREDENCIALES_INSERTAR(
    @password_hash,        -- password_hash
    NULL,                  -- ultimo_login
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
    '✓ Usuario estudiante creado exitosamente' AS resultado,
    'alexleandro846@gmail.com' AS email,
    'Estudiante123!' AS password_temporal,
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
║  USUARIO ESTUDIANTE CREADO                                 ║
╠════════════════════════════════════════════════════════════╣
║  Email:    alexleandro846@gmail.com                        ║
║  Password: Estudiante123!                                 ║
╠════════════════════════════════════════════════════════════╣
║  ⚠️  IMPORTANTE:                                           ║
║  1. Cambiar el password en el primer login                 ║
║  2. Usuario con rol ESTUDIANTE                             ║
╚════════════════════════════════════════════════════════════╝
' AS INSTRUCCIONES;
