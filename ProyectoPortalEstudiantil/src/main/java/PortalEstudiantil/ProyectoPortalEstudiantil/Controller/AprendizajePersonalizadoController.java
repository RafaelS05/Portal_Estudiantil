package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.AprendizajePersonalizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/aprendizaje-personalizado")
public class AprendizajePersonalizadoController {

    @Autowired
    private AprendizajePersonalizadoService service;

    @GetMapping
    public String verEstrategias(
            @AuthenticationPrincipal PortalUserDetails usuarioActual,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            Model model) {

        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("calificacion").descending());

        boolean esEstudiante = usuarioActual.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));

        Page<Map<String, Object>> estudiantes;

        if (esEstudiante) {
            estudiantes = service.obtenerEstrategiaParaEstudiante(usuarioActual.getIdUsuario(), pageable);
        } else {
            estudiantes = service.obtenerEstudiantesConEstrategia(busqueda, pageable);
        }

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", estudiantes.getTotalPages());
        model.addAttribute("totalElementos", estudiantes.getTotalElements());
        model.addAttribute("esEstudiante", esEstudiante);

        return "aprendizaje_personalizado/listado";
    }

    @GetMapping("/estudiante/{id}")
    public String verDetalleEstudiante(@RequestParam Long id, Model model) {
        return "aprendizaje_personalizado/detalle-estudiante";
    }
}