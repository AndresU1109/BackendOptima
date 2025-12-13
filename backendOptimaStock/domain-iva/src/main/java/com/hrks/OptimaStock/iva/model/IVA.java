package com.hrks.OptimaStock.iva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "IVA")
public class IVA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIVA")
    private Integer id;

    @Column(name = "iva", precision = 5, scale = 2)
    private BigDecimal iva;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getIva() {
        return iva;
    }
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
}
