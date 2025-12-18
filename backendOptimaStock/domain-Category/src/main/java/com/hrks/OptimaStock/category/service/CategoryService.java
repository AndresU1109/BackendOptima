package com.hrks.OptimaStock.category.service;

import com.hrks.OptimaStock.category.model.Category;
import com.hrks.OptimaStock.category.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> findAll() {
        logger.debug("Fetching all categories from database");
        return categoryRepository.findAll();
    }

    @Cacheable(value = "categories", key = "#id")
    public Optional<Category> findById(Integer id) {
        logger.debug("Fetching category with id: {}", id);
        return categoryRepository.findById(id);
    }

    @CachePut(value = "categories", key = "#result.id")
    @CacheEvict(value = "categories", key = "'all'")
    public Category save(Category category) {
        logger.info("Saving category: {}", category.getDescription());
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category saved successfully with id: {}", savedCategory.getId());
        return savedCategory;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void delete(Integer id) {
        logger.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
        logger.info("Category deleted successfully");
    }
}
