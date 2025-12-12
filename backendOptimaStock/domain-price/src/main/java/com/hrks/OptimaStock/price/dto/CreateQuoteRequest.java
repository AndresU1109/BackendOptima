package com.hrks.OptimaStock.price.dto;

import com.hrks.OptimaStock.price.model.CustomerInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateQuoteRequest {

    @NotNull(message = "La información del cliente es requerida")
    @Valid
    private CustomerInfo customerInfo;

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<QuoteItemRequest> items;

    private String notes;

    public CreateQuoteRequest() {
    }

    public CreateQuoteRequest(CustomerInfo customerInfo, List<QuoteItemRequest> items, String notes) {
        this.customerInfo = customerInfo;
        this.items = items;
        this.notes = notes;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<QuoteItemRequest> getItems() {
        return items;
    }

    public void setItems(List<QuoteItemRequest> items) {
        this.items = items;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
