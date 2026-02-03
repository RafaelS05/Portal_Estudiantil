/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Provincia;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
 // =====================
    // LECTURA
    // =====================
    Provincia findByIdProvincia(Long idProvincia);

    // =====================
    // INSERTAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        PROVINCIA_INSERTAR(
            :nombre,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarProvincia(
        @Param("nombre") String nombre,
        @Param("idEstado") Long idEstado
    );

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        PROVINCIA_MODIFICAR(
            :idProvincia,
            :nombre
        )
    """, nativeQuery = true)
    void modificarProvincia(
        @Param("idProvincia") Long idProvincia,
        @Param("nombre") String nombre
    );
}
