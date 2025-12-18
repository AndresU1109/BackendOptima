package com.hrks.OptimaStock.product.service;

import com.hrks.OptimaStock.category.model.Category;
import com.hrks.OptimaStock.category.repository.CategoryRepository;
import com.hrks.OptimaStock.iva.model.IVA;
import com.hrks.OptimaStock.iva.repository.IVARepository;
import com.hrks.OptimaStock.product.model.Product;
import com.hrks.OptimaStock.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IVARepository ivaRepository;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            IVARepository ivaRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.ivaRepository = ivaRepository;
    }

    @Cacheable(value = "products", key = "'all'")
    public List<Product> findAll() {
        logger.debug("Fetching all products from database");
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findById(Integer id) {
        logger.debug("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    @CachePut(value = "products", key = "#result.id")
    @CacheEvict(value = "products", key = "'all'")
    public Product save(Product product) {
        logger.info("Saving product: {}", product.getName());

        // Fetch and validate Category
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category with id " +
                            product.getCategory().getId() + " not found"));
            product.setCategory(category);
        }

        // Fetch and validate IVA
        if (product.getIva() != null && product.getIva().getId() != null) {
            IVA iva = ivaRepository.findById(product.getIva().getId())
                    .orElseThrow(() -> new RuntimeException("IVA with id " +
                            product.getIva().getId() + " not found"));
            product.setIva(iva);
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Product saved successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }

    @CacheEvict(value = "products", allEntries = true)
    public void delete(Integer id) {
        logger.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
        logger.info("Product deleted successfully");
    }
}
