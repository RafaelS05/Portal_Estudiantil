-- =========================================================
-- PORTAL ESCOLAR - INSERTAR ESTADOS
-- Total: 16 estados para el sistema
-- =========================================================

USE PORTAL_ESCOLAR;

-- Estados base (2)
CALL ESTADOS_INSERTAR('ACTIVO');
CALL ESTADOS_INSERTAR('INACTIVO');

-- Estados académicos (4)
CALL ESTADOS_INSERTAR('MATRICULADO');
CALL ESTADOS_INSERTAR('RETIRADO');
CALL ESTADOS_INSERTAR('APROBADO');
CALL ESTADOS_INSERTAR('REPROBADO');

-- Estados de asistencia (4)
CALL ESTADOS_INSERTAR('PRESENTE');
CALL ESTADOS_INSERTAR('AUSENTE');
CALL ESTADOS_INSERTAR('TARDE');
CALL ESTADOS_INSERTAR('JUSTIFICADO');

-- Estados de soporte (4)
CALL ESTADOS_INSERTAR('ABIERTO');
CALL ESTADOS_INSERTAR('EN_PROCESO');
CALL ESTADOS_INSERTAR('RESUELTO');
CALL ESTADOS_INSERTAR('CERRADO');

-- Estados de seguridad (2)
CALL ESTADOS_INSERTAR('BLOQUEADO');
CALL ESTADOS_INSERTAR('EXPIRADO');

-- =========================================================
-- VERIFICACIÓN
-- =========================================================
SELECT * FROM ESTADOS_TB ORDER BY ID_ESTADO;

SELECT COUNT(*) AS total_estados FROM ESTADOS_TB;








