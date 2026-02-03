-- =========================================================
-- PORTAL ESCOLAR - TIPOS DE USUARIO
-- 4 tipos de usuario del sistema
-- =========================================================

USE PORTAL_ESCOLAR;


CALL TIPOUSUARIO_INSERTAR('Administrador', 1);
CALL TIPOUSUARIO_INSERTAR('Profesor', 1);
CALL TIPOUSUARIO_INSERTAR('Estudiante', 1);
CALL TIPOUSUARIO_INSERTAR('Encargado', 1);

-- =========================================================
-- VERIFICACIÓN
-- =========================================================
SELECT * FROM TIPOUSUARIO_TB ORDER BY ID_TIPOUSUARIO;

SELECT COUNT(*) AS total_tipos FROM tipousuario_tb;
