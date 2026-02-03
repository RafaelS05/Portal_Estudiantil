package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Long crearUsuario(String nombre, String primerApellido, String segundoApellido,
                            Long idTipoUsuario, Long idEstado) {
        try {
 
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            if (primerApellido == null || primerApellido.trim().isEmpty()) {
                throw new IllegalArgumentException("El primer apellido es obligatorio");
            }
            if (idTipoUsuario == null) {
                throw new IllegalArgumentException("El tipo de usuario es obligatorio");
            }
            if (idEstado == null) {
                throw new IllegalArgumentException("El estado es obligatorio");
            }
            

            usuarioRepository.insertarUsuario(nombre.trim(), primerApellido.trim(), 
                                             segundoApellido != null ? segundoApellido.trim() : "",
                                             idTipoUsuario, idEstado);
            
          
            List<Usuario> usuariosRecientes = usuarioRepository.findByNombreContaining(nombre);
            Usuario usuarioCreado = usuariosRecientes.stream()
                .filter(u -> u.getPrimerApellido().equals(primerApellido))
                .findFirst()
                .orElse(null);
                
            if (usuarioCreado != null) {
                System.out.println("Usuario creado exitosamente con ID: " + usuarioCreado.getIdUsuario());
                return usuarioCreado.getIdUsuario();
            }
            
            throw new RuntimeException("No se pudo obtener el ID del usuario creado");
            
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            throw new RuntimeException("Error en la creación del usuario: " + e.getMessage(), e);
        }
    }

 
    @Transactional
    public Long crearUsuario(Usuario usuario) {
        return crearUsuario(
            usuario.getNombre(),
            usuario.getPrimerApellido(),
            usuario.getSegundoApellido(),
            usuario.getIdTipoUsuarioFk(),
            usuario.getIdEstadoFk()
        );
    }

 
    @Transactional
    public void actualizarUsuario(Long idUsuario, String nombre, String primerApellido,
                                 String segundoApellido, Long idTipoUsuario) {
        try {
  
            if (!usuarioRepository.existsById(idUsuario)) {
                throw new RuntimeException("No existe un usuario con ID: " + idUsuario);
            }
            
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            if (primerApellido == null || primerApellido.trim().isEmpty()) {
                throw new IllegalArgumentException("El primer apellido es obligatorio");
            }
            
            usuarioRepository.modificarUsuario(idUsuario, nombre.trim(), primerApellido.trim(),
                                              segundoApellido != null ? segundoApellido.trim() : "",
                                              idTipoUsuario);
            
            System.out.println("Usuario ID " + idUsuario + " actualizado exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException("Error en la actualización del usuario: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para actualizar");
        }
        
        actualizarUsuario(
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getPrimerApellido(),
            usuario.getSegundoApellido(),
            usuario.getIdTipoUsuarioFk()
        );
    }

    @Transactional
    public void cambiarEstadoUsuario(Long idUsuario, Long idEstado) {
        try {
          
            if (!usuarioRepository.existsById(idUsuario)) {
                throw new RuntimeException("No existe un usuario con ID: " + idUsuario);
            }
            
            if (idEstado == null) {
                throw new IllegalArgumentException("El estado es obligatorio");
            }
            
         
            usuarioRepository.cambiarEstadoUsuario(idUsuario, idEstado);
            
            String estadoTexto = idEstado == 1L ? "ACTIVADO" : "DESACTIVADO";
            System.out.println("✅ Usuario ID " + idUsuario + " " + estadoTexto);
            
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado del usuario: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }


    @Transactional
    public void activarUsuario(Long idUsuario) {
        cambiarEstadoUsuario(idUsuario, 1L);
    }

    @Transactional
    public void desactivarUsuario(Long idUsuario) {
        cambiarEstadoUsuario(idUsuario, 2L);
    }

    public Usuario obtenerUsuario(Long idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        return usuario.orElseThrow(() -> 
            new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es obligatorio");
        }
        return usuarioRepository.findByNombreContaining(nombre.trim());
    }


    public List<Usuario> buscarPorTipoUsuario(Long idTipoUsuario) {
        if (idTipoUsuario == null) {
            throw new IllegalArgumentException("El ID del tipo de usuario es obligatorio");
        }
        return usuarioRepository.findByIdTipoUsuarioFk(idTipoUsuario);
    }

    public List<Usuario> buscarPorEstado(Long idEstado) {
        if (idEstado == null) {
            throw new IllegalArgumentException("El ID del estado es obligatorio");
        }
        return usuarioRepository.findByIdEstadoFk(idEstado);
    }

    public List<Usuario> obtenerUsuariosActivos() {
        return buscarPorEstado(1L); 
    }


    public List<Usuario> obtenerUsuariosInactivos() {
        return buscarPorEstado(2L);
    }

    public List<Usuario> buscarPorNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        return usuarioRepository.findByNombreCompletoContaining(nombreCompleto.trim());
    }

    public boolean existeUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return false;
        }
        return usuarioRepository.existsById(idUsuario);
    }

    public long contarTotalUsuarios() {
        return usuarioRepository.count();
    }

    public long contarUsuariosPorEstado(Long idEstado) {
        return usuarioRepository.findByIdEstadoFk(idEstado).size();
    }

    public long contarUsuariosPorTipo(Long idTipoUsuario) {
        return usuarioRepository.findByIdTipoUsuarioFk(idTipoUsuario).size();
    }

    public String obtenerInformacionUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuario(idUsuario);
        return String.format("Usuario ID: %d - %s %s %s (Tipo: %d, Estado: %d)",
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getPrimerApellido(),
            usuario.getSegundoApellido() != null ? usuario.getSegundoApellido() : "",
            usuario.getIdTipoUsuarioFk(),
            usuario.getIdEstadoFk());
    }

    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        try {
            if (!usuarioRepository.existsById(idUsuario)) {
                throw new RuntimeException("No existe un usuario con ID: " + idUsuario);
            }
            
            usuarioRepository.deleteById(idUsuario);
            System.out.println("✅ Usuario ID " + idUsuario + " eliminado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar el usuario: " + e.getMessage(), e);
        }
    }

    public boolean validarDatosUsuario(Usuario usuario) {
        if (usuario == null) return false;
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) return false;
        if (usuario.getPrimerApellido() == null || usuario.getPrimerApellido().trim().isEmpty()) return false;
        if (usuario.getIdTipoUsuarioFk() == null) return false;
        if (usuario.getIdEstadoFk() == null) return false;
        return true;
    }
}