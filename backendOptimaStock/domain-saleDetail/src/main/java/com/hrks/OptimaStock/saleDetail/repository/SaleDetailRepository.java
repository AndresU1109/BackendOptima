package com.hrks.OptimaStock.saleDetail.repository;

import com.hrks.OptimaStock.saleDetail.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Integer> {

    @Query("SELECT sd.product, SUM(sd.quantity) as totalQty FROM SaleDetail sd GROUP BY sd.product ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProducts();
}
