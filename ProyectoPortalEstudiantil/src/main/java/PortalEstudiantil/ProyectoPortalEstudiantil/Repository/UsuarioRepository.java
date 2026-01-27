/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    List<Usuario> findByPrimerApellidoContainingIgnoreCase(String apellido);
    
    List<Usuario> findByTipoUsuarioNombre(String nombreTipoUsuario);
    
    List<Usuario> findByEstadoDescripcion(String descripcionEstado);
}
