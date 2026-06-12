package com.bank.analysis.solucion.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.analysis.solucion.model.SearchResult;
import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.service.FilterService;
import com.bank.analysis.solucion.service.SearchService;
import com.bank.analysis.solucion.service.SortService;
import com.bank.analysis.solucion.service.SortService.SortResult;

@Service
public class PerformanceReport {

    private static final Logger log = LoggerFactory.getLogger(PerformanceReport.class);

    /** Tamaño del dataset para la fase de warm-up */
    private static final int WARMUP_SIZE = 1000;

    private final SearchService searchService;
    private final SortService sortService;
    private final FilterService filterService;

    /**
     * Inyección de dependencias por constructor.
     *
     * @param searchService servicio de búsqueda (lineal + binaria)
     * @param sortService   servicio de ordenamiento (bubble + built-in)
     * @param filterService servicio de filtrado de transacciones
     */
    public PerformanceReport(SearchService searchService, SortService sortService, FilterService filterService) {
        this.searchService = searchService;
        this.sortService = sortService;
        this.filterService = filterService;
        log.info("PerformanceReport inicializado con SearchService, SortService y FilterService");
    }

    /**
     * Genera un reporte completo de rendimiento para todos los algoritmos y
     * tamaños de datos.
     *
     * @param transacciones lista base de transacciones para los tests
     * @return String formateado con la tabla de resultados
     */
    public String generarReporte(List<Transaction> transacciones) {
        log.info("=== INICIANDO REPORTE DE RENDIMIENTO ===");

        // === FASE DE WARM-UP ===
        ejecutarWarmup(transacciones);

        // === RECOLECCIÓN DE MÉTRICAS ===
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(repeatString("=", 110)).append("\n");
        sb.append(centrar("REPORTE DE RENDIMIENTO - ANÁLISIS DE ALGORITMOS", 110)).append("\n");
        sb.append(repeatString("=", 110)).append("\n\n");

        // Cabecera de la tabla
        sb.append(formatearFila("Algoritmo", "n=100", "n=1,000", "n=10,000", "n=100,000", "Big O Teórico"));
        sb.append(repeatString("-", 110)).append("\n");

        // Búsqueda Lineal
        sb.append(formatearFila("Búsqueda Lineal",
                medirConTamano(transacciones, 100, this::medirBusquedaLineal),
                medirConTamano(transacciones, 1000, this::medirBusquedaLineal),
                medirConTamano(transacciones, 10000, this::medirBusquedaLineal),
                medirConTamano(transacciones, 100000, this::medirBusquedaLineal),
                "O(n)"));

        // Búsqueda Binaria
        sb.append(formatearFila("Búsqueda Binaria",
                medirConTamano(transacciones, 100, this::medirBusquedaBinaria),
                medirConTamano(transacciones, 1000, this::medirBusquedaBinaria),
                medirConTamano(transacciones, 10000, this::medirBusquedaBinaria),
                medirConTamano(transacciones, 100000, this::medirBusquedaBinaria),
                "O(log n)"));

        sb.append("\n");

        // Bubble Sort
        sb.append(formatearFila("Bubble Sort",
                medirConTamano(transacciones, 100, this::medirBubbleSort),
                medirConTamano(transacciones, 1000, this::medirBubbleSort),
                medirConTamano(transacciones, 10000, this::medirBubbleSort),
                "N/A (> 60s)",
                "O(n²)"));

        // Built-in Sort
        sb.append(formatearFila("TimSort (built-in)",
                medirConTamano(transacciones, 100, this::medirTimSort),
                medirConTamano(transacciones, 1000, this::medirTimSort),
                medirConTamano(transacciones, 10000, this::medirTimSort),
                medirConTamano(transacciones, 100000, this::medirTimSort),
                "O(n log n)"));

        sb.append(repeatString("=", 110)).append("\n");
        sb.append("\nNota: Los tiempos están en nanosegundos (ns). 1 ms = 1,000,000 ns\n");
        sb.append("Para Bubble Sort con n=100,000 el tiempo es excesivo (> 60s), se omite la medición.\n");

        log.info("=== REPORTE DE RENDIMIENTO COMPLETADO ===");
        return sb.toString();
    }

