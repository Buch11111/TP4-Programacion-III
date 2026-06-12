package com.bank.analysis.solucion.service;

import java.util.List;

import com.bank.analysis.solucion.model.Transaction;

/**
 * Servicio de ordenamiento de transacciones que permite comparar algoritmos
 * cuadráticos (Bubble Sort) con los optimizados del JDK (TimSort).
 * <p>
 * <b>Objetivo educativo:</b> Demostrar la diferencia entre:
 * <ul>
 * <li>Bubble Sort — O(n²) — implementación manual con fines didácticos</li>
 * <li>Ordenamiento built-in ({@link List#sort}) — O(n log n) — TimSort en JDK</li>
 * </ul>
 * <p>
 * Ambos métodos retornan un {@link SortResult} que incluye la lista ordenada y
 * métricas de rendimiento (tiempo, comparaciones e intercambios/swap).
 */
public interface SortService {

    /**
     * Ordena una lista de transacciones por monto usando el algoritmo Bubble Sort.
     * <p>
     * <b>Nota:</b> Este método es puramente didáctico. Bubble Sort tiene
     * complejidad O(n²) y no debe usarse en producción. Su propósito es que los
     * estudiantes experimenten la diferencia de rendimiento con algoritmos
     * cuadráticos.
     * <p>
     * <b>Optimización incluida:</b> Se detiene tempranamente si en una pasada no
     * se realizan intercambios (la lista ya está ordenada). Mejor caso: O(n).
     *
     * @param transacciones lista de transacciones a ordenar (no se modifica)
     * @param ascendente    {@code true} para orden ascendente (menor a mayor),
     *                      {@code false} para descendente
     * @return {@link SortResult} con la nueva lista ordenada y métricas
     */
    SortResult ordenarManual(List<Transaction> transacciones, boolean ascendente);

    /**
     * Ordena una lista de transacciones por monto usando el algoritmo nativo del
     * JDK ({@link List#sort}).
     * <p>
     * Utiliza TimSort (híbrido de Merge Sort + Insertion Sort) con complejidad
     * O(n log n). Es la opción recomendada para código productivo.
     *
     * @param transacciones lista de transacciones a ordenar (no se modifica)
     * @param ascendente    {@code true} para orden ascendente (menor a mayor),
     *                      {@code false} para descendente
     * @return {@link SortResult} con la nueva lista ordenada y métricas
     */
    SortResult ordenarBuiltIn(List<Transaction> transacciones, boolean ascendente);

    /**
     * Record que encapsula el resultado de una operación de ordenamiento.
     * <p>
     * Incluye tanto la lista ordenada como métricas de rendimiento detalladas.
     *
     * @param ordenada      nueva lista con las transacciones ordenadas
     * @param comparaciones número de comparaciones realizadas
     * @param intercambios  número de intercambios (swaps) realizados (0 para
     *                      built-in)
     * @param nanos         tiempo de ejecución en nanosegundos
     */
    record SortResult(
            List<Transaction> ordenada,
            int comparaciones,
            int intercambios,
            long nanos) {

        /**
         * Constructor canónico con validación.
         */
        public SortResult {
            if (ordenada == null) {
                throw new IllegalArgumentException("La lista ordenada no puede ser null");
            }
            if (comparaciones < 0)
                comparaciones = 0;
            if (intercambios < 0)
                intercambios = 0;
            if (nanos < 0)
                nanos = 0;
        }
    }
}
