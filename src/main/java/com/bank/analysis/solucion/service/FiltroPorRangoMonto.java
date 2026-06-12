package com.bank.analysis.solucion.service;

import com.bank.analysis.solucion.model.Transaction;

/**
 * Filtro de transacciones por rango de monto [min, max] inclusive.
 * <p>
 * Implementa {@link FiltroPredicate} para filtrar transacciones cuyo monto
 * esté dentro de un rango específico. Parte del patrón Strategy para filtros
 * combinables.
 *
 * @see FiltroPredicate
 * @since 1.0
 */
public class FiltroPorRangoMonto implements FiltroPredicate {

    private final double montoMin;
    private final double montoMax;

    /**
     * Crea un filtro por rango de monto.
     *
     * @param montoMin monto mínimo (inclusive)
     * @param montoMax monto máximo (inclusive)
     * @throws IllegalArgumentException si montoMin > montoMax
     */
    public FiltroPorRangoMonto(double montoMin, double montoMax) {
        if (montoMin > montoMax) {
            throw new IllegalArgumentException(
                    "montoMin (" + montoMin + ") no puede ser mayor que montoMax (" + montoMax + ")");
        }
        this.montoMin = montoMin;
        this.montoMax = montoMax;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retorna {@code true} si el monto de la transacción está en el rango
     * [montoMin, montoMax] inclusive.
     */
    @Override
    public boolean cumple(Transaction transaccion) {
        double monto = transaccion.getMonto();
        return monto >= montoMin && monto <= montoMax;
    }
}
