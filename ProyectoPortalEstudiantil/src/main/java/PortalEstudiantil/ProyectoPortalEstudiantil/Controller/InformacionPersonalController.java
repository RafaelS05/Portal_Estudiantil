/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.InformacionPersonalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/informacion-personal")
public class InformacionPersonalController {

    private final InformacionPersonalService service;

    public InformacionPersonalController(InformacionPersonalService service) {
        this.service = service;
    }

    //  MOSTRAR PERFIL
    @GetMapping
    public String verPerfil(Model model) {

        Long idUsuario = 1L; // luego se obtiene del usuario logueado

        model.addAttribute("usuario", service.obtenerUsuario(idUsuario));
        model.addAttribute("telefono", service.obtenerTelefono(idUsuario));
        model.addAttribute("correo", service.obtenerCorreoLogin(idUsuario));
        model.addAttribute("direccion", service.obtenerDireccion(idUsuario));
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
            @RequestParam String otrasSenas
    ) {

        service.actualizarInformacion(usuario, correo, numero, otrasSenas);
        return "redirect:/informacion-personal";
    }

}
