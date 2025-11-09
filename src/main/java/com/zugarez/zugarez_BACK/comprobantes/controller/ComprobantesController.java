package com.zugarez.zugarez_BACK.comprobantes.controller;

import com.zugarez.zugarez_BACK.nomina.entity.NominaCalculo;
import com.zugarez.zugarez_BACK.nomina.repository.NominaCalculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Genera un PDF simple con datos de la nómina.
 * Endpoint esperado por el front: GET /api/comprobantes/nomina/{id}/pdf
 */
@RestController
@RequestMapping("/api/comprobantes")
@CrossOrigin(origins = "*")
public class ComprobantesController {

    private final NominaCalculoRepository nominaCalculoRepository;

    public ComprobantesController(NominaCalculoRepository nominaCalculoRepository) {
        this.nominaCalculoRepository = nominaCalculoRepository;
    }

    @GetMapping("/nomina/{id}/pdf")
    public ResponseEntity<byte[]> comprobanteNominaPdf(@PathVariable Long id) {
        NominaCalculo n = nominaCalculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nómina no encontrada"));

        String contenido = "Comprobante de Nómina\n\n" +
                "Empleado: " + n.getEmpleadoNombre() + "\n" +
                "Periodo: " + n.getInicio() + " - " + n.getFin() + "\n" +
                "Estado: " + n.getEstado() + "\n" +
                "Fecha de pago: " + (n.getFechaPago() != null ? n.getFechaPago().format(DateTimeFormatter.ISO_DATE) : "-") + "\n" +
                "Neto a pagar: " + n.getNetoPagar() + "\n" +
                "Transacción: " + (n.getNumeroTransaccion() != null ? n.getNumeroTransaccion() : "-") + "\n";

        // PDF muy simple: se entrega como texto dentro de un contenedor PDF mínimo
        // Nota: Es un PDF básico para descarga; si necesitas un PDF más elaborado, integra una librería (PDFBox/iText).
        String pdf = "%PDF-1.4\n" +
                "1 0 obj<<>>endobj\n" +
                "2 0 obj<< /Length 3 0 R >>stream\n" +
                "BT /F1 12 Tf 50 750 Td (" + escapePdf(contenido) + ") Tj ET\n" +
                "endstream\n" +
                "endobj\n" +
                "3 0 obj  " + (contenido.length() + 60) + " endobj\n" +
                "4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n" +
                "5 0 obj<< /Type /Page /Parent 6 0 R /Resources << /Font << /F1 4 0 R >> >> /Contents 2 0 R /MediaBox [0 0 612 792] >>endobj\n" +
                "6 0 obj<< /Type /Pages /Kids [5 0 R] /Count 1 >>endobj\n" +
                "7 0 obj<< /Type /Catalog /Pages 6 0 R >>endobj\n" +
                "xref\n0 8\n0000000000 65535 f \n" +
                "trailer<< /Root 7 0 R /Size 8 >>\nstartxref\n0\n%%EOF";

        byte[] bytes = pdf.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    private String escapePdf(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