    /**
     * Ejecuta una fase de warm-up para que la JVM compile y optimice el código
     * antes de las mediciones reales.
     */
    private void ejecutarWarmup(List<Transaction> transacciones) {
        log.info("Ejecutando warm-up de la JVM...");
        List<Transaction> sublista = obtenerSublista(transacciones, WARMUP_SIZE);

        // Varias iteraciones para trigger JIT compilation
        for (int i = 0; i < 5; i++) {
            searchService.buscarLineal(sublista.get(sublista.size() / 2).getId(), sublista);
            searchService.buscarBinaria(sublista.get(sublista.size() / 2).getId(), sublista);
            sortService.ordenarManual(sublista, true);
            sortService.ordenarBuiltIn(sublista, true);
        }
        log.info("Warm-up completado.");
    }

    /**
     * Obtiene una sublista de transacciones del tamaño especificado.
     * Si no hay suficientes, genera aleatorias adicionales.
     */
    private List<Transaction> obtenerSublista(List<Transaction> fuente, int tamanio) {
        if (fuente.size() >= tamanio) {
            return new ArrayList<>(fuente.subList(0, tamanio));
        }
        // Si necesitamos más, generamos
        List<Transaction> lista = new ArrayList<>(fuente);
        Random rnd = new Random(42);
        for (int i = lista.size(); i < tamanio; i++) {
            lista.add(com.bank.analysis.solucion.config.TransactionDataGenerator.generarAleatoria(rnd, 5000 + i));
        }
        return lista;
    }

    // ==================== MÉTODOS DE MEDICIÓN ====================

    private String medirBusquedaLineal(List<Transaction> sublista) {
        long idBuscado = sublista.get(sublista.size() - 1).getId(); // Peor caso: último
        SearchResult result = searchService.buscarLineal(idBuscado, sublista);
        return formatNanos(result.tiempoBusquedaNs());
    }

    private String medirBusquedaBinaria(List<Transaction> sublista) {
        // Ordenar por ID para búsqueda binaria (crear copia para no afectar otras
        // mediciones)
        List<Transaction> ordenada = new ArrayList<>(sublista);
        ordenada.sort(Comparator.comparingLong(Transaction::getId));
        long idBuscado = ordenada.get(ordenada.size() - 1).getId();
        SearchResult result = searchService.buscarBinaria(idBuscado, ordenada);
        return formatNanos(result.tiempoBusquedaNs());
    }

    private String medirBubbleSort(List<Transaction> sublista) {
        SortResult result = sortService.ordenarManual(sublista, true);
        return formatNanos(result.nanos());
    }

    private String medirTimSort(List<Transaction> sublista) {
        SortResult result = sortService.ordenarBuiltIn(sublista, true);
        return formatNanos(result.nanos());
    }

    // ==================== HELPERS DE FORMATEO ====================

    /**
     * Mide una operación con un tamaño específico.
     */
    private String medirConTamano(List<Transaction> fuente, int tamanio,
            java.util.function.Function<List<Transaction>, String> operacion) {
        try {
            List<Transaction> sublista = obtenerSublista(fuente, Math.min(tamanio, fuente.size()));
            return operacion.apply(sublista);
        } catch (Exception e) {
            return "ERROR";
        }
    }

    /**
     * Formatea nanosegundos para mostrar de forma legible.
     */
    public static String formatNanos(long nanos) {
        if (nanos < 1_000) {
            return String.format("%,d ns", nanos);
        } else if (nanos < 1_000_000) {
            return String.format("%,d μs", nanos / 1_000);
        } else if (nanos < 1_000_000_000) {
            return String.format("%,d ms", nanos / 1_000_000);
        } else {
            return String.format("%,.2f s", nanos / 1_000_000_000.0);
        }
    }

    /**
     * Formatea una fila de la tabla con columnas alineadas.
     */
    private String formatearFila(String algoritmo, String v1, String v2, String v3, String v4, String bigO) {
        return String.format("%-22s %-16s %-16s %-16s %-16s %-16s%n",
                algoritmo, v1, v2, v3, v4, bigO);
    }

    /**
     * Centra un texto en un ancho dado.
     */
    private String centrar(String texto, int ancho) {
        int padding = (ancho - texto.length()) / 2;
        if (padding <= 0)
            return texto;
        return repeatString(" ", padding) + texto;
    }

    /**
     * Repite un string n veces.
     */
    private String repeatString(String s, int veces) {
        return s.repeat(veces);
    }
}
