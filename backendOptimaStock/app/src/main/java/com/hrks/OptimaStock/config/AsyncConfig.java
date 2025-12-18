package com.hrks.OptimaStock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Hilos mínimos activos siempre
        executor.setCorePoolSize(2);
        // Máximo de hilos permitidos (si la cola se llena)
        executor.setMaxPoolSize(10);
        // Cola de espera antes de rechazar tareas
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OptimaAsync-");
        executor.initialize();
        return executor;
    }
}
