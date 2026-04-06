package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class MensajeriaRepository {

    private final JdbcTemplate        jdbc;
    private final SimpleJdbcCall      conversacionInsertar;
    private final SimpleJdbcCall      conversacionModificar;
    private final SimpleJdbcCall      conversacionCambiarEstado;
    private final SimpleJdbcCall      mensajeInsertar;
    private final SimpleJdbcCall      mensajeCambiarEstado;
    private final SimpleJdbcCall      archivoInsertar;
    private final SimpleJdbcCall      archivoCambiarEstado;

    // Estados resueltos dinámicamente desde BD — funciona en cualquier ambiente
    private final int ESTADO_ACTIVO;
    private final int ESTADO_INACTIVO;
    private final int ESTADO_LEIDO;
    private final int ESTADO_NO_LEIDO;

    public MensajeriaRepository(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);

        // Resolver IDs por nombre
        this.ESTADO_ACTIVO    = estado("ACTIVO");
        this.ESTADO_INACTIVO  = estado("INACTIVO");
        this.ESTADO_LEIDO     = estado("LEIDO");
        this.ESTADO_NO_LEIDO  = estado("NO_LEIDO");

        this.conversacionInsertar = new SimpleJdbcCall(dataSource)
                .withProcedureName("CONVERSACION_INSERTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_titulo",                 Types.VARCHAR),
                        new SqlParameter("p_fecha_creacion_conv",    Types.DATE),
                        new SqlParameter("p_fecha_actualizacion",    Types.DATE),
                        new SqlParameter("p_ultimo_mensaje",         Types.VARCHAR),
                        new SqlParameter("p_id_tipoconversacion_fk", Types.INTEGER),
                        new SqlParameter("p_id_creado_por_fk",       Types.INTEGER),
                        new SqlParameter("p_id_estado_fk",           Types.INTEGER)
                );

        this.conversacionModificar = new SimpleJdbcCall(dataSource)
                .withProcedureName("CONVERSACION_MODIFICAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_conversacion",        Types.INTEGER),
                        new SqlParameter("p_titulo",                 Types.VARCHAR),
                        new SqlParameter("p_fecha_creacion_conv",    Types.DATE),
                        new SqlParameter("p_fecha_actualizacion",    Types.DATE),
                        new SqlParameter("p_ultimo_mensaje",         Types.VARCHAR),
                        new SqlParameter("p_id_tipoconversacion_fk", Types.INTEGER),
                        new SqlParameter("p_id_creado_por_fk",       Types.INTEGER)
                );

        this.conversacionCambiarEstado = new SimpleJdbcCall(dataSource)
                .withProcedureName("CONVERSACION_CAMBIAR_ESTADO")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_conversacion", Types.INTEGER),
                        new SqlParameter("p_id_estado",       Types.INTEGER)
                );

        this.mensajeInsertar = new SimpleJdbcCall(dataSource)
                .withProcedureName("MENSAJES_INSERTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_fecha_envio",         Types.DATE),
                        new SqlParameter("p_fecha_editado",       Types.DATE),
                        new SqlParameter("p_asunto",              Types.VARCHAR),
                        new SqlParameter("p_contenido",           Types.VARCHAR),
                        new SqlParameter("p_id_conversacion_fk",  Types.INTEGER),
                        new SqlParameter("p_id_emisor_fk",        Types.INTEGER),
                        new SqlParameter("p_id_remitente_fk",     Types.INTEGER),
                        new SqlParameter("p_id_estado_fk",        Types.INTEGER)
                );

        this.mensajeCambiarEstado = new SimpleJdbcCall(dataSource)
                .withProcedureName("MENSAJES_CAMBIAR_ESTADO")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_mensaje", Types.INTEGER),
                        new SqlParameter("p_id_estado",  Types.INTEGER)
                );

        this.archivoInsertar = new SimpleJdbcCall(dataSource)
                .withProcedureName("MENSAJESARCHIVOS_INSERTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_ruta_archivo",  Types.VARCHAR),
                        new SqlParameter("p_fecha_creado",  Types.DATE),
                        new SqlParameter("p_id_mensaje_fk", Types.INTEGER),
                        new SqlParameter("p_id_estado_fk",  Types.INTEGER)
                );

        this.archivoCambiarEstado = new SimpleJdbcCall(dataSource)
                .withProcedureName("MENSAJESARCHIVOS_CAMBIAR_ESTADO")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_archivo", Types.INTEGER),
                        new SqlParameter("p_id_estado",  Types.INTEGER)
                );
    }

    // Busca el ID de un estado por descripción en ESTADOS_TB
    private int estado(String nombre) {
        Integer id = jdbc.queryForObject(
                "SELECT ID_ESTADO FROM ESTADOS_TB WHERE DESCRIPCION = ? LIMIT 1",
                Integer.class, nombre);
        if (id == null) throw new IllegalStateException("Estado no encontrado en BD: " + nombre);
        return id;
    }

    // ------------------------------------------------------------------
    //  CONVERSACIONES
    // ------------------------------------------------------------------

    public List<Map<String, Object>> listarConversaciones(Long idUsuario) {
        String sql =
            "SELECT DISTINCT " +
            "    c.ID_CONVERSACION      AS idConversacion, " +
            "    c.TITULO               AS titulo, " +
            "    c.ULTIMO_MENSAJE       AS ultimoMensaje, " +
            "    c.FECHA_ACTUALIZACION  AS fechaActualizacion, " +
            "    tc.NOMBRE              AS tipo, " +
            "    CONCAT(uc.NOMBRE,' ',uc.PRIMER_APELLIDO) AS creador, " +
            "    c.ID_CREADO_POR_FK     AS idCreador, " +
            "    (SELECT COUNT(*) FROM MENSAJES_TB m2 " +
            "     WHERE m2.ID_CONVERSACION_FK = c.ID_CONVERSACION " +
            "       AND m2.ID_REMITENTE_FK = ? " +
            "       AND m2.ID_ESTADO_FK = " + ESTADO_NO_LEIDO + ") AS noLeidos " +
            "FROM CONVERSACION_TB c " +
            "JOIN TIPOCONVERSACION_TB tc ON tc.ID_TIPOCONVERSACION = c.ID_TIPOCONVERSACION_FK " +
            "JOIN USUARIOS_TB uc ON uc.ID_USUARIO = c.ID_CREADO_POR_FK " +
            "WHERE c.ID_ESTADO_FK = " + ESTADO_ACTIVO + " " +
            "  AND EXISTS ( " +
            "      SELECT 1 FROM MENSAJES_TB m " +
            "      WHERE m.ID_CONVERSACION_FK = c.ID_CONVERSACION " +
            "        AND (m.ID_EMISOR_FK = ? OR m.ID_REMITENTE_FK = ?) " +
            "        AND m.ID_ESTADO_FK IN (" + ESTADO_LEIDO + "," + ESTADO_NO_LEIDO + ") " +
            "  ) " +
            "ORDER BY c.FECHA_ACTUALIZACION DESC";
        return jdbc.queryForList(sql, idUsuario, idUsuario, idUsuario);
    }

    public Map<String, Object> obtenerConversacion(Long idConversacion) {
        return jdbc.queryForMap("""
                SELECT c.ID_CONVERSACION, c.TITULO, c.ULTIMO_MENSAJE,
                       c.FECHA_ACTUALIZACION, c.ID_TIPOCONVERSACION_FK,
                       c.ID_CREADO_POR_FK, tc.NOMBRE AS tipo
                FROM CONVERSACION_TB c
                JOIN TIPOCONVERSACION_TB tc ON tc.ID_TIPOCONVERSACION = c.ID_TIPOCONVERSACION_FK
                WHERE c.ID_CONVERSACION = ?
                """, idConversacion);
    }

    public Long insertarConversacion(String titulo, String ultimoMensaje,
                                     Integer idTipo, Long idCreador) {
        conversacionInsertar.execute(new MapSqlParameterSource()
                .addValue("p_titulo",                 titulo)
                .addValue("p_fecha_creacion_conv",    LocalDate.now())
                .addValue("p_fecha_actualizacion",    LocalDate.now())
                .addValue("p_ultimo_mensaje",         ultimoMensaje)
                .addValue("p_id_tipoconversacion_fk", idTipo)
                .addValue("p_id_creado_por_fk",       idCreador.intValue())
                .addValue("p_id_estado_fk",           ESTADO_ACTIVO));
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public void actualizarConversacion(Long idConversacion, String ultimoMensaje,
                                       String titulo, Integer idTipo, Long idCreador) {
        conversacionModificar.execute(new MapSqlParameterSource()
                .addValue("p_id_conversacion",        idConversacion.intValue())
                .addValue("p_titulo",                 titulo)
                .addValue("p_fecha_creacion_conv",    LocalDate.now())
                .addValue("p_fecha_actualizacion",    LocalDate.now())
                .addValue("p_ultimo_mensaje",         ultimoMensaje)
                .addValue("p_id_tipoconversacion_fk", idTipo)
                .addValue("p_id_creado_por_fk",       idCreador.intValue()));
    }

    public void eliminarConversacion(Long idConversacion) {
        conversacionCambiarEstado.execute(new MapSqlParameterSource()
                .addValue("p_id_conversacion", idConversacion.intValue())
                .addValue("p_id_estado",       ESTADO_INACTIVO));
    }

    // ------------------------------------------------------------------
    //  MENSAJES
    // ------------------------------------------------------------------

    public List<Map<String, Object>> listarMensajes(Long idConversacion) {
        String sql =
            "SELECT m.ID_MENSAJE    AS idMensaje, " +
            "       m.FECHA_ENVIO   AS fechaEnvio, " +
            "       m.ASUNTO        AS asunto, " +
            "       m.CONTENIDO     AS contenido, " +
            "       m.ID_EMISOR_FK  AS idEmisor, " +
            "       CONCAT(ue.NOMBRE,' ',ue.PRIMER_APELLIDO) AS nombreEmisor, " +
            "       m.ID_REMITENTE_FK AS idRemitente, " +
            "       CONCAT(ur.NOMBRE,' ',ur.PRIMER_APELLIDO) AS nombreRemitente, " +
            "       m.ID_ESTADO_FK  AS idEstado " +
            "FROM MENSAJES_TB m " +
            "JOIN USUARIOS_TB ue ON ue.ID_USUARIO = m.ID_EMISOR_FK " +
            "JOIN USUARIOS_TB ur ON ur.ID_USUARIO = m.ID_REMITENTE_FK " +
            "WHERE m.ID_CONVERSACION_FK = ? " +
            "  AND m.ID_ESTADO_FK IN (" + ESTADO_LEIDO + "," + ESTADO_NO_LEIDO + ") " +
            "ORDER BY m.FECHA_ENVIO ASC, m.FECHA_CREACION ASC";
        return jdbc.queryForList(sql, idConversacion);
    }

    public Long insertarMensaje(String asunto, String contenido, Long idConversacion,
                                Long idEmisor, Long idRemitente) {
        mensajeInsertar.execute(new MapSqlParameterSource()
                .addValue("p_fecha_envio",        LocalDate.now())
                .addValue("p_fecha_editado",       null)
                .addValue("p_asunto",              asunto)
                .addValue("p_contenido",           contenido)
                .addValue("p_id_conversacion_fk",  idConversacion.intValue())
                .addValue("p_id_emisor_fk",        idEmisor.intValue())
                .addValue("p_id_remitente_fk",     idRemitente.intValue())
                .addValue("p_id_estado_fk",        ESTADO_NO_LEIDO));
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public void marcarLeidosEnConversacion(Long idConversacion, Long idUsuario) {
        jdbc.update(
                "UPDATE MENSAJES_TB " +
                "SET ID_ESTADO_FK = ?, FECHA_MODIFICACION = NOW(), " +
                "    MODIFICADO_POR = USER(), ACCION = 'LEIDO' " +
                "WHERE ID_CONVERSACION_FK = ? AND ID_REMITENTE_FK = ? AND ID_ESTADO_FK = ?",
                ESTADO_LEIDO, idConversacion, idUsuario, ESTADO_NO_LEIDO);
    }

    public void eliminarMensaje(Long idMensaje) {
        mensajeCambiarEstado.execute(new MapSqlParameterSource()
                .addValue("p_id_mensaje", idMensaje.intValue())
                .addValue("p_id_estado",  ESTADO_INACTIVO));
    }

    // ------------------------------------------------------------------
    //  ARCHIVOS
    // ------------------------------------------------------------------

    public List<Map<String, Object>> listarArchivos(Long idMensaje) {
        return jdbc.queryForList(
                "SELECT ID_ARCHIVO AS idArchivo, RUTA_ARCHIVO AS rutaArchivo, " +
                "       FECHA_CREADO AS fechaCreado " +
                "FROM MENSAJESARCHIVOS_TB " +
                "WHERE ID_MENSAJE_FK = ? AND ID_ESTADO_FK = ?",
                idMensaje, ESTADO_ACTIVO);
    }

    public void insertarArchivo(String ruta, Long idMensaje) {
        archivoInsertar.execute(new MapSqlParameterSource()
                .addValue("p_ruta_archivo",  ruta)
                .addValue("p_fecha_creado",  LocalDate.now())
                .addValue("p_id_mensaje_fk", idMensaje.intValue())
                .addValue("p_id_estado_fk",  ESTADO_ACTIVO));
    }

    public void eliminarArchivo(Long idArchivo) {
        archivoCambiarEstado.execute(new MapSqlParameterSource()
                .addValue("p_id_archivo", idArchivo.intValue())
                .addValue("p_id_estado",  ESTADO_INACTIVO));
    }

    // ------------------------------------------------------------------
    //  USUARIOS Y TIPOS
    // ------------------------------------------------------------------

    public List<Map<String, Object>> listarUsuariosActivos(Long idUsuarioActual) {
        return jdbc.queryForList("""
                SELECT u.ID_USUARIO AS idUsuario,
                       CONCAT(u.NOMBRE,' ',u.PRIMER_APELLIDO,
                              CASE WHEN u.SEGUNDO_APELLIDO IS NULL OR u.SEGUNDO_APELLIDO=''
                                   THEN '' ELSE CONCAT(' ',u.SEGUNDO_APELLIDO) END
                       ) AS nombreCompleto,
                       tu.NOMBRE AS tipoUsuario
                FROM USUARIOS_TB u
                JOIN TIPOUSUARIO_TB tu ON tu.ID_TIPOUSUARIO = u.ID_TIPOUSUARIO_FK
                WHERE u.ID_ESTADO_FK = 1
                  AND u.ID_USUARIO <> ?
                ORDER BY tu.NOMBRE, u.PRIMER_APELLIDO
                """, idUsuarioActual);
    }

    public Long idTipoDirecto() {
        return jdbc.queryForObject(
                "SELECT ID_TIPOCONVERSACION FROM TIPOCONVERSACION_TB WHERE NOMBRE='DIRECTO' LIMIT 1",
                Long.class);
    }

    // ------------------------------------------------------------------
    //  BADGE NAVBAR
    // ------------------------------------------------------------------

    public int contarNoLeidos(Long idUsuario) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM MENSAJES_TB m " +
                "JOIN CONVERSACION_TB c ON c.ID_CONVERSACION = m.ID_CONVERSACION_FK " +
                "WHERE m.ID_REMITENTE_FK = ? AND m.ID_ESTADO_FK = ? AND c.ID_ESTADO_FK = ?",
                Integer.class, idUsuario, ESTADO_NO_LEIDO, ESTADO_ACTIVO);
        return count != null ? count : 0;
    }
}