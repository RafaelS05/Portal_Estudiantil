DROP PROCEDURE IF EXISTS PERIODOS_INSERTAR;
DELIMITER $$

CREATE PROCEDURE PERIODOS_INSERTAR(
    IN p_nombre VARCHAR(255),
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO PERIODOS_TB (NOMBRE, FECHA_INICIO, FECHA_FIN, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
    VALUES (p_nombre, p_fecha_inicio, p_fecha_fin, p_id_estado_fk, NOW(), USER(), 'INSERT');

    -- Esto permite que Spring/JPA lo lea como resultset (1 fila, 1 columna)
    SELECT LAST_INSERT_ID() AS idPeriodo;
END$$

DELIMITER ;

-- ------------------------------------------------------
DROP PROCEDURE IF EXISTS MATERIA_INSERTAR;
DELIMITER $$

CREATE PROCEDURE MATERIA_INSERTAR(
    IN p_nombre VARCHAR(255),
    IN p_codigo VARCHAR(255),
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO MATERIA_TB (NOMBRE, CODIGO, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
    VALUES (p_nombre, p_codigo, p_id_estado_fk, NOW(), USER(), 'INSERT');

    SELECT LAST_INSERT_ID() AS idMateria;
END$$

DELIMITER ;

-- -----------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCION_INSERTAR;
DELIMITER $$

CREATE PROCEDURE SECCION_INSERTAR(
    IN p_numero VARCHAR(255),
    IN p_id_periodo_fk INT,
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO SECCION_TB (NUMERO, ID_PERIODO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
    VALUES (p_numero, p_id_periodo_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');

    SELECT LAST_INSERT_ID() AS idSeccion;
END$$

DELIMITER ;

-- --------------------------------------------------------------
DROP PROCEDURE IF EXISTS AULA_INSERTAR;
DELIMITER $$

CREATE PROCEDURE AULA_INSERTAR(
    IN p_numero VARCHAR(255),
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO AULA_TB (NUMERO, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
    VALUES (p_numero, p_id_estado_fk, NOW(), USER(), 'INSERT');

    SELECT LAST_INSERT_ID() AS idAula;
END$$

DELIMITER ;

-- ---------------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCIONMATERIA_INSERTAR;
DELIMITER $$

CREATE PROCEDURE SECCIONMATERIA_INSERTAR(
    IN p_id_seccion_fk INT,
    IN p_id_materia_fk INT,
    IN p_id_usuario_docente_fk INT,
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO SECCIONMATERIA_TB (
        ID_SECCION_FK, ID_MATERIA_FK, ID_USUARIO_DOCENTE_FK, ID_ESTADO_FK,
        FECHA_CREACION, CREADO_POR, ACCION
    )
    VALUES (
        p_id_seccion_fk, p_id_materia_fk, p_id_usuario_docente_fk, p_id_estado_fk,
        NOW(), USER(), 'INSERT'
    );

    SELECT LAST_INSERT_ID() AS idSeccionMateria;
END$$

DELIMITER ;

-- ------------------------------------------------------------------
DROP PROCEDURE IF EXISTS HORARIO_INSERTAR;
DELIMITER $$

CREATE PROCEDURE HORARIO_INSERTAR(
    IN p_dia_semana INT,
    IN p_hora_inicio VARCHAR(5),
    IN p_hora_fin VARCHAR(5),
    IN p_id_aula_fk INT,
    IN p_id_seccionmateria_fk INT,
    IN p_id_estado_fk INT
)
BEGIN
    INSERT INTO HORARIO_TB (
        DIA_SEMANA, HORA_INICIO, HORA_FIN, ID_AULA_FK, ID_SECCIONMATERIA_FK, ID_ESTADO_FK,
        FECHA_CREACION, CREADO_POR, ACCION
    )
    VALUES (
        p_dia_semana, p_hora_inicio, p_hora_fin, p_id_aula_fk, p_id_seccionmateria_fk, p_id_estado_fk,
        NOW(), USER(), 'INSERT'
    );

    SELECT LAST_INSERT_ID() AS idHorario;
END$$

DELIMITER ;


