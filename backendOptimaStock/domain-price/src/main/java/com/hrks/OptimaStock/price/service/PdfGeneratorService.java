package com.hrks.OptimaStock.price.service;

import com.hrks.OptimaStock.price.model.Quote;
import com.hrks.OptimaStock.price.model.QuoteItem;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    /**
     * Genera un PDF de la cotización
     */
    public byte[] generateQuotePdf(Quote quote) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        addHeader(document, quote);

        // Customer Info
        addCustomerInfo(document, quote);

        // Items Table
        addItemsTable(document, quote);

        // Summary
        addSummary(document, quote);

        // Footer
        addFooter(document, quote);

        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document doc, Quote quote) {
        doc.add(new Paragraph("COTIZACIÓN")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        doc.add(new Paragraph("OptimaStock")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        doc.add(new Paragraph("Número de Cotización: " + quote.getQuoteNumber())
                .setFontSize(12)
                .setBold());

        doc.add(new Paragraph("Fecha: " + quote.getCreatedAt().format(DATE_FORMAT))
                .setFontSize(10));

        doc.add(new Paragraph("Válida hasta: " + quote.getValidUntil().format(DATE_FORMAT))
                .setFontSize(10)
                .setMarginBottom(15));

        doc.add(new Paragraph("Estado: " + quote.getStatus())
                .setFontSize(10)
                .setMarginBottom(20));
    }

    private void addCustomerInfo(Document doc, Quote quote) {
        doc.add(new Paragraph("INFORMACIÓN DEL CLIENTE")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        if (quote.getCustomerInfo().getName() != null) {
            doc.add(new Paragraph("Nombre: " + quote.getCustomerInfo().getName())
                    .setFontSize(10));
        }

        if (quote.getCustomerInfo().getCompany() != null) {
            doc.add(new Paragraph("Empresa: " + quote.getCustomerInfo().getCompany())
                    .setFontSize(10));
        }

        if (quote.getCustomerInfo().getEmail() != null) {
            doc.add(new Paragraph("Email: " + quote.getCustomerInfo().getEmail())
                    .setFontSize(10));
        }

        if (quote.getCustomerInfo().getPhone() != null) {
            doc.add(new Paragraph("Teléfono: " + quote.getCustomerInfo().getPhone())
                    .setFontSize(10));
        }

        if (quote.getCustomerInfo().getAddress() != null) {
            doc.add(new Paragraph("Dirección: " + quote.getCustomerInfo().getAddress())
                    .setFontSize(10));
        }

        doc.add(new Paragraph("").setMarginBottom(15));
    }

    private void addItemsTable(Document doc, Quote quote) {
        doc.add(new Paragraph("DETALLE DE PRODUCTOS")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        // Crear tabla con 6 columnas
        float[] columnWidths = {2, 4, 1.5f, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        // Header de tabla
        table.addHeaderCell(createHeaderCell("Código"));
        table.addHeaderCell(createHeaderCell("Producto"));
        table.addHeaderCell(createHeaderCell("Cant."));
        table.addHeaderCell(createHeaderCell("Precio Unit."));
        table.addHeaderCell(createHeaderCell("IVA"));
        table.addHeaderCell(createHeaderCell("Total"));

        // Items
        for (QuoteItem item : quote.getItems()) {
            table.addCell(createCell(item.getProductCode()));
            table.addCell(createCell(item.getProductName()));
            table.addCell(createCell(String.valueOf(item.getQuantity())));
            table.addCell(createCell(CURRENCY_FORMAT.format(item.getUnitPrice())));
            table.addCell(createCell(CURRENCY_FORMAT.format(item.getIvaAmount())));
            table.addCell(createCell(CURRENCY_FORMAT.format(item.getTotal())));
        }

        doc.add(table);
        doc.add(new Paragraph("").setMarginBottom(15));
    }

    private void addSummary(Document doc, Quote quote) {
        doc.add(new Paragraph("RESUMEN")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        float[] summaryWidths = {4, 2};
        Table summaryTable = new Table(UnitValue.createPercentArray(summaryWidths))
                .useAllAvailableWidth();

        summaryTable.addCell(createCell("Subtotal:"));
        summaryTable.addCell(createCell(CURRENCY_FORMAT.format(quote.getSummary().getSubtotal()))
                .setTextAlignment(TextAlignment.RIGHT));

        summaryTable.addCell(createCell("IVA Total:"));
        summaryTable.addCell(createCell(CURRENCY_FORMAT.format(quote.getSummary().getTotalIva()))
                .setTextAlignment(TextAlignment.RIGHT));

        summaryTable.addCell(createHeaderCell("TOTAL:"));
        summaryTable.addCell(createHeaderCell(CURRENCY_FORMAT.format(quote.getSummary().getTotal()))
                .setTextAlignment(TextAlignment.RIGHT));

        doc.add(summaryTable);
        doc.add(new Paragraph("").setMarginBottom(20));
    }

    private void addFooter(Document doc, Quote quote) {
        if (quote.getNotes() != null && !quote.getNotes().isEmpty()) {
            doc.add(new Paragraph("NOTAS")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(5));

            doc.add(new Paragraph(quote.getNotes())
                    .setFontSize(10)
                    .setMarginBottom(20));
        }

        doc.add(new Paragraph("Gracias por su preferencia")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());

        doc.add(new Paragraph("OptimaStock - Sistema de Gestión de Inventarios")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));
    }

    private com.itextpdf.layout.element.Cell createHeaderCell(String text) {
        return new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text).setBold())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private com.itextpdf.layout.element.Cell createCell(String text) {
        return new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text))
                .setFontSize(9);
    }
}
