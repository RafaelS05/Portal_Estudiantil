package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // Interface para mapear los resultados al frontend
    interface AsistenciaListadoRow {
        Long getIdAsistencia();
        String getNombreCompleto();
        String getCorreo();
        String getGrupo();
        String getFecha();
        String getEstado(); // Ej: Presente, Tarde, Ausente
    }

    @Query(value = """
        SELECT 
            a.ID_ASISTENCIA AS idAsistencia,
            CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO) AS nombreCompleto,
            c.CORREO AS correo,
            s.NUMERO AS grupo,
            DATE_FORMAT(a.FECHA_ASISTENCIA, '%d/%m/%Y') AS fecha,
            e.DESCRIPCION AS estado
        FROM ASISTENCIAS_TB a
        JOIN MATRICULA_TB m ON a.ID_MATRICULA_FK = m.ID_MATRICULA
        JOIN USUARIOS_TB u ON m.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
        LEFT JOIN CORREO_TB c ON c.ID_USUARIO_FK = u.ID_USUARIO AND c.ES_LOGIN = 'S'
        JOIN SECCION_TB s ON m.ID_SECCION_FK = s.ID_SECCION
        JOIN ESTADOS_TB e ON a.ID_ESTADO_FK = e.ID_ESTADO
        WHERE (:busqueda IS NULL OR LOWER(CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO)) LIKE LOWER(CONCAT('%', :busqueda, '%')))
        ORDER BY a.FECHA_ASISTENCIA DESC
    """, nativeQuery = true)
    List<AsistenciaListadoRow> listarAsistenciasFrontend(@Param("busqueda") String busqueda);
}