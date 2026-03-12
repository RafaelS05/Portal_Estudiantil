package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.InformacionPersonalService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UbicacionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/informacion-personal")
public class InformacionPersonalController {

    private final InformacionPersonalService service;
    private final UbicacionService ubicacionService;

    public InformacionPersonalController(
            InformacionPersonalService service,
            UbicacionService ubicacionService) {
        this.service = service;
        this.ubicacionService = ubicacionService;
    }

    // MOSTRAR PERFIL
    @GetMapping
    public String verPerfil(Model model,
            @AuthenticationPrincipal PortalUserDetails userDetails) { // ← CAMBIADO

        Long idUsuario = service.obtenerIdUsuarioPorEmail(userDetails.getUsername()); // ← CAMBIADO

        Usuario usuario = service.obtenerUsuario(idUsuario);
        Telefono telefono = service.obtenerTelefono(idUsuario);
        Correo correo = service.obtenerCorreoLogin(idUsuario);
        Direccion direccion = service.obtenerDireccion(idUsuario);

        // Proveer listas para dropdowns
        List<Provincia> provincias = ubicacionService.listarProvincias();
        List<Canton> cantones;
        List<Distrito> distritos;

        if (direccion != null && direccion.getProvincia() != null) {
            cantones = ubicacionService.listarCantonesPorProvincia(direccion.getProvincia().getIdProvincia());
        } else {
            cantones = List.of();
        }

        if (direccion != null && direccion.getCanton() != null) {
            distritos = ubicacionService.listarDistritosPorCanton(direccion.getCanton().getIdCanton());
        } else {
            distritos = List.of();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("telefono", telefono);
        model.addAttribute("correo", correo);
        model.addAttribute("direccion", direccion);
        model.addAttribute("provincias", provincias);
        model.addAttribute("cantones", cantones);
        model.addAttribute("distritos", distritos);
        model.addAttribute("titulo", "Información Personal");
        model.addAttribute("contenido", "informacion_personal");
        model.addAttribute("pageTitle", "Información personal");

        // ← AGREGADO: sección hijos para Encargados (ID tipo 4)
        if (usuario != null && Long.valueOf(4L).equals(usuario.getIdTipoUsuarioFk())) {
            List<EncargadoEstudiante> relaciones = service.obtenerHijosVinculados(idUsuario);
            List<Usuario> hijos = relaciones.stream()
                    .map(r -> service.obtenerUsuario(r.getIdUsuarioEstudianteFk()))
                    .filter(u -> u != null)
                    .toList();
            model.addAttribute("relaciones", relaciones);
            model.addAttribute("hijos", hijos);
            model.addAttribute("esEncargado", true);
        } else {
            model.addAttribute("esEncargado", false);
        }

        return "informacion_personal/informacion_personal";
    }

    // GUARDAR CAMBIOS
    @PostMapping("/guardar")
    public String guardarPerfil(
            @ModelAttribute Usuario usuario,
            @RequestParam String correo,
            @RequestParam String numero,
            @RequestParam(required = false) Long idProvincia,
            @RequestParam(required = false) Long idCanton,
            @RequestParam(required = false) Long idDistrito,
            @RequestParam(required = false) String otrasSenas,
            @AuthenticationPrincipal PortalUserDetails userDetails // ← AGREGADO
    ) {
        // ← AGREGADO: forzar el ID del usuario logueado por seguridad
        Long idUsuario = service.obtenerIdUsuarioPorEmail(userDetails.getUsername());
        usuario.setIdUsuario(idUsuario);

        service.actualizarInformacion(
                usuario,
                correo,
                numero,
                idProvincia,
                idCanton,
                idDistrito,
                otrasSenas
        );
        return "redirect:/informacion-personal";
    }

    // ENDPOINTS PARA DROPDOWNS DINÁMICOS
    @GetMapping("/cantones")
    @ResponseBody
    public List<Canton> obtenerCantones(@RequestParam Long idProvincia) {
        return ubicacionService.listarCantonesPorProvincia(idProvincia);
    }

    @GetMapping("/distritos")
    @ResponseBody
    public List<Distrito> obtenerDistritos(@RequestParam Long idCanton) {
        return ubicacionService.listarDistritosPorCanton(idCanton);
    }
}
