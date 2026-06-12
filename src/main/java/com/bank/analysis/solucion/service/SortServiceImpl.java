package com.bank.analysis.solucion.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.analysis.solucion.model.Transaction;

/**
 * Implementación del servicio de ordenamiento de transacciones.
 * <p>
 * Proporciona dos estrategias de ordenamiento con fines comparativos:
 * <ul>
 * <li><b>Bubble Sort (manual):</b> O(n²) — puramente didáctico</li>
 * <li><b>TimSort (built-in):</b> O(n log n) — vía {@link List#sort}</li>
 * </ul>
 * <p>
 * Ambas implementaciones trabajan sobre copias de la lista original y
 * recolectan métricas detalladas de rendimiento.
 */
@Service
public class SortServiceImpl implements SortService {

    private static final Logger log = LoggerFactory.getLogger(SortServiceImpl.class);

    /**
     * Constructor sin dependencias. Preparado para DI futura.
     */
    public SortServiceImpl() {
        log.debug("SortServiceImpl inicializado");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementación completa del algoritmo Bubble Sort con optimización de
     * parada temprana. Cuenta tanto comparaciones como intercambios (swaps).
     */
    @Override
    public SortResult ordenarManual(List<Transaction> transacciones, boolean ascendente) {
        Objects.requireNonNull(transacciones, "La lista de transacciones no puede ser null");

        // Crear copia para no modificar la lista original
        List<Transaction> ordenada = new ArrayList<>(transacciones);
        int n = ordenada.size();
        int comparaciones = 0;
        int intercambios = 0;

        long inicio = System.nanoTime();

        for (int i = 0; i < n - 1; i++) {
            boolean huboIntercambio = false;

            for (int j = 0; j < n - i - 1; j++) {
                comparaciones++;
                double montoJ = ordenada.get(j).getMonto();
                double montoJ1 = ordenada.get(j + 1).getMonto();

                boolean debeIntercambiar;
                if (ascendente) {
                    debeIntercambiar = montoJ > montoJ1;
                } else {
                    debeIntercambiar = montoJ < montoJ1;
                }

                if (debeIntercambiar) {
                    // Intercambio (swap)
                    Transaction temp = ordenada.get(j);
                    ordenada.set(j, ordenada.get(j + 1));
                    ordenada.set(j + 1, temp);
                    intercambios++;
                    huboIntercambio = true;
                }
            }

            // Optimización: si no hubo intercambios, la lista ya está ordenada
            if (!huboIntercambio) {
                break;
            }
        }

        long fin = System.nanoTime();
        long nanos = fin - inicio;

        log.info("Bubble Sort: n={}, comparaciones={}, intercambios={}, nanos={}",
                n, comparaciones, intercambios, nanos);

        return new SortResult(ordenada, comparaciones, intercambios, nanos);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utiliza {@link List#sort} con un {@link Comparator} basado en el monto.
     * El JDK usa TimSort internamente con complejidad O(n log n).
     */
    @Override
    public SortResult ordenarBuiltIn(List<Transaction> transacciones, boolean ascendente) {
        Objects.requireNonNull(transacciones, "La lista de transacciones no puede ser null");

        // Crear copia para no modificar la lista original
        List<Transaction> ordenada = new ArrayList<>(transacciones);

        Comparator<Transaction> comparador = Comparator.comparingDouble(Transaction::getMonto);
        if (!ascendente) {
            comparador = comparador.reversed();
        }

        long inicio = System.nanoTime();
        ordenada.sort(comparador);
        long fin = System.nanoTime();
        long nanos = fin - inicio;

        // No podemos contar comparaciones internas de TimSort, usamos 0 como indicador
        log.info("Built-in sort (TimSort): n={}, nanos={}", transacciones.size(), nanos);

        return new SortResult(ordenada, 0, 0, nanos);
    }
}
