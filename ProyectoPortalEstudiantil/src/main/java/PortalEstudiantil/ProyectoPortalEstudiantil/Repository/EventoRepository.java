package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    // =========================================================
    // CONSULTAS DE LECTURA
    // =========================================================

    // Trae todas las noticias y anuncios activos, ordenados del más reciente al más antiguo.
    // Solo muestra los que tienen estado ACTIVO (ID_ESTADO_FK = 1) y cuya fecha de vencimiento
    // no ha pasado (o no tienen fecha de vencimiento).
    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
          AND e.ID_ESTADO_FK = 1
          AND (e.FECHA_FIN IS NULL OR e.FECHA_FIN >= :hoy)
        ORDER BY e.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Evento> listarNoticiasActivas(@Param("hoy") LocalDate hoy);

    // Igual que la anterior pero sin filtro de fecha — para el panel de admin,
    // que necesita ver todo incluyendo vencidas e inactivas.
    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
        ORDER BY e.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Evento> listarTodasParaAdmin();

    // Cuenta cuántas noticias/anuncios nuevos hay desde una fecha dada.
    // Se usa para el badge de "noticias nuevas" en el topbar.
    @Query(value = """
        SELECT COUNT(*)
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
          AND e.ID_ESTADO_FK = 1
          AND e.FECHA_INICIO >= :desde
    """, nativeQuery = true)
    long contarNoticiasDesde(@Param("desde") LocalDate desde);

    // =========================================================
    // CRUD — Stored Procedures
    // =========================================================

    // Llama al stored procedure EVENTO_INSERTAR.
    // ID_TIPOVISIBILIDAD_FK = 1 (GENERAL), ID_SECCION_FK y ID_SECCIONMATERIA_FK = null
    // porque las noticias son para todos, no para una sección específica.
    @Modifying
    @Transactional
    @Query(value = """
        CALL EVENTO_INSERTAR(
            :titulo,
            :descripcion,
            :fechaInicio,
            :fechaFin,
            :tipoEvento,
            :idTipoVisibilidad,
            NULL,
            NULL,
            :idUsuario,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarEvento(
            @Param("titulo") String titulo,
            @Param("descripcion") String descripcion,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("tipoEvento") String tipoEvento,
            @Param("idTipoVisibilidad") Long idTipoVisibilidad,
            @Param("idUsuario") Long idUsuario,
            @Param("idEstado") Long idEstado
    );

    // Llama al stored procedure EVENTO_MODIFICAR.
    @Modifying
    @Transactional
    @Query(value = """
        CALL EVENTO_MODIFICAR(
            :idEvento,
            :titulo,
            :descripcion,
            :fechaInicio,
            :fechaFin,
            :tipoEvento,
            :idTipoVisibilidad,
            NULL,
            NULL,
            :idUsuario
        )
    """, nativeQuery = true)
    void modificarEvento(
            @Param("idEvento") Long idEvento,
            @Param("titulo") String titulo,
            @Param("descripcion") String descripcion,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("tipoEvento") String tipoEvento,
            @Param("idTipoVisibilidad") Long idTipoVisibilidad,
            @Param("idUsuario") Long idUsuario
    );

    // Llama al stored procedure EVENTO_CAMBIAR_ESTADO.
    // Se usa para activar (1) o desactivar (2) una noticia.
    @Modifying
    @Transactional
    @Query(value = """
        CALL EVENTO_CAMBIAR_ESTADO(:idEvento, :idEstado)
    """, nativeQuery = true)
    void cambiarEstadoEvento(
            @Param("idEvento") Long idEvento,
            @Param("idEstado") Long idEstado
    );
}