package com.hrks.OptimaStock.listener;

import com.hrks.OptimaStock.price.event.QuoteCreatedEvent;
import com.hrks.OptimaStock.price.model.Quote;
import com.hrks.OptimaStock.price.service.PdfGeneratorService;
import com.hrks.OptimaStock.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class QuoteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(QuoteEventListener.class);

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private EmailService emailService;

    @Async
    @EventListener
    public void handleQuoteCreatedEvent(QuoteCreatedEvent event) {
        Quote quote = event.getQuote();

        logger.info("Recibido evento de creación de cotización: {}", quote.getQuoteNumber());

        if (quote.getCustomerInfo() == null || quote.getCustomerInfo().getEmail() == null) {
            logger.warn("No se puede enviar email: Falta información del cliente o email para cotización {}",
                    quote.getQuoteNumber());
            return;
        }

        try {
            logger.info("Generando PDF para cotización: {}", quote.getQuoteNumber());
            byte[] pdfBytes = pdfGeneratorService.generateQuotePdf(quote);

            String subject = "Nueva Cotización Generada - " + quote.getQuoteNumber();
            String body = "Estimado cliente,\n\n" +
                    "Adjunto encontrará la cotización solicitada con número " + quote.getQuoteNumber() + ".\n\n" +
                    "Gracias por su preferencia,\n" +
                    "Equipo OptimaStock";

            logger.info("Solicitando envío de email a: {}", quote.getCustomerInfo().getEmail());
            emailService.sendEmailWithAttachment(
                    quote.getCustomerInfo().getEmail(),
                    subject,
                    body,
                    pdfBytes,
                    "Cotizacion_" + quote.getQuoteNumber() + ".pdf");

        } catch (IOException e) {
            logger.error("Error generando PDF para email de cotización {}: {}", quote.getQuoteNumber(), e.getMessage(),
                    e);
        } catch (Exception e) {
            logger.error("Error inesperado en listener de cotización {}: {}", quote.getQuoteNumber(), e.getMessage(),
                    e);
        }
    }
}
