package com.bank.analysis.solucion.service;

import com.bank.analysis.solucion.model.Transaction;

/**
 * Interfaz funcional que define un criterio de filtrado para transacciones.
 * <p>
 * <b>Propósito educativo:</b> Demostrar el patrón Strategy aplicado al
 * Principio Abierto/Cerrado (OCP). Cada criterio de filtro es una
 * implementación independiente de esta interfaz, permitiendo agregar
 * nuevos filtros sin modificar el código existente.
 * <p>
 * <b>Contraste con el código "sucio":</b> En
 * {@code inicial.TransactionProcessor}, los filtros se implementaban con
 * cadenas de if-else. Agregar un nuevo criterio requería modificar el
 * método existente. Con esta interfaz, cada filtro es una clase separada
 * que se puede combinar con otras.
 *
 * @see FilterService
 * @see FiltroPorTipo
 * @see FiltroPorRangoMonto
 * @see FiltroPorFecha
 * @since 1.0
 */
@FunctionalInterface
public interface FiltroPredicate {

    /**
     * Evalúa si una transacción cumple con este criterio de filtro.
     *
     * @param transaccion la transacción a evaluar
     * @return {@code true} si la transacción cumple el criterio
     */
    boolean cumple(Transaction transaccion);
}
