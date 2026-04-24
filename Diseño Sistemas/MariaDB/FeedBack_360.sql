-- ────────────────────────────────────────────────────────────
--  1. FEEDBACK360_INSERTAR
-- ────────────────────────────────────────────────────────────
DROP PROCEDURE IF EXISTS FEEDBACK360_INSERTAR;
 
DELIMITER $$
 
CREATE PROCEDURE FEEDBACK360_INSERTAR(
    IN p_calificacion         TINYINT,
    IN p_comentario           VARCHAR(500),
    IN p_fecha_evaluacion     DATE,
    IN p_id_seccionmateria_fk INT,
    IN p_id_matricula_fk      INT,
    IN p_id_estado_fk         INT
)
BEGIN
    INSERT INTO FEEDBACK360_TB (
        CALIFICACION,
        COMENTARIO,
        FECHA_EVALUACION,
        ID_SECCIONMATERIA_FK,
        ID_MATRICULA_FK,
        ID_ESTADO_FK,
        FECHA_CREACION,
        CREADO_POR,
        ACCION
    ) VALUES (
        p_calificacion,
        p_comentario,
        p_fecha_evaluacion,
        p_id_seccionmateria_fk,
        p_id_matricula_fk,
        p_id_estado_fk,
        NOW(),
        USER(),
        'INSERT'
    );
END$$
 
DELIMITER ;
 
 
-- ────────────────────────────────────────────────────────────
--  2. FEEDBACK360_MODIFICAR
-- ────────────────────────────────────────────────────────────
DROP PROCEDURE IF EXISTS FEEDBACK360_MODIFICAR;
 
DELIMITER $$
 
CREATE PROCEDURE FEEDBACK360_MODIFICAR(
    IN p_id_feedback          INT,
    IN p_calificacion         TINYINT,
    IN p_comentario           VARCHAR(500),
    IN p_fecha_evaluacion     DATE,
    IN p_id_seccionmateria_fk INT,
    IN p_id_matricula_fk      INT
)
BEGIN
    UPDATE FEEDBACK360_TB
    SET
        CALIFICACION         = p_calificacion,
        COMENTARIO           = p_comentario,
        FECHA_EVALUACION     = p_fecha_evaluacion,
        ID_SECCIONMATERIA_FK = p_id_seccionmateria_fk,
        ID_MATRICULA_FK      = p_id_matricula_fk,
        FECHA_MODIFICACION   = NOW(),
        MODIFICADO_POR       = USER(),
        ACCION               = 'UPDATE'
    WHERE ID_FEEDBACK = p_id_feedback;
END$$
 
DELIMITER ;
 
 
-- ────────────────────────────────────────────────────────────
--  3. FEEDBACK360_CAMBIAR_ESTADO
-- ────────────────────────────────────────────────────────────
DROP PROCEDURE IF EXISTS FEEDBACK360_CAMBIAR_ESTADO;
 feedback360_tb
DELIMITER $$
 
CREATE PROCEDURE FEEDBACK360_CAMBIAR_ESTADO(
    IN p_id_feedback INT,
    IN p_id_estado   INT
)
BEGIN
    UPDATE FEEDBACK360_TB
    SET
        ID_ESTADO_FK       = p_id_estado,
        FECHA_MODIFICACION = NOW(),
        MODIFICADO_POR     = USER(),
        ACCION             = 'CAMBIO_ESTADO'
    WHERE ID_FEEDBACK = p_id_feedback;
END$$
 
DELIMITER ;
 