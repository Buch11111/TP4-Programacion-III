package com.bank.analysis.solucion.service;

import java.util.Objects;

import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.model.TransactionType;

/**
 * Filtro de transacciones por tipo (DEPOSITO, RETIRO, TRANSFERENCIA, PAGO).
 * <p>
 * Implementa {@link FiltroPredicate} para filtrar transacciones que coincidan
 * con un tipo específico. Parte del patrón Strategy para filtros combinables.
 *
 * @see FiltroPredicate
 * @since 1.0
 */
public class FiltroPorTipo implements FiltroPredicate {

    private final TransactionType tipo;

    /**
     * Crea un filtro para un tipo de transacción específico.
     *
     * @param tipo el tipo de transacción a filtrar (no puede ser null)
     * @throws NullPointerException si tipo es null
     */
    public FiltroPorTipo(TransactionType tipo) {
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser null");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retorna {@code true} si la transacción es del tipo configurado.
     */
    @Override
    public boolean cumple(Transaction transaccion) {
        return transaccion.getTipo() == tipo;
    }
}
