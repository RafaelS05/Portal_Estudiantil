/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Canton;
import jakarta.transaction.Transactional;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CantonRepository extends JpaRepository<Canton, Long> {

   // =====================
    // LECTURA
    // =====================
    List<Canton> findByProvincia_IdProvincia(Long idProvincia);

    // =====================
    // INSERTAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL CANTON_INSERTAR(
            :nombre,
            :idProvincia,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarCanton(
        @Param("nombre") String nombre,
        @Param("idProvincia") Long idProvincia,
        @Param("idEstado") Long idEstado
    );

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL CANTON_MODIFICAR(
            :idCanton,
            :nombre,
            :idProvincia
        )
    """, nativeQuery = true)
    void modificarCanton(
        @Param("idCanton") Long idCanton,
        @Param("nombre") String nombre,
        @Param("idProvincia") Long idProvincia
    );
}