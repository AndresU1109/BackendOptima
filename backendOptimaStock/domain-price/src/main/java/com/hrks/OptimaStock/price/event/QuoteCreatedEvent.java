package com.hrks.OptimaStock.price.event;

import com.hrks.OptimaStock.price.model.Quote;
import org.springframework.context.ApplicationEvent;

public class QuoteCreatedEvent extends ApplicationEvent {

    private final Quote quote;

    public QuoteCreatedEvent(Object source, Quote quote) {
        super(source);
        this.quote = quote;
    }

    public Quote getQuote() {
        return quote;
    }
}
