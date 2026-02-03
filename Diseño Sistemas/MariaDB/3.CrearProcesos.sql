-- =========================================================
-- PORTAL ESCOLAR - TODOS LOS PROCEDIMIENTOS
-- Conversión Oracle → MariaDB
-- Total: 110 procedimientos (107 CRUD + 3 AUTH)
-- =========================================================

USE PORTAL_ESCOLAR;

DELIMITER $$


-- ---------------------------------------------------------
-- ESTADOS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ESTADOS_INSERTAR$$

CREATE PROCEDURE ESTADOS_INSERTAR(
    p_descripcion VARCHAR(255)
)
BEGIN
    INSERT INTO ESTADOS_TB (DESCRIPCION, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_descripcion, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- ESTADOS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ESTADOS_MODIFICAR$$

CREATE PROCEDURE ESTADOS_MODIFICAR(
    p_id_estado INT,
    p_descripcion VARCHAR(255)
)
BEGIN
    UPDATE ESTADOS_TB
      SET DESCRIPCION = p_descripcion, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_ESTADO = p_id_estado;
END$$


-- ---------------------------------------------------------
-- TIPOUSUARIO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOUSUARIO_INSERTAR$$

CREATE PROCEDURE TIPOUSUARIO_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TIPOUSUARIO_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TIPOUSUARIO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOUSUARIO_MODIFICAR$$

CREATE PROCEDURE TIPOUSUARIO_MODIFICAR(
    p_id_tipousuario INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE TIPOUSUARIO_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TIPOUSUARIO = p_id_tipousuario;
END$$


-- ---------------------------------------------------------
-- TIPOUSUARIO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOUSUARIO_CAMBIAR_ESTADO$$

CREATE PROCEDURE TIPOUSUARIO_CAMBIAR_ESTADO(
    p_id_tipousuario INT,
    p_id_estado INT
)
BEGIN
    UPDATE TIPOUSUARIO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TIPOUSUARIO = p_id_tipousuario;
END$$


-- ---------------------------------------------------------
-- TIPOVISIBILIDAD_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOVISIBILIDAD_INSERTAR$$

CREATE PROCEDURE TIPOVISIBILIDAD_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TIPOVISIBILIDAD_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TIPOVISIBILIDAD_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOVISIBILIDAD_MODIFICAR$$

CREATE PROCEDURE TIPOVISIBILIDAD_MODIFICAR(
    p_id_tipovisibilidad INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE TIPOVISIBILIDAD_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TIPOVISIBILIDAD = p_id_tipovisibilidad;
END$$


-- ---------------------------------------------------------
-- TIPOVISIBILIDAD_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOVISIBILIDAD_CAMBIAR_ESTADO$$

CREATE PROCEDURE TIPOVISIBILIDAD_CAMBIAR_ESTADO(
    p_id_tipovisibilidad INT,
    p_id_estado INT
)
BEGIN
    UPDATE TIPOVISIBILIDAD_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TIPOVISIBILIDAD = p_id_tipovisibilidad;
END$$


-- ---------------------------------------------------------
-- TIPOREPORTE_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOREPORTE_INSERTAR$$

CREATE PROCEDURE TIPOREPORTE_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TIPOREPORTE_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TIPOREPORTE_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOREPORTE_MODIFICAR$$

CREATE PROCEDURE TIPOREPORTE_MODIFICAR(
    p_id_tiporeporte INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE TIPOREPORTE_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TIPOREPORTE = p_id_tiporeporte;
END$$


-- ---------------------------------------------------------
-- TIPOREPORTE_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOREPORTE_CAMBIAR_ESTADO$$

CREATE PROCEDURE TIPOREPORTE_CAMBIAR_ESTADO(
    p_id_tiporeporte INT,
    p_id_estado INT
)
BEGIN
    UPDATE TIPOREPORTE_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TIPOREPORTE = p_id_tiporeporte;
END$$


-- ---------------------------------------------------------
-- TIPOCONVERSACION_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOCONVERSACION_INSERTAR$$

CREATE PROCEDURE TIPOCONVERSACION_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TIPOCONVERSACION_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TIPOCONVERSACION_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOCONVERSACION_MODIFICAR$$

CREATE PROCEDURE TIPOCONVERSACION_MODIFICAR(
    p_id_tipoconversacion INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE TIPOCONVERSACION_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TIPOCONVERSACION = p_id_tipoconversacion;
END$$


-- ---------------------------------------------------------
-- TIPOCONVERSACION_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TIPOCONVERSACION_CAMBIAR_ESTADO$$

CREATE PROCEDURE TIPOCONVERSACION_CAMBIAR_ESTADO(
    p_id_tipoconversacion INT,
    p_id_estado INT
)
BEGIN
    UPDATE TIPOCONVERSACION_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TIPOCONVERSACION = p_id_tipoconversacion;
END$$


-- ---------------------------------------------------------
-- PROVINCIA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PROVINCIA_INSERTAR$$

CREATE PROCEDURE PROVINCIA_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO PROVINCIA_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- PROVINCIA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PROVINCIA_MODIFICAR$$

CREATE PROCEDURE PROVINCIA_MODIFICAR(
    p_id_provincia INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE PROVINCIA_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_PROVINCIA = p_id_provincia;
END$$


-- ---------------------------------------------------------
-- PROVINCIA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PROVINCIA_CAMBIAR_ESTADO$$

CREATE PROCEDURE PROVINCIA_CAMBIAR_ESTADO(
    p_id_provincia INT,
    p_id_estado INT
)
BEGIN
    UPDATE PROVINCIA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_PROVINCIA = p_id_provincia;
END$$


-- ---------------------------------------------------------
-- CANTON_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CANTON_INSERTAR$$

CREATE PROCEDURE CANTON_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_provincia_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CANTON_TB (NOMBRE, ID_PROVINCIA_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_provincia_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CANTON_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CANTON_MODIFICAR$$

CREATE PROCEDURE CANTON_MODIFICAR(
    p_id_canton INT,
    p_nombre VARCHAR(255),
    p_id_provincia_fk INT
)
BEGIN
    UPDATE CANTON_TB
      SET NOMBRE = p_nombre, ID_PROVINCIA_FK = p_id_provincia_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CANTON = p_id_canton;
END$$


-- ---------------------------------------------------------
-- CANTON_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CANTON_CAMBIAR_ESTADO$$

CREATE PROCEDURE CANTON_CAMBIAR_ESTADO(
    p_id_canton INT,
    p_id_estado INT
)
BEGIN
    UPDATE CANTON_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CANTON = p_id_canton;
END$$


-- ---------------------------------------------------------
-- DISTRITO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DISTRITO_INSERTAR$$

CREATE PROCEDURE DISTRITO_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_canton_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO DISTRITO_TB (NOMBRE, ID_CANTON_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_canton_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- DISTRITO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DISTRITO_MODIFICAR$$

CREATE PROCEDURE DISTRITO_MODIFICAR(
    p_id_distrito INT,
    p_nombre VARCHAR(255),
    p_id_canton_fk INT
)
BEGIN
    UPDATE DISTRITO_TB
      SET NOMBRE = p_nombre, ID_CANTON_FK = p_id_canton_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_DISTRITO = p_id_distrito;
END$$


-- ---------------------------------------------------------
-- DISTRITO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DISTRITO_CAMBIAR_ESTADO$$

CREATE PROCEDURE DISTRITO_CAMBIAR_ESTADO(
    p_id_distrito INT,
    p_id_estado INT
)
BEGIN
    UPDATE DISTRITO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_DISTRITO = p_id_distrito;
END$$


-- ---------------------------------------------------------
-- USUARIOS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS USUARIOS_INSERTAR$$

CREATE PROCEDURE USUARIOS_INSERTAR(
    p_nombre VARCHAR(255),
    p_primer_apellido VARCHAR(255),
    p_segundo_apellido VARCHAR(255),
    p_id_tipousuario_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO USUARIOS_TB (NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, ID_TIPOUSUARIO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_primer_apellido, p_segundo_apellido, p_id_tipousuario_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- USUARIOS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS USUARIOS_MODIFICAR$$

CREATE PROCEDURE USUARIOS_MODIFICAR(
    p_id_usuario INT,
    p_nombre VARCHAR(255),
    p_primer_apellido VARCHAR(255),
    p_segundo_apellido VARCHAR(255),
    p_id_tipousuario_fk INT
)
BEGIN
    UPDATE USUARIOS_TB
      SET NOMBRE = p_nombre, PRIMER_APELLIDO = p_primer_apellido, SEGUNDO_APELLIDO = p_segundo_apellido, ID_TIPOUSUARIO_FK = p_id_tipousuario_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_USUARIO = p_id_usuario;
END$$


-- ---------------------------------------------------------
-- USUARIOS_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS USUARIOS_CAMBIAR_ESTADO$$

CREATE PROCEDURE USUARIOS_CAMBIAR_ESTADO(
    p_id_usuario INT,
    p_id_estado INT
)
BEGIN
    UPDATE USUARIOS_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_USUARIO = p_id_usuario;
END$$


-- ---------------------------------------------------------
-- TELEFONO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TELEFONO_INSERTAR$$

CREATE PROCEDURE TELEFONO_INSERTAR(
    p_numero VARCHAR(255),
    p_id_usuario_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TELEFONO_TB (NUMERO, ID_USUARIO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_numero, p_id_usuario_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TELEFONO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TELEFONO_MODIFICAR$$

CREATE PROCEDURE TELEFONO_MODIFICAR(
    p_id_telefono INT,
    p_numero VARCHAR(255),
    p_id_usuario_fk INT
)
BEGIN
    UPDATE TELEFONO_TB
      SET NUMERO = p_numero, ID_USUARIO_FK = p_id_usuario_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TELEFONO = p_id_telefono;
END$$


-- ---------------------------------------------------------
-- TELEFONO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TELEFONO_CAMBIAR_ESTADO$$

CREATE PROCEDURE TELEFONO_CAMBIAR_ESTADO(
    p_id_telefono INT,
    p_id_estado INT
)
BEGIN
    UPDATE TELEFONO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TELEFONO = p_id_telefono;
END$$


-- ---------------------------------------------------------
-- CORREO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CORREO_INSERTAR$$

CREATE PROCEDURE CORREO_INSERTAR(
    p_correo VARCHAR(255),
    p_es_login CHAR(1),
    p_id_usuario_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CORREO_TB (CORREO, ES_LOGIN, ID_USUARIO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_correo, p_es_login, p_id_usuario_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CORREO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CORREO_MODIFICAR$$

CREATE PROCEDURE CORREO_MODIFICAR(
    p_id_correo INT,
    p_correo VARCHAR(255),
    p_es_login CHAR(1),
    p_id_usuario_fk INT
)
BEGIN
    UPDATE CORREO_TB
      SET CORREO = p_correo, ES_LOGIN = p_es_login, ID_USUARIO_FK = p_id_usuario_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CORREO = p_id_correo;
END$$


-- ---------------------------------------------------------
-- CORREO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CORREO_CAMBIAR_ESTADO$$

CREATE PROCEDURE CORREO_CAMBIAR_ESTADO(
    p_id_correo INT,
    p_id_estado INT
)
BEGIN
    UPDATE CORREO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CORREO = p_id_correo;
END$$


-- ---------------------------------------------------------
-- DIRECCION_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DIRECCION_INSERTAR$$

CREATE PROCEDURE DIRECCION_INSERTAR(
    p_otras_senas VARCHAR(255),
    p_id_usuario_fk INT,
    p_id_provincia_fk INT,
    p_id_canton_fk INT,
    p_id_distrito_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO DIRECCION_TB (OTRAS_SENAS, ID_USUARIO_FK, ID_PROVINCIA_FK, ID_CANTON_FK, ID_DISTRITO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_otras_senas, p_id_usuario_fk, p_id_provincia_fk, p_id_canton_fk, p_id_distrito_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- DIRECCION_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DIRECCION_MODIFICAR$$

CREATE PROCEDURE DIRECCION_MODIFICAR(
    p_id_direccion INT,
    p_otras_senas VARCHAR(255),
    p_id_usuario_fk INT,
    p_id_provincia_fk INT,
    p_id_canton_fk INT,
    p_id_distrito_fk INT
)
BEGIN
    UPDATE DIRECCION_TB
      SET OTRAS_SENAS = p_otras_senas, ID_USUARIO_FK = p_id_usuario_fk, ID_PROVINCIA_FK = p_id_provincia_fk, ID_CANTON_FK = p_id_canton_fk, ID_DISTRITO_FK = p_id_distrito_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_DIRECCION = p_id_direccion;
END$$


-- ---------------------------------------------------------
-- DIRECCION_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS DIRECCION_CAMBIAR_ESTADO$$

CREATE PROCEDURE DIRECCION_CAMBIAR_ESTADO(
    p_id_direccion INT,
    p_id_estado INT
)
BEGIN
    UPDATE DIRECCION_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_DIRECCION = p_id_direccion;
END$$


-- ---------------------------------------------------------
-- ENCARGADOESTUDIANTE_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ENCARGADOESTUDIANTE_INSERTAR$$

CREATE PROCEDURE ENCARGADOESTUDIANTE_INSERTAR(
    p_parentesco VARCHAR(255),
    p_id_usuario_estudiante_fk INT,
    p_id_usuario_encargado_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO ENCARGADOESTUDIANTE_TB (PARENTESCO, ID_USUARIO_ESTUDIANTE_FK, ID_USUARIO_ENCARGADO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_parentesco, p_id_usuario_estudiante_fk, p_id_usuario_encargado_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- ENCARGADOESTUDIANTE_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ENCARGADOESTUDIANTE_MODIFICAR$$

CREATE PROCEDURE ENCARGADOESTUDIANTE_MODIFICAR(
    p_id_encargadoestudiante INT,
    p_parentesco VARCHAR(255),
    p_id_usuario_estudiante_fk INT,
    p_id_usuario_encargado_fk INT
)
BEGIN
    UPDATE ENCARGADOESTUDIANTE_TB
      SET PARENTESCO = p_parentesco, ID_USUARIO_ESTUDIANTE_FK = p_id_usuario_estudiante_fk, ID_USUARIO_ENCARGADO_FK = p_id_usuario_encargado_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_ENCARGADOESTUDIANTE = p_id_encargadoestudiante;
END$$


-- ---------------------------------------------------------
-- ENCARGADOESTUDIANTE_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ENCARGADOESTUDIANTE_CAMBIAR_ESTADO$$

CREATE PROCEDURE ENCARGADOESTUDIANTE_CAMBIAR_ESTADO(
    p_id_encargadoestudiante INT,
    p_id_estado INT
)
BEGIN
    UPDATE ENCARGADOESTUDIANTE_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_ENCARGADOESTUDIANTE = p_id_encargadoestudiante;
END$$


-- ---------------------------------------------------------
-- CREDENCIALES_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CREDENCIALES_INSERTAR$$

CREATE PROCEDURE CREDENCIALES_INSERTAR(
    p_password_hash VARCHAR(255),
    p_ultimo_login DATETIME,
    p_intentos_fallidos INT,
    p_bloqueado_hasta DATETIME,
    p_id_usuario_fk INT,
    p_id_correo_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CREDENCIALES_TB (PASSWORD_HASH, ULTIMO_LOGIN, INTENTOS_FALLIDOS, BLOQUEADO_HASTA, ID_USUARIO_FK, ID_CORREO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_password_hash, p_ultimo_login, p_intentos_fallidos, p_bloqueado_hasta, p_id_usuario_fk, p_id_correo_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CREDENCIALES_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CREDENCIALES_MODIFICAR$$

CREATE PROCEDURE CREDENCIALES_MODIFICAR(
    p_id_credencial INT,
    p_password_hash VARCHAR(255),
    p_ultimo_login DATETIME,
    p_intentos_fallidos INT,
    p_bloqueado_hasta DATETIME,
    p_id_usuario_fk INT,
    p_id_correo_fk INT
)
BEGIN
    UPDATE CREDENCIALES_TB
      SET PASSWORD_HASH = p_password_hash, ULTIMO_LOGIN = p_ultimo_login, INTENTOS_FALLIDOS = p_intentos_fallidos, BLOQUEADO_HASTA = p_bloqueado_hasta, ID_USUARIO_FK = p_id_usuario_fk, ID_CORREO_FK = p_id_correo_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CREDENCIAL = p_id_credencial;
END$$


-- ---------------------------------------------------------
-- CREDENCIALES_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CREDENCIALES_CAMBIAR_ESTADO$$

CREATE PROCEDURE CREDENCIALES_CAMBIAR_ESTADO(
    p_id_credencial INT,
    p_id_estado INT
)
BEGIN
    UPDATE CREDENCIALES_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CREDENCIAL = p_id_credencial;
END$$


-- ---------------------------------------------------------
-- RESET_PASSWORD_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS RESET_PASSWORD_INSERTAR$$

CREATE PROCEDURE RESET_PASSWORD_INSERTAR(
    p_token VARCHAR(255),
    p_fecha_creacion_token DATETIME,
    p_fecha_expiracion DATETIME,
    p_fecha_uso DATETIME,
    p_id_credencial_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO RESET_PASSWORD_TB (TOKEN, FECHA_CREACION_TOKEN, FECHA_EXPIRACION, FECHA_USO, ID_CREDENCIAL_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_token, p_fecha_creacion_token, p_fecha_expiracion, p_fecha_uso, p_id_credencial_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- RESET_PASSWORD_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS RESET_PASSWORD_MODIFICAR$$

CREATE PROCEDURE RESET_PASSWORD_MODIFICAR(
    p_id_reset INT,
    p_token VARCHAR(255),
    p_fecha_creacion_token DATETIME,
    p_fecha_expiracion DATETIME,
    p_fecha_uso DATETIME,
    p_id_credencial_fk INT
)
BEGIN
    UPDATE RESET_PASSWORD_TB
      SET TOKEN = p_token, FECHA_CREACION_TOKEN = p_fecha_creacion_token, FECHA_EXPIRACION = p_fecha_expiracion, FECHA_USO = p_fecha_uso, ID_CREDENCIAL_FK = p_id_credencial_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_RESET = p_id_reset;
END$$


-- ---------------------------------------------------------
-- RESET_PASSWORD_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS RESET_PASSWORD_CAMBIAR_ESTADO$$

CREATE PROCEDURE RESET_PASSWORD_CAMBIAR_ESTADO(
    p_id_reset INT,
    p_id_estado INT
)
BEGIN
    UPDATE RESET_PASSWORD_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_RESET = p_id_reset;
END$$


-- ---------------------------------------------------------
-- PERIODOS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PERIODOS_INSERTAR$$

CREATE PROCEDURE PERIODOS_INSERTAR(
    p_nombre VARCHAR(255),
    p_fecha_inicio DATE,
    p_fecha_fin DATE,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO PERIODOS_TB (NOMBRE, FECHA_INICIO, FECHA_FIN, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_fecha_inicio, p_fecha_fin, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- PERIODOS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PERIODOS_MODIFICAR$$

CREATE PROCEDURE PERIODOS_MODIFICAR(
    p_id_periodo INT,
    p_nombre VARCHAR(255),
    p_fecha_inicio DATE,
    p_fecha_fin DATE
)
BEGIN
    UPDATE PERIODOS_TB
      SET NOMBRE = p_nombre, FECHA_INICIO = p_fecha_inicio, FECHA_FIN = p_fecha_fin, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_PERIODO = p_id_periodo;
END$$


-- ---------------------------------------------------------
-- PERIODOS_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS PERIODOS_CAMBIAR_ESTADO$$

CREATE PROCEDURE PERIODOS_CAMBIAR_ESTADO(
    p_id_periodo INT,
    p_id_estado INT
)
BEGIN
    UPDATE PERIODOS_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_PERIODO = p_id_periodo;
END$$


-- ---------------------------------------------------------
-- MATERIA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATERIA_INSERTAR$$

CREATE PROCEDURE MATERIA_INSERTAR(
    p_nombre VARCHAR(255),
    p_codigo VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO MATERIA_TB (NOMBRE, CODIGO, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_codigo, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- MATERIA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATERIA_MODIFICAR$$

CREATE PROCEDURE MATERIA_MODIFICAR(
    p_id_materia INT,
    p_nombre VARCHAR(255),
    p_codigo VARCHAR(255)
)
BEGIN
    UPDATE MATERIA_TB
      SET NOMBRE = p_nombre, CODIGO = p_codigo, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_MATERIA = p_id_materia;
END$$


-- ---------------------------------------------------------
-- MATERIA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATERIA_CAMBIAR_ESTADO$$

CREATE PROCEDURE MATERIA_CAMBIAR_ESTADO(
    p_id_materia INT,
    p_id_estado INT
)
BEGIN
    UPDATE MATERIA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_MATERIA = p_id_materia;
END$$


-- ---------------------------------------------------------
-- SECCION_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCION_INSERTAR$$

CREATE PROCEDURE SECCION_INSERTAR(
    p_numero VARCHAR(255),
    p_id_periodo_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO SECCION_TB (NUMERO, ID_PERIODO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_numero, p_id_periodo_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- SECCION_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCION_MODIFICAR$$

CREATE PROCEDURE SECCION_MODIFICAR(
    p_id_seccion INT,
    p_numero VARCHAR(255),
    p_id_periodo_fk INT
)
BEGIN
    UPDATE SECCION_TB
      SET NUMERO = p_numero, ID_PERIODO_FK = p_id_periodo_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_SECCION = p_id_seccion;
END$$


-- ---------------------------------------------------------
-- SECCION_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCION_CAMBIAR_ESTADO$$

CREATE PROCEDURE SECCION_CAMBIAR_ESTADO(
    p_id_seccion INT,
    p_id_estado INT
)
BEGIN
    UPDATE SECCION_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_SECCION = p_id_seccion;
END$$


-- ---------------------------------------------------------
-- AULA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AULA_INSERTAR$$

CREATE PROCEDURE AULA_INSERTAR(
    p_numero VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO AULA_TB (NUMERO, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_numero, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- AULA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AULA_MODIFICAR$$

CREATE PROCEDURE AULA_MODIFICAR(
    p_id_aula INT,
    p_numero VARCHAR(255)
)
BEGIN
    UPDATE AULA_TB
      SET NUMERO = p_numero, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_AULA = p_id_aula;
END$$


-- ---------------------------------------------------------
-- AULA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AULA_CAMBIAR_ESTADO$$

CREATE PROCEDURE AULA_CAMBIAR_ESTADO(
    p_id_aula INT,
    p_id_estado INT
)
BEGIN
    UPDATE AULA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_AULA = p_id_aula;
END$$


-- ---------------------------------------------------------
-- SECCIONMATERIA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCIONMATERIA_INSERTAR$$

CREATE PROCEDURE SECCIONMATERIA_INSERTAR(
    p_id_seccion_fk INT,
    p_id_materia_fk INT,
    p_id_usuario_docente_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO SECCIONMATERIA_TB (ID_SECCION_FK, ID_MATERIA_FK, ID_USUARIO_DOCENTE_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_id_seccion_fk, p_id_materia_fk, p_id_usuario_docente_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- SECCIONMATERIA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCIONMATERIA_MODIFICAR$$

CREATE PROCEDURE SECCIONMATERIA_MODIFICAR(
    p_id_seccionmateria INT,
    p_id_seccion_fk INT,
    p_id_materia_fk INT,
    p_id_usuario_docente_fk INT
)
BEGIN
    UPDATE SECCIONMATERIA_TB
      SET ID_SECCION_FK = p_id_seccion_fk, ID_MATERIA_FK = p_id_materia_fk, ID_USUARIO_DOCENTE_FK = p_id_usuario_docente_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_SECCIONMATERIA = p_id_seccionmateria;
END$$


-- ---------------------------------------------------------
-- SECCIONMATERIA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS SECCIONMATERIA_CAMBIAR_ESTADO$$

CREATE PROCEDURE SECCIONMATERIA_CAMBIAR_ESTADO(
    p_id_seccionmateria INT,
    p_id_estado INT
)
BEGIN
    UPDATE SECCIONMATERIA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_SECCIONMATERIA = p_id_seccionmateria;
END$$


-- ---------------------------------------------------------
-- MATRICULA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATRICULA_INSERTAR$$

CREATE PROCEDURE MATRICULA_INSERTAR(
    p_fecha_matricula DATE,
    p_id_usuario_estudiante_fk INT,
    p_id_seccion_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO MATRICULA_TB (FECHA_MATRICULA, ID_USUARIO_ESTUDIANTE_FK, ID_SECCION_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_fecha_matricula, p_id_usuario_estudiante_fk, p_id_seccion_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- MATRICULA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATRICULA_MODIFICAR$$

CREATE PROCEDURE MATRICULA_MODIFICAR(
    p_id_matricula INT,
    p_fecha_matricula DATE,
    p_id_usuario_estudiante_fk INT,
    p_id_seccion_fk INT
)
BEGIN
    UPDATE MATRICULA_TB
      SET FECHA_MATRICULA = p_fecha_matricula, ID_USUARIO_ESTUDIANTE_FK = p_id_usuario_estudiante_fk, ID_SECCION_FK = p_id_seccion_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_MATRICULA = p_id_matricula;
END$$


-- ---------------------------------------------------------
-- MATRICULA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MATRICULA_CAMBIAR_ESTADO$$

CREATE PROCEDURE MATRICULA_CAMBIAR_ESTADO(
    p_id_matricula INT,
    p_id_estado INT
)
BEGIN
    UPDATE MATRICULA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_MATRICULA = p_id_matricula;
END$$


-- ---------------------------------------------------------
-- HORARIO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS HORARIO_INSERTAR$$

CREATE PROCEDURE HORARIO_INSERTAR(
    p_dia_semana INT,
    p_hora_inicio VARCHAR(255),
    p_hora_fin VARCHAR(255),
    p_id_aula_fk INT,
    p_id_seccionmateria_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO HORARIO_TB (DIA_SEMANA, HORA_INICIO, HORA_FIN, ID_AULA_FK, ID_SECCIONMATERIA_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_dia_semana, p_hora_inicio, p_hora_fin, p_id_aula_fk, p_id_seccionmateria_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- HORARIO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS HORARIO_MODIFICAR$$

CREATE PROCEDURE HORARIO_MODIFICAR(
    p_id_horario INT,
    p_dia_semana INT,
    p_hora_inicio VARCHAR(255),
    p_hora_fin VARCHAR(255),
    p_id_aula_fk INT,
    p_id_seccionmateria_fk INT
)
BEGIN
    UPDATE HORARIO_TB
      SET DIA_SEMANA = p_dia_semana, HORA_INICIO = p_hora_inicio, HORA_FIN = p_hora_fin, ID_AULA_FK = p_id_aula_fk, ID_SECCIONMATERIA_FK = p_id_seccionmateria_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_HORARIO = p_id_horario;
END$$


-- ---------------------------------------------------------
-- HORARIO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS HORARIO_CAMBIAR_ESTADO$$

CREATE PROCEDURE HORARIO_CAMBIAR_ESTADO(
    p_id_horario INT,
    p_id_estado INT
)
BEGIN
    UPDATE HORARIO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_HORARIO = p_id_horario;
END$$


-- ---------------------------------------------------------
-- ASISTENCIAS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ASISTENCIAS_INSERTAR$$

CREATE PROCEDURE ASISTENCIAS_INSERTAR(
    p_fecha_asistencia DATE,
    p_id_matricula_fk INT,
    p_id_seccionmateria_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO ASISTENCIAS_TB (FECHA_ASISTENCIA, ID_MATRICULA_FK, ID_SECCIONMATERIA_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_fecha_asistencia, p_id_matricula_fk, p_id_seccionmateria_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- ASISTENCIAS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ASISTENCIAS_MODIFICAR$$

CREATE PROCEDURE ASISTENCIAS_MODIFICAR(
    p_id_asistencia INT,
    p_fecha_asistencia DATE,
    p_id_matricula_fk INT,
    p_id_seccionmateria_fk INT
)
BEGIN
    UPDATE ASISTENCIAS_TB
      SET FECHA_ASISTENCIA = p_fecha_asistencia, ID_MATRICULA_FK = p_id_matricula_fk, ID_SECCIONMATERIA_FK = p_id_seccionmateria_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_ASISTENCIA = p_id_asistencia;
END$$


-- ---------------------------------------------------------
-- ASISTENCIAS_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ASISTENCIAS_CAMBIAR_ESTADO$$

CREATE PROCEDURE ASISTENCIAS_CAMBIAR_ESTADO(
    p_id_asistencia INT,
    p_id_estado INT
)
BEGIN
    UPDATE ASISTENCIAS_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_ASISTENCIA = p_id_asistencia;
END$$


-- ---------------------------------------------------------
-- JUSTIFICANTEASISTENCIA_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS JUSTIFICANTEASISTENCIA_INSERTAR$$

CREATE PROCEDURE JUSTIFICANTEASISTENCIA_INSERTAR(
    p_archivo_justificante VARCHAR(255),
    p_tipo VARCHAR(255),
    p_fecha_subida DATE,
    p_id_asistencia_fk INT,
    p_id_matricula_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO JUSTIFICANTEASISTENCIA_TB (ARCHIVO_JUSTIFICANTE, TIPO, FECHA_SUBIDA, ID_ASISTENCIA_FK, ID_MATRICULA_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_archivo_justificante, p_tipo, p_fecha_subida, p_id_asistencia_fk, p_id_matricula_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- JUSTIFICANTEASISTENCIA_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS JUSTIFICANTEASISTENCIA_MODIFICAR$$

CREATE PROCEDURE JUSTIFICANTEASISTENCIA_MODIFICAR(
    p_id_justificante INT,
    p_archivo_justificante VARCHAR(255),
    p_tipo VARCHAR(255),
    p_fecha_subida DATE,
    p_id_asistencia_fk INT,
    p_id_matricula_fk INT
)
BEGIN
    UPDATE JUSTIFICANTEASISTENCIA_TB
      SET ARCHIVO_JUSTIFICANTE = p_archivo_justificante, TIPO = p_tipo, FECHA_SUBIDA = p_fecha_subida, ID_ASISTENCIA_FK = p_id_asistencia_fk, ID_MATRICULA_FK = p_id_matricula_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_JUSTIFICANTE = p_id_justificante;
END$$


-- ---------------------------------------------------------
-- JUSTIFICANTEASISTENCIA_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS JUSTIFICANTEASISTENCIA_CAMBIAR_ESTADO$$

CREATE PROCEDURE JUSTIFICANTEASISTENCIA_CAMBIAR_ESTADO(
    p_id_justificante INT,
    p_id_estado INT
)
BEGIN
    UPDATE JUSTIFICANTEASISTENCIA_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_JUSTIFICANTE = p_id_justificante;
END$$


-- ---------------------------------------------------------
-- EVALUACION_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACION_INSERTAR$$

CREATE PROCEDURE EVALUACION_INSERTAR(
    p_tipo VARCHAR(255),
    p_porcentaje INT,
    p_id_seccionmateria_fk INT,
    p_id_periodo_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO EVALUACION_TB (TIPO, PORCENTAJE, ID_SECCIONMATERIA_FK, ID_PERIODO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_tipo, p_porcentaje, p_id_seccionmateria_fk, p_id_periodo_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- EVALUACION_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACION_MODIFICAR$$

CREATE PROCEDURE EVALUACION_MODIFICAR(
    p_id_evaluacion INT,
    p_tipo VARCHAR(255),
    p_porcentaje INT,
    p_id_seccionmateria_fk INT,
    p_id_periodo_fk INT
)
BEGIN
    UPDATE EVALUACION_TB
      SET TIPO = p_tipo, PORCENTAJE = p_porcentaje, ID_SECCIONMATERIA_FK = p_id_seccionmateria_fk, ID_PERIODO_FK = p_id_periodo_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_EVALUACION = p_id_evaluacion;
END$$


-- ---------------------------------------------------------
-- EVALUACION_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACION_CAMBIAR_ESTADO$$

CREATE PROCEDURE EVALUACION_CAMBIAR_ESTADO(
    p_id_evaluacion INT,
    p_id_estado INT
)
BEGIN
    UPDATE EVALUACION_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_EVALUACION = p_id_evaluacion;
END$$


-- ---------------------------------------------------------
-- CALIFICACIONES_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CALIFICACIONES_INSERTAR$$

CREATE PROCEDURE CALIFICACIONES_INSERTAR(
    p_calificacion INT,
    p_id_matricula_fk INT,
    p_id_evaluacion_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CALIFICACIONES_TB (CALIFICACION, ID_MATRICULA_FK, ID_EVALUACION_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_calificacion, p_id_matricula_fk, p_id_evaluacion_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CALIFICACIONES_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CALIFICACIONES_MODIFICAR$$

CREATE PROCEDURE CALIFICACIONES_MODIFICAR(
    p_id_calificaciones INT,
    p_calificacion INT,
    p_id_matricula_fk INT,
    p_id_evaluacion_fk INT
)
BEGIN
    UPDATE CALIFICACIONES_TB
      SET CALIFICACION = p_calificacion, ID_MATRICULA_FK = p_id_matricula_fk, ID_EVALUACION_FK = p_id_evaluacion_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CALIFICACIONES = p_id_calificaciones;
END$$


-- ---------------------------------------------------------
-- CALIFICACIONES_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CALIFICACIONES_CAMBIAR_ESTADO$$

CREATE PROCEDURE CALIFICACIONES_CAMBIAR_ESTADO(
    p_id_calificaciones INT,
    p_id_estado INT
)
BEGIN
    UPDATE CALIFICACIONES_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CALIFICACIONES = p_id_calificaciones;
END$$


-- ---------------------------------------------------------
-- EVENTO_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVENTO_INSERTAR$$

CREATE PROCEDURE EVENTO_INSERTAR(
    p_titulo VARCHAR(255),
    p_descripcion VARCHAR(255),
    p_fecha_inicio DATE,
    p_fecha_fin DATE,
    p_tipo_evento VARCHAR(255),
    p_id_tipovisibilidad_fk INT,
    p_id_seccion_fk INT,
    p_id_seccionmateria_fk INT,
    p_id_usuario_creado_por_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO EVENTO_TB (TITULO, DESCRIPCION, FECHA_INICIO, FECHA_FIN, TIPO_EVENTO, ID_TIPOVISIBILIDAD_FK, ID_SECCION_FK, ID_SECCIONMATERIA_FK, ID_USUARIO_CREADO_POR_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_titulo, p_descripcion, p_fecha_inicio, p_fecha_fin, p_tipo_evento, p_id_tipovisibilidad_fk, p_id_seccion_fk, p_id_seccionmateria_fk, p_id_usuario_creado_por_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- EVENTO_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVENTO_MODIFICAR$$

CREATE PROCEDURE EVENTO_MODIFICAR(
    p_id_evento INT,
    p_titulo VARCHAR(255),
    p_descripcion VARCHAR(255),
    p_fecha_inicio DATE,
    p_fecha_fin DATE,
    p_tipo_evento VARCHAR(255),
    p_id_tipovisibilidad_fk INT,
    p_id_seccion_fk INT,
    p_id_seccionmateria_fk INT,
    p_id_usuario_creado_por_fk INT
)
BEGIN
    UPDATE EVENTO_TB
      SET TITULO = p_titulo, DESCRIPCION = p_descripcion, FECHA_INICIO = p_fecha_inicio, FECHA_FIN = p_fecha_fin, TIPO_EVENTO = p_tipo_evento, ID_TIPOVISIBILIDAD_FK = p_id_tipovisibilidad_fk, ID_SECCION_FK = p_id_seccion_fk, ID_SECCIONMATERIA_FK = p_id_seccionmateria_fk, ID_USUARIO_CREADO_POR_FK = p_id_usuario_creado_por_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_EVENTO = p_id_evento;
END$$


-- ---------------------------------------------------------
-- EVENTO_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVENTO_CAMBIAR_ESTADO$$

CREATE PROCEDURE EVENTO_CAMBIAR_ESTADO(
    p_id_evento INT,
    p_id_estado INT
)
BEGIN
    UPDATE EVENTO_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_EVENTO = p_id_evento;
END$$


-- ---------------------------------------------------------
-- CONVERSACION_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CONVERSACION_INSERTAR$$

CREATE PROCEDURE CONVERSACION_INSERTAR(
    p_titulo VARCHAR(255),
    p_fecha_creacion_conv DATE,
    p_fecha_actualizacion DATE,
    p_ultimo_mensaje VARCHAR(255),
    p_id_tipoconversacion_fk INT,
    p_id_creado_por_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CONVERSACION_TB (TITULO, FECHA_CREACION_CONV, FECHA_ACTUALIZACION, ULTIMO_MENSAJE, ID_TIPOCONVERSACION_FK, ID_CREADO_POR_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_titulo, p_fecha_creacion_conv, p_fecha_actualizacion, p_ultimo_mensaje, p_id_tipoconversacion_fk, p_id_creado_por_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CONVERSACION_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CONVERSACION_MODIFICAR$$

CREATE PROCEDURE CONVERSACION_MODIFICAR(
    p_id_conversacion INT,
    p_titulo VARCHAR(255),
    p_fecha_creacion_conv DATE,
    p_fecha_actualizacion DATE,
    p_ultimo_mensaje VARCHAR(255),
    p_id_tipoconversacion_fk INT,
    p_id_creado_por_fk INT
)
BEGIN
    UPDATE CONVERSACION_TB
      SET TITULO = p_titulo, FECHA_CREACION_CONV = p_fecha_creacion_conv, FECHA_ACTUALIZACION = p_fecha_actualizacion, ULTIMO_MENSAJE = p_ultimo_mensaje, ID_TIPOCONVERSACION_FK = p_id_tipoconversacion_fk, ID_CREADO_POR_FK = p_id_creado_por_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CONVERSACION = p_id_conversacion;
END$$


-- ---------------------------------------------------------
-- CONVERSACION_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CONVERSACION_CAMBIAR_ESTADO$$

CREATE PROCEDURE CONVERSACION_CAMBIAR_ESTADO(
    p_id_conversacion INT,
    p_id_estado INT
)
BEGIN
    UPDATE CONVERSACION_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CONVERSACION = p_id_conversacion;
END$$


-- ---------------------------------------------------------
-- MENSAJES_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJES_INSERTAR$$

CREATE PROCEDURE MENSAJES_INSERTAR(
    p_fecha_envio DATE,
    p_fecha_editado DATE,
    p_asunto VARCHAR(255),
    p_contenido VARCHAR(255),
    p_id_conversacion_fk INT,
    p_id_emisor_fk INT,
    p_id_remitente_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO MENSAJES_TB (FECHA_ENVIO, FECHA_EDITADO, ASUNTO, CONTENIDO, ID_CONVERSACION_FK, ID_EMISOR_FK, ID_REMITENTE_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_fecha_envio, p_fecha_editado, p_asunto, p_contenido, p_id_conversacion_fk, p_id_emisor_fk, p_id_remitente_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- MENSAJES_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJES_MODIFICAR$$

CREATE PROCEDURE MENSAJES_MODIFICAR(
    p_id_mensaje INT,
    p_fecha_envio DATE,
    p_fecha_editado DATE,
    p_asunto VARCHAR(255),
    p_contenido VARCHAR(255),
    p_id_conversacion_fk INT,
    p_id_emisor_fk INT,
    p_id_remitente_fk INT
)
BEGIN
    UPDATE MENSAJES_TB
      SET FECHA_ENVIO = p_fecha_envio, FECHA_EDITADO = p_fecha_editado, ASUNTO = p_asunto, CONTENIDO = p_contenido, ID_CONVERSACION_FK = p_id_conversacion_fk, ID_EMISOR_FK = p_id_emisor_fk, ID_REMITENTE_FK = p_id_remitente_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_MENSAJE = p_id_mensaje;
END$$


-- ---------------------------------------------------------
-- MENSAJES_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJES_CAMBIAR_ESTADO$$

CREATE PROCEDURE MENSAJES_CAMBIAR_ESTADO(
    p_id_mensaje INT,
    p_id_estado INT
)
BEGIN
    UPDATE MENSAJES_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_MENSAJE = p_id_mensaje;
END$$


-- ---------------------------------------------------------
-- MENSAJESARCHIVOS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJESARCHIVOS_INSERTAR$$

CREATE PROCEDURE MENSAJESARCHIVOS_INSERTAR(
    p_ruta_archivo VARCHAR(255),
    p_fecha_creado DATE,
    p_id_mensaje_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO MENSAJESARCHIVOS_TB (RUTA_ARCHIVO, FECHA_CREADO, ID_MENSAJE_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_ruta_archivo, p_fecha_creado, p_id_mensaje_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- MENSAJESARCHIVOS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJESARCHIVOS_MODIFICAR$$

CREATE PROCEDURE MENSAJESARCHIVOS_MODIFICAR(
    p_id_archivo INT,
    p_ruta_archivo VARCHAR(255),
    p_fecha_creado DATE,
    p_id_mensaje_fk INT
)
BEGIN
    UPDATE MENSAJESARCHIVOS_TB
      SET RUTA_ARCHIVO = p_ruta_archivo, FECHA_CREADO = p_fecha_creado, ID_MENSAJE_FK = p_id_mensaje_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_ARCHIVO = p_id_archivo;
END$$


-- ---------------------------------------------------------
-- MENSAJESARCHIVOS_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS MENSAJESARCHIVOS_CAMBIAR_ESTADO$$

CREATE PROCEDURE MENSAJESARCHIVOS_CAMBIAR_ESTADO(
    p_id_archivo INT,
    p_id_estado INT
)
BEGIN
    UPDATE MENSAJESARCHIVOS_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_ARCHIVO = p_id_archivo;
END$$


-- ---------------------------------------------------------
-- CATEGORIATICKET_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CATEGORIATICKET_INSERTAR$$

CREATE PROCEDURE CATEGORIATICKET_INSERTAR(
    p_nombre VARCHAR(255),
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO CATEGORIATICKET_TB (NOMBRE, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_nombre, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- CATEGORIATICKET_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CATEGORIATICKET_MODIFICAR$$

CREATE PROCEDURE CATEGORIATICKET_MODIFICAR(
    p_id_categoria INT,
    p_nombre VARCHAR(255)
)
BEGIN
    UPDATE CATEGORIATICKET_TB
      SET NOMBRE = p_nombre, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_CATEGORIA = p_id_categoria;
END$$


-- ---------------------------------------------------------
-- CATEGORIATICKET_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS CATEGORIATICKET_CAMBIAR_ESTADO$$

CREATE PROCEDURE CATEGORIATICKET_CAMBIAR_ESTADO(
    p_id_categoria INT,
    p_id_estado INT
)
BEGIN
    UPDATE CATEGORIATICKET_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_CATEGORIA = p_id_categoria;
END$$


-- ---------------------------------------------------------
-- TICKET_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TICKET_INSERTAR$$

CREATE PROCEDURE TICKET_INSERTAR(
    p_titulo VARCHAR(255),
    p_comentario VARCHAR(255),
    p_fecha_creacion_ticket DATE,
    p_fecha_cierre DATE,
    p_prioridad VARCHAR(255),
    p_id_usuario_reporta_fk INT,
    p_id_usuario_tecnico_fk INT,
    p_id_categoria_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO TICKET_TB (TITULO, COMENTARIO, FECHA_CREACION_TICKET, FECHA_CIERRE, PRIORIDAD, ID_USUARIO_REPORTA_FK, ID_USUARIO_TECNICO_FK, ID_CATEGORIA_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_titulo, p_comentario, p_fecha_creacion_ticket, p_fecha_cierre, p_prioridad, p_id_usuario_reporta_fk, p_id_usuario_tecnico_fk, p_id_categoria_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- TICKET_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TICKET_MODIFICAR$$

CREATE PROCEDURE TICKET_MODIFICAR(
    p_id_ticket INT,
    p_titulo VARCHAR(255),
    p_comentario VARCHAR(255),
    p_fecha_creacion_ticket DATE,
    p_fecha_cierre DATE,
    p_prioridad VARCHAR(255),
    p_id_usuario_reporta_fk INT,
    p_id_usuario_tecnico_fk INT,
    p_id_categoria_fk INT
)
BEGIN
    UPDATE TICKET_TB
      SET TITULO = p_titulo, COMENTARIO = p_comentario, FECHA_CREACION_TICKET = p_fecha_creacion_ticket, FECHA_CIERRE = p_fecha_cierre, PRIORIDAD = p_prioridad, ID_USUARIO_REPORTA_FK = p_id_usuario_reporta_fk, ID_USUARIO_TECNICO_FK = p_id_usuario_tecnico_fk, ID_CATEGORIA_FK = p_id_categoria_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_TICKET = p_id_ticket;
END$$


-- ---------------------------------------------------------
-- TICKET_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS TICKET_CAMBIAR_ESTADO$$

CREATE PROCEDURE TICKET_CAMBIAR_ESTADO(
    p_id_ticket INT,
    p_id_estado INT
)
BEGIN
    UPDATE TICKET_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_TICKET = p_id_ticket;
END$$


-- ---------------------------------------------------------
-- ADJUNTOTICKET_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ADJUNTOTICKET_INSERTAR$$

CREATE PROCEDURE ADJUNTOTICKET_INSERTAR(
    p_ruta_archivo VARCHAR(255),
    p_id_usuario_subido_por_fk INT,
    p_id_ticket_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO ADJUNTOTICKET_TB (RUTA_ARCHIVO, ID_USUARIO_SUBIDO_POR_FK, ID_TICKET_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_ruta_archivo, p_id_usuario_subido_por_fk, p_id_ticket_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- ADJUNTOTICKET_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ADJUNTOTICKET_MODIFICAR$$

CREATE PROCEDURE ADJUNTOTICKET_MODIFICAR(
    p_id_adjuntoticket INT,
    p_ruta_archivo VARCHAR(255),
    p_id_usuario_subido_por_fk INT,
    p_id_ticket_fk INT
)
BEGIN
    UPDATE ADJUNTOTICKET_TB
      SET RUTA_ARCHIVO = p_ruta_archivo, ID_USUARIO_SUBIDO_POR_FK = p_id_usuario_subido_por_fk, ID_TICKET_FK = p_id_ticket_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_ADJUNTOTICKET = p_id_adjuntoticket;
END$$


-- ---------------------------------------------------------
-- ADJUNTOTICKET_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS ADJUNTOTICKET_CAMBIAR_ESTADO$$

CREATE PROCEDURE ADJUNTOTICKET_CAMBIAR_ESTADO(
    p_id_adjuntoticket INT,
    p_id_estado INT
)
BEGIN
    UPDATE ADJUNTOTICKET_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_ADJUNTOTICKET = p_id_adjuntoticket;
END$$


-- ---------------------------------------------------------
-- EVALUACIONSOPORTE_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACIONSOPORTE_INSERTAR$$

CREATE PROCEDURE EVALUACIONSOPORTE_INSERTAR(
    p_puntuacion INT,
    p_comentario VARCHAR(255),
    p_fecha_evaluacion DATE,
    p_id_usuario_fk INT,
    p_id_ticket_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO EVALUACIONSOPORTE_TB (PUNTUACION, COMENTARIO, FECHA_EVALUACION, ID_USUARIO_FK, ID_TICKET_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_puntuacion, p_comentario, p_fecha_evaluacion, p_id_usuario_fk, p_id_ticket_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- EVALUACIONSOPORTE_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACIONSOPORTE_MODIFICAR$$

CREATE PROCEDURE EVALUACIONSOPORTE_MODIFICAR(
    p_id_evaluacionsoporte INT,
    p_puntuacion INT,
    p_comentario VARCHAR(255),
    p_fecha_evaluacion DATE,
    p_id_usuario_fk INT,
    p_id_ticket_fk INT
)
BEGIN
    UPDATE EVALUACIONSOPORTE_TB
      SET PUNTUACION = p_puntuacion, COMENTARIO = p_comentario, FECHA_EVALUACION = p_fecha_evaluacion, ID_USUARIO_FK = p_id_usuario_fk, ID_TICKET_FK = p_id_ticket_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_EVALUACIONSOPORTE = p_id_evaluacionsoporte;
END$$


-- ---------------------------------------------------------
-- EVALUACIONSOPORTE_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS EVALUACIONSOPORTE_CAMBIAR_ESTADO$$

CREATE PROCEDURE EVALUACIONSOPORTE_CAMBIAR_ESTADO(
    p_id_evaluacionsoporte INT,
    p_id_estado INT
)
BEGIN
    UPDATE EVALUACIONSOPORTE_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_EVALUACIONSOPORTE = p_id_evaluacionsoporte;
END$$


-- ---------------------------------------------------------
-- REPORTESACADEMICOS_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESACADEMICOS_INSERTAR$$

CREATE PROCEDURE REPORTESACADEMICOS_INSERTAR(
    p_fecha_creacion_reporte DATE,
    p_promedio_ponderado INT,
    p_id_tiporeporte_fk INT,
    p_id_generado_por_fk INT,
    p_id_matricula_fk INT,
    p_id_periodo_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO REPORTESACADEMICOS_TB (FECHA_CREACION_REPORTE, PROMEDIO_PONDERADO, ID_TIPOREPORTE_FK, ID_GENERADO_POR_FK, ID_MATRICULA_FK, ID_PERIODO_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_fecha_creacion_reporte, p_promedio_ponderado, p_id_tiporeporte_fk, p_id_generado_por_fk, p_id_matricula_fk, p_id_periodo_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- REPORTESACADEMICOS_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESACADEMICOS_MODIFICAR$$

CREATE PROCEDURE REPORTESACADEMICOS_MODIFICAR(
    p_id_reporte INT,
    p_fecha_creacion_reporte DATE,
    p_promedio_ponderado INT,
    p_id_tiporeporte_fk INT,
    p_id_generado_por_fk INT,
    p_id_matricula_fk INT,
    p_id_periodo_fk INT
)
BEGIN
    UPDATE REPORTESACADEMICOS_TB
      SET FECHA_CREACION_REPORTE = p_fecha_creacion_reporte, PROMEDIO_PONDERADO = p_promedio_ponderado, ID_TIPOREPORTE_FK = p_id_tiporeporte_fk, ID_GENERADO_POR_FK = p_id_generado_por_fk, ID_MATRICULA_FK = p_id_matricula_fk, ID_PERIODO_FK = p_id_periodo_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_REPORTE = p_id_reporte;
END$$


-- ---------------------------------------------------------
-- REPORTESACADEMICOS_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESACADEMICOS_CAMBIAR_ESTADO$$

CREATE PROCEDURE REPORTESACADEMICOS_CAMBIAR_ESTADO(
    p_id_reporte INT,
    p_id_estado INT
)
BEGIN
    UPDATE REPORTESACADEMICOS_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_REPORTE = p_id_reporte;
END$$


-- ---------------------------------------------------------
-- REPORTESLOG_INSERTAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESLOG_INSERTAR$$

CREATE PROCEDURE REPORTESLOG_INSERTAR(
    p_formato VARCHAR(255),
    p_ruta_archivo VARCHAR(255),
    p_enviadoa VARCHAR(255),
    p_enviadopor VARCHAR(255),
    p_fecha_exportacion DATE,
    p_fecha_envio DATE,
    p_id_reporte_fk INT,
    p_id_generado_por_fk INT,
    p_id_estado_fk INT
)
BEGIN
    INSERT INTO REPORTESLOG_TB (FORMATO, RUTA_ARCHIVO, ENVIADOA, ENVIADOPOR, FECHA_EXPORTACION, FECHA_ENVIO, ID_REPORTE_FK, ID_GENERADO_POR_FK, ID_ESTADO_FK, FECHA_CREACION, CREADO_POR, ACCION)
      VALUES (p_formato, p_ruta_archivo, p_enviadoa, p_enviadopor, p_fecha_exportacion, p_fecha_envio, p_id_reporte_fk, p_id_generado_por_fk, p_id_estado_fk, NOW(), USER(), 'INSERT');
END$$


-- ---------------------------------------------------------
-- REPORTESLOG_MODIFICAR
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESLOG_MODIFICAR$$

CREATE PROCEDURE REPORTESLOG_MODIFICAR(
    p_id_reportelog INT,
    p_formato VARCHAR(255),
    p_ruta_archivo VARCHAR(255),
    p_enviadoa VARCHAR(255),
    p_enviadopor VARCHAR(255),
    p_fecha_exportacion DATE,
    p_fecha_envio DATE,
    p_id_reporte_fk INT,
    p_id_generado_por_fk INT
)
BEGIN
    UPDATE REPORTESLOG_TB
      SET FORMATO = p_formato, RUTA_ARCHIVO = p_ruta_archivo, ENVIADOA = p_enviadoa, ENVIADOPOR = p_enviadopor, FECHA_EXPORTACION = p_fecha_exportacion, FECHA_ENVIO = p_fecha_envio, ID_REPORTE_FK = p_id_reporte_fk, ID_GENERADO_POR_FK = p_id_generado_por_fk, FECHA_MODIFICACION = NOW(), MODIFICADO_POR = USER(), ACCION = 'UPDATE'
      WHERE ID_REPORTELOG = p_id_reportelog;
END$$


-- ---------------------------------------------------------
-- REPORTESLOG_CAMBIAR_ESTADO
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS REPORTESLOG_CAMBIAR_ESTADO$$

CREATE PROCEDURE REPORTESLOG_CAMBIAR_ESTADO(
    p_id_reportelog INT,
    p_id_estado INT
)
BEGIN
    UPDATE REPORTESLOG_TB
      SET ID_ESTADO_FK = p_id_estado,
          FECHA_MODIFICACION = NOW(),
          MODIFICADO_POR = USER(),
          ACCION = 'CAMBIO_ESTADO'
      WHERE ID_REPORTELOG = p_id_reportelog;
END$$


-- =========================================================
-- PROCEDIMIENTOS DE AUTENTICACIÓN
-- =========================================================

-- ---------------------------------------------------------
-- AUTH_GET_USER_BY_EMAIL
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AUTH_GET_USER_BY_EMAIL$$

CREATE PROCEDURE AUTH_GET_USER_BY_EMAIL(
    p_email VARCHAR(255),
    OUT p_username VARCHAR(255),
    OUT p_password_hash VARCHAR(255),
    OUT p_role VARCHAR(255),
    OUT p_intentos INT,
    OUT p_bloqueado_hasta DATETIME,
    OUT p_enabled INT,
    OUT p_id_credencial INT
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
        c.ID_CREDENCIAL
    INTO
        p_username,
        p_password_hash,
        p_role,
        p_intentos,
        p_bloqueado_hasta,
        p_enabled,
        p_id_credencial
    FROM CORREO_TB co
    JOIN CREDENCIALES_TB c ON c.ID_CORREO_FK = co.ID_CORREO
    JOIN USUARIOS_TB u ON u.ID_USUARIO = c.ID_USUARIO_FK
    JOIN TIPOUSUARIO_TB tu ON tu.ID_TIPOUSUARIO = u.ID_TIPOUSUARIO_FK
    WHERE LOWER(co.CORREO) = LOWER(p_email)
      AND co.ES_LOGIN = 'S';
END$$


-- ---------------------------------------------------------
-- AUTH_LOGIN_SUCCESS
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AUTH_LOGIN_SUCCESS$$

CREATE PROCEDURE AUTH_LOGIN_SUCCESS(
    p_id_credencial INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    UPDATE CREDENCIALES_TB
    SET
        ULTIMO_LOGIN = NOW(),
        INTENTOS_FALLIDOS = 0,
        BLOQUEADO_HASTA = NULL,
        FECHA_MODIFICACION = NOW(),
        MODIFICADO_POR = USER(),
        ACCION = 'LOGIN_OK'
    WHERE ID_CREDENCIAL = p_id_credencial;
    
    COMMIT;
END$$


-- ---------------------------------------------------------
-- AUTH_LOGIN_FAIL
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS AUTH_LOGIN_FAIL$$

CREATE PROCEDURE AUTH_LOGIN_FAIL(
    p_id_credencial INT,
    p_max_intentos INT,
    p_minutos_bloqueo INT
)
BEGIN
    DECLARE v_intentos INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Incrementar intentos fallidos
    UPDATE CREDENCIALES_TB
    SET
        INTENTOS_FALLIDOS = INTENTOS_FALLIDOS + 1,
        FECHA_MODIFICACION = NOW(),
        MODIFICADO_POR = USER(),
        ACCION = 'LOGIN_FAIL'
    WHERE ID_CREDENCIAL = p_id_credencial;
    
    -- Obtener intentos actuales
    SELECT INTENTOS_FALLIDOS INTO v_intentos
    FROM CREDENCIALES_TB
    WHERE ID_CREDENCIAL = p_id_credencial;
    
    -- Si excede el máximo, bloquear
    IF v_intentos >= p_max_intentos THEN
        UPDATE CREDENCIALES_TB
        SET
            BLOQUEADO_HASTA = DATE_ADD(NOW(), INTERVAL p_minutos_bloqueo MINUTE),
            FECHA_MODIFICACION = NOW(),
            MODIFICADO_POR = USER(),
            ACCION = 'LOGIN_LOCK'
        WHERE ID_CREDENCIAL = p_id_credencial;
    END IF;
    
    COMMIT;
END$$


DELIMITER ;

-- =========================================================
-- VERIFICACIÓN
-- =========================================================
SELECT COUNT(*) AS total_procedimientos 
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'PORTAL_ESCOLAR' 
  AND ROUTINE_TYPE = 'PROCEDURE';
-- Debería retornar: 110
--
-- =========================================================