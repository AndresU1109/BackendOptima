package com.hrks.OptimaStock.report.controller;

import com.hrks.OptimaStock.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<byte[]> getInventoryReport() throws IOException {
        byte[] bytes = reportService.generateInventoryReport(false);
        return createResponse(bytes, "inventario_general.xlsx");
    }

    @GetMapping("/inventory/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<byte[]> getLowStockReport() throws IOException {
        byte[] bytes = reportService.generateInventoryReport(true);
        return createResponse(bytes, "bajo_stock.xlsx");
    }

    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<byte[]> getSalesReport(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
        
        if (date == null) date = LocalDate.now();
        
        byte[] bytes = reportService.generateSalesReport(type, date);
        return createResponse(bytes, "ventas_" + type.toLowerCase() + "_" + date + ".xlsx");
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<byte[]> getTopProductsReport() throws IOException {
        byte[] bytes = reportService.generateTopSellingReport();
        return createResponse(bytes, "top_productos.xlsx");
    }

    private ResponseEntity<byte[]> createResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}
