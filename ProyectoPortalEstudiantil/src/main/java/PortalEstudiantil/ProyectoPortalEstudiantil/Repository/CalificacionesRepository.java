/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Calificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 *
 * @author marjo
 */
@Repository
public interface CalificacionesRepository extends JpaRepository<Calificaciones, Long> {

    @Query("SELECT c FROM Calificaciones c WHERE c.idEstadoFk = :estadoActivo")
    Page<Calificaciones> findAllActivos(@Param("estadoActivo") Long estadoActivo, Pageable pageable);

    @Query("SELECT c FROM Calificaciones c WHERE c.idEstadoFk = :estadoActivo AND "
            + "(LOWER(c.matricula.estudiante.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR "
            + "LOWER(c.matricula.estudiante.primerApellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR "
            + "LOWER(c.matricula.estudiante.segundoApellido) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Calificaciones> searchByEstudianteNombre(@Param("busqueda") String busqueda,
            @Param("estadoActivo") Long estadoActivo,
            Pageable pageable);

    @Query("SELECT c FROM Calificaciones c WHERE c.idEstadoFk = :estadoActivo AND "
            + "c.matricula.seccion.idSeccion = :idSeccion")
    Page<Calificaciones> findBySeccionAndActivos(@Param("idSeccion") Long idSeccion,
            @Param("estadoActivo") Long estadoActivo,
            Pageable pageable);

    @Query("SELECT c FROM Calificaciones c WHERE c.idEstadoFk = :estadoActivo AND "
            + "c.matricula.seccion.idSeccion = :idSeccion AND "
            + "(LOWER(c.matricula.estudiante.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR "
            + "LOWER(c.matricula.estudiante.primerApellido) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Calificaciones> searchByEstudianteAndSeccion(@Param("busqueda") String busqueda,
            @Param("idSeccion") Long idSeccion,
            @Param("estadoActivo") Long estadoActivo,
            Pageable pageable);
}
