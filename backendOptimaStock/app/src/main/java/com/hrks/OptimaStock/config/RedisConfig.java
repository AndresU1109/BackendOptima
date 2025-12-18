package com.hrks.OptimaStock.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

        /**
         * Configure RedisTemplate with JSON serialization
         */
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                // Create ObjectMapper with JavaTimeModule for LocalDateTime support
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Enable default typing for polymorphic deserialization
                PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(Object.class)
                                .build();
                objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                                objectMapper);

                // Set serializers
                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(jackson2JsonRedisSerializer);
                template.setHashValueSerializer(jackson2JsonRedisSerializer);

                template.afterPropertiesSet();
                return template;
        }

        /**
         * Configure CacheManager with custom TTL for different cache regions
         */
        @Bean
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                // Create ObjectMapper with JavaTimeModule
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(Object.class)
                                .build();
                objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
                                objectMapper);

                // Default cache configuration
                RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(jackson2JsonRedisSerializer))
                                .disableCachingNullValues();

                // Custom TTL configurations for specific caches
                Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

                // Categories, IVA, Types - Low change frequency (1 hour)
                cacheConfigurations.put("categories", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
                cacheConfigurations.put("iva", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
                cacheConfigurations.put("typeDocuments", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
                cacheConfigurations.put("typePersons", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
                cacheConfigurations.put("typeMovements", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
                cacheConfigurations.put("paymentMethods", defaultCacheConfig.entryTtl(Duration.ofHours(1)));

                // Products - Medium change frequency (30 minutes)
                cacheConfigurations.put("products", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));

                // Persons - Higher change frequency (15 minutes)
                cacheConfigurations.put("persons", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

                // Inventory - Frequently changing (5 minutes)
                cacheConfigurations.put("inventory", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultCacheConfig)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .build();
        }
}
