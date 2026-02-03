USE PORTAL_ESCOLAR;

DELIMITER $$

CREATE PROCEDURE AUTH_REQUEST_RESET (
    IN p_email VARCHAR(120),
    OUT p_token VARCHAR(255),
    OUT p_id_credencial INT,
    OUT p_exists INT
)
BEGIN
    DECLARE v_id_credencial INT;
    DECLARE v_token VARCHAR(255);
    DECLARE v_estado_activo INT;
    DECLARE v_estado_expirado INT;

    -- Estado ACTIVO
    SELECT ID_ESTADO INTO v_estado_activo
    FROM ESTADOS_TB
    WHERE DESCRIPCION = 'ACTIVO'
    LIMIT 1;

    -- Estado EXPIRADO
    SELECT ID_ESTADO INTO v_estado_expirado
    FROM ESTADOS_TB
    WHERE DESCRIPCION = 'EXPIRADO'
    LIMIT 1;

    -- Verificar correo
    SELECT c.ID_CREDENCIAL
    INTO v_id_credencial
    FROM CREDENCIALES_TB c
    JOIN CORREO_TB co ON c.ID_CORREO_FK = co.ID_CORREO
    WHERE co.CORREO = p_email
      AND co.ES_LOGIN = 'S'
      AND c.ID_ESTADO_FK = v_estado_activo
    LIMIT 1;

    IF v_id_credencial IS NULL THEN
        SET p_exists = 0;
        SET p_token = NULL;
        SET p_id_credencial = NULL;
    ELSE
        SET p_exists = 1;
        SET p_id_credencial = v_id_credencial;
        SET v_token = UUID();
        SET p_token = v_token;

        -- Invalidar tokens anteriores
        UPDATE RESET_PASSWORD_TB
        SET ID_ESTADO_FK = v_estado_expirado
        WHERE ID_CREDENCIAL_FK = v_id_credencial
          AND FECHA_EXPIRACION > NOW()
          AND FECHA_USO IS NULL;

        -- Insertar nuevo token
        INSERT INTO RESET_PASSWORD_TB (
            TOKEN,
            FECHA_CREACION_TOKEN,
            FECHA_EXPIRACION,
            ID_CREDENCIAL_FK,
            ID_ESTADO_FK,
            CREADO_POR,
            ACCION
        ) VALUES (
            v_token,
            NOW(),
            DATE_ADD(NOW(), INTERVAL 30 MINUTE),
            v_id_credencial,
            v_estado_activo,
            'SYSTEM',
            'INSERT'
        );
    END IF;
END$$




CREATE PROCEDURE AUTH_VALIDATE_TOKEN (
    IN p_token VARCHAR(255),
    OUT p_valid INT,
    OUT p_id_credencial INT,
    OUT p_email VARCHAR(120)
)
BEGIN
    DECLARE v_fecha_exp DATETIME;
    DECLARE v_fecha_uso DATETIME;
    DECLARE v_estado_activo INT;
    DECLARE v_estado_expirado INT;

    SET p_valid = 0;

    SELECT ID_ESTADO INTO v_estado_activo
    FROM ESTADOS_TB WHERE DESCRIPCION = 'ACTIVO' LIMIT 1;

    SELECT ID_ESTADO INTO v_estado_expirado
    FROM ESTADOS_TB WHERE DESCRIPCION = 'EXPIRADO' LIMIT 1;

    SELECT 
        rp.ID_CREDENCIAL_FK,
        co.CORREO,
        rp.FECHA_EXPIRACION,
        rp.FECHA_USO
    INTO
        p_id_credencial,
        p_email,
        v_fecha_exp,
        v_fecha_uso
    FROM RESET_PASSWORD_TB rp
    JOIN CREDENCIALES_TB cr ON rp.ID_CREDENCIAL_FK = cr.ID_CREDENCIAL
    JOIN CORREO_TB co ON cr.ID_CORREO_FK = co.ID_CORREO
    WHERE rp.TOKEN = p_token
      AND rp.ID_ESTADO_FK = v_estado_activo
    LIMIT 1;

    IF p_id_credencial IS NULL THEN
        SET p_valid = 0;
    ELSEIF v_fecha_uso IS NOT NULL THEN
        SET p_valid = 0;
    ELSEIF v_fecha_exp < NOW() THEN
        UPDATE RESET_PASSWORD_TB
        SET ID_ESTADO_FK = v_estado_expirado
        WHERE TOKEN = p_token;
        SET p_valid = 0;
    ELSE
        SET p_valid = 1;
    END IF;
END$$

CREATE PROCEDURE AUTH_RESET_PASSWORD (
    IN p_token VARCHAR(255),
    IN p_new_password_hash VARCHAR(255),
    OUT p_success INT
)
BEGIN
    DECLARE v_valid INT;
    DECLARE v_id_credencial INT;
    DECLARE v_email VARCHAR(120);

    CALL AUTH_VALIDATE_TOKEN(p_token, v_valid, v_id_credencial, v_email);

    IF v_valid = 1 THEN
        UPDATE CREDENCIALES_TB
        SET PASSWORD_HASH = p_new_password_hash,
            INTENTOS_FALLIDOS = 0,
            BLOQUEADO_HASTA = NULL,
            MODIFICADO_POR = 'RESET_PASSWORD',
            ACCION = 'PASSWORD_RESET'
        WHERE ID_CREDENCIAL = v_id_credencial;

        UPDATE RESET_PASSWORD_TB
        SET FECHA_USO = NOW(),
            MODIFICADO_POR = 'SYSTEM',
            ACCION = 'TOKEN_USED'
        WHERE TOKEN = p_token;

        SET p_success = 1;
    ELSE
        SET p_success = 0;
    END IF;
END$$

DELIMITER ;