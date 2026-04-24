package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Matricula;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

 
    public Object findBySeccion_IdSeccion(Long idSeccion);
  
    interface MatriculaRow {

        Long getIdMatricula();

        Long getIdSeccionFk();

        String getNumeroSeccion();

        Long getIdUsuarioEstudianteFk();

        String getNombreCompleto();

        String getFechaMatricula();

        Long getIdEstadoFk();
    }

    //Conteos
    @Query(value = """
        SELECT COUNT(*) FROM MATRICULA_TB
        WHERE ID_ESTADO_FK = :idEstado
    """, nativeQuery = true)
    long contarPorEstado(@Param("idEstado") Long idEstado);

    @Query(value = """
        SELECT COUNT(*) FROM MATRICULA_TB
        WHERE ID_SECCION_FK = :idSeccion
          AND ID_ESTADO_FK  = :idEstado
    """, nativeQuery = true)
    long contarPorSeccionYEstado(@Param("idSeccion") Long idSeccion,
            @Param("idEstado") Long idEstado);

    //Validación duplicado
    @Query(value = """
        SELECT COUNT(*) FROM MATRICULA_TB
        WHERE ID_USUARIO_ESTUDIANTE_FK = :idEstudiante
          AND ID_SECCION_FK            = :idSeccion
    """, nativeQuery = true)
    long contarEstudianteEnSeccion(@Param("idEstudiante") Long idEstudiante,
            @Param("idSeccion") Long idSeccion);

    //Queries
    @Query(value = """
        SELECT
            m.ID_MATRICULA               AS idMatricula,
            m.ID_SECCION_FK              AS idSeccionFk,
            s.NUMERO                     AS numeroSeccion,
            m.ID_USUARIO_ESTUDIANTE_FK   AS idUsuarioEstudianteFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   IFNULL(CONCAT(' ', u.SEGUNDO_APELLIDO), '')) AS nombreCompleto,
            m.FECHA_MATRICULA            AS fechaMatricula,
            m.ID_ESTADO_FK               AS idEstadoFk
        FROM MATRICULA_TB m
        JOIN USUARIOS_TB u ON u.ID_USUARIO = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB  s ON s.ID_SECCION = m.ID_SECCION_FK
        ORDER BY s.NUMERO, u.PRIMER_APELLIDO, u.NOMBRE
    """, nativeQuery = true)
    List<MatriculaRow> listarTodos();

    @Query(value = """
        SELECT
            m.ID_MATRICULA               AS idMatricula,
            m.ID_SECCION_FK              AS idSeccionFk,
            s.NUMERO                     AS numeroSeccion,
            m.ID_USUARIO_ESTUDIANTE_FK   AS idUsuarioEstudianteFk,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO,
                   IFNULL(CONCAT(' ', u.SEGUNDO_APELLIDO), '')) AS nombreCompleto,
            m.FECHA_MATRICULA            AS fechaMatricula,
            m.ID_ESTADO_FK               AS idEstadoFk
        FROM MATRICULA_TB m
        JOIN USUARIOS_TB u ON u.ID_USUARIO = m.ID_USUARIO_ESTUDIANTE_FK
        JOIN SECCION_TB  s ON s.ID_SECCION = m.ID_SECCION_FK
        WHERE m.ID_SECCION_FK = :idSeccion
        ORDER BY u.PRIMER_APELLIDO, u.NOMBRE
    """, nativeQuery = true)
    List<MatriculaRow> listarPorSeccion(@Param("idSeccion") Long idSeccion);

    // ── CRUD ─────────────────────────────────────────────────────
    @Modifying
    @Transactional
    @Query(value = "CALL MATRICULA_CAMBIAR_ESTADO(:idMatricula, :idEstado)",
            nativeQuery = true)
    void cambiarEstadoMatricula(@Param("idMatricula") Long idMatricula,
            @Param("idEstado") Long idEstado);

    @Modifying
    @Transactional
    @Query(value = "CALL MATRICULA_DESMATRICULAR(:idMatricula)", nativeQuery = true)
    void desmatricularEstudiante(@Param("idMatricula") Long idMatricula);
}
