package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository.HorarioRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.HorarioRepository.SeccionEncargadoRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.SeccionRepository.SeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.HorarioService;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.SeccionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/horarios")
public class HorarioVistaController {

    private final HorarioService horarioService;
    private final SeccionService seccionService;

    public HorarioVistaController(HorarioService horarioService, SeccionService seccionService) {
        this.horarioService = horarioService;
        this.seccionService = seccionService;
    }

    public record OpcionSeccion(Long idSeccion, String etiqueta) {

        public Long getIdSeccion() {
            return idSeccion;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }

    @GetMapping
    public String ver(@RequestParam(required = false) Long idSeccion,
            @AuthenticationPrincipal PortalUserDetails usuario,
            Model model) {

        boolean esEncargado = usuario != null && "ENCARGADO".equals(usuario.getRole());

        List<OpcionSeccion> opciones = new ArrayList<>();
        String seccionSelLabel = null;

        if (esEncargado) {
            // El encargado solo puede ver las secciones de sus estudiantes ligados.
            List<SeccionEncargadoRow> secciones =
                    horarioService.seccionesDeEncargado(usuario.getIdUsuario());

            for (SeccionEncargadoRow s : secciones) {
                opciones.add(new OpcionSeccion(s.getIdSeccion(),
                        s.getNombreEstudiante() + " — Sección " + s.getNumeroSeccion()
                        + " · " + s.getPeriodo()));
            }

            // Restringir la selección: si pide una sección que no es de sus estudiantes,
            // se ignora y se cae al primer valor permitido.
            final Long pedido = idSeccion;
            boolean permitida = pedido != null
                    && secciones.stream().anyMatch(s -> s.getIdSeccion().equals(pedido));
            if (!permitida) {
                idSeccion = secciones.isEmpty() ? null : secciones.get(0).getIdSeccion();
            }
            final Long sel = idSeccion;
            seccionSelLabel = secciones.stream()
                    .filter(s -> s.getIdSeccion().equals(sel))
                    .map(s -> "Sección " + s.getNumeroSeccion() + " · " + s.getPeriodo())
                    .findFirst().orElse(null);

        } else {
            // Admin / Profesor: todas las secciones.
            List<SeccionRow> secciones = seccionService.listarResumen();

            for (SeccionRow s : secciones) {
                opciones.add(new OpcionSeccion(s.getIdSeccion(),
                        "Sección " + s.getNumero() + " · " + s.getNombrePeriodo()));
            }

            if (idSeccion == null && !secciones.isEmpty()) {
                idSeccion = secciones.get(0).getIdSeccion();
            }
            final Long sel = idSeccion;
            seccionSelLabel = secciones.stream()
                    .filter(s -> s.getIdSeccion().equals(sel))
                    .map(s -> "Sección " + s.getNumero() + " · " + s.getNombrePeriodo())
                    .findFirst().orElse(null);
        }

        List<HorarioRow> bloques = idSeccion != null
                ? horarioService.horarioPorSeccion(idSeccion)
                : List.of();

        model.addAttribute("esEncargado", esEncargado);
        model.addAttribute("opciones", opciones);
        model.addAttribute("idSeccionSel", idSeccion);
        model.addAttribute("seccionSelLabel", seccionSelLabel);
        model.addAttribute("dias", horarioService.agruparPorDia(bloques));
        model.addAttribute("pageTitle", "Horarios");
        return "horarios/horarioSeccion";
    }
}
