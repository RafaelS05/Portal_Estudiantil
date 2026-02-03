/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;


import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Telefono;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelefonoRepository extends JpaRepository<Telefono, Long> {

  
    // =====================
    // LECTURA
    // =====================
    Telefono findByUsuario_IdUsuario(Long idUsuario);

    // =====================
    // INSERTAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        TELEFONO_INSERTAR(
            :numero,
            :idUsuario,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarTelefono(
        @Param("numero") String numero,
        @Param("idUsuario") Long idUsuario,
        @Param("idEstado") Long idEstado
    );

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        TELEFONO_MODIFICAR(
            :idTelefono,
            :numero,
            :idUsuario
        )
    """, nativeQuery = true)
    void modificarTelefono(
        @Param("idTelefono") Long idTelefono,
        @Param("numero") String numero,
        @Param("idUsuario") Long idUsuario
    );
}