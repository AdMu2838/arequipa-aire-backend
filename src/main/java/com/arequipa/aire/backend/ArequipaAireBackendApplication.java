package com.arequipa.aire.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicación principal del Sistema Inteligente de Monitoreo y Predicción 
 * de Calidad del Aire Urbano para Arequipa.
 * 
 * @author Arequipa Aire Team
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ArequipaAireBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArequipaAireBackendApplication.class, args);
    }
}