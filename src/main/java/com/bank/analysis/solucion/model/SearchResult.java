package com.bank.analysis.solucion.model;

/**
 * Record inmutable que encapsula el resultado de una operación de búsqueda.
 * <p>
 * <b>Propósito educativo:</b> Demostrar el uso de Records de Java 21 para DTOs
 * inmutables que transportan datos entre capas sin lógica de negocio.
 * <p>
 * Incluye tanto la transacción encontrada como métricas de rendimiento de la
 * búsqueda, permitiendo a los estudiantes analizar la eficiencia de diferentes
 * algoritmos.
 *
 * @param transaction           la transacción encontrada, o {@code null} si no se
 *                              encontró
 * @param tiempoBusquedaNs      tiempo de ejecución en nanosegundos
 * @param comparacionesRealizadas número de comparaciones realizadas durante la búsqueda
 */
public record SearchResult(
        Transaction transaction,
        long tiempoBusquedaNs,
        int comparacionesRealizadas) {

    /**
     * Constructor canónico con validación.
     *
     * @param transaction              la transacción encontrada (puede ser null)
     * @param tiempoBusquedaNs         tiempo en nanosegundos (debe ser >= 0)
     * @param comparacionesRealizadas  número de comparaciones (debe ser >= 0)
     * @throws IllegalArgumentException si tiempoBusquedaNs o comparacionesRealizadas son negativos
     */
    public SearchResult {
        if (tiempoBusquedaNs < 0) {
            throw new IllegalArgumentException("tiempoBusquedaNs no puede ser negativo: " + tiempoBusquedaNs);
        }
        if (comparacionesRealizadas < 0) {
            throw new IllegalArgumentException("comparacionesRealizadas no puede ser negativo: " + comparacionesRealizadas);
        }
    }

    /**
     * Indica si la búsqueda encontró una transacción.
     *
     * @return {@code true} si se encontró la transacción
     */
    public boolean encontrada() {
        return transaction != null;
    }

    /**
     * Representación legible del resultado de búsqueda, útil para reportes.
     *
     * @return resumen formateado del resultado
     */
    @Override
    public String toString() {
        return String.format("SearchResult{encontrada=%s, tiempoBusquedaNs=%,d, comparacionesRealizadas=%,d}",
                encontrada(), tiempoBusquedaNs, comparacionesRealizadas);
    }
}
