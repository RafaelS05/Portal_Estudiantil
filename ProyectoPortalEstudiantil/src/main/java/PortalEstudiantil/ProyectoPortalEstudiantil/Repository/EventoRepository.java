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
    // NOTICIAS Y ANUNCIOS (módulo anterior — sin cambios)
    // =========================================================

    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
          AND e.ID_ESTADO_FK = 1
          AND (e.FECHA_FIN IS NULL OR e.FECHA_FIN >= :hoy)
        ORDER BY e.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Evento> listarNoticiasActivas(@Param("hoy") LocalDate hoy);

    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
        ORDER BY e.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Evento> listarTodasParaAdmin();

    @Query(value = """
        SELECT COUNT(*)
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO IN ('NOTICIA', 'ANUNCIO')
          AND e.ID_ESTADO_FK = 1
          AND e.FECHA_INICIO >= :desde
    """, nativeQuery = true)
    long contarNoticiasDesde(@Param("desde") LocalDate desde);

    // =========================================================
    // GESTIÓN DE EVENTOS — nuevas queries
    // =========================================================

    // Todos los eventos para el panel de admin (sin filtro de estado).
    // Excluye NOTICIA y ANUNCIO — esos los maneja NoticiaService.
    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO NOT IN ('NOTICIA', 'ANUNCIO')
        ORDER BY e.FECHA_INICIO DESC
    """, nativeQuery = true)
    List<Evento> listarEventosParaAdmin();

    // Eventos activos y vigentes para el calendario (todos los usuarios).
    // Trae eventos cuya fecha de fin no ha pasado, o que no tienen fecha de fin.
    @Query(value = """
        SELECT e.*
        FROM EVENTO_TB e
        WHERE e.TIPO_EVENTO NOT IN ('NOTICIA', 'ANUNCIO')
          AND e.ID_ESTADO_FK = 1
          AND (e.FECHA_FIN IS NULL OR e.FECHA_FIN >= :hoy)
        ORDER BY e.FECHA_INICIO ASC
    """, nativeQuery = true)
    List<Evento> listarEventosActivosParaCalendario(@Param("hoy") LocalDate hoy);

    // =========================================================
    // CRUD — Stored Procedures (compartidos por noticias y eventos)
    // =========================================================

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