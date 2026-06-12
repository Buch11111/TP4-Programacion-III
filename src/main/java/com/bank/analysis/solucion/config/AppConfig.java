package com.bank.analysis.solucion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración explícita de beans de Spring para el contexto de la aplicación.
 * <p>
 * <b>Propósito educativo:</b> Demostrar la Inversión de Control (IoC) y la
 * Inyección de Dependencias (DI) de forma explícita con {@code @Configuration}
 * y {@code @Bean}.
 * <p>
 * Los servicios concretos ({@code SearchServiceImpl}, {@code SortServiceImpl},
 * {@code FilterServiceImpl}) son detectados automáticamente por
 * {@code @ComponentScan} gracias a sus anotaciones {@code @Service}.
 * <p>
 * Esta clase define manualmente los beans que requieren ensamblaje explícito
 * o que no están anotados con estereotipos de Spring.
 * <p>
 * <b>Grafo de dependencias:</b>
 * <pre>
 * TransactionDataGenerator (independiente, sin anotación)
 * SearchServiceImpl (@Service, independiente)
 * SortServiceImpl (@Service, independiente)
 * FilterServiceImpl (@Service, independiente)
 * PerformanceReport (@Service, auto-detectado) → SearchService, SortService, FilterService
 * MainRefactorizado → PerformanceReport, TransactionDataGenerator,
 *                      FilterService, SearchService, SortService
 * </pre>
 */
@Configuration
public class AppConfig {

    /**
     * Generador de datos de prueba. No tiene {@code @Service} porque es una
     * utilidad, no un servicio de negocio. Se define explícitamente aquí para
     * demostrar la creación programática de beans.
     *
     * @return instancia de {@link TransactionDataGenerator}
     */
    @Bean
    public TransactionDataGenerator transactionDataGenerator() {
        return new TransactionDataGenerator();
    }
}
