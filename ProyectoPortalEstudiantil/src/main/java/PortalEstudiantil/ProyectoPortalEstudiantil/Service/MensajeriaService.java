package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.MensajeriaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MensajeriaService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final MensajeriaRepository repo;

    public MensajeriaService(MensajeriaRepository repo) {
        this.repo = repo;
    }

    public List<Map<String, Object>> bandeja(Long idUsuario) {
        return repo.listarConversaciones(idUsuario);
    }

    public List<Map<String, Object>> mensajes(Long idConversacion) {
        return repo.listarMensajes(idConversacion);
    }

    public List<Map<String, Object>> archivos(Long idMensaje) {
        return repo.listarArchivos(idMensaje);
    }

    public Map<String, Object> conversacion(Long idConversacion) {
        return repo.obtenerConversacion(idConversacion);
    }

    public List<Map<String, Object>> usuariosDisponibles(Long idUsuario) {
        return repo.listarUsuariosActivos(idUsuario);
    }

    public int contarNoLeidos(Long idUsuario) {
        return repo.contarNoLeidos(idUsuario);
    }

    // Crear conversación directa y enviar el primer mensaje
    public Long nuevoMensajeDirecto(String asunto, String contenido,
                                    Long idEmisor, Long idRemitente,
                                    List<MultipartFile> archivos) throws IOException {

        String preview = contenido.length() > 80
                ? contenido.substring(0, 77) + "..."
                : contenido;

        Long idTipo = repo.idTipoDirecto();
        String titulo = asunto != null && !asunto.isBlank() ? asunto : "Conversación directa";

        Long idConversacion = repo.insertarConversacion(titulo, preview, idTipo.intValue(), idEmisor);
        Long idMensaje = repo.insertarMensaje(asunto, contenido, idConversacion, idEmisor, idRemitente);

        guardarArchivos(archivos, idMensaje);
        return idConversacion;
    }

    // Responder en una conversación existente
    public void responder(Long idConversacion, String contenido,
                          Long idEmisor, Long idRemitente,
                          List<MultipartFile> archivos) throws IOException {

        String preview = contenido.length() > 80
                ? contenido.substring(0, 77) + "..."
                : contenido;

        Map<String, Object> conv = repo.obtenerConversacion(idConversacion);
        Long idTipo    = ((Number) conv.get("ID_TIPOCONVERSACION_FK")).longValue();
        Long idCreador = ((Number) conv.get("ID_CREADO_POR_FK")).longValue();
        String titulo  = (String) conv.get("TITULO");

        Long idMensaje = repo.insertarMensaje(null, contenido, idConversacion, idEmisor, idRemitente);
        repo.actualizarConversacion(idConversacion, preview, titulo, idTipo.intValue(), idCreador);

        guardarArchivos(archivos, idMensaje);
    }

    public void eliminarMensaje(Long idMensaje) {
        repo.eliminarMensaje(idMensaje);
    }

    public void eliminarConversacion(Long idConversacion) {
        repo.eliminarConversacion(idConversacion);
    }

    public void eliminarArchivo(Long idArchivo) {
        repo.eliminarArchivo(idArchivo);
    }

    // Guardar archivos en disco y registrar en BD
    private void guardarArchivos(List<MultipartFile> archivos, Long idMensaje) throws IOException {
        if (archivos == null || archivos.isEmpty()) return;
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        for (MultipartFile archivo : archivos) {
            if (archivo == null || archivo.isEmpty()) continue;
            String nombre = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path destino  = dir.resolve(nombre);
            Files.copy(archivo.getInputStream(), destino);
            // Solo guardamos el nombre — la URL se construye en la vista como /uploads/mensajes/nombre
            repo.insertarArchivo(nombre, idMensaje);
        }
    }
    public void marcarLeidos(Long idConversacion, Long idUsuario) {
    repo.marcarLeidosEnConversacion(idConversacion, idUsuario);
}
}