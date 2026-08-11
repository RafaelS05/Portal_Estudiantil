package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.ReporteDetalleRow;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteAcademicoPdfService {

    private static final Color COLOR_PRIMARIO   = new Color(13, 110, 253);   // Bootstrap blue
    private static final Color COLOR_EXITO      = new Color(25, 135, 84);    // Bootstrap green
    private static final Color COLOR_PELIGRO    = new Color(220, 53, 69);    // Bootstrap red
    private static final Color COLOR_GRIS_CLARO = new Color(248, 249, 250);  // table-light
    private static final Color COLOR_BORDE      = new Color(222, 226, 230);  // Bootstrap border

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Genera un PDF en memoria para la boleta indicada y lo devuelve como byte[].
     */
    public byte[] generarPdf(List<ReporteDetalleRow> detalle) throws Exception {

        if (detalle == null || detalle.isEmpty())
            throw new IllegalArgumentException("No hay datos para generar el PDF");

        ReporteDetalleRow cab = detalle.get(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // ── Pie de página con número ──────────────────────────────────────
        writer.setPageEvent(new PieNumeroPagina());

        doc.open();

        // ── ENCABEZADO ────────────────────────────────────────────────────
        agregarEncabezado(doc, cab);

        // ── TABLA DE DATOS GENERALES ──────────────────────────────────────
        agregarDatosGenerales(doc, cab);

        doc.add(Chunk.NEWLINE);

        // ── TABLA DE CALIFICACIONES ───────────────────────────────────────
        agregarTablaCalificaciones(doc, detalle);

        // ── PROMEDIO FINAL ────────────────────────────────────────────────
        if (cab.getPromedioPonderado() != null) {
            agregarPromedioFinal(doc, cab.getPromedioPonderado());
        }

        doc.close();
        return baos.toByteArray();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SECCIONES INTERNAS
    // ═══════════════════════════════════════════════════════════════════════

    private void agregarEncabezado(Document doc, ReporteDetalleRow cab) throws DocumentException {
        Font fTitulo  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  18, COLOR_PRIMARIO);
        Font fSubtit  = FontFactory.getFont(FontFactory.HELVETICA,       10, Color.GRAY);
        Font fTipo    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  11, Color.WHITE);

        // Recuadro azul con el tipo de reporte
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3f, 1.2f});

        // Columna izquierda: título
        PdfPCell celdaTitulo = new PdfPCell();
        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setPaddingBottom(8);
        celdaTitulo.addElement(new Paragraph("Portal Estudiantil", fTitulo));
        celdaTitulo.addElement(new Paragraph("Sistema de Gestión Académica", fSubtit));
        header.addCell(celdaTitulo);

        // Columna derecha: badge tipo reporte
        String tipo = cab.getNombreTipoReporte() != null ? cab.getNombreTipoReporte() : "REPORTE";
        PdfPCell celdaTipo = new PdfPCell(new Phrase(tipo, fTipo));
        celdaTipo.setBackgroundColor(COLOR_PRIMARIO);
        celdaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTipo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTipo.setPadding(10);
        celdaTipo.setBorder(Rectangle.NO_BORDER);
        header.addCell(celdaTipo);

        doc.add(header);

        // Línea separadora
        LineSeparator sep = new LineSeparator(1.5f, 100, COLOR_PRIMARIO, Element.ALIGN_CENTER, -5);
        doc.add(new Chunk(sep));
        doc.add(Chunk.NEWLINE);
    }

    private void agregarDatosGenerales(Document doc, ReporteDetalleRow cab) throws DocumentException {
        Font fLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.GRAY);
        Font fValor = FontFactory.getFont(FontFactory.HELVETICA,      10, Color.BLACK);
        Font fTitSec = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_PRIMARIO);

        // Título sección
        Paragraph tit = new Paragraph("Datos del Reporte", fTitSec);
        tit.setSpacingAfter(6);
        doc.add(tit);

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
        tabla.setSpacingAfter(10);

        Object[][] datos = {
            {"Estudiante",  cab.getNombreEstudiante()},
            {"Sección",     cab.getNombreSeccion()},
            {"Período",     cab.getNombrePeriodo()},
            {"Fecha",       cab.getFechaCreacionReporte() != null
                                ? cab.getFechaCreacionReporte().format(FMT) : "—"}
        };

        for (Object[] par : datos) {
            PdfPCell lbl = celda(par[0].toString(), fLabel, COLOR_GRIS_CLARO, Element.ALIGN_LEFT);
            PdfPCell val = celda(par[1] != null ? par[1].toString() : "—", fValor, Color.WHITE, Element.ALIGN_LEFT);
            tabla.addCell(lbl);
            tabla.addCell(val);
        }

        doc.add(tabla);
    }

    private void agregarTablaCalificaciones(Document doc, List<ReporteDetalleRow> detalle)
            throws DocumentException {

        Font fTitSec  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_PRIMARIO);
        Font fTheme   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  9, Color.WHITE);
        Font fCelda   = FontFactory.getFont(FontFactory.HELVETICA,       9, Color.BLACK);
        Font fBold    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  9, Color.BLACK);

        Paragraph tit = new Paragraph("Calificaciones por Materia", fTitSec);
        tit.setSpacingAfter(6);
        doc.add(tit);

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{2.5f, 1.8f, 0.8f, 0.8f, 0.8f, 1.2f});

        String[] headers = {"Materia", "Evaluación", "%", "Nota", "Aporte", "Estado"};
        for (String h : headers) {
            PdfPCell th = new PdfPCell(new Phrase(h, fTheme));
            th.setBackgroundColor(COLOR_PRIMARIO);
            th.setHorizontalAlignment(Element.ALIGN_CENTER);
            th.setPadding(6);
            th.setBorderColor(COLOR_BORDE);
            tabla.addCell(th);
        }

        boolean alterno = false;
        for (ReporteDetalleRow fila : detalle) {
            if (fila.getNombreMateria() == null) continue;
            Color bg = alterno ? COLOR_GRIS_CLARO : Color.WHITE;

            tabla.addCell(celda(nulo(fila.getNombreMateria()),     fBold,  bg, Element.ALIGN_LEFT));
            tabla.addCell(celda(nulo(fila.getTipoEvaluacion()),    fCelda, bg, Element.ALIGN_CENTER));
            tabla.addCell(celda(pct(fila.getPorcentajeEvaluacion()), fCelda, bg, Element.ALIGN_CENTER));

            // Nota con color
            BigDecimal nota = fila.getCalificacion();
            Font fNota = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
                    nota != null && nota.compareTo(BigDecimal.valueOf(70)) >= 0
                            ? COLOR_EXITO : COLOR_PELIGRO);
            PdfPCell cNota = celda(nota != null ? fmt1(nota) : "—", fNota, bg, Element.ALIGN_CENTER);
            tabla.addCell(cNota);

            tabla.addCell(celda(nota != null ? fmt2(fila.getAporte()) : "—", fCelda, bg, Element.ALIGN_CENTER));

            // Badge estado
            boolean aprobado = nota != null && nota.compareTo(BigDecimal.valueOf(70)) >= 0;
            String estadoTxt = nota != null ? (aprobado ? "APROBADO" : "REPROBADO") : "—";
            Font fEst = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
            PdfPCell cEst = new PdfPCell(new Phrase(estadoTxt, fEst));
            cEst.setBackgroundColor(nota != null ? (aprobado ? COLOR_EXITO : COLOR_PELIGRO) : Color.LIGHT_GRAY);
            cEst.setHorizontalAlignment(Element.ALIGN_CENTER);
            cEst.setPadding(5);
            cEst.setBorderColor(COLOR_BORDE);
            tabla.addCell(cEst);

            alterno = !alterno;
        }

        doc.add(tabla);
    }

    private void agregarPromedioFinal(Document doc, BigDecimal promedio) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        boolean aprobado = promedio.compareTo(BigDecimal.valueOf(70)) >= 0;

        Font fLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font fValor = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(40);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.setWidths(new float[]{1.5f, 1f});

        Color bgColor = aprobado ? COLOR_EXITO : COLOR_PELIGRO;

        PdfPCell lbl = new PdfPCell(new Phrase("Promedio Final", fLabel));
        lbl.setBackgroundColor(bgColor);
        lbl.setPadding(8);
        lbl.setBorder(Rectangle.NO_BORDER);
        t.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(fmt2(promedio), fValor));
        val.setBackgroundColor(bgColor);
        val.setPadding(8);
        val.setHorizontalAlignment(Element.ALIGN_CENTER);
        val.setBorder(Rectangle.NO_BORDER);
        t.addCell(val);

        doc.add(t);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ═══════════════════════════════════════════════════════════════════════

    private PdfPCell celda(String texto, Font fuente, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fuente));
        c.setBackgroundColor(bg);
        c.setHorizontalAlignment(align);
        c.setPadding(6);
        c.setBorderColor(COLOR_BORDE);
        return c;
    }

    private String nulo(String s)          { return s != null ? s : "—"; }
    private String pct(BigDecimal b)       { return b != null ? b.toPlainString() + "%" : "—"; }
    private String fmt1(BigDecimal b)      { return b != null ? String.format("%.1f", b) : "—"; }
    private String fmt2(BigDecimal b)      { return b != null ? String.format("%.2f", b) : "—"; }

    // ── Numeración de página en pie ──────────────────────────────────────
    static class PieNumeroPagina extends PdfPageEventHelper {
        private final Font font = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase pie = new Phrase("Página " + writer.getPageNumber() +
                    "  |  Portal Estudiantil — Generado automáticamente", font);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, pie,
                    (document.left() + document.right()) / 2f, document.bottom() - 10, 0);
        }
    }
}