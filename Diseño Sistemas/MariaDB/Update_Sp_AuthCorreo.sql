
DELIMITER $$

DROP PROCEDURE IF EXISTS AUTH_GET_USER_BY_EMAIL$$

CREATE PROCEDURE AUTH_GET_USER_BY_EMAIL(
    p_email VARCHAR(255),
    OUT p_username VARCHAR(255),
    OUT p_password_hash VARCHAR(255),
    OUT p_role VARCHAR(255),
    OUT p_intentos INT,
    OUT p_bloqueado_hasta DATETIME,
    OUT p_enabled INT,
    OUT p_id_credencial INT,
    OUT p_id_usuario INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_username = NULL;
        SET p_password_hash = NULL;
        SET p_role = NULL;
        SET p_intentos = 0;
        SET p_bloqueado_hasta = NULL;
        SET p_enabled = 0;
        SET p_id_credencial = NULL;
        SET p_id_usuario = NULL;
    END;

    SELECT
        CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS username,
        c.PASSWORD_HASH,
        tu.NOMBRE AS role_name,
        c.INTENTOS_FALLIDOS,
        c.BLOQUEADO_HASTA,
        CASE
            WHEN u.ID_ESTADO_FK = 1
             AND co.ID_ESTADO_FK = 1
             AND c.ID_ESTADO_FK = 1
            THEN 1 ELSE 0
        END AS enabled,
        c.ID_CREDENCIAL,
        u.ID_USUARIO
    INTO
        p_username,
        p_password_hash,
        p_role,
        p_intentos,
        p_bloqueado_hasta,
        p_enabled,
        p_id_credencial,
        p_id_usuario
    FROM CORREO_TB co
    JOIN CREDENCIALES_TB c  ON c.ID_CORREO_FK    = co.ID_CORREO
    JOIN USUARIOS_TB u      ON u.ID_USUARIO       = c.ID_USUARIO_FK
    JOIN TIPOUSUARIO_TB tu  ON tu.ID_TIPOUSUARIO  = u.ID_TIPOUSUARIO_FK
    WHERE LOWER(co.CORREO) = LOWER(p_email)
      AND co.ES_LOGIN = 'S';
END$$

DELIMITER ;