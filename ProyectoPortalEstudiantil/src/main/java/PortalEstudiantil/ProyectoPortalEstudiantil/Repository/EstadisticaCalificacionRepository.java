package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.EstadisticaCalificacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EstadisticaCalificacionRepository extends JpaRepository<EstadisticaCalificacion, Long> {
    
    EstadisticaCalificacion findByIdEstadistica(Long idEstadistica);
    
    List<EstadisticaCalificacion> findByIdMateriaFk(Long idMateriaFk);
    
    List<EstadisticaCalificacion> findByIdSeccionFk(Long idSeccionFk);
    
    List<EstadisticaCalificacion> findByIdDocenteFk(Long idDocenteFk);
    
    List<EstadisticaCalificacion> findByIdEstadoFk(Long idEstadoFk);
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.idMateriaFk = :idMateria AND e.idSeccionFk = :idSeccion")
    List<EstadisticaCalificacion> findByMateriaAndSeccion(
        @Param("idMateria") Long idMateria, 
        @Param("idSeccion") Long idSeccion
    );
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.idMateriaFk = :idMateria AND e.idDocenteFk = :idDocente")
    List<EstadisticaCalificacion> findByMateriaAndDocente(
        @Param("idMateria") Long idMateria, 
        @Param("idDocente") Long idDocente
    );
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.idSeccionFk = :idSeccion AND e.idDocenteFk = :idDocente")
    List<EstadisticaCalificacion> findBySeccionAndDocente(
        @Param("idSeccion") Long idSeccion, 
        @Param("idDocente") Long idDocente
    );
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.idMateriaFk = :idMateria AND e.idSeccionFk = :idSeccion AND e.idDocenteFk = :idDocente")
    EstadisticaCalificacion findByMateriaSeccionDocente(
        @Param("idMateria") Long idMateria, 
        @Param("idSeccion") Long idSeccion,
        @Param("idDocente") Long idDocente
    );
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.promedio >= :promedioMinimo")
    List<EstadisticaCalificacion> findByPromedioGreaterThanEqual(@Param("promedioMinimo") Double promedioMinimo);
    
    @Query("SELECT e FROM EstadisticaCalificacion e WHERE e.porcentajeAprobacion >= :porcentajeMinimo")
    List<EstadisticaCalificacion> findByPorcentajeAprobacionGreaterThanEqual(@Param("porcentajeMinimo") Double porcentajeMinimo);
    
    @Modifying
    @Transactional
    @Query(value = """
        CALL ESTADISTICAS_CALIFICACIONES_INSERTAR(
            :idMateriaFk,
            :idSeccionFk,
            :idDocenteFk,
            :promedio,
            :notaMaxima,
            :notaMinima,
            :totalEstudiantes,
            :aprobados,
            :reprobados,
            :porcentajeAprobacion,
            :idEstadoFk
        )
    """, nativeQuery = true)
    void insertarEstadistica(
        @Param("idMateriaFk") Long idMateriaFk,
        @Param("idSeccionFk") Long idSeccionFk,
        @Param("idDocenteFk") Long idDocenteFk,
        @Param("promedio") Double promedio,
        @Param("notaMaxima") Double notaMaxima,
        @Param("notaMinima") Double notaMinima,
        @Param("totalEstudiantes") Long totalEstudiantes,
        @Param("aprobados") Long aprobados,
        @Param("reprobados") Long reprobados,
        @Param("porcentajeAprobacion") Double porcentajeAprobacion,
        @Param("idEstadoFk") Long idEstadoFk
    );
    
    @Modifying
    @Transactional
    @Query(value = """
        CALL ESTADISTICAS_CALIFICACIONES_MODIFICAR(
            :idEstadistica,
            :idMateriaFk,
            :idSeccionFk,
            :idDocenteFk,
            :promedio,
            :notaMaxima,
            :notaMinima,
            :totalEstudiantes,
            :aprobados,
            :reprobados,
            :porcentajeAprobacion
        )
    """, nativeQuery = true)
    void modificarEstadistica(
        @Param("idEstadistica") Long idEstadistica,
        @Param("idMateriaFk") Long idMateriaFk,
        @Param("idSeccionFk") Long idSeccionFk,
        @Param("idDocenteFk") Long idDocenteFk,
        @Param("promedio") Double promedio,
        @Param("notaMaxima") Double notaMaxima,
        @Param("notaMinima") Double notaMinima,
        @Param("totalEstudiantes") Long totalEstudiantes,
        @Param("aprobados") Long aprobados,
        @Param("reprobados") Long reprobados,
        @Param("porcentajeAprobacion") Double porcentajeAprobacion
    );
    
    @Modifying
    @Transactional
    @Query(value = """
        CALL ESTADISTICAS_CALIFICACIONES_CAMBIAR_ESTADO(
            :idEstadistica,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoEstadistica(
        @Param("idEstadistica") Long idEstadistica,
        @Param("idEstado") Long idEstado
    );
}