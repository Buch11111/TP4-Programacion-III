package com.bank.analysis.solucion;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bank.analysis.solucion.config.TransactionDataGenerator;
import com.bank.analysis.solucion.model.SearchResult;
import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.model.TransactionType;
import com.bank.analysis.solucion.report.PerformanceReport;
import com.bank.analysis.solucion.service.FilterService;
import com.bank.analysis.solucion.service.SearchService;
import com.bank.analysis.solucion.service.SortService;
import com.bank.analysis.solucion.service.SortService.SortResult;

/**
 * Punto de entrada de la aplicación refactorizada.
 * <p>
 * <b>Demostración de principios SOLID y Spring Boot:</b>
 * <ul>
 * <li><b>Inyección de Dependencias (DIP):</b> Todas las dependencias se
 * inyectan por constructor. No hay {@code new} en ninguna parte del código de
 * negocio.</li>
 * <li><b>Inversión de Control (IoC):</b> Spring gestiona el ciclo de vida de
 * los beans y ensambla el grafo de dependencias.</li>
 * <li><b>Separación de responsabilidades:</b> Cada servicio tiene una única
 * responsabilidad (búsqueda, ordenamiento, filtrado, reportes).</li>
 * <li><b>Programación contra interfaces:</b> Las dependencias son interfaces
 * ({@code SearchService}, {@code SortService}), no implementaciones
 * concretas.</li>
 * </ul>
 * <p>
 * <b>Contraste con el código "sucio":</b>
 * En {@code inicial.Main}, todo se hacía con {@code new} y toda la lógica
 * estaba en un solo método {@code main}. Aquí cada componente está aislado,
 * es testeable y es intercambiable.
 */
@SpringBootApplication
public class MainRefactorizado implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MainRefactorizado.class);

    private final TransactionDataGenerator generador;
    private final PerformanceReport performanceReport;
    private final FilterService filterService;
    private final SearchService searchService;
    private final SortService sortService;

    /**
     * Inyección de todas las dependencias por constructor.
     * <p>
     * <b>Nota educativa:</b> Spring resuelve automáticamente estas dependencias
     * a partir de los beans definidos en {@link com.bank.analysis.solucion.config.AppConfig}
     * (o detectados mediante escaneo de componentes). El programador nunca
     * instancia estas clases manualmente.
     */
    public MainRefactorizado(TransactionDataGenerator generador,
            PerformanceReport performanceReport,
            FilterService filterService,
            SearchService searchService,
            SortService sortService) {
        this.generador = generador;
        this.performanceReport = performanceReport;
        this.filterService = filterService;
        this.searchService = searchService;
        this.sortService = sortService;
    }

    /**
     * Punto de entrada de Spring Boot.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(MainRefactorizado.class, args);
    }

    /**
     * Ejecuta la demostración de la aplicación refactorizada.
     * <p>
     * Genera datos de prueba, ejecuta búsquedas, ordenamientos y filtros,
     * y finalmente imprime el reporte de rendimiento comparativo.
     */
    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("  SISTEMA DE ANÁLISIS DE TRANSACCIONES");
        log.info("  VERSIÓN REFACTORIZADA (SOLID + Spring)");
        log.info("==========================================");

        // === GENERACIÓN DE DATOS ===
        List<Transaction> transacciones = generador.generarDatosPredecibles(1000);
        log.info("Generadas {} transacciones de prueba.", transacciones.size());

        // === DEMOSTRACIÓN DE BÚSQUEDA ===
        demostrarBusqueda(transacciones);

        // === DEMOSTRACIÓN DE ORDENAMIENTO ===
        demostrarOrdenamiento(transacciones);

        // === DEMOSTRACIÓN DE FILTRADO ===
        demostrarFiltrado(transacciones);

        // === REPORTE DE RENDIMIENTO ===
        String reporte = performanceReport.generarReporte(transacciones);
        System.out.println(reporte);

        log.info("=== DEMOSTRACIÓN COMPLETADA ===");
    }

    /**
     * Demuestra el uso de búsqueda lineal y binaria.
     */
    private void demostrarBusqueda(List<Transaction> transacciones) {
        System.out.println("\n=== DEMOSTRACIÓN DE BÚSQUEDA ===\n");

        long idBuscado = transacciones.get(500).getId();

        // Búsqueda lineal
        SearchResult resultadoLineal = searchService.buscarLineal(idBuscado, transacciones);
        System.out.println("Búsqueda Lineal:");
        System.out.println("  ID buscado: " + idBuscado);
        System.out.println("  Encontrada: " + resultadoLineal.encontrada());
        System.out.println("  Comparaciones: " + resultadoLineal.comparacionesRealizadas());
        System.out.println("  Tiempo: " + PerformanceReport.formatNanos(resultadoLineal.tiempoBusquedaNs()));

        // Preparar lista ordenada para búsqueda binaria
        List<Transaction> ordenadasPorId = transacciones.stream()
                .sorted(Comparator.comparingLong(Transaction::getId))
                .toList();

        // Búsqueda binaria
        SearchResult resultadoBinaria = searchService.buscarBinaria(idBuscado, ordenadasPorId);
        System.out.println("\nBúsqueda Binaria (lista ordenada por ID):");
        System.out.println("  ID buscado: " + idBuscado);
        System.out.println("  Encontrada: " + resultadoBinaria.encontrada());
        System.out.println("  Comparaciones: " + resultadoBinaria.comparacionesRealizadas());
        System.out.println("  Tiempo: " + PerformanceReport.formatNanos(resultadoBinaria.tiempoBusquedaNs()));
    }

    /**
     * Demuestra el uso de Bubble Sort vs TimSort.
     */
    private void demostrarOrdenamiento(List<Transaction> transacciones) {
        System.out.println("\n=== DEMOSTRACIÓN DE ORDENAMIENTO ===\n");

        // Sublista pequeña para Bubble Sort (demora mucho con listas grandes)
        List<Transaction> sublista = transacciones.subList(0, Math.min(100, transacciones.size()));

        // Bubble Sort
        SortResult resultadoBubble = sortService.ordenarManual(sublista, false);
        System.out.println("Bubble Sort (descendente, n=" + sublista.size() + "):");
        System.out.println("  Comparaciones: " + resultadoBubble.comparaciones());
        System.out.println("  Intercambios: " + resultadoBubble.intercambios());
        System.out.println("  Tiempo: " + PerformanceReport.formatNanos(resultadoBubble.nanos()));
        System.out.println("  Mayor monto: $" + String.format("%,.2f",
                resultadoBubble.ordenada().get(0).getMonto()));
        System.out.println("  Menor monto: $" + String.format("%,.2f",
                resultadoBubble.ordenada().get(resultadoBubble.ordenada().size() - 1).getMonto()));

        // TimSort
        SortResult resultadoTim = sortService.ordenarBuiltIn(transacciones, true);
        System.out.println("\nTimSort (ascendente, n=" + transacciones.size() + "):");
        System.out.println("  Tiempo: " + PerformanceReport.formatNanos(resultadoTim.nanos()));
        System.out.println("  Menor monto: $" + String.format("%,.2f",
                resultadoTim.ordenada().get(0).getMonto()));
        System.out.println("  Mayor monto: $" + String.format("%,.2f",
                resultadoTim.ordenada().get(resultadoTim.ordenada().size() - 1).getMonto()));
    }

    /**
     * Demuestra el filtrado con composición de predicados (OCP).
     */
    private void demostrarFiltrado(List<Transaction> transacciones) {
        System.out.println("\n=== DEMOSTRACIÓN DE FILTRADO (OCP) ===\n");

        // Filtro por tipo
        List<Transaction> depositos = filterService.filtrarPorTipo(transacciones, TransactionType.DEPOSITO);
        System.out.println("DEPÓSITOS: " + depositos.size() + " transacciones");

        // Filtro combinado
        List<Transaction> filtradas = filterService.filtrarCombinado(
                transacciones,
                TransactionType.TRANSFERENCIA, // solo transferencias
                1000.0, // monto mínimo $1,000
                20000.0, // monto máximo $20,000
                LocalDate.of(2024, 1, 1), // desde enero 2024
                LocalDate.of(2024, 6, 30) // hasta junio 2024
        );
        System.out.println("TRANSFERENCIAS entre $1,000-$20,000 en H1 2024: " + filtradas.size() + " transacciones");

        // Filtro solo por fechas
        List<Transaction> q1 = filterService.filtrarPorFecha(
                transacciones,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 31));
        System.out.println("Transacciones en Q1 2024: " + q1.size());
    }
}
