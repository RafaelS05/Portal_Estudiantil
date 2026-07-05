package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evento;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/calendario")
public class CalendarioController {

    private final EventoService eventoService;

    public CalendarioController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // Pantalla del calendario — accesible para todos los roles.
    @GetMapping
    public String calendario(Model model) {
        model.addAttribute("pageTitle", "Calendario");
        return "calendario/index";
    }

    // Endpoint AJAX que devuelve los eventos en formato JSON para FullCalendar.
    // FullCalendar necesita: title, start, end, color.
    // Se llama automáticamente cuando el usuario navega entre meses en el calendario
    // y también cada vez que se cambia el filtro de tipo (refetchEvents en el front).
    @GetMapping("/eventos")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerEventosJson() {
        List<Evento> eventos = eventoService.listarParaCalendario();

        List<Map<String, Object>> resultado = eventos.stream().map(e -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getIdEvento());
            map.put("title", e.getTitulo());
            // FullCalendar usa formato ISO (yyyy-MM-dd)
            map.put("start", e.getFechaInicio().toString());
            // Si tiene fecha fin, FullCalendar necesita el día siguiente para incluirlo
            if (e.getFechaFin() != null) {
                map.put("end", e.getFechaFin().plusDays(1).toString());
            }
            map.put("description", e.getDescripcion() != null ? e.getDescripcion() : "");
            // Tipo de evento — se usa para filtrar en el front-end
            map.put("tipo", e.getTipoEvento());
            // Color por tipo de evento
            map.put("backgroundColor", colorPorTipo(e.getTipoEvento()));
            map.put("borderColor", colorPorTipo(e.getTipoEvento()));
            map.put("textColor", "#ffffff");
            return map;
        }).toList();

        return ResponseEntity.ok(resultado);
    }

    // Asigna un color distinto a cada tipo de evento para diferenciarlo visualmente.
    private String colorPorTipo(String tipo) {
        if (tipo == null) {
            return "#6c757d";
        }
        return switch (tipo) {
            case "REUNION" ->
                "#1976d2"; // azul
            case "ACTIVIDAD" ->
                "#388e3c"; // verde
            case "FERIADO" ->
                "#d32f2f"; // rojo
            case "EXAMEN" ->
                "#7b1fa2"; // morado
            default ->
                "#f57c00"; // naranja (OTRO)
        };
    }
}
