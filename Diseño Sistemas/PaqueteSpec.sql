create or replace PACKAGE PORTAL_ESCOLAR_PKG AS

 -- ESTADOS_TB
  PROCEDURE ESTADOS_INSERTAR (
      p_descripcion IN VARCHAR2
  );

  PROCEDURE ESTADOS_MODIFICAR (
      p_id_estado IN NUMBER,
      p_descripcion IN VARCHAR2
  );

  -- TIPOUSUARIO_TB
  PROCEDURE TIPOUSUARIO_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TIPOUSUARIO_MODIFICAR (
      p_id_tipousuario IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE TIPOUSUARIO_CAMBIAR_ESTADO (
      p_id_tipousuario IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- TIPOVISIBILIDAD_TB
  PROCEDURE TIPOVISIBILIDAD_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TIPOVISIBILIDAD_MODIFICAR (
      p_id_tipovisibilidad IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE TIPOVISIBILIDAD_CAMBIAR_ESTADO (
      p_id_tipovisibilidad IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- TIPOREPORTE_TB
  PROCEDURE TIPOREPORTE_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TIPOREPORTE_MODIFICAR (
      p_id_tiporeporte IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE TIPOREPORTE_CAMBIAR_ESTADO (
      p_id_tiporeporte IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- TIPOCONVERSACION_TB
  PROCEDURE TIPOCONVERSACION_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TIPOCONVERSACION_MODIFICAR (
      p_id_tipoconversacion IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE TIPOCONVERSACION_CAMBIAR_ESTADO (
      p_id_tipoconversacion IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- PROVINCIA_TB
  PROCEDURE PROVINCIA_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE PROVINCIA_MODIFICAR (
      p_id_provincia IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE PROVINCIA_CAMBIAR_ESTADO (
      p_id_provincia IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CANTON_TB
  PROCEDURE CANTON_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_provincia_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE CANTON_MODIFICAR (
      p_id_canton IN NUMBER,
      p_nombre IN VARCHAR2,
      p_id_provincia_fk IN NUMBER
  );

  PROCEDURE CANTON_CAMBIAR_ESTADO (
      p_id_canton IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- DISTRITO_TB
  PROCEDURE DISTRITO_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_canton_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE DISTRITO_MODIFICAR (
      p_id_distrito IN NUMBER,
      p_nombre IN VARCHAR2,
      p_id_canton_fk IN NUMBER
  );

  PROCEDURE DISTRITO_CAMBIAR_ESTADO (
      p_id_distrito IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- USUARIOS_TB
  PROCEDURE USUARIOS_INSERTAR (
      p_nombre IN VARCHAR2,
      p_primer_apellido IN VARCHAR2,
      p_segundo_apellido IN VARCHAR2,
      p_id_tipousuario_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE USUARIOS_MODIFICAR (
      p_id_usuario IN NUMBER,
      p_nombre IN VARCHAR2,
      p_primer_apellido IN VARCHAR2,
      p_segundo_apellido IN VARCHAR2,
      p_id_tipousuario_fk IN NUMBER
  );

  PROCEDURE USUARIOS_CAMBIAR_ESTADO (
      p_id_usuario IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- TELEFONO_TB
  PROCEDURE TELEFONO_INSERTAR (
      p_numero IN VARCHAR2,
      p_id_usuario_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TELEFONO_MODIFICAR (
      p_id_telefono IN NUMBER,
      p_numero IN VARCHAR2,
      p_id_usuario_fk IN NUMBER
  );

  PROCEDURE TELEFONO_CAMBIAR_ESTADO (
      p_id_telefono IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CORREO_TB
  PROCEDURE CORREO_INSERTAR (
      p_correo IN VARCHAR2,
      p_es_login IN CHAR,
      p_id_usuario_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE CORREO_MODIFICAR (
      p_id_correo IN NUMBER,
      p_correo IN VARCHAR2,
      p_es_login IN CHAR,
      p_id_usuario_fk IN NUMBER
  );

  PROCEDURE CORREO_CAMBIAR_ESTADO (
      p_id_correo IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- DIRECCION_TB
  PROCEDURE DIRECCION_INSERTAR (
      p_otras_senas IN VARCHAR2,
      p_id_usuario_fk IN NUMBER,
      p_id_provincia_fk IN NUMBER,
      p_id_canton_fk IN NUMBER,
      p_id_distrito_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE DIRECCION_MODIFICAR (
      p_id_direccion IN NUMBER,
      p_otras_senas IN VARCHAR2,
      p_id_usuario_fk IN NUMBER,
      p_id_provincia_fk IN NUMBER,
      p_id_canton_fk IN NUMBER,
      p_id_distrito_fk IN NUMBER
  );

  PROCEDURE DIRECCION_CAMBIAR_ESTADO (
      p_id_direccion IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- ENCARGADOESTUDIANTE_TB
  PROCEDURE ENCARGADOESTUDIANTE_INSERTAR (
      p_parentesco IN VARCHAR2,
      p_id_usuario_estudiante_fk IN NUMBER,
      p_id_usuario_encargado_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE ENCARGADOESTUDIANTE_MODIFICAR (
      p_id_encargadoestudiante IN NUMBER,
      p_parentesco IN VARCHAR2,
      p_id_usuario_estudiante_fk IN NUMBER,
      p_id_usuario_encargado_fk IN NUMBER
  );

  PROCEDURE ENCARGADOESTUDIANTE_CAMBIAR_ESTADO (
      p_id_encargadoestudiante IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CREDENCIALES_TB
  PROCEDURE CREDENCIALES_INSERTAR (
    p_password_hash     IN VARCHAR2,
    p_ultimo_login      IN TIMESTAMP,
    p_intentos_fallidos IN NUMBER,
    p_bloqueado_hasta   IN TIMESTAMP,
    p_id_usuario_fk     IN NUMBER,
    p_id_correo_fk      IN NUMBER,
    p_id_estado_fk      IN NUMBER
);

PROCEDURE CREDENCIALES_MODIFICAR (
    p_id_credencial     IN NUMBER,
    p_password_hash     IN VARCHAR2,
    p_ultimo_login      IN TIMESTAMP,
    p_intentos_fallidos IN NUMBER,
    p_bloqueado_hasta   IN TIMESTAMP,
    p_id_usuario_fk     IN NUMBER,
    p_id_correo_fk      IN NUMBER
);


  PROCEDURE CREDENCIALES_CAMBIAR_ESTADO (
      p_id_credencial IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- RESET_PASSWORD_TB
  PROCEDURE RESET_PASSWORD_INSERTAR (
      p_token IN VARCHAR2,
      p_fecha_creacion_token IN TIMESTAMP,
      p_fecha_expiracion IN TIMESTAMP,
      p_fecha_uso IN TIMESTAMP,
      p_id_credencial_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE RESET_PASSWORD_MODIFICAR (
      p_id_reset IN NUMBER,
      p_token IN VARCHAR2,
      p_fecha_creacion_token IN TIMESTAMP,
      p_fecha_expiracion IN TIMESTAMP,
      p_fecha_uso IN TIMESTAMP,
      p_id_credencial_fk IN NUMBER
  );

  PROCEDURE RESET_PASSWORD_CAMBIAR_ESTADO (
      p_id_reset IN NUMBER,
      p_id_estado IN NUMBER
  );

 -- RESET_PASSWORD_PROCEDIMIENTOS_AUTH
PROCEDURE AUTH_REQUEST_RESET (
    p_email IN VARCHAR2,
    p_token OUT VARCHAR2,
    p_id_credencial OUT NUMBER,
    p_exists OUT NUMBER
);

PROCEDURE AUTH_VALIDATE_TOKEN (
    p_token IN VARCHAR2,
    p_valid OUT NUMBER,
    p_id_credencial OUT NUMBER,
    p_email OUT VARCHAR2
);

PROCEDURE AUTH_RESET_PASSWORD (
    p_token IN VARCHAR2,
    p_new_password_hash IN VARCHAR2,
    p_success OUT NUMBER
);

  -- PERIODOS_TB
  PROCEDURE PERIODOS_INSERTAR (
      p_nombre IN VARCHAR2,
      p_fecha_inicio IN DATE,
      p_fecha_fin IN DATE,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE PERIODOS_MODIFICAR (
      p_id_periodo IN NUMBER,
      p_nombre IN VARCHAR2,
      p_fecha_inicio IN DATE,
      p_fecha_fin IN DATE
  );

  PROCEDURE PERIODOS_CAMBIAR_ESTADO (
      p_id_periodo IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- MATERIA_TB
  PROCEDURE MATERIA_INSERTAR (
      p_nombre IN VARCHAR2,
      p_codigo IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE MATERIA_MODIFICAR (
      p_id_materia IN NUMBER,
      p_nombre IN VARCHAR2,
      p_codigo IN VARCHAR2
  );

  PROCEDURE MATERIA_CAMBIAR_ESTADO (
      p_id_materia IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- SECCION_TB
  PROCEDURE SECCION_INSERTAR (
      p_numero IN VARCHAR2,
      p_id_periodo_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE SECCION_MODIFICAR (
      p_id_seccion IN NUMBER,
      p_numero IN VARCHAR2,
      p_id_periodo_fk IN NUMBER
  );

  PROCEDURE SECCION_CAMBIAR_ESTADO (
      p_id_seccion IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- AULA_TB
  PROCEDURE AULA_INSERTAR (
      p_numero IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE AULA_MODIFICAR (
      p_id_aula IN NUMBER,
      p_numero IN VARCHAR2
  );

  PROCEDURE AULA_CAMBIAR_ESTADO (
      p_id_aula IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- SECCIONMATERIA_TB
  PROCEDURE SECCIONMATERIA_INSERTAR (
      p_id_seccion_fk IN NUMBER,
      p_id_materia_fk IN NUMBER,
      p_id_usuario_docente_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE SECCIONMATERIA_MODIFICAR (
      p_id_seccionmateria IN NUMBER,
      p_id_seccion_fk IN NUMBER,
      p_id_materia_fk IN NUMBER,
      p_id_usuario_docente_fk IN NUMBER
  );

  PROCEDURE SECCIONMATERIA_CAMBIAR_ESTADO (
      p_id_seccionmateria IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- MATRICULA_TB
  PROCEDURE MATRICULA_INSERTAR (
      p_fecha_matricula IN DATE,
      p_id_usuario_estudiante_fk IN NUMBER,
      p_id_seccion_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE MATRICULA_MODIFICAR (
      p_id_matricula IN NUMBER,
      p_fecha_matricula IN DATE,
      p_id_usuario_estudiante_fk IN NUMBER,
      p_id_seccion_fk IN NUMBER
  );

  PROCEDURE MATRICULA_CAMBIAR_ESTADO (
      p_id_matricula IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- HORARIO_TB
  PROCEDURE HORARIO_INSERTAR (
      p_dia_semana IN NUMBER,
      p_hora_inicio IN VARCHAR2,
      p_hora_fin IN VARCHAR2,
      p_id_aula_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE HORARIO_MODIFICAR (
      p_id_horario IN NUMBER,
      p_dia_semana IN NUMBER,
      p_hora_inicio IN VARCHAR2,
      p_hora_fin IN VARCHAR2,
      p_id_aula_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER
  );

  PROCEDURE HORARIO_CAMBIAR_ESTADO (
      p_id_horario IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- ASISTENCIAS_TB
  PROCEDURE ASISTENCIAS_INSERTAR (
      p_fecha_asistencia IN DATE,
      p_id_matricula_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE ASISTENCIAS_MODIFICAR (
      p_id_asistencia IN NUMBER,
      p_fecha_asistencia IN DATE,
      p_id_matricula_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER
  );

  PROCEDURE ASISTENCIAS_CAMBIAR_ESTADO (
      p_id_asistencia IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- JUSTIFICANTEASISTENCIA_TB
  PROCEDURE JUSTIFICANTEASISTENCIA_INSERTAR (
      p_archivo_justificante IN VARCHAR2,
      p_tipo IN VARCHAR2,
      p_fecha_subida IN DATE,
      p_id_asistencia_fk IN NUMBER,
      p_id_matricula_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE JUSTIFICANTEASISTENCIA_MODIFICAR (
      p_id_justificante IN NUMBER,
      p_archivo_justificante IN VARCHAR2,
      p_tipo IN VARCHAR2,
      p_fecha_subida IN DATE,
      p_id_asistencia_fk IN NUMBER,
      p_id_matricula_fk IN NUMBER
  );

  PROCEDURE JUSTIFICANTEASISTENCIA_CAMBIAR_ESTADO (
      p_id_justificante IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- EVALUACION_TB
  PROCEDURE EVALUACION_INSERTAR (
      p_tipo IN VARCHAR2,
      p_porcentaje IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_periodo_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE EVALUACION_MODIFICAR (
      p_id_evaluacion IN NUMBER,
      p_tipo IN VARCHAR2,
      p_porcentaje IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_periodo_fk IN NUMBER
  );

  PROCEDURE EVALUACION_CAMBIAR_ESTADO (
      p_id_evaluacion IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CALIFICACIONES_TB
  PROCEDURE CALIFICACIONES_INSERTAR (
      p_calificacion IN NUMBER,
      p_id_matricula_fk IN NUMBER,
      p_id_evaluacion_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE CALIFICACIONES_MODIFICAR (
      p_id_calificaciones IN NUMBER,
      p_calificacion IN NUMBER,
      p_id_matricula_fk IN NUMBER,
      p_id_evaluacion_fk IN NUMBER
  );

  PROCEDURE CALIFICACIONES_CAMBIAR_ESTADO (
      p_id_calificaciones IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- EVENTO_TB
  PROCEDURE EVENTO_INSERTAR (
      p_titulo IN VARCHAR2,
      p_descripcion IN VARCHAR2,
      p_fecha_inicio IN DATE,
      p_fecha_fin IN DATE,
      p_tipo_evento IN VARCHAR2,
      p_id_tipovisibilidad_fk IN NUMBER,
      p_id_seccion_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_usuario_creado_por_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE EVENTO_MODIFICAR (
      p_id_evento IN NUMBER,
      p_titulo IN VARCHAR2,
      p_descripcion IN VARCHAR2,
      p_fecha_inicio IN DATE,
      p_fecha_fin IN DATE,
      p_tipo_evento IN VARCHAR2,
      p_id_tipovisibilidad_fk IN NUMBER,
      p_id_seccion_fk IN NUMBER,
      p_id_seccionmateria_fk IN NUMBER,
      p_id_usuario_creado_por_fk IN NUMBER
  );

  PROCEDURE EVENTO_CAMBIAR_ESTADO (
      p_id_evento IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CONVERSACION_TB
  PROCEDURE CONVERSACION_INSERTAR (
      p_titulo IN VARCHAR2,
      p_fecha_creacion_conv IN DATE,
      p_fecha_actualizacion IN DATE,
      p_ultimo_mensaje IN VARCHAR2,
      p_id_tipoconversacion_fk IN NUMBER,
      p_id_creado_por_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE CONVERSACION_MODIFICAR (
      p_id_conversacion IN NUMBER,
      p_titulo IN VARCHAR2,
      p_fecha_creacion_conv IN DATE,
      p_fecha_actualizacion IN DATE,
      p_ultimo_mensaje IN VARCHAR2,
      p_id_tipoconversacion_fk IN NUMBER,
      p_id_creado_por_fk IN NUMBER
  );

  PROCEDURE CONVERSACION_CAMBIAR_ESTADO (
      p_id_conversacion IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- MENSAJES_TB
  PROCEDURE MENSAJES_INSERTAR (
      p_fecha_envio IN DATE,
      p_fecha_editado IN DATE,
      p_asunto IN VARCHAR2,
      p_contenido IN VARCHAR2,
      p_id_conversacion_fk IN NUMBER,
      p_id_emisor_fk IN NUMBER,
      p_id_remitente_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE MENSAJES_MODIFICAR (
      p_id_mensaje IN NUMBER,
      p_fecha_envio IN DATE,
      p_fecha_editado IN DATE,
      p_asunto IN VARCHAR2,
      p_contenido IN VARCHAR2,
      p_id_conversacion_fk IN NUMBER,
      p_id_emisor_fk IN NUMBER,
      p_id_remitente_fk IN NUMBER
  );

  PROCEDURE MENSAJES_CAMBIAR_ESTADO (
      p_id_mensaje IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- MENSAJESARCHIVOS_TB
  PROCEDURE MENSAJESARCHIVOS_INSERTAR (
      p_ruta_archivo IN VARCHAR2,
      p_fecha_creado IN DATE,
      p_id_mensaje_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE MENSAJESARCHIVOS_MODIFICAR (
      p_id_archivo IN NUMBER,
      p_ruta_archivo IN VARCHAR2,
      p_fecha_creado IN DATE,
      p_id_mensaje_fk IN NUMBER
  );

  PROCEDURE MENSAJESARCHIVOS_CAMBIAR_ESTADO (
      p_id_archivo IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- CATEGORIATICKET_TB
  PROCEDURE CATEGORIATICKET_INSERTAR (
      p_nombre IN VARCHAR2,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE CATEGORIATICKET_MODIFICAR (
      p_id_categoria IN NUMBER,
      p_nombre IN VARCHAR2
  );

  PROCEDURE CATEGORIATICKET_CAMBIAR_ESTADO (
      p_id_categoria IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- TICKET_TB
  PROCEDURE TICKET_INSERTAR (
      p_titulo IN VARCHAR2,
      p_comentario IN VARCHAR2,
      p_fecha_creacion_ticket IN DATE,
      p_fecha_cierre IN DATE,
      p_prioridad IN VARCHAR2,
      p_id_usuario_reporta_fk IN NUMBER,
      p_id_usuario_tecnico_fk IN NUMBER,
      p_id_categoria_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE TICKET_MODIFICAR (
      p_id_ticket IN NUMBER,
      p_titulo IN VARCHAR2,
      p_comentario IN VARCHAR2,
      p_fecha_creacion_ticket IN DATE,
      p_fecha_cierre IN DATE,
      p_prioridad IN VARCHAR2,
      p_id_usuario_reporta_fk IN NUMBER,
      p_id_usuario_tecnico_fk IN NUMBER,
      p_id_categoria_fk IN NUMBER
  );

  PROCEDURE TICKET_CAMBIAR_ESTADO (
      p_id_ticket IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- ADJUNTOTICKET_TB
  PROCEDURE ADJUNTOTICKET_INSERTAR (
      p_ruta_archivo IN VARCHAR2,
      p_id_usuario_subido_por_fk IN NUMBER,
      p_id_ticket_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE ADJUNTOTICKET_MODIFICAR (
      p_id_adjuntoticket IN NUMBER,
      p_ruta_archivo IN VARCHAR2,
      p_id_usuario_subido_por_fk IN NUMBER,
      p_id_ticket_fk IN NUMBER
  );

  PROCEDURE ADJUNTOTICKET_CAMBIAR_ESTADO (
      p_id_adjuntoticket IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- EVALUACIONSOPORTE_TB
  PROCEDURE EVALUACIONSOPORTE_INSERTAR (
      p_puntuacion IN NUMBER,
      p_comentario IN VARCHAR2,
      p_fecha_evaluacion IN DATE,
      p_id_usuario_fk IN NUMBER,
      p_id_ticket_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE EVALUACIONSOPORTE_MODIFICAR (
      p_id_evaluacionsoporte IN NUMBER,
      p_puntuacion IN NUMBER,
      p_comentario IN VARCHAR2,
      p_fecha_evaluacion IN DATE,
      p_id_usuario_fk IN NUMBER,
      p_id_ticket_fk IN NUMBER
  );

  PROCEDURE EVALUACIONSOPORTE_CAMBIAR_ESTADO (
      p_id_evaluacionsoporte IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- REPORTESACADEMICOS_TB
  PROCEDURE REPORTESACADEMICOS_INSERTAR (
      p_fecha_creacion_reporte IN DATE,
      p_promedio_ponderado IN NUMBER,
      p_id_tiporeporte_fk IN NUMBER,
      p_id_generado_por_fk IN NUMBER,
      p_id_matricula_fk IN NUMBER,
      p_id_periodo_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE REPORTESACADEMICOS_MODIFICAR (
      p_id_reporte IN NUMBER,
      p_fecha_creacion_reporte IN DATE,
      p_promedio_ponderado IN NUMBER,
      p_id_tiporeporte_fk IN NUMBER,
      p_id_generado_por_fk IN NUMBER,
      p_id_matricula_fk IN NUMBER,
      p_id_periodo_fk IN NUMBER
  );

  PROCEDURE REPORTESACADEMICOS_CAMBIAR_ESTADO (
      p_id_reporte IN NUMBER,
      p_id_estado IN NUMBER
  );

  -- REPORTESLOG_TB
  PROCEDURE REPORTESLOG_INSERTAR (
      p_formato IN VARCHAR2,
      p_ruta_archivo IN VARCHAR2,
      p_enviadoa IN VARCHAR2,
      p_enviadopor IN VARCHAR2,
      p_fecha_exportacion IN DATE,
      p_fecha_envio IN DATE,
      p_id_reporte_fk IN NUMBER,
      p_id_generado_por_fk IN NUMBER,
      p_id_estado_fk IN NUMBER
  );

  PROCEDURE REPORTESLOG_MODIFICAR (
      p_id_reportelog IN NUMBER,
      p_formato IN VARCHAR2,
      p_ruta_archivo IN VARCHAR2,
      p_enviadoa IN VARCHAR2,
      p_enviadopor IN VARCHAR2,
      p_fecha_exportacion IN DATE,
      p_fecha_envio IN DATE,
      p_id_reporte_fk IN NUMBER,
      p_id_generado_por_fk IN NUMBER
  );

  PROCEDURE REPORTESLOG_CAMBIAR_ESTADO (
      p_id_reportelog IN NUMBER,
      p_id_estado IN NUMBER 
    );



END PORTAL_ESCOLAR_PKG;