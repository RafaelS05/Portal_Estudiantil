package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inicio")
public class InicioController {
    
    @GetMapping("/index")
    public String Inicio(){
        return "inicio/index";
    }
    
}
