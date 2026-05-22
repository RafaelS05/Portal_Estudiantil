package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ReporteAcademicoRepository.ReporteDetalleRow;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteAcademicoCorreoService {

    private static final Logger log = LoggerFactory.getLogger(ReporteAcademicoCorreoService.class);

    private final JavaMailSender         mailSender;
    private final ReporteAcademicoPdfService pdfService;

    @Value("${spring.mail.username:noreply@portalestudiantil.com}")
    private String remitente;

    public ReporteAcademicoCorreoService(JavaMailSender mailSender,
                                         ReporteAcademicoPdfService pdfService) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    /**
     * Genera el PDF de la boleta y lo envía al correo del estudiante.
     *
     * @param detalle   filas del reporte (proyección)
     * @param destinatario correo del estudiante
     * @return true si se envió con éxito
     */
    public boolean enviarBoleta(List<ReporteDetalleRow> detalle, String destinatario) {
        if (detalle == null || detalle.isEmpty()) {
            log.warn("Intento de enviar boleta con detalle vacío a {}", destinatario);
            return false;
        }

        try {
            byte[] pdfBytes = pdfService.generarPdf(detalle);

            ReporteDetalleRow cab = detalle.get(0);
            String nombre  = cab.getNombreEstudiante() != null ? cab.getNombreEstudiante() : "Estudiante";
            String periodo = cab.getNombrePeriodo()    != null ? cab.getNombrePeriodo()    : "";
            String tipo    = cab.getNombreTipoReporte()!= null ? cab.getNombreTipoReporte(): "Reporte";

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(remitente, "Portal Estudiantil");
            helper.setTo(destinatario);
            helper.setSubject(tipo + " Académica — " + nombre + " (" + periodo + ")");
            helper.setText(construirCuerpoHtml(nombre, tipo, periodo), true);

            String nombreArchivo = "boleta_" + nombre.replaceAll("\\s+", "_") + ".pdf";
            helper.addAttachment(nombreArchivo, new ByteArrayResource(pdfBytes), "application/pdf");

            mailSender.send(msg);
            log.info("Boleta enviada a {} para estudiante {}", destinatario, nombre);
            return true;

        } catch (Exception e) {
            log.error("Error al enviar boleta a {}: {}", destinatario, e.getMessage(), e);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    private String construirCuerpoHtml(String nombre, String tipo, String periodo) {
        return """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;">
              <div style="background:#0d6efd;padding:24px 32px;border-radius:8px 8px 0 0;">
                <h2 style="color:#fff;margin:0;font-size:20px;">Portal Estudiantil</h2>
                <p style="color:#cfe2ff;margin:4px 0 0;">Sistema de Gestión Académica</p>
              </div>
              <div style="background:#f8f9fa;padding:32px;border-radius:0 0 8px 8px;
                          border:1px solid #dee2e6;border-top:none;">
                <p style="margin-top:0;">Estimado/a <strong>%s</strong>,</p>
                <p>Adjunto encontrará su <strong>%s</strong> correspondiente al período
                   <strong>%s</strong>.</p>
                <p>Si tiene alguna consulta, comuníquese con la administración académica.</p>
                <hr style="border:none;border-top:1px solid #dee2e6;margin:24px 0;">
                <p style="color:#6c757d;font-size:12px;margin:0;">
                  Este correo fue generado automáticamente por el Portal Estudiantil.
                  Por favor no responda a este mensaje.
                </p>
              </div>
            </div>
            """.formatted(nombre, tipo, periodo);
    }
}