package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.MensajeriaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mensajeria")
public class MensajeriaController {

    private final MensajeriaService service;

    public MensajeriaController(MensajeriaService service) {
        this.service = service;
    }

    // BANDEJA DE ENTRADA
    @GetMapping
    public String bandeja(@AuthenticationPrincipal PortalUserDetails usuario, Model model) {
        Long idUsuario = usuario.getIdUsuario();
        model.addAttribute("conversaciones", service.bandeja(idUsuario));
        model.addAttribute("pageTitle", "Mensajería");
        return "mensajeria/Bandeja";
    }

    // VER CONVERSACIÓN CON SUS MENSAJES
    @GetMapping("/{idConversacion}")
    public String verConversacion(@PathVariable Long idConversacion,
                                  @AuthenticationPrincipal PortalUserDetails usuario,
                                  Model model) {

        Long idUsuario = usuario.getIdUsuario();

        // 🔥 SOLUCIÓN: marcar mensajes como leídos
        service.marcarLeidos(idConversacion, idUsuario);

        List<Map<String, Object>> mensajes = service.mensajes(idConversacion);
        Map<String, Object> conversacion   = service.conversacion(idConversacion);

        // Determinar el otro participante para la respuesta
        Long idOtro = null;
        if (!mensajes.isEmpty()) {
            Map<String, Object> primer = mensajes.get(0);
            Long idEmisor    = ((Number) primer.get("idEmisor")).longValue();
            Long idRemitente = ((Number) primer.get("idRemitente")).longValue();
            idOtro = idEmisor.equals(idUsuario) ? idRemitente : idEmisor;
        }

        // Inyectar archivos en cada mensaje
        for (Map<String, Object> msg : mensajes) {
            Long idMsg = ((Number) msg.get("idMensaje")).longValue();
            List<Map<String, Object>> archivos = service.archivos(idMsg);

            for (Map<String, Object> arch : archivos) {
                String ruta = (String) arch.get("rutaArchivo");
                String nombre = ruta != null ? ruta.replace("\\", "/") : "";

                if (nombre.contains("/")) {
                    nombre = nombre.substring(nombre.lastIndexOf("/") + 1);
                }

                arch.put("nombreArchivo", nombre);

                String ext = nombre.toLowerCase();
                arch.put("esImagen", ext.endsWith(".jpg") || ext.endsWith(".jpeg")
                        || ext.endsWith(".png") || ext.endsWith(".gif") || ext.endsWith(".webp"));

                arch.put("nombreLegible", nombre.contains("_")
                        ? nombre.substring(nombre.indexOf("_") + 1)
                        : nombre);
            }

            msg.put("archivos", archivos);
        }

        model.addAttribute("conversacion",      conversacion);
        model.addAttribute("mensajes",          mensajes);
        model.addAttribute("idConversacion",    idConversacion);
        model.addAttribute("idUsuario",         idUsuario);
        model.addAttribute("idOtro",            idOtro);
        model.addAttribute("pageTitle",         "Mensajería");

        return "mensajeria/Conversacion";
    }

    // FORMULARIO NUEVO MENSAJE
    @GetMapping("/nuevo")
    public String formularioNuevo(@AuthenticationPrincipal PortalUserDetails usuario, Model model) {
        model.addAttribute("usuarios",  service.usuariosDisponibles(usuario.getIdUsuario()));
        model.addAttribute("pageTitle", "Nuevo Mensaje");
        return "mensajeria/Nuevo";
    }

    // ENVIAR NUEVO MENSAJE
    @PostMapping("/nuevo")
    public String enviarNuevo(@RequestParam String asunto,
                              @RequestParam String contenido,
                              @RequestParam Long idRemitente,
                              @RequestParam(required = false) List<MultipartFile> archivos,
                              @AuthenticationPrincipal PortalUserDetails usuario,
                              RedirectAttributes ra) {
        try {
            Long idConversacion = service.nuevoMensajeDirecto(
                    asunto, contenido, usuario.getIdUsuario(), idRemitente, archivos);
            ra.addFlashAttribute("mensajeExito", "Mensaje enviado correctamente.");
            return "redirect:/mensajeria/" + idConversacion;
        } catch (IOException e) {
            ra.addFlashAttribute("mensajeError", "Error al subir archivos: " + e.getMessage());
            return "redirect:/mensajeria/nuevo";
        }
    }

    // RESPONDER EN CONVERSACIÓN EXISTENTE
    @PostMapping("/{idConversacion}/responder")
    public String responder(@PathVariable Long idConversacion,
                            @RequestParam String contenido,
                            @RequestParam Long idRemitente,
                            @RequestParam(required = false) List<MultipartFile> archivos,
                            @AuthenticationPrincipal PortalUserDetails usuario,
                            RedirectAttributes ra) {
        try {
            service.responder(idConversacion, contenido,
                              usuario.getIdUsuario(), idRemitente, archivos);
        } catch (IOException e) {
            ra.addFlashAttribute("mensajeError", "Error al subir archivos: " + e.getMessage());
        }
        return "redirect:/mensajeria/" + idConversacion;
    }

    // ELIMINAR MENSAJE
    @PostMapping("/mensaje/{idMensaje}/eliminar")
    public String eliminarMensaje(@PathVariable Long idMensaje,
                                  @RequestParam Long idConversacion,
                                  RedirectAttributes ra) {
        service.eliminarMensaje(idMensaje);
        ra.addFlashAttribute("mensajeExito", "Mensaje eliminado.");
        return "redirect:/mensajeria/" + idConversacion;
    }

    // ELIMINAR CONVERSACIÓN
    @PostMapping("/{idConversacion}/eliminar")
    public String eliminarConversacion(@PathVariable Long idConversacion,
                                       RedirectAttributes ra) {
        service.eliminarConversacion(idConversacion);
        ra.addFlashAttribute("mensajeExito", "Conversación eliminada.");
        return "redirect:/mensajeria";
    }

    // ELIMINAR ARCHIVO
    @PostMapping("/archivo/{idArchivo}/eliminar")
    public String eliminarArchivo(@PathVariable Long idArchivo,
                                  @RequestParam Long idConversacion,
                                  RedirectAttributes ra) {
        service.eliminarArchivo(idArchivo);
        return "redirect:/mensajeria/" + idConversacion;
    }
}