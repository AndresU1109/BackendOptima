package com.hrks.OptimaStock.price.controller;

import com.hrks.OptimaStock.price.dto.CreateQuoteRequest;
import com.hrks.OptimaStock.price.model.Quote;
import com.hrks.OptimaStock.price.service.PdfGeneratorService;
import com.hrks.OptimaStock.price.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private PdfGeneratorService pdfGenerator;

    /**
     * Crear cotización (PÚBLICO - desde sitio web)
     */
    @PostMapping
    public ResponseEntity<?> createQuote(@Valid @RequestBody CreateQuoteRequest request) {
        try {
            Quote quote = quoteService.createQuote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(quote);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Listar todas las cotizaciones (protegido - ADMIN/EMPLEADO)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<Quote>> getAllQuotes() {
        return ResponseEntity.ok(quoteService.findAll());
    }

    /**
     * Obtener cotización por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable String id) {
        return quoteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener cotización por número
     */
    @GetMapping("/number/{quoteNumber}")
    public ResponseEntity<Quote> getQuoteByNumber(@PathVariable String quoteNumber) {
        return quoteService.findByQuoteNumber(quoteNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar cotizaciones por email del cliente
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Quote>> getQuotesByEmail(@PathVariable String email) {
        return ResponseEntity.ok(quoteService.findByEmail(email));
    }

    /**
     * Buscar cotizaciones por estado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Quote>> getQuotesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(quoteService.findByStatus(status));
    }

    /**
     * Generar y descargar PDF de cotización (PÚBLICO)
     */
    /**
     * Generar y descargar PDF de cotización (ASÍNCRONO)
     */
    @GetMapping("/{id}/pdf")
    public java.util.concurrent.CompletableFuture<ResponseEntity<byte[]>> downloadQuotePdf(@PathVariable String id) {
        return quoteService.findById(id)
                .map(quote -> pdfGenerator.generateQuotePdfAsync(quote) // Llama al método @Async
                        .thenApply(pdfBytes -> {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_PDF);
                            try {
                                headers.setContentDispositionFormData("attachment",
                                        "cotizacion-" + quote.getQuoteNumber() + ".pdf");
                            } catch (Exception e) {
                                // Fallback nombre simple si falla
                                headers.setContentDispositionFormData("attachment", "cotizacion.pdf");
                            }

                            return ResponseEntity.ok()
                                    .headers(headers)
                                    .body(pdfBytes);
                        })
                        .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .orElseGet(() -> java.util.concurrent.CompletableFuture
                        .completedFuture(ResponseEntity.notFound().build()));
    }

    /**
     * Actualizar estado de cotización (protegido - ADMIN/EMPLEADO)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Quote> updateStatus(@PathVariable String id,
            @RequestParam String status) {
        try {
            Quote updated = quoteService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar cotización (protegido - ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable String id) {
        try {
            quoteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
