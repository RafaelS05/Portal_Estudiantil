package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Periodo;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AlertaEstadisticaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.AsistenciaEstudianteRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorMateriaRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.CalificacionPorSeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.Estadisticas.KpiGeneralRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.EstadisticasRepository.SeccionRow;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.EstadisticasService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.*;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // =========================================================
    // DTO interno — agrupa todos los datos del dashboard admin
    // para reutilizarlos entre la vista y las exportaciones.
    // =========================================================
    private record DashboardData(
            Periodo periodo,
            KpiGeneralRow kpi,
            List<SeccionRow> secciones,
            String nombreSeccionFiltrada,
            List<CalificacionPorSeccionRow> porSeccion,
            List<CalificacionPorMateriaRow> porMateria,
            List<AlertaEstadisticaRow> alertas
            ) {

    }

    private DashboardData construirDatosDashboard(Periodo periodo, Long idSeccion) {
        Long idPeriodo = periodo.getIdPeriodo();

        KpiGeneralRow kpi = estadisticasService.obtenerKpiGeneral(idPeriodo);
        List<SeccionRow> secciones = estadisticasService.obtenerSecciones(idPeriodo);

        List<CalificacionPorSeccionRow> porSeccion
                = estadisticasService.obtenerCalificacionesPorSeccion(idPeriodo);

        String nombreSeccionFiltrada = null;

        if (idSeccion != null) {
            nombreSeccionFiltrada = secciones.stream()
                    .filter(s -> s.getIdSeccion().equals(idSeccion))
                    .map(SeccionRow::getNumero)
                    .findFirst()
                    .orElse(null);

            if (nombreSeccionFiltrada != null) {
                final String numeroFinal = nombreSeccionFiltrada;
                porSeccion = porSeccion.stream()
                        .filter(r -> r.getSeccion().equals(numeroFinal))
                        .toList();
            }
        }

        List<CalificacionPorMateriaRow> porMateria
                = estadisticasService.obtenerCalificacionesPorMateria(idPeriodo, idSeccion);

        List<AlertaEstadisticaRow> alertas
                = estadisticasService.obtenerAlertas(idPeriodo, idSeccion);

        return new DashboardData(periodo, kpi, secciones, nombreSeccionFiltrada,
                porSeccion, porMateria, alertas);
    }

    // =========================================================
    // PANEL ADMINISTRADOR / DIRECTOR
    // =========================================================
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public String admin(@RequestParam(value = "idSeccion", required = false) Long idSeccion,
            Model model) {

        Periodo periodo = estadisticasService.obtenerPeriodoActual();
        if (periodo == null) {
            model.addAttribute("sinPeriodo", true);
            return "estadisticas/admin";
        }

        DashboardData datos = construirDatosDashboard(periodo, idSeccion);

        String seccionLabels = toJsonStringArray(datos.porSeccion().stream()
                .map(r -> "Sección " + r.getSeccion()).toList());
        String seccionPromedios = toJsonDoubleArray(datos.porSeccion().stream()
                .map(CalificacionPorSeccionRow::getPromedio).toList());

        String materiaLabels = toJsonStringArray(datos.porMateria().stream()
                .map(CalificacionPorMateriaRow::getMateria).toList());
        String materiaPromedios = toJsonDoubleArray(datos.porMateria().stream()
                .map(CalificacionPorMateriaRow::getPromedio).toList());

        model.addAttribute("periodo", datos.periodo());
        model.addAttribute("kpi", datos.kpi());
        model.addAttribute("secciones", datos.secciones());
        model.addAttribute("idSeccionActual", idSeccion);
        model.addAttribute("alertas", datos.alertas());
        model.addAttribute("seccionLabels", seccionLabels);
        model.addAttribute("seccionPromedios", seccionPromedios);
        model.addAttribute("materiaLabels", materiaLabels);
        model.addAttribute("materiaPromedios", materiaPromedios);
        model.addAttribute("pageTitle", "Estadísticas");

        return "estadisticas/admin";
    }

    // =========================================================
    // EXPORTAR — EXCEL (respeta el filtro de sección aplicado)
    // =========================================================
    @GetMapping("/admin/exportar/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public void exportarExcel(@RequestParam(value = "idSeccion", required = false) Long idSeccion,
            HttpServletResponse response) throws IOException {

        Periodo periodo = estadisticasService.obtenerPeriodoActual();
        DashboardData datos = construirDatosDashboard(periodo, idSeccion);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombreArchivo = "reporte_estadisticas_" + datos.periodo().getNombre().replace(" ", "_") + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            CellStyle estiloTitulo = workbook.createCellStyle();
            Font fuenteTitulo = workbook.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 12);
            estiloTitulo.setFont(fuenteTitulo);

            CellStyle estiloEncabezado = workbook.createCellStyle();
            Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            estiloEncabezado.setFont(fuenteEncabezado);
            estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ── Hoja 1: Resumen ──────────────────────────────
            Sheet hojaResumen = workbook.createSheet("Resumen");
            int fila = 0;

            Row filaTitulo = hojaResumen.createRow(fila++);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue("Reporte de Estadísticas — Período: " + datos.periodo().getNombre());
            celdaTitulo.setCellStyle(estiloTitulo);

            Row filaFiltro = hojaResumen.createRow(fila++);
            filaFiltro.createCell(0).setCellValue("Filtro aplicado: "
                    + (datos.nombreSeccionFiltrada() != null ? "Sección " + datos.nombreSeccionFiltrada() : "Todas las secciones"));
            fila++;

            if (datos.kpi() != null) {
                escribirFilaKpi(hojaResumen, fila++, "Promedio Global", datos.kpi().getPromedioGlobal());
                escribirFilaKpi(hojaResumen, fila++, "% Asistencia Global", datos.kpi().getPorcentajeAsistenciaGlobal());
                escribirFilaKpi(hojaResumen, fila++, "Estudiantes Activos", datos.kpi().getTotalEstudiantesActivos());
                escribirFilaKpi(hojaResumen, fila++, "Alertas Activas", datos.kpi().getTotalAlertasCalificacion());
            }
            hojaResumen.autoSizeColumn(0);
            hojaResumen.autoSizeColumn(1);

            // ── Hoja 2: Calificaciones por Sección ───────────
            Sheet hojaSeccion = workbook.createSheet("Por Seccion");
            Row encSeccion = hojaSeccion.createRow(0);
            String[] colsSeccion = {"Sección", "Período", "Promedio", "Total Estudiantes"};
            for (int i = 0; i < colsSeccion.length; i++) {
                Cell c = encSeccion.createCell(i);
                c.setCellValue(colsSeccion[i]);
                c.setCellStyle(estiloEncabezado);
            }
            int filaSeccion = 1;
            for (CalificacionPorSeccionRow r : datos.porSeccion()) {
                Row row = hojaSeccion.createRow(filaSeccion++);
                row.createCell(0).setCellValue("Sección " + r.getSeccion());
                row.createCell(1).setCellValue(r.getPeriodo());
                row.createCell(2).setCellValue(r.getPromedio() != null ? r.getPromedio() : 0);
                row.createCell(3).setCellValue(r.getTotalEstudiantes());
            }
            for (int i = 0; i < colsSeccion.length; i++) {
                hojaSeccion.autoSizeColumn(i);
            }

            // ── Hoja 3: Calificaciones por Materia ───────────
            Sheet hojaMateria = workbook.createSheet("Por Materia");
            Row encMateria = hojaMateria.createRow(0);
            String[] colsMateria = {"Materia", "Promedio", "Total Evaluaciones"};
            for (int i = 0; i < colsMateria.length; i++) {
                Cell c = encMateria.createCell(i);
                c.setCellValue(colsMateria[i]);
                c.setCellStyle(estiloEncabezado);
            }
            int filaMateria = 1;
            for (CalificacionPorMateriaRow r : datos.porMateria()) {
                Row row = hojaMateria.createRow(filaMateria++);
                row.createCell(0).setCellValue(r.getMateria());
                row.createCell(1).setCellValue(r.getPromedio() != null ? r.getPromedio() : 0);
                row.createCell(2).setCellValue(r.getTotalEvaluaciones());
            }
            for (int i = 0; i < colsMateria.length; i++) {
                hojaMateria.autoSizeColumn(i);
            }

            // ── Hoja 4: Alertas ───────────────────────────────
            Sheet hojaAlertas = workbook.createSheet("Alertas");
            Row encAlertas = hojaAlertas.createRow(0);
            String[] colsAlertas = {"Tipo", "Nombre", "Promedio", "Estudiantes Afectados"};
            for (int i = 0; i < colsAlertas.length; i++) {
                Cell c = encAlertas.createCell(i);
                c.setCellValue(colsAlertas[i]);
                c.setCellStyle(estiloEncabezado);
            }
            int filaAlertas = 1;
            for (AlertaEstadisticaRow a : datos.alertas()) {
                Row row = hojaAlertas.createRow(filaAlertas++);
                row.createCell(0).setCellValue(a.getTipo());
                row.createCell(1).setCellValue(a.getNombre());
                row.createCell(2).setCellValue(a.getPromedio() != null ? a.getPromedio() : 0);
                row.createCell(3).setCellValue(a.getTotalAfectados());
            }
            for (int i = 0; i < colsAlertas.length; i++) {
                hojaAlertas.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private void escribirFilaKpi(Sheet hoja, int numeroFila, String etiqueta, Object valor) {
        Row row = hoja.createRow(numeroFila);
        row.createCell(0).setCellValue(etiqueta);
        Cell celdaValor = row.createCell(1);
        if (valor instanceof Number n) {
            celdaValor.setCellValue(n.doubleValue());
        } else {
            celdaValor.setCellValue(valor != null ? valor.toString() : "—");
        }
    }

    // =========================================================
    // EXPORTAR — PDF (respeta el filtro de sección aplicado)
    // =========================================================
    @GetMapping("/admin/exportar/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR')")
    public void exportarPdf(@RequestParam(value = "idSeccion", required = false) Long idSeccion,
            HttpServletResponse response) throws IOException {

        Periodo periodo = estadisticasService.obtenerPeriodoActual();
        DashboardData datos = construirDatosDashboard(periodo, idSeccion);

        response.setContentType("application/pdf");
        String nombreArchivo = "reporte_estadisticas_" + datos.periodo().getNombre().replace(" ", "_") + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream()); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("Reporte de Estadísticas").setFontSize(18).setBold());
            document.add(new Paragraph("Período: " + datos.periodo().getNombre()));
            document.add(new Paragraph("Filtro aplicado: "
                    + (datos.nombreSeccionFiltrada() != null ? "Sección " + datos.nombreSeccionFiltrada() : "Todas las secciones"))
                    .setMarginBottom(15));

            if (datos.kpi() != null) {
                document.add(new Paragraph("Resumen General").setBold().setFontSize(14));
                Table tablaKpi = new Table(2).useAllAvailableWidth();
                agregarFilaTabla(tablaKpi, "Promedio Global", String.valueOf(datos.kpi().getPromedioGlobal()));
                agregarFilaTabla(tablaKpi, "% Asistencia Global", datos.kpi().getPorcentajeAsistenciaGlobal() + "%");
                agregarFilaTabla(tablaKpi, "Estudiantes Activos", String.valueOf(datos.kpi().getTotalEstudiantesActivos()));
                agregarFilaTabla(tablaKpi, "Alertas Activas", String.valueOf(datos.kpi().getTotalAlertasCalificacion()));
                document.add(tablaKpi);
            }

            document.add(new Paragraph("Calificaciones por Sección").setBold().setFontSize(14).setMarginTop(15));
            Table tablaSeccion = new Table(3).useAllAvailableWidth();
            agregarEncabezados(tablaSeccion, "Sección", "Promedio", "Total Estudiantes");
            for (CalificacionPorSeccionRow r : datos.porSeccion()) {
                tablaSeccion.addCell(String.valueOf("Sección " + r.getSeccion()));
                tablaSeccion.addCell(String.valueOf(r.getPromedio()));
                tablaSeccion.addCell(String.valueOf(r.getTotalEstudiantes()));
            }
            document.add(tablaSeccion);

            document.add(new Paragraph("Calificaciones por Materia").setBold().setFontSize(14).setMarginTop(15));
            Table tablaMateria = new Table(3).useAllAvailableWidth();
            agregarEncabezados(tablaMateria, "Materia", "Promedio", "Total Evaluaciones");
            for (CalificacionPorMateriaRow r : datos.porMateria()) {
                tablaMateria.addCell(r.getMateria());
                tablaMateria.addCell(String.valueOf(r.getPromedio()));
                tablaMateria.addCell(String.valueOf(r.getTotalEvaluaciones()));
            }
            document.add(tablaMateria);

            document.add(new Paragraph("Alertas").setBold().setFontSize(14).setMarginTop(15));
            if (datos.alertas().isEmpty()) {
                document.add(new Paragraph("No hay alertas para los filtros actuales."));
            } else {
                Table tablaAlertas = new Table(4).useAllAvailableWidth();
                agregarEncabezados(tablaAlertas, "Tipo", "Nombre", "Promedio", "Afectados");
                for (AlertaEstadisticaRow a : datos.alertas()) {
                    tablaAlertas.addCell(a.getTipo());
                    tablaAlertas.addCell(a.getNombre());
                    tablaAlertas.addCell(String.valueOf(a.getPromedio()));
                    tablaAlertas.addCell(String.valueOf(a.getTotalAfectados()));
                }
                document.add(tablaAlertas);
            }
        }
    }

    private void agregarFilaTabla(Table tabla, String etiqueta, String valor) {
        tabla.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(etiqueta).setBold()));
        tabla.addCell(new Paragraph(valor));
    }

    private void agregarEncabezados(Table tabla, String... encabezados) {
        for (String e : encabezados) {
            tabla.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(e).setBold()));
        }
    }

    // =========================================================
    // PANEL ENCARGADO (PADRE DE FAMILIA)
    // =========================================================
    @GetMapping("/encargado")
    @PreAuthorize("hasRole('ENCARGADO')")
    public String panelEncargado(Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Periodo periodo = estadisticasService.obtenerPeriodoActual();
            Long idPeriodo = periodo.getIdPeriodo();

            List<AsistenciaEstudianteRow> asistencias
                    = estadisticasService.obtenerAsistenciaHijos(authentication, idPeriodo);

            String hijosLabels = toJsonStringArray(asistencias.stream()
                    .map(AsistenciaEstudianteRow::getNombreEstudiante).toList());
            String hijosAsistencia = toJsonDoubleArray(asistencias.stream()
                    .map(r -> r.getPorcentajeAsistencia() != null ? r.getPorcentajeAsistencia() : 0.0).toList());

            model.addAttribute("periodo", periodo);
            model.addAttribute("asistencias", asistencias);
            model.addAttribute("hijosLabels", hijosLabels);
            model.addAttribute("hijosAsistencia", hijosAsistencia);
            model.addAttribute("horaActualizacion", LocalDateTime.now());

            return "estadisticas/encargado";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar estadísticas: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/";
        }
    }

    // =========================================================
    // REDIRECCIONADOR — link del sidebar /estadisticas
    // =========================================================
    @GetMapping({"", "/"})
    public String redirigir(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ENCARGADO"))) {
            return "redirect:/estadisticas/encargado";
        }
        return "redirect:/estadisticas/admin";
    }

    // =========================================================
    // UTILIDADES
    // =========================================================
    private String toJsonStringArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(
                    list.get(i) == null ? "" : list.get(i).replace("\"", "'")
            ).append("\"");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.append("]").toString();
    }

    private String toJsonDoubleArray(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.append("]").toString();
    }
}
