package com.hrks.OptimaStock.iva.service;

import com.hrks.OptimaStock.iva.model.IVA;
import com.hrks.OptimaStock.iva.repository.IVARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IVAService {

    private static final Logger logger = LoggerFactory.getLogger(IVAService.class);

    private final IVARepository ivaRepository;

    public IVAService(IVARepository ivaRepository) {
        this.ivaRepository = ivaRepository;
    }

    @Cacheable(value = "iva", key = "'all'")
    public List<IVA> findAll() {
        logger.debug("Fetching all IVA rates from database");
        return ivaRepository.findAll();
    }

    @Cacheable(value = "iva", key = "#id")
    public Optional<IVA> findById(Integer id) {
        logger.debug("Fetching IVA with id: {}", id);
        return ivaRepository.findById(id);
    }

    @CachePut(value = "iva", key = "#result.id")
    @CacheEvict(value = "iva", key = "'all'")
    public IVA save(IVA iva) {
        logger.info("Saving IVA rate: {}%", iva.getIva());
        IVA savedIVA = ivaRepository.save(iva);
        logger.info("IVA rate saved successfully with id: {}", savedIVA.getId());
        return savedIVA;
    }

    @CacheEvict(value = "iva", allEntries = true)
    public void delete(Integer id) {
        logger.info("Deleting IVA with id: {}", id);
        ivaRepository.deleteById(id);
        logger.info("IVA deleted successfully");
    }
}
