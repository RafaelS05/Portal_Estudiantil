package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recuperarContrasenna")
public class RecuperarContrasennaController {
    
    @GetMapping("/recuperar-contrasenna")
    public String Inicio(){
        return "recuperarContrasenna/recuperar-contrasenna";
    }
}
