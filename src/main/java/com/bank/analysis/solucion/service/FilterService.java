package com.bank.analysis.solucion.service;

import java.time.LocalDate;
import java.util.List;

import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.model.TransactionType;

/**
 * Servicio de filtrado de transacciones que aplica el Principio de
 * Abierto/Cerrado (OCP) mediante composición de predicados.
 * <p>
 * <b>Objetivo educativo:</b> Contrastar este diseño con las cadenas de if-else
 * del código "sucio" ({@code inicial.TransactionProcessor#filtrarAvanzado}).
 * <p>
 * <b>Ventajas de este diseño:</b>
 * <ul>
 * <li>Cada criterio de filtro es un método independiente con una sola
 * responsabilidad</li>
 * <li>Agregar un nuevo criterio no requiere modificar código existente (OCP)</li>
 * <li>Los filtros se pueden combinar arbitrariamente mediante composición</li>
 * <li>Uso de Streams API y {@link java.util.function.Predicate} para claridad y
 * concurrencia potencial</li>
 * </ul>
 */
public interface FilterService {

    /**
     * Filtra transacciones por tipo.
     *
     * @param transacciones lista de transacciones a filtrar
     * @param tipo          tipo de transacción deseado
     * @return lista filtrada (nueva, no modifica la original)
     */
    List<Transaction> filtrarPorTipo(List<Transaction> transacciones, TransactionType tipo);

    /**
     * Filtra transacciones cuyo monto esté en el rango [min, max] (inclusive).
     *
     * @param transacciones lista de transacciones a filtrar
     * @param min           monto mínimo (inclusive)
     * @param max           monto máximo (inclusive)
     * @return lista filtrada (nueva, no modifica la original)
     * @throws IllegalArgumentException si min > max
     */
    List<Transaction> filtrarPorRangoMonto(List<Transaction> transacciones, double min, double max);

    /**
     * Filtra transacciones cuya fecha esté en el rango [desde, hasta] (inclusive).
     *
     * @param transacciones lista de transacciones a filtrar
     * @param desde         fecha mínima (inclusive)
     * @param hasta         fecha máxima (inclusive)
     * @return lista filtrada (nueva, no modifica la original)
     * @throws IllegalArgumentException si desde es posterior a hasta
     */
    List<Transaction> filtrarPorFecha(List<Transaction> transacciones, LocalDate desde, LocalDate hasta);

    /**
     * Filtro combinado que aplica todos los criterios no nulos mediante
     * composición de predicados.
     * <p>
     * <b>Diseño OCP:</b> Cada parámetro no nulo se convierte en un predicado
     * independiente que se combina con {@code AND}. Agregar un nuevo criterio
     * implica agregar un nuevo parámetro (opcional, con valor por defecto null)
     * sin modificar la lógica existente.
     *
     * @param transacciones lista de transacciones a filtrar
     * @param tipo          tipo de transacción ({@code null} para ignorar)
     * @param montoMin      monto mínimo ({@code null} para ignorar)
     * @param montoMax      monto máximo ({@code null} para ignorar)
     * @param fechaDesde    fecha mínima ({@code null} para ignorar)
     * @param fechaHasta    fecha máxima ({@code null} para ignorar)
     * @return lista de transacciones que cumplen todos los criterios no nulos
     */
    List<Transaction> filtrarCombinado(List<Transaction> transacciones,
            TransactionType tipo,
            Double montoMin,
            Double montoMax,
            LocalDate fechaDesde,
            LocalDate fechaHasta);

    /**
     * Aplica una lista de filtros encadenados usando el patrón Strategy.
     * <p>
     * <b>Diseño OCP:</b> Cada filtro es una implementación independiente de
     * {@link FiltroPredicate}. Agregar un nuevo criterio de filtrado implica
     * crear una nueva clase que implemente {@code FiltroPredicate}, sin
     * modificar este método ni las clases existentes.
     * <p>
     * <b>Complejidad:</b> O(n × f) donde n es el tamaño de la lista y f es
     * la cantidad de filtros. Cada transacción se evalúa contra todos los
     * filtros.
     *
     * @param transacciones lista de transacciones a filtrar
     * @param filtros       lista de criterios de filtro a aplicar (todos deben cumplirse)
     * @return lista de transacciones que cumplen TODOS los filtros
     */
    List<Transaction> filtrar(List<Transaction> transacciones, List<FiltroPredicate> filtros);
}
