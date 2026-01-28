/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Distrito;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DistritoRepository extends JpaRepository<Distrito, Long> {

 // =====================
    // LECTURA
    // =====================
    List<Distrito> findByCanton_IdCanton(Long idCanton);

    // =====================
    // INSERTAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL PORTAL_ESCOLAR_PKG.DISTRITO_INSERTAR(
            :nombre,
            :idCanton,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarDistrito(
        @Param("nombre") String nombre,
        @Param("idCanton") Long idCanton,
        @Param("idEstado") Long idEstado
    );

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL PORTAL_ESCOLAR_PKG.DISTRITO_MODIFICAR(
            :idDistrito,
            :nombre,
            :idCanton
        )
    """, nativeQuery = true)
    void modificarDistrito(
        @Param("idDistrito") Long idDistrito,
        @Param("nombre") String nombre,
        @Param("idCanton") Long idCanton
    );
}
