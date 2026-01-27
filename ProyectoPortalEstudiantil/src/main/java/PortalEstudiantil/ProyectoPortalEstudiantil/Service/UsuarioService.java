package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.TipoUsuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Estado;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.UsuarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.TipoUsuarioRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    // Buscar usuario por ID
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }
    
    // Crear usuario
    public Usuario crearUsuario(String nombre, String primerApellido, String segundoApellido, 
                                Long idTipoUsuario, Long idEstado) {
        
        // Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        // Obtener tipo de usuario
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(idTipoUsuario)
                .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado"));
        
        // Obtener estado
        Estado estado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        // Crear y guardar usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstado(estado);
        
        return usuarioRepository.save(usuario);
    }
    
    // Actualizar usuario
    public Usuario actualizarUsuario(Long id, String nombre, String primerApellido, 
                                     String segundoApellido, Long idTipoUsuario, Long idEstado) {
        
        Usuario usuario = obtenerUsuarioPorId(id);
        
        // Actualizar campos básicos
        usuario.setNombre(nombre);
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        
        // Actualizar tipo de usuario si se proporciona
        if (idTipoUsuario != null) {
            TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(idTipoUsuario)
                    .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado"));
            usuario.setTipoUsuario(tipoUsuario);
        }
        
        // Actualizar estado si se proporciona
        if (idEstado != null) {
            Estado estado = estadoRepository.findById(idEstado)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            usuario.setEstado(estado);
        }
        
        return usuarioRepository.save(usuario);
    }
    
    // Eliminar usuario
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    // Cambiar estado de usuario
    public Usuario cambiarEstadoUsuario(Long idUsuario, Long idEstado) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        Estado nuevoEstado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }
    
    // Método para activar usuario (busca estado "Activo" por descripción)
    public Usuario activarUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        
        // Buscar estado "Activo" por su descripción
        Estado estadoActivo = estadoRepository.findByDescripcion("Activo")
                .orElseThrow(() -> new RuntimeException("Estado 'Activo' no encontrado en la BD"));
        
        usuario.setEstado(estadoActivo);
        return usuarioRepository.save(usuario);
    }
    
    // Método para desactivar usuario (busca estado "Inactivo" por descripción)
    public Usuario desactivarUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        
        // Buscar estado "Inactivo" por su descripción
        Estado estadoInactivo = estadoRepository.findByDescripcion("Inactivo")
                .orElseThrow(() -> new RuntimeException("Estado 'Inactivo' no encontrado en la BD"));
        
        usuario.setEstado(estadoInactivo);
        return usuarioRepository.save(usuario);
    }
    
    // Buscar usuarios por término
    public List<Usuario> buscarUsuarios(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return usuarioRepository.findAll();
        }
        
        // Buscar en nombre y apellidos
        List<Usuario> resultados = usuarioRepository.findByNombreContainingIgnoreCase(termino);
        resultados.addAll(usuarioRepository.findByPrimerApellidoContainingIgnoreCase(termino));
        
        // Eliminar duplicados
        return resultados.stream().distinct().toList();
    }
    
    // Obtener todos los tipos de usuario
    public List<TipoUsuario> obtenerTodosTiposUsuario() {
        return tipoUsuarioRepository.findAll();
    }
    
    // Obtener todos los estados
    public List<Estado> obtenerTodosEstados() {
        return estadoRepository.findAll();
    }
    
    // Obtener estado por ID
    public Estado obtenerEstadoPorId(Long id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }
    
    // Buscar estado por descripción
    public Estado obtenerEstadoPorDescripcion(String descripcion) {
        return estadoRepository.findByDescripcion(descripcion)
                .orElseThrow(() -> new RuntimeException("Estado '" + descripcion + "' no encontrado"));
    }
}