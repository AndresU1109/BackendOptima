package com.hrks.OptimaStock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
                "com.hrks.OptimaStock",
                "com.hrks.OptimaStock.typeDocument",
                "com.hrks.OptimaStock.typePerson",
                "com.hrks.OptimaStock.category",
                "com.hrks.OptimaStock.person",
                "com.hrks.OptimaStock.iva",
                "com.hrks.OptimaStock.product",
                "com.hrks.OptimaStock.typeMovement",
                "com.hrks.OptimaStock.paymentMethod",
                "com.hrks.OptimaStock.sale",
                "com.hrks.OptimaStock.saleDetail",
                "com.hrks.OptimaStock.user",
                "com.hrks.OptimaStock.inventory",
                "com.hrks.OptimaStock.inventoryMovement",
                "com.hrks.OptimaStock.price"

})
@EntityScan(basePackages = {
                "com.hrks.OptimaStock.typeDocument.model",
                "com.hrks.OptimaStock.typePerson.model",
                "com.hrks.OptimaStock.category.model",
                "com.hrks.OptimaStock.person.model",
                "com.hrks.OptimaStock.iva.model",
                "com.hrks.OptimaStock.product.model",
                "com.hrks.OptimaStock.typeMovement.model",
                "com.hrks.OptimaStock.paymentMethod.model",
                "com.hrks.OptimaStock.sale.model",
                "com.hrks.OptimaStock.saleDetail.model",
                "com.hrks.OptimaStock.user.model",
                "com.hrks.OptimaStock.inventory.model",
                "com.hrks.OptimaStock.inventoryMovement.model"
})
@EnableJpaRepositories(basePackages = {
                "com.hrks.OptimaStock.typeDocument.repository",
                "com.hrks.OptimaStock.typePerson.repository",
                "com.hrks.OptimaStock.category.repository",
                "com.hrks.OptimaStock.person.repository",
                "com.hrks.OptimaStock.iva.repository",
                "com.hrks.OptimaStock.product.repository",
                "com.hrks.OptimaStock.typeMovement.repository",
                "com.hrks.OptimaStock.paymentMethod.repository",
                "com.hrks.OptimaStock.sale.repository",
                "com.hrks.OptimaStock.saleDetail.repository",
                "com.hrks.OptimaStock.user.repository",
                "com.hrks.OptimaStock.inventory.repository",
                "com.hrks.OptimaStock.inventoryMovement.repository"
})
@EnableMongoRepositories(basePackages = "com.hrks.OptimaStock.price.repository")
public class OptimaStockApplication {
        public static void main(String[] args) {
                SpringApplication.run(OptimaStockApplication.class, args);
        }
}
