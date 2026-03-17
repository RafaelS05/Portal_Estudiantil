package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CorreoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CorreoRepository correoRepository;

    public EventoService(EventoRepository eventoRepository,
                         CorreoRepository correoRepository) {
        this.eventoRepository = eventoRepository;
        this.correoRepository = correoRepository;
    }

    // =========================================================
    // LECTURA
    // =========================================================

    // Lista todos los eventos para el panel de gestión del admin.
    // Incluye activos e inactivos para que el admin tenga control total.
    public List<Evento> listarParaAdmin() {
        return eventoRepository.listarEventosParaAdmin();
    }

    // Lista eventos activos y vigentes para el calendario.
    // Lo usan todos los roles — admin, profesor, encargado.
    public List<Evento> listarParaCalendario() {
        return eventoRepository.listarEventosActivosParaCalendario(LocalDate.now());
    }

    // Busca un evento por ID. Si no existe lanza excepción.
    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
    }

    // =========================================================
    // ESCRITURA
    // =========================================================

    // Crea un nuevo evento institucional.
    // ID_TIPOVISIBILIDAD = 1 (GENERAL) → visible para todos.
    // ID_ESTADO = 1 (ACTIVO) al crear.
    @Transactional
    public void crear(Evento evento, String emailUsuario) {
        Long idUsuario = obtenerIdUsuarioPorEmail(emailUsuario);
        eventoRepository.insertarEvento(
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getTipoEvento(),
                1L,       // GENERAL
                idUsuario,
                1L        // ACTIVO
        );
    }

    // Actualiza un evento existente.
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
                1L,       // GENERAL
                idUsuario
        );
    }

    // Cambia el estado: 1 = ACTIVO (visible), 2 = INACTIVO (oculto).
    @Transactional
    public void cambiarEstado(Long idEvento, Long idEstado) {
        eventoRepository.cambiarEstadoEvento(idEvento, idEstado);
    }

    // =========================================================
    // UTIL
    // =========================================================
    private Long obtenerIdUsuarioPorEmail(String email) {
        var correo = correoRepository.findByCorreo(email);
        if (correo == null) {
            throw new RuntimeException("No se encontró usuario con email: " + email);
        }
        return correo.getUsuario().getIdUsuario();
    }
}