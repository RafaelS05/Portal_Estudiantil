/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;



import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Direccion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
  // =====================
    // LECTURA
    // =====================
    Direccion findByUsuario_IdUsuario(Long idUsuario);

    // =====================
    // INSERTAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL DIRECCION_INSERTAR(
            :otrasSenas,
            :idUsuario,
            :idProvincia,
            :idCanton,
            :idDistrito,
            :idEstado
        )
    """, nativeQuery = true)
    void insertarDireccion(
        @Param("otrasSenas") String otrasSenas,
        @Param("idUsuario") Long idUsuario,
        @Param("idProvincia") Long idProvincia,
        @Param("idCanton") Long idCanton,
        @Param("idDistrito") Long idDistrito,
        @Param("idEstado") Long idEstado
    );

    // =====================
    // MODIFICAR
    // =====================
    @Modifying
    @Transactional
    @Query(value = """
        CALL DIRECCION_MODIFICAR(
            :idDireccion,
            :otrasSenas,
            :idUsuario,
            :idProvincia,
            :idCanton,
            :idDistrito
        )
    """, nativeQuery = true)
    void modificarDireccion(
        @Param("idDireccion") Long idDireccion,
        @Param("otrasSenas") String otrasSenas,
        @Param("idUsuario") Long idUsuario,
        @Param("idProvincia") Long idProvincia,
        @Param("idCanton") Long idCanton,
        @Param("idDistrito") Long idDistrito
    );
}
