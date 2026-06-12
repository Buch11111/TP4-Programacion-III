package com.bank.analysis.solucion.service;

import java.util.List;

import com.bank.analysis.solucion.model.SearchResult;
import com.bank.analysis.solucion.model.Transaction;

/**
 * Servicio de búsqueda de transacciones que demuestra la diferencia entre
 * algoritmos de búsqueda con distintas complejidades algorítmicas.
 * <p>
 * <b>Objetivo educativo:</b> Que los estudiantes comparen empíricamente:
 * <ul>
 * <li>Búsqueda lineal — O(n) — no requiere ordenamiento previo</li>
 * <li>Búsqueda binaria — O(log n) — requiere lista ordenada por ID</li>
 * </ul>
 * <p>
 * Ambos métodos retornan {@link SearchResult} que incluye métricas de
 * rendimiento (tiempo y comparaciones) para facilitar el análisis.
 */
public interface SearchService {

    /**
     * Realiza una búsqueda lineal (secuencial) sobre la lista de transacciones.
     * <p>
     * Recorre la lista elemento por elemento hasta encontrar el ID buscado o
     * llegar al final. No requiere que la lista esté ordenada.
     * <p>
     * <b>Complejidad temporal:</b> O(n) en el peor caso.
     * <b>Complejidad espacial:</b> O(1).
     *
     * @param id             identificador de la transacción a buscar
     * @param transacciones  lista de transacciones donde buscar (no requiere
     *                       orden)
     * @return {@link SearchResult} con la transacción encontrada (o null) y
     *         métricas
     * @throws IllegalArgumentException si transacciones es null
     */
    SearchResult buscarLineal(long id, List<Transaction> transacciones);

    /**
     * Realiza una búsqueda binaria sobre una lista de transacciones previamente
     * ordenada por ID.
     * <p>
     * <b>¡IMPORTANTE!</b> La lista DEBE estar ordenada por ID de forma
     * ascendente. Si no lo está, el resultado será incorrecto.
     * <p>
     * <b>Complejidad temporal:</b> O(log n).
     * <b>Complejidad espacial:</b> O(1).
     *
     * @param id             identificador de la transacción a buscar
     * @param transacciones  lista de transacciones ordenada por ID ascendente
     * @return {@link SearchResult} con la transacción encontrada (o null) y
     *         métricas
     * @throws IllegalArgumentException si transacciones es null
     */
    SearchResult buscarBinaria(long id, List<Transaction> transacciones);
}
