package com.hrks.OptimaStock.inventoryMovement.service;

import com.hrks.OptimaStock.inventoryMovement.model.InventoryMovement;
import com.hrks.OptimaStock.inventoryMovement.repository.InventoryMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryMovementService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryMovementService.class);

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    public List<InventoryMovement> getAllInventoryMovements() {
        return inventoryMovementRepository.findAll();
    }

    public Optional<InventoryMovement> getInventoryMovementById(Integer id) {
        return inventoryMovementRepository.findById(id);
    }

    public InventoryMovement saveInventoryMovement(InventoryMovement inventoryMovement) {
        logger.info("Saving inventory movement - Quantity: {}", inventoryMovement.getQuantity());

        InventoryMovement saved = inventoryMovementRepository.save(inventoryMovement);
        logger.info("Inventory movement saved successfully with ID: {}", saved.getId());

        return saved;
    }

    public void deleteInventoryMovement(Integer id) {
        inventoryMovementRepository.deleteById(id);
    }
}
