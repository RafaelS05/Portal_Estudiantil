package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.InformacionPersonalService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.UbicacionService;
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
    public String verPerfil(Model model) {

        Long idUsuario = 1L; // luego se obtiene del usuario logueado

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
            @RequestParam(required = false) String otrasSenas
    ) {

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
