package com.dmpacademy.certificate;

import com.dmpacademy.certificate.dto.CertificateResponse;
import com.dmpacademy.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Certificate generation and verification")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/api/v1/certificates")
    @Operation(summary = "List my certificates (Student)")
    public ResponseEntity<PageResponse<CertificateResponse>> listCertificates(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        PageResponse<CertificateResponse> response = certificateService.listCertificates(studentId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/certificates/{id}/verify")
    @Operation(summary = "Verify a certificate (Public)")
    public ResponseEntity<CertificateResponse> verifyCertificate(@PathVariable UUID id) {
        CertificateResponse response = certificateService.verifyCertificate(id);
        return ResponseEntity.ok(response);
    }
}
