package com.hrks.OptimaStock.price.service;

import com.hrks.OptimaStock.price.dto.CreateQuoteRequest;
import com.hrks.OptimaStock.price.dto.QuoteItemRequest;
import com.hrks.OptimaStock.price.model.Quote;
import com.hrks.OptimaStock.price.model.QuoteItem;
import com.hrks.OptimaStock.price.model.QuoteSummary;
import com.hrks.OptimaStock.price.repository.QuoteRepository;
import com.hrks.OptimaStock.product.model.Product;
import com.hrks.OptimaStock.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private ProductService productService;

    public List<Quote> findAll() {
        return quoteRepository.findAll();
    }

    public Optional<Quote> findById(String id) {
        return quoteRepository.findById(id);
    }

    public Optional<Quote> findByQuoteNumber(String quoteNumber) {
        return quoteRepository.findByQuoteNumber(quoteNumber);
    }

    public List<Quote> findByEmail(String email) {
        return quoteRepository.findByCustomerInfoEmail(email);
    }

    public List<Quote> findByStatus(String status) {
        return quoteRepository.findByStatus(status);
    }

    /**
     * Crea una nueva cotización
     */
    public Quote createQuote(CreateQuoteRequest request) {
        // 1. Generar número de cotización
        String quoteNumber = generateQuoteNumber();

        // 2. Obtener información de productos desde MySQL y crear items
        List<QuoteItem> items = new ArrayList<>();
        for (QuoteItemRequest itemReq : request.getItems()) {
            Product product = productService.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto con ID " + itemReq.getProductId() + " no encontrado"));

            QuoteItem item = new QuoteItem();
            item.setProductId(product.getId());
            item.setProductCode(product.getCode());
            item.setProductName(product.getName());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(product.getPrice());
            
            // Obtener IVA del producto (si existe)
            if (product.getIva() != null && product.getIva().getIva() != null) {
                item.setIva(product.getIva().getIva().intValue());
            } else {
                item.setIva(0);
            }
            
            item.calculateTotals();
            items.add(item);
        }

        // 3. Calcular resumen
        QuoteSummary summary = calculateSummary(items);

        // 4. Crear cotización
        Quote quote = new Quote();
        quote.setQuoteNumber(quoteNumber);
        quote.setCustomerInfo(request.getCustomerInfo());
        quote.setItems(items);
        quote.setSummary(summary);
        quote.setStatus("PENDING");
        quote.setValidUntil(LocalDateTime.now().plusDays(15));
        quote.setNotes(request.getNotes());
        quote.setCreatedAt(LocalDateTime.now());
        quote.setCreatedBy("sistema");
        quote.setUpdatedAt(LocalDateTime.now());

        return quoteRepository.save(quote);
    }

    /**
     * Actualiza el estado de una cotización
     */
    public Quote updateStatus(String id, String status) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        
        quote.setStatus(status);
        quote.setUpdatedAt(LocalDateTime.now());
        
        return quoteRepository.save(quote);
    }

    /**
     * Elimina una cotización
     */
    public void delete(String id) {
        quoteRepository.deleteById(id);
    }

    /**
     * Genera un número único de cotización
     */
    private String generateQuoteNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = quoteRepository.count() + 1;
        return String.format("COT-%s-%04d", year, count);
    }

    /**
     * Calcula el resumen de totales de la cotización
     */
    private QuoteSummary calculateSummary(List<QuoteItem> items) {
        int subtotal = 0;
        int totalIva = 0;

        for (QuoteItem item : items) {
            subtotal += item.getSubtotal();
            totalIva += item.getIvaAmount();
        }

        int total = subtotal + totalIva;

        return new QuoteSummary(subtotal, totalIva, total, items.size());
    }
}
