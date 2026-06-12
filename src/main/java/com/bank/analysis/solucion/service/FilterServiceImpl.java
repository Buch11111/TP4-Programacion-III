package com.bank.analysis.solucion.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.model.TransactionType;

/**
 * Implementación del servicio de filtrado de transacciones usando Streams API y
 * composición de predicados.
 * <p>
 * <b>Demostración del Principio Abierto/Cerrado (OCP):</b>
 * <p>
 * En el código "sucio" ({@code inicial.TransactionProcessor}), los filtros se
 * implementaban con cadenas de if-else anidadas. Agregar un nuevo criterio
 * requería modificar el método existente, violando el OCP.
 * <p>
 * En esta implementación, cada criterio es un {@link Predicate} independiente.
 * Los predicados se combinan con {@code AND} de forma declarativa. Agregar un
 * nuevo criterio implica:
 * <ol>
 * <li>Agregar un nuevo método de filtrado específico</li>
 * <li>Agregar el parámetro opcional a {@code filtrarCombinado}</li>
 * <li>Agregar una línea para componer el predicado condicionalmente</li>
 * </ol>
 * Esto mantiene el código abierto a extensión pero cerrado a modificación de
 * la lógica existente.
 *
 * @see FilterService
 */
@Service
public class FilterServiceImpl implements FilterService {

    private static final Logger log = LoggerFactory.getLogger(FilterServiceImpl.class);

    /**
     * Constructor sin dependencias. Preparado para DI.
     */
    public FilterServiceImpl() {
        log.debug("FilterServiceImpl inicializado");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> filtrarPorTipo(List<Transaction> transacciones, TransactionType tipo) {
        Objects.requireNonNull(transacciones, "La lista no puede ser null");
        Objects.requireNonNull(tipo, "El tipo no puede ser null");

        return transacciones.stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> filtrarPorRangoMonto(List<Transaction> transacciones, double min, double max) {
        Objects.requireNonNull(transacciones, "La lista no puede ser null");
        if (min > max) {
            throw new IllegalArgumentException(
                    "El monto mínimo (" + min + ") no puede ser mayor que el máximo (" + max + ")");
        }

        return transacciones.stream()
                .filter(t -> t.getMonto() >= min && t.getMonto() <= max)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> filtrarPorFecha(List<Transaction> transacciones, LocalDate desde, LocalDate hasta) {
        Objects.requireNonNull(transacciones, "La lista no puede ser null");
        Objects.requireNonNull(desde, "La fecha desde no puede ser null");
        Objects.requireNonNull(hasta, "La fecha hasta no puede ser null");
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha desde (" + desde + ") no puede ser posterior a hasta (" + hasta + ")");
        }

        return transacciones.stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Diseño OCP:</b> Construye un predicado compuesto condicionalmente.
     * Cada criterio no nulo agrega una condición al predicado mediante
     * {@link Predicate#and}. El resultado es un filtro declarativo que no
     * requiere if-else anidados.
     * <p>
     * <b>Complejidad:</b> O(n) donde n es el tamaño de la lista. Cada elemento
     * se evalúa contra todos los predicados activos.
     */
    @Override
    public List<Transaction> filtrarCombinado(List<Transaction> transacciones,
            TransactionType tipo,
            Double montoMin,
            Double montoMax,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        Objects.requireNonNull(transacciones, "La lista no puede ser null");

        // Predicado base: acepta todo
        Predicate<Transaction> predicado = t -> true;

        // Composición condicional de predicados — cada criterio es independiente
        if (tipo != null) {
            predicado = predicado.and(t -> t.getTipo() == tipo);
        }
        if (montoMin != null) {
            double min = montoMin;
            predicado = predicado.and(t -> t.getMonto() >= min);
        }
        if (montoMax != null) {
            double max = montoMax;
            predicado = predicado.and(t -> t.getMonto() <= max);
        }
        if (fechaDesde != null) {
            LocalDate desde = fechaDesde;
            predicado = predicado.and(t -> !t.getFecha().isBefore(desde));
        }
        if (fechaHasta != null) {
            LocalDate hasta = fechaHasta;
            predicado = predicado.and(t -> !t.getFecha().isAfter(hasta));
        }

        List<Transaction> resultado = transacciones.stream()
                .filter(predicado)
                .collect(Collectors.toList());

        log.debug("Filtro combinado: {} transacciones -> {} resultados", transacciones.size(), resultado.size());

        return resultado;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementación del patrón Strategy:</b>
     * Recibe una lista de {@link FiltroPredicate} y los aplica encadenados
     * usando {@code Stream.filter()}. Cada filtro se evalúa de forma
     * independiente: si una transacción no cumple alguno de los filtros,
     * es descartada.
     * <p>
     * Este diseño permite extender el comportamiento del filtrado sin
     * modificar el código existente (OCP). Para agregar un nuevo criterio,
     * basta con crear una nueva implementación de {@link FiltroPredicate}.
     *
     * @param transacciones lista de transacciones a filtrar
     * @param filtros       criterios de filtro (vacío = sin filtro)
     * @return lista filtrada
     */
    @Override
    public List<Transaction> filtrar(List<Transaction> transacciones, List<FiltroPredicate> filtros) {
        Objects.requireNonNull(transacciones, "La lista no puede ser null");
        Objects.requireNonNull(filtros, "La lista de filtros no puede ser null");

        if (filtros.isEmpty()) {
            return new ArrayList<>(transacciones);
        }

        return transacciones.stream()
                .filter(t -> {
                    for (FiltroPredicate filtro : filtros) {
                        if (!filtro.cumple(t)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
