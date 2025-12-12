package com.hrks.OptimaStock.price.model;

public class QuoteSummary {

    private Integer subtotal;
    private Integer totalIva;
    private Integer total;
    private Integer itemCount;

    public QuoteSummary() {
    }

    public QuoteSummary(Integer subtotal, Integer totalIva, Integer total, Integer itemCount) {
        this.subtotal = subtotal;
        this.totalIva = totalIva;
        this.total = total;
        this.itemCount = itemCount;
    }

    // Getters and Setters
    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(Integer totalIva) {
        this.totalIva = totalIva;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}
