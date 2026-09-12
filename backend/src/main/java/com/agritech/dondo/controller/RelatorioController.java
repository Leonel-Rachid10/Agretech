package com.agritech.dondo.controller;

import com.agritech.dondo.service.RelatorioPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioPdfService relatorioPdfService;

    public RelatorioController(RelatorioPdfService relatorioPdfService) {
        this.relatorioPdfService = relatorioPdfService;
    }

    /**
     * RF06: Geração de Relatórios Consolidados em PDF
     */
    @GetMapping("/producao/pdf")
    public ResponseEntity<byte[]> baixarRelatorioProducaoPdf() {
        byte[] pdfBytes = relatorioPdfService.gerarRelatorioProducaoPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"AgriTech_Dondo_Relatorio_Producao.pdf\"");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
