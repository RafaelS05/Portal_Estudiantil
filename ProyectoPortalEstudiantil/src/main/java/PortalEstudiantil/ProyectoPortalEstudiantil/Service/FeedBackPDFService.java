package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.FeedBackResumen;
import com.lowagie.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class FeedBackPDFService {

    private static final String AZUL = "#1a73e8";
    private static final String AZUL_BG = "#e8f0fe";
    private static final String VERDE = "#34a853";
    private static final String ROJO = "#ea4335";
    private static final String NARANJA = "#fa7b17";
    private static final String GRIS_BG = "#f8fafc";
    private static final String GRIS_TX = "#6b7280";
    private static final String DARK = "#111827";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ARCHIVO
            = DateTimeFormatter.ofPattern("yyyyMMdd");

    public byte[] generarReporteEstudiante(
            List<FeedBackResumen> feedbacks,
            String nombreEstudiante,
            String nombreUsuario,
            boolean esEncargado) throws DocumentException {

        double promedio = calcularPromedio(feedbacks);
        long alertas = feedbacks.stream().filter(FeedBackResumen::isAlerta).count();
        String hoy = LocalDate.now().format(FMT);

        String subtitulo = esEncargado
                ? "Reporte de desempeño académico — Encargado: " + nombreUsuario
                : "Reporte de desempeño académico";

        StringBuilder html = new StringBuilder();
        html.append(docHeader("Feedback 360° — " + nombreEstudiante));
        html.append("<body>");
        html.append(encabezadoPagina(
                "Feedback 360°",
                subtitulo,
                hoy));

        // KPIs
        html.append("<table class='kpi-row'><tr>");
        html.append(kpiCell(AZUL, AZUL_BG, "Promedio General",
                String.format("%.1f", promedio) + " / 5"));
        html.append(kpiCell(VERDE, "#e6f4ea", "Evaluaciones",
                String.valueOf(feedbacks.size())));
        html.append(kpiCell(ROJO, "#fce8e6", "Alertas (≤ 2★)",
                String.valueOf(alertas)));
        html.append("</tr></table>");

        // Info estudiante
        html.append("<div class='section-title'>Estudiante evaluado</div>");
        html.append("<table class='info-box'><tr>");
        html.append("<td class='info-label'>Nombre</td>");
        html.append("<td class='info-value'>").append(esc(nombreEstudiante)).append("</td>");
        html.append("<td class='info-label'>Fecha de emisión</td>");
        html.append("<td class='info-value'>").append(hoy).append("</td>");
        html.append("</tr></table>");

        // Tabla de evaluaciones
        html.append("<div class='section-title'>Detalle de Evaluaciones</div>");

        if (feedbacks.isEmpty()) {
            html.append("<p class='empty-msg'>No hay evaluaciones registradas aún.</p>");
        } else {
            html.append("<table class='data-table'>");
            html.append("<thead><tr>");
            html.append("<th>#</th><th>Docente</th><th>Materia</th>");
            html.append("<th>Sección</th><th>Calificación</th>");
            html.append("<th>Nivel</th><th>Fecha</th><th>Comentario</th>");
            html.append("</tr></thead><tbody>");

            int idx = 1;
            for (FeedBackResumen fb : feedbacks) {
                String rowClass = fb.isAlerta() ? " class='row-alerta'" : (idx % 2 == 0 ? " class='row-par'" : "");
                html.append("<tr").append(rowClass).append(">");
                html.append("<td class='tc'>").append(idx++).append("</td>");
                html.append("<td>").append(esc(fb.getNombreDocente())).append("</td>");
                html.append("<td>").append(esc(fb.getNombreMateria())).append("</td>");
                html.append("<td class='tc'><span class='badge-sec'>")
                        .append(esc(fb.getNombreSeccion())).append("</span></td>");
                html.append("<td class='tc'>").append(estrellas(fb.getCalificacion())).append("</td>");
                html.append("<td class='tc'>").append(badgeNivel(fb.getCalificacion())).append("</td>");
                html.append("<td class='tc text-muted'>")
                        .append(fb.getFechaEvaluacion() != null
                                ? fb.getFechaEvaluacion().format(FMT) : "—")
                        .append("</td>");
                html.append("<td class='comentario'>")
                        .append(fb.getComentario() != null && !fb.getComentario().isBlank()
                                ? esc(fb.getComentario()) : "<span class='text-muted'>Sin comentario</span>")
                        .append("</td>");
                html.append("</tr>");
            }
            html.append("</tbody></table>");
        }

        // Nota de alerta si hay
        if (alertas > 0) {
            html.append("<div class='alerta-notice'>")
                    .append("&#9888; Se detectaron <strong>").append(alertas)
                    .append("</strong> evaluación(es) con calificación baja (≤ 2 estrellas).")
                    .append(" Se recomienda comunicarse con el docente correspondiente.")
                    .append("</div>");
        }

        html.append(pie());
        html.append("</body></html>");

        return renderizar(html.toString());
    }

    public byte[] generarReporteAdmin(
            List<FeedBackResumen> feedbacks,
            List<Map<String, Object>> promediosPorSeccion,
            List<Map<String, Object>> promediosPorDocente,
            List<FeedBackResumen> alertas,
            String nombreAdmin) throws DocumentException {

        double promGeneral = calcularPromedio(feedbacks);
        String hoy = LocalDate.now().format(FMT);

        StringBuilder html = new StringBuilder();
        html.append(docHeader("Feedback 360° — Reporte Administrativo"));
        html.append("<body>");
        html.append(encabezadoPagina(
                "Feedback 360°",
                "Reporte Administrativo — Generado por: " + nombreAdmin,
                hoy));

        // KPIs
        html.append("<table class='kpi-row'><tr>");
        html.append(kpiCell(AZUL, AZUL_BG, "Total Evaluaciones",
                String.valueOf(feedbacks.size())));
        html.append(kpiCell(VERDE, "#e6f4ea", "Promedio General",
                String.format("%.1f", promGeneral) + " / 5"));
        html.append(kpiCell(ROJO, "#fce8e6", "Alertas (≤ 2★)",
                String.valueOf(alertas.size())));
        html.append(kpiCell(NARANJA, "#fef0e6", "Secciones",
                String.valueOf(promediosPorSeccion != null ? promediosPorSeccion.size() : 0)));
        html.append("</tr></table>");

        // Promedios por sección y por docente
        html.append("<table class='two-col'><tr><td class='two-col-cell'>");

        html.append("<div class='section-title'>Promedios por Sección</div>");
        if (promediosPorSeccion != null && !promediosPorSeccion.isEmpty()) {
            html.append("<table class='data-table'>");
            html.append("<thead><tr><th>Sección</th><th>Promedio</th><th>Evaluaciones</th></tr></thead>");
            html.append("<tbody>");
            int idx = 1;
            for (Map<String, Object> row : promediosPorSeccion) {
                String cls = idx++ % 2 == 0 ? " class='row-par'" : "";
                html.append("<tr").append(cls).append(">");
                html.append("<td><span class='badge-sec'>").append(esc(str(row.get("seccion")))).append("</span></td>");
                html.append("<td class='tc fw-bold' style='color:").append(AZUL).append(";'>")
                        .append(str(row.get("promedio"))).append(" / 5</td>");
                html.append("<td class='tc text-muted'>").append(str(row.get("total"))).append("</td>");
                html.append("</tr>");
            }
            html.append("</tbody></table>");
        } else {
            html.append("<p class='empty-msg'>Sin datos.</p>");
        }

        html.append("</td><td class='two-col-cell'>");

        html.append("<div class='section-title'>Promedios por Docente</div>");
        if (promediosPorDocente != null && !promediosPorDocente.isEmpty()) {
            html.append("<table class='data-table'>");
            html.append("<thead><tr><th>Docente</th><th>Promedio</th><th>Evaluaciones</th></tr></thead>");
            html.append("<tbody>");
            int idx2 = 1;
            for (Map<String, Object> row : promediosPorDocente) {
                String cls = idx2++ % 2 == 0 ? " class='row-par'" : "";
                html.append("<tr").append(cls).append(">");
                html.append("<td>").append(esc(str(row.get("docente")))).append("</td>");
                html.append("<td class='tc fw-bold' style='color:").append(VERDE).append(";'>")
                        .append(str(row.get("promedio"))).append(" / 5</td>");
                html.append("<td class='tc text-muted'>").append(str(row.get("total"))).append("</td>");
                html.append("</tr>");
            }
            html.append("</tbody></table>");
        } else {
            html.append("<p class='empty-msg'>Sin datos.</p>");
        }

        html.append("</td></tr></table>");

        // Alertas
        if (!alertas.isEmpty()) {
            html.append("<div class='section-title' style='color:").append(ROJO).append(";'>")
                    .append("\u26A0 Estudiantes conn Calificación ≤ 2</div>");
            html.append("<table class='data-table'>");
            html.append("<thead><tr><th>Estudiante</th><th>Materia</th><th>Sección</th>")
                    .append("<th>Docente</th><th>Cal.</th><th>Nivel</th></tr></thead>");
            html.append("<tbody>");
            int idx3 = 1;
            for (FeedBackResumen fb : alertas) {
                html.append("<tr class='row-alerta'>");
                html.append("<td>\u26A0 ").append(esc(fb.getNombreEstudiante())).append("</td>");
                html.append("<td>").append(esc(fb.getNombreMateria())).append("</td>");
                html.append("<td class='tc'><span class='badge-sec'>")
                        .append(esc(fb.getNombreSeccion())).append("</span></td>");
                html.append("<td>").append(esc(fb.getNombreDocente())).append("</td>");
                html.append("<td class='tc'>").append(estrellas(fb.getCalificacion())).append("</td>");
                html.append("<td class='tc'>").append(badgeNivel(fb.getCalificacion())).append("</td>");
                html.append("</tr>");
                idx3++;
            }
            html.append("</tbody></table>");
        }

        // Historial completo
        html.append("<div style='page-break-before:always;'></div>");
        html.append("<div class='section-title'>Historial Completo de Evaluaciones</div>");

        if (feedbacks.isEmpty()) {
            html.append("<p class='empty-msg'>No hay evaluaciones registradas.</p>");
        } else {
            html.append("<table class='data-table'>");
            html.append("<thead><tr>");
            html.append("<th>#</th><th>Estudiante</th><th>Docente</th>");
            html.append("<th>Materia</th><th>Sec.</th>");
            html.append("<th>Cal.</th><th>Nivel</th><th>Fecha</th><th>Comentario</th>");
            html.append("</tr></thead><tbody>");

            int idx4 = 1;
            for (FeedBackResumen fb : feedbacks) {
                String rowClass = fb.isAlerta()
                        ? " class='row-alerta'"
                        : (idx4 % 2 == 0 ? " class='row-par'" : "");
                html.append("<tr").append(rowClass).append(">");
                html.append("<td class='tc text-muted'>").append(idx4++).append("</td>");
                html.append("<td>");
                if (fb.isAlerta()) {
                    html.append("&#9888; ");
                }
                html.append(esc(fb.getNombreEstudiante())).append("</td>");
                html.append("<td>").append(esc(fb.getNombreDocente())).append("</td>");
                html.append("<td>").append(esc(fb.getNombreMateria())).append("</td>");
                html.append("<td class='tc'><span class='badge-sec'>")
                        .append(esc(fb.getNombreSeccion())).append("</span></td>");
                html.append("<td class='tc'>").append(estrellas(fb.getCalificacion())).append("</td>");
                html.append("<td class='tc'>").append(badgeNivel(fb.getCalificacion())).append("</td>");
                html.append("<td class='tc text-muted'>")
                        .append(fb.getFechaEvaluacion() != null
                                ? fb.getFechaEvaluacion().format(FMT) : "—")
                        .append("</td>");
                html.append("<td class='comentario'>")
                        .append(fb.getComentario() != null && !fb.getComentario().isBlank()
                                ? esc(fb.getComentario())
                                : "<span class='text-muted'>—</span>")
                        .append("</td>");
                html.append("</tr>");
            }
            html.append("</tbody></table>");
        }

        html.append(pie());
        html.append("</body></html>");

        return renderizar(html.toString());
    }

    private String docHeader(String titulo) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>""" + esc(titulo) + """
                </title>
                <style type="text/css">
                    /* ── Página ── */
                    @page {
                        size: A4 landscape;
                        margin: 14mm 16mm 18mm 16mm;
                        @bottom-center {
                            content: "Página " counter(page) " de " counter(pages);
                            font-size: 8pt;
                            color: #9ca3af;
                        }
                    }
                    body {
                        font-family: "DejaVu Sans", Arial, sans-serif;
                        font-size: 9pt;
                        color: #111827;
                        background: #fff;
                        margin: 0;
                        padding: 0;
                    }
 
                    /* ── Encabezado ── */
                    .page-header {
                        background: linear-gradient(135deg, #1a73e8 0%, #0b62d6 100%);
                        color: #fff;
                        padding: 14px 18px 12px 18px;
                        border-radius: 8px;
                        margin-bottom: 14px;
                    }
                    .page-header-title {
                        font-size: 17pt;
                        font-weight: bold;
                        letter-spacing: 0.5px;
                    }
                    .page-header-sub {
                        font-size: 8.5pt;
                        opacity: 0.88;
                        margin-top: 3px;
                    }
                    .page-header-date {
                        font-size: 8pt;
                        opacity: 0.75;
                        text-align: right;
                    }
                    .header-row {
                        width: 100%;
                    }
                    .header-row td {
                        vertical-align: middle;
                    }
 
                    /* ── KPI cards ── */
                    .kpi-row {
                        width: 100%;
                        border-collapse: separate;
                        border-spacing: 6px 0;
                        margin-bottom: 14px;
                    }
                    .kpi-cell {
                        border-radius: 8px;
                        padding: 10px 14px;
                        width: 25%;
                        vertical-align: middle;
                    }
                    .kpi-label {
                        font-size: 7pt;
                        font-weight: bold;
                        text-transform: uppercase;
                        letter-spacing: 0.4px;
                        color: #6b7280;
                        margin-bottom: 2px;
                    }
                    .kpi-value {
                        font-size: 18pt;
                        font-weight: bold;
                        color: #111827;
                        line-height: 1;
                    }
                    .kpi-icon-cell {
                        width: 36px;
                        text-align: center;
                        vertical-align: middle;
                    }
                    .kpi-icon {
                        font-size: 14pt;
                        width: 36px;
                        height: 36px;
                        border-radius: 8px;
                        display: inline-block;
                        text-align: center;
                        vertical-align: middle;
                        line-height: 36px;
                    }
 
                    /* ── Sección titulo ── */
                    .section-title {
                        font-size: 10.5pt;
                        font-weight: bold;
                        color: #1a73e8;
                        border-bottom: 2px solid #e8f0fe;
                        padding-bottom: 4px;
                        margin-top: 14px;
                        margin-bottom: 8px;
                    }
 
                    /* ── Tabla principal ── */
                    .data-table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 8pt;
                    }
                    .data-table thead tr {
                        background: #1a73e8;
                        color: #fff;
                    }
                    .data-table thead th {
                        padding: 6px 8px;
                        font-weight: bold;
                        font-size: 7.5pt;
                        text-transform: uppercase;
                        letter-spacing: 0.3px;
                        border: none;
                    }
                    .data-table tbody td {
                        padding: 5px 8px;
                        border-bottom: 1px solid #f1f5f9;
                        vertical-align: middle;
                    }
                    .row-par td { background: #f8fafc; }
                    .row-alerta td {
                        background: #fff5f5;
                        border-left: 3px solid #ea4335;
                    }
 
                    /* ── Info box ── */
                    .info-box {
                        width: 100%;
                        border-collapse: collapse;
                        background: #f8fafc;
                        border-radius: 6px;
                        margin-bottom: 12px;
                        font-size: 8.5pt;
                    }
                    .info-box td { padding: 6px 12px; }
                    .info-label {
                        font-weight: bold;
                        color: #6b7280;
                        text-transform: uppercase;
                        font-size: 7pt;
                        width: 120px;
                    }
                    .info-value { color: #111827; font-weight: bold; }
 
                    /* ── Dos columnas ── */
                    .two-col { width: 100%; border-collapse: separate; border-spacing: 10px 0; }
                    .two-col-cell { width: 50%; vertical-align: top; }
 
                    /* ── Badges ── */
                    .badge-sec {
                        background: #e2e8f0;
                        color: #475569;
                        border-radius: 99px;
                        padding: 1px 7px;
                        font-size: 7.5pt;
                        font-weight: bold;
                    }
                    .badge-nivel {
                        border-radius: 4px;
                        padding: 2px 7px;
                        font-size: 7.5pt;
                        font-weight: bold;
                    }
                    .nivel-1 { background: #fce8e6; color: #c5221f; }
                    .nivel-2 { background: #fef0e6; color: #c1550a; }
                    .nivel-3 { background: #fef9e0; color: #a07300; }
                    .nivel-4 { background: #e6f4ea; color: #1e7e34; }
                    .nivel-5 { background: #e8f0fe; color: #1557b0; }

 
                    /* ── Utilidades ── */
                    .tc { text-align: center; }
                    .text-muted { color: #9ca3af; }
                    .fw-bold { font-weight: bold; }
                    .comentario { color: #4b5563; font-style: italic; font-size: 7.5pt; }
                    .empty-msg { color: #9ca3af; font-style: italic; font-size: 8.5pt; text-align: center; padding: 12px; }
 
                    /* ── Nota alerta ── */
                    .alerta-notice {
                        background: #fff5f5;
                        border: 1px solid #fca5a5;
                        border-left: 4px solid #ea4335;
                        border-radius: 6px;
                        padding: 8px 12px;
                        margin-top: 14px;
                        font-size: 8pt;
                        color: #991b1b;
                    }
 
                    /* ── Pie de página ── */
                    .footer {
                        margin-top: 20px;
                        padding-top: 8px;
                        border-top: 1px solid #e5e7eb;
                        font-size: 7.5pt;
                        color: #9ca3af;
                        text-align: center;
                    }
                </style>
            </head>
            """;
    }

    private String encabezadoPagina(String titulo, String subtitulo, String fecha) {
        return "<div class='page-header'>"
                + "<table class='header-row'><tr>"
                + "<td>"
                + "<div class='page-header-title'>\u2713 " + esc(titulo) + "</div>"
                + "<div class='page-header-sub'>" + esc(subtitulo) + "</div>"
                + "</td>"
                + "<td class='page-header-date'>Fecha de emisión<br/><strong>" + fecha + "</strong></td>"
                + "</tr></table>"
                + "</div>";
    }

    private String kpiCell(String borderColor, String bgColor, String label, String value) {
        return "<td class='kpi-cell' style='background:" + bgColor
                + ";border-left:4px solid " + borderColor + ";'>"
                + "<div class='kpi-label'>" + esc(label) + "</div>"
                + "<div class='kpi-value' style='color:" + borderColor + ";'>" + esc(value) + "</div>"
                + "</td>";
    }

    private String pie() {
        return "<div class='footer'>"
                + "Portal Estudiantil y Administrativo &mdash; "
                + "Documento generado el " + LocalDate.now().format(FMT)
                + " &mdash; Uso interno"
                + "</div>";
    }

    private byte[] renderizar(String xhtml) throws DocumentException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(os);
            return os.toByteArray();
        } catch (Exception e) {
            throw new DocumentException("Error al generar PDF: " + e.getMessage());
        }
    }

    private String estrellas(Integer cal) {
        if (cal == null) {
            return "<span class='text-muted'>—</span>";
        }
        int v = Math.max(1, Math.min(5, cal));
        String[] colores = {"", "#ea4335", "#fa7b17", "#a07300", "#1e7e34", "#1557b0"};
        return "<span style='font-weight:bold; font-size:10pt; color:"
                + colores[v] + ";'>" + v + " / 5</span>";
    }

    private String badgeNivel(Integer cal) {
        if (cal == null) {
            return "—";
        }
        String[] labels = {"", "Deficiente", "Regular", "Bueno", "Muy Bueno", "Excelente"};
        int v = Math.max(1, Math.min(5, cal));
        return "<span class='badge-nivel nivel-" + v + "'>" + labels[v] + "</span>";
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String str(Object o) {
        return o == null ? "—" : o.toString();
    }

    private double calcularPromedio(List<FeedBackResumen> feedbacks) {
        return feedbacks.stream()
                .filter(f -> f.getCalificacion() != null)
                .mapToInt(FeedBackResumen::getCalificacion)
                .average()
                .orElse(0.0);
    }

}
