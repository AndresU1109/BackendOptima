package com.hrks.OptimaStock.iva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.io.Serializable;

@Entity
@Table(name = "IVA")
<<<<<<< HEAD
@Getter
@Setter
public class IVA implements Serializable {

    private static final long serialVersionUID = 1L;
=======
public class IVA {
>>>>>>> ae56ddbeefc9397f11a9229656024094f5064dc7

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
