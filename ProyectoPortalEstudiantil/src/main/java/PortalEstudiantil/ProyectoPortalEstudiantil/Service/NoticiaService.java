package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoticiaService {

    private final EventoRepository eventoRepository;
    private final CorreoRepository correoRepository;

    public NoticiaService(EventoRepository eventoRepository,
                          CorreoRepository correoRepository) {
        this.eventoRepository = eventoRepository;
        this.correoRepository = correoRepository;
    }

    // =========================================================
    // LECTURA
    // =========================================================

    // Devuelve noticias y anuncios activos y vigentes.
    // Se usa en la pantalla de inicio para todos los usuarios.
    public List<Evento> listarParaInicio() {
        return eventoRepository.listarNoticiasActivas(LocalDate.now());
    }

    // Devuelve todas las noticias sin filtrar.
    // Se usa en el panel de gestión del administrador.
    public List<Evento> listarParaAdmin() {
        return eventoRepository.listarTodasParaAdmin();
    }

    // Busca un evento por su ID. Si no existe lanza excepción.
    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada con ID: " + id));
    }

    // Cuenta las noticias publicadas desde una fecha.
    // Se usa para el badge de "noticias nuevas" en el topbar.
    // Si el usuario no ha ingresado antes, se muestra desde hace 7 días.
    public long contarNoticiasNuevas(LocalDate desde) {
        if (desde == null) {
            desde = LocalDate.now().minusDays(7);
        }
        return eventoRepository.contarNoticiasDesde(desde);
    }

    // =========================================================
    // ESCRITURA
    // =========================================================

    // Crea una nueva noticia o anuncio.
    // El ID del usuario que la crea se obtiene del email del login.
    // ID_TIPOVISIBILIDAD = 1 (GENERAL) porque las noticias son para todos.
    // ID_ESTADO = 1 (ACTIVO) al crear.
    @Transactional
    public void crear(Evento evento, String emailUsuario) {
        Long idUsuario = obtenerIdUsuarioPorEmail(emailUsuario);
        eventoRepository.insertarEvento(
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaInicio() != null ? evento.getFechaInicio() : LocalDate.now(),
                evento.getFechaFin(),
                evento.getTipoEvento(),
                1L,       // ID_TIPOVISIBILIDAD = GENERAL
                idUsuario,
                1L        // ID_ESTADO = ACTIVO
        );
    }

    // Actualiza una noticia existente.
    // Se mantiene el mismo usuario creador original.
    @Transactional
    public void actualizar(Evento evento, String emailUsuario) {
        Long idUsuario = obtenerIdUsuarioPorEmail(emailUsuario);
        eventoRepository.modificarEvento(
                evento.getIdEvento(),
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getTipoEvento(),
                1L,       // ID_TIPOVISIBILIDAD = GENERAL
                idUsuario
        );
    }

    // Cambia el estado de una noticia: 1 = ACTIVO, 2 = INACTIVO.
    @Transactional
    public void cambiarEstado(Long idEvento, Long idEstado) {
        eventoRepository.cambiarEstadoEvento(idEvento, idEstado);
    }

    // =========================================================
    // UTIL — reutiliza el mismo patrón que los otros services
    // =========================================================
    private Long obtenerIdUsuarioPorEmail(String email) {
        var correo = correoRepository.findByCorreo(email);
        if (correo == null) {
            throw new RuntimeException("No se encontró usuario con email: " + email);
        }
        return correo.getUsuario().getIdUsuario();
    }
}