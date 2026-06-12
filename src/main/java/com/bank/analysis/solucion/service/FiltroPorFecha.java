package com.bank.analysis.solucion.service;

import java.time.LocalDate;
import java.util.Objects;

import com.bank.analysis.solucion.model.Transaction;

/**
 * Filtro de transacciones por rango de fechas [desde, hasta] inclusive.
 * <p>
 * Implementa {@link FiltroPredicate} para filtrar transacciones cuya fecha
 * esté dentro de un rango específico. Parte del patrón Strategy para filtros
 * combinables.
 *
 * @see FiltroPredicate
 * @since 1.0
 */
public class FiltroPorFecha implements FiltroPredicate {

    private final LocalDate fechaDesde;
    private final LocalDate fechaHasta;

    /**
     * Crea un filtro por rango de fechas.
     *
     * @param fechaDesde fecha mínima (inclusive, no puede ser null)
     * @param fechaHasta fecha máxima (inclusive, no puede ser null)
     * @throws NullPointerException     si fechaDesde o fechaHasta son null
     * @throws IllegalArgumentException si fechaDesde es posterior a fechaHasta
     */
    public FiltroPorFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        this.fechaDesde = Objects.requireNonNull(fechaDesde, "fechaDesde no puede ser null");
        this.fechaHasta = Objects.requireNonNull(fechaHasta, "fechaHasta no puede ser null");
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException(
                    "fechaDesde (" + fechaDesde + ") no puede ser posterior a fechaHasta (" + fechaHasta + ")");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retorna {@code true} si la fecha de la transacción está en el rango
     * [fechaDesde, fechaHasta] inclusive.
     */
    @Override
    public boolean cumple(Transaction transaccion) {
        LocalDate fecha = transaccion.getFecha();
        return !fecha.isBefore(fechaDesde) && !fecha.isAfter(fechaHasta);
    }
}
