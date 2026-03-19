package com.example.demo.controller;

import com.example.demo.dto.CertificateVerificationDTO;
import com.example.demo.entity.Certificate;
import com.example.demo.service.CertificateService;
import com.example.demo.service.CertificatePdfService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificatePdfService certificatePdfService;

    public CertificateController(
            CertificateService certificateService,
            CertificatePdfService certificatePdfService) {

        this.certificateService = certificateService;
        this.certificatePdfService = certificatePdfService;
    }

    @PostMapping("/issue")
    public ResponseEntity<Certificate> issueCertificate(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {

        return ResponseEntity.ok(
                certificateService.issueCertificate(studentId, courseId)
        );
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {

        try {
            Certificate certificate =
                    certificateService.getCertificate(studentId, courseId);

            byte[] pdf =
                    certificatePdfService.generateCertificatePdf(certificate);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=certificate.pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(("Certificate error: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(("Cannot download certificate: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(("Internal error: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/verify/{hash}")
    public ResponseEntity<CertificateVerificationDTO> verifyCertificate(
            @PathVariable String hash) {

        return ResponseEntity.ok(
                certificateService.verifyCertificate(hash)
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(
            IllegalStateException ex) {

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
