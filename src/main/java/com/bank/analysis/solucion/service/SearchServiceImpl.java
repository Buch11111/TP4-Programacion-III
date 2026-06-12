package com.bank.analysis.solucion.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.analysis.solucion.model.SearchResult;
import com.bank.analysis.solucion.model.Transaction;

/**
 * Implementación del servicio de búsqueda de transacciones.
 * <p>
 * Implementa dos algoritmos de búsqueda — lineal y binaria — cada uno midiendo
 * su tiempo de ejecución y número de comparaciones. Diseñado para que los
 * estudiantes puedan comparar empíricamente el rendimiento de O(n) vs O(log n).
 * <p>
 * <b>Nota sobre warm-up de la JVM:</b> Las primeras ejecuciones pueden ser más
 * lentas debido a la compilación JIT. Para mediciones precisas, se recomienda
 * ejecutar una fase de calentamiento antes de tomar mediciones (esto se maneja
 * en {@code PerformanceReport}).
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    /**
     * Constructor sin dependencias externas.
     * Preparado para inyección de dependencias futura.
     */
    public SearchServiceImpl() {
        log.debug("SearchServiceImpl inicializado");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementación: recorre la lista secuencialmente contando cada comparación.
     */
    @Override
    public SearchResult buscarLineal(long id, List<Transaction> transacciones) {
        Objects.requireNonNull(transacciones, "La lista de transacciones no puede ser null");

        int comparaciones = 0;
        long inicio = System.nanoTime();

        // Búsqueda lineal: O(n)
        for (Transaction t : transacciones) {
            comparaciones++;
            if (t.getId() == id) {
                long fin = System.nanoTime();
                SearchResult result = new SearchResult(t, fin - inicio, comparaciones);
                log.debug("Búsqueda lineal: ID={} encontrado en {} comparaciones, {} ns",
                        id, comparaciones, fin - inicio);
                return result;
            }
        }

        long fin = System.nanoTime();
        log.debug("Búsqueda lineal: ID={} NO encontrado después de {} comparaciones, {} ns",
                id, comparaciones, fin - inicio);
        return new SearchResult(null, fin - inicio, comparaciones);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementación: búsqueda binaria iterativa sobre lista ordenada por ID.
     * Requiere que {@code transacciones} esté ordenada por ID ascendente.
     */
    @Override
    public SearchResult buscarBinaria(long id, List<Transaction> transacciones) {
        Objects.requireNonNull(transacciones, "La lista de transacciones no puede ser null");

        int comparaciones = 0;
        long inicio = System.nanoTime();

        int izquierda = 0;
        int derecha = transacciones.size() - 1;

        while (izquierda <= derecha) {
            comparaciones++;
            int medio = izquierda + (derecha - izquierda) / 2; // Evita overflow
            Transaction t = transacciones.get(medio);

            if (t.getId() == id) {
                long fin = System.nanoTime();
                SearchResult result = new SearchResult(t, fin - inicio, comparaciones);
                log.debug("Búsqueda binaria: ID={} encontrado en {} comparaciones, {} ns",
                        id, comparaciones, fin - inicio);
                return result;
            } else if (t.getId() < id) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }

        long fin = System.nanoTime();
        log.debug("Búsqueda binaria: ID={} NO encontrado después de {} comparaciones, {} ns",
                id, comparaciones, fin - inicio);
        return new SearchResult(null, fin - inicio, comparaciones);
    }
}
