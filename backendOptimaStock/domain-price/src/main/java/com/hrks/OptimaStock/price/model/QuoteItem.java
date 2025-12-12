package com.hrks.OptimaStock.price.model;

public class QuoteItem {

    private Integer productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Integer unitPrice;
    private Integer subtotal;
    private Integer iva; // Porcentaje de IVA
    private Integer ivaAmount;
    private Integer total;

    public QuoteItem() {
    }

    /**
     * Calcula subtotal, IVA y total basado en cantidad, precio unitario y porcentaje IVA
     */
    public void calculateTotals() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = quantity * unitPrice;
            
            if (iva != null && iva > 0) {
                this.ivaAmount = (int) Math.round(subtotal * (iva / 100.0));
            } else {
                this.ivaAmount = 0;
            }
            
            this.total = subtotal + ivaAmount;
        }
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getIva() {
        return iva;
    }

    public void setIva(Integer iva) {
        this.iva = iva;
    }

    public Integer getIvaAmount() {
        return ivaAmount;
    }

    public void setIvaAmount(Integer ivaAmount) {
        this.ivaAmount = ivaAmount;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
