package com.hrks.OptimaStock.price.repository;

import com.hrks.OptimaStock.price.model.Quote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends MongoRepository<Quote, String> {
    
    Optional<Quote> findByQuoteNumber(String quoteNumber);
    
    List<Quote> findByCustomerInfoEmail(String email);
    
    List<Quote> findByStatus(String status);
    
    List<Quote> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Quote> findByCustomerInfoNameContainingIgnoreCase(String name);
}
