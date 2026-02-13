package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping({"", "/"})
    public String listarAsistencia(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {
            
        var asistencias = asistenciaService.listarAsistencias(busqueda);
        
        model.addAttribute("pageTitle", "Asistencia estudiantil");
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("totalRegistros", asistencias.size());
        model.addAttribute("busqueda", busqueda);

        return "asistencia/listado"; // Ruta hacia el HTML
    }
}