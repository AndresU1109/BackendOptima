package com.hrks.OptimaStock.report.service;

import com.hrks.OptimaStock.inventory.model.Inventory;
import com.hrks.OptimaStock.inventory.service.InventoryService;
import com.hrks.OptimaStock.product.model.Product;
import com.hrks.OptimaStock.sale.model.Sale;
import com.hrks.OptimaStock.sale.repository.SaleRepository;
import com.hrks.OptimaStock.saleDetail.repository.SaleDetailRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final InventoryService inventoryService;
    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ExcelGeneratorService excelGenerator;

    public ReportService(InventoryService inventoryService, SaleRepository saleRepository,
                         SaleDetailRepository saleDetailRepository, ExcelGeneratorService excelGenerator) {
        this.inventoryService = inventoryService;
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.excelGenerator = excelGenerator;
    }

    // 1. Reporte de Inventario (General y Bajo Stock)
    public byte[] generateInventoryReport(boolean onlyLowStock) throws IOException {
        List<Inventory> inventoryList = inventoryService.findAll();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Inventory inv : inventoryList) {
            // Lógica de bajo stock (ejemplo: < 5 unidades)
            boolean isLowStock = inv.getQuantity() != null && inv.getQuantity() < 5; 

            if (onlyLowStock && !isLowStock) continue;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID Inventario", inv.getId());
            if (inv.getProduct() != null) {
                row.put("Código", inv.getProduct().getCode());
                row.put("Producto", inv.getProduct().getName());
                if (inv.getProduct().getCategory() != null)
                    row.put("Categoría", inv.getProduct().getCategory().getDescription());
                
                // Asumiendo que el precio está en Producto
                row.put("Precio Venta", inv.getProduct().getPrice());
            }
            row.put("Stock Actual", inv.getQuantity());
            row.put("Estado", isLowStock ? "BAJO STOCK" : "OK");

            data.add(row);
        }

        String[] headers = {"ID Inventario", "Código", "Producto", "Categoría", "Stock Actual", "Precio Venta", "Estado"};
        String sheetName = onlyLowStock ? "Bajo Stock" : "Inventario General";
        
        return excelGenerator.generateExcel(sheetName, headers, data);
    }

    // 2. Reporte de Ventas (Diario, Mensual, Anual)
    public byte[] generateSalesReport(String type, LocalDate date) throws IOException {
        LocalDateTime start;
        LocalDateTime end;

        switch (type.toUpperCase()) {
            case "DAILY":
                start = date.atStartOfDay();
                end = date.atTime(LocalTime.MAX);
                break;
            case "MONTHLY":
                start = date.withDayOfMonth(1).atStartOfDay();
                end = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
                break;
            case "YEARLY":
                start = date.withDayOfYear(1).atStartOfDay();
                end = date.withDayOfYear(date.lengthOfYear()).atTime(LocalTime.MAX);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte no válido: " + type);
        }

        List<Sale> sales = saleRepository.findByDateTimeBetween(start, end);
        List<Map<String, Object>> data = new ArrayList<>();

        for (Sale sale : sales) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID Venta", sale.getId());
            row.put("Fecha", sale.getDateTime());
            row.put("Cliente", sale.getClient() != null ? sale.getClient().getFirstName() + " " + sale.getClient().getLastName() : "N/A");
            row.put("Vendedor", sale.getEmployee() != null ? sale.getEmployee().getFirstName() : "N/A");
            row.put("Método Pago", sale.getPaymentMethod() != null ? sale.getPaymentMethod().getDescription() : "N/A");
            row.put("Total", sale.getTotal());

            data.add(row);
        }

        String[] headers = {"ID Venta", "Fecha", "Cliente", "Vendedor", "Método Pago", "Total"};
        return excelGenerator.generateExcel("Ventas " + type, headers, data);
    }

    // 3. Reporte de Productos Más Vendidos
    public byte[] generateTopSellingReport() throws IOException {
        List<Object[]> results = saleDetailRepository.findTopSellingProducts();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] result : results) {
            Product product = (Product) result[0];
            Long totalQty = (Long) result[1];

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Código", product.getCode());
            row.put("Producto", product.getName());
            if (product.getCategory() != null)
                 row.put("Categoría", product.getCategory().getDescription());
            row.put("Total Unidades Vendidas", totalQty);

            data.add(row);
        }

        String[] headers = {"Código", "Producto", "Categoría", "Total Unidades Vendidas"};
        return excelGenerator.generateExcel("Top Productos", headers, data);
    }
}
