package com.bank.analysis.inicial;

import java.util.List;

/**
 * Clase principal del paquete "inicial".
 * <p>
 * <b>⚠ CÓDIGO PROCEDURAL - VIOLACIONES INTENCIONALES:</b>
 * <ul>
 * <li>Usa {@code new} directamente para crear dependencias (sin DI)</li>
 * <li>Toda la lógica está en el {@code main} (sin separación de
 * responsabilidades)</li>
 * <li>Usa {@code System.out.println} directamente (sin logging)</li>
 * <li>Sin manejo de excepciones</li>
 * </ul>
 * <p>
 * Este código representa el "antes" de la refactorización: sin Spring,
 * sin inyección de dependencias, sin separación de capas.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  SISTEMA DE ANÁLISIS DE TRANSACCIONES");
        System.out.println("  VERSIÓN INICIAL (CÓDIGO SUCIO)");
        System.out.println("==========================================\n");

        // Crear el procesador y generar datos
        TransactionProcessor procesador = new TransactionProcessor();
        procesador.generarTransacciones(100);

        System.out.println("Total de transacciones en memoria: " + procesador.getTransacciones().size());
        System.out.println();

        // --- Búsqueda por ID ---
        System.out.println("=== BÚSQUEDA POR ID ===");
        Transaction encontrada = procesador.buscarPorId(1005);
        System.out.println("Resultado buscarPorId(1005): " + encontrada);

        Transaction noEncontrada = procesador.buscarPorId(9999);
        System.out.println("Resultado buscarPorId(9999): " + noEncontrada + " (esperado: null)");
        System.out.println();

        // --- Búsqueda por monto ---
        System.out.println("=== BÚSQUEDA POR MONTO ===");
        List<Transaction> porMonto = procesador.buscarPorMonto(1000.0, 5000.0);
        System.out.println("Transacciones entre $1,000 y $5,000: "
                + (porMonto != null ? porMonto.size() : 0));
        System.out.println();

        // --- Búsqueda por tipo ---
        System.out.println("=== BÚSQUEDA POR TIPO ===");
        List<Transaction> depositos = procesador.buscarPorTipo("DEPOSITO");
        System.out.println("Total de DEPÓSITOS: " + depositos.size());
        List<Transaction> retiros = procesador.buscarPorTipo("RETIRO");
        System.out.println("Total de RETIROS: " + retiros.size());
        System.out.println();

        // --- Ordenamiento ---
        System.out.println("=== ORDENAMIENTO BUILT-IN POR MONTO ===");
        List<Transaction> ordenadas = procesador.ordenarBuiltIn(false); // descendente
        if (ordenadas != null && !ordenadas.isEmpty()) {
            System.out.println("Mayor monto: " + ordenadas.get(0));
            System.out.println("Menor monto: " + ordenadas.get(ordenadas.size() - 1));
        } else {
            System.out.println("Método no implementado aún.");
        }
        System.out.println();

        // --- Filtro avanzado ---
        System.out.println("=== FILTRO AVANZADO ===");
        List<Transaction> filtradas = procesador.filtrarAvanzado(
                "DEPOSITO", 1000.0, 20000.0, "2024-01-01", "2024-03-31");
        System.out.println("DEPÓSITOS entre $1,000 y $20,000 en Q1 2024: " + filtradas.size());
        System.out.println();

        // --- Balance ---
        System.out.println("=== BALANCE TOTAL ===");
        double balance = procesador.calcularBalance();
        System.out.printf("Balance total: $%,.2f%n", balance);
        System.out.println();

        // --- Conteo por tipo ---
        procesador.contarPorTipo();
        System.out.println();

        // --- Reporte de Desempeño ---
        generarReporteDesempeno();
    }

    private static void generarReporteDesempeno() {
        System.out.println("=== REPORTE DE DESEMPEÑO ===");
        System.out.println("+------------------+---------------------+---------------------+------------------+------------------+");
        System.out.println("| Tamaño entrada   | Búsqueda Lineal (ns)| Búsqueda Binaria(ns)| Bubble Sort (ns) | Built-in Sort(ns)|");
        System.out.println("+------------------+---------------------+---------------------+------------------+------------------+");

        int[] tamanos = { 100, 1000, 10000, 100000 };

        for (int n : tamanos) {
            TransactionProcessor processor = new TransactionProcessor();
            // Limpiar los hardcodeados y generar exactamente 'n'
            processor.getTransacciones().clear();
            processor.generarTransacciones(n);

            // Warm-up de la JVM (ejecutar una vez sin medir para que JIT compile)
            processor.ordenarBuiltIn(true);
            processor.buscarPorId(-1);

            long tiempoLineal = 0, tiempoBinaria = 0, tiempoBubble = 0, tiempoBuiltIn = 0;
            long inicio, fin;
            long idInexistente = 999999999L; // Peor caso para búsquedas: no existe

            // 1. Búsqueda Lineal (Peor caso)
            inicio = System.nanoTime();
            processor.buscarPorId(idInexistente);
            fin = System.nanoTime();
            tiempoLineal = fin - inicio;

            // 2. Búsqueda Binaria (Peor caso)
            // Primero ordenamos la lista internamente por ID para que la búsqueda binaria funcione
            processor.getTransacciones().sort((t1, t2) -> Long.compare(t1.id, t2.id));
            inicio = System.nanoTime();
            processor.buscarPorIdBinario(idInexistente);
            fin = System.nanoTime();
            tiempoBinaria = fin - inicio;

            // 3. Bubble Sort
            // Para N=100,000 puede tardar bastante, pero lo ejecutamos para medir.
            if (n <= 10000) {
                inicio = System.nanoTime();
                processor.ordenarManual(true);
                fin = System.nanoTime();
                tiempoBubble = fin - inicio;
            } else {
                // Si tarda demasiado en tu PC, descomentar la siguiente línea y comentar la ejecución de ordenarManual
                // tiempoBubble = -1;
                inicio = System.nanoTime();
                processor.ordenarManual(true);
                fin = System.nanoTime();
                tiempoBubble = fin - inicio;
            }

            // 4. Built-in Sort
            inicio = System.nanoTime();
            processor.ordenarBuiltIn(true);
            fin = System.nanoTime();
            tiempoBuiltIn = fin - inicio;

            // Imprimir fila
            String bubbleStr = tiempoBubble == -1 ? "N/A" : String.format("%,14d", tiempoBubble);
            System.out.printf("| %-16d | %,19d | %,19d | %16s | %,16d |%n",
                    n, tiempoLineal, tiempoBinaria, bubbleStr, tiempoBuiltIn);
        }
        System.out.println("+------------------+---------------------+---------------------+------------------+------------------+");

        System.out.println("\n=== JUSTIFICACIÓN DE LOS RESULTADOS ===");
        System.out.println("1. Búsqueda Lineal (O(n)):");
        System.out.println("   Tiene que revisar uno por uno, así que si la lista es 10 veces más grande,");
        System.out.println("   tarda más o menos 10 veces más. Se nota en los nanosegundos cómo sube de forma lineal.");
        System.out.println("2. Búsqueda Binaria (O(log n)):");
        System.out.println("   Acá como va partiendo la lista a la mitad cada vez, es rapidísimo.");
        System.out.println("   Incluso con 100 mil elementos, hace re pocas comparaciones y los tiempos casi ni se mueven.");
        System.out.println("3. Bubble Sort (O(n²)):");
        System.out.println("   Pésimo para listas grandes porque tiene dos for anidados.");
        System.out.println("   Cuando pasamos a 10.000 se nota que ya le cuesta, y con 100.000 directamente se queda");
        System.out.println("   pensando banda de tiempo. Se ve re claro el crecimiento cuadrático.");
        System.out.println("4. Built-in Sort / TimSort (O(n log n)):");
        System.out.println("   Es el sort que ya viene hecho en Java. Al ser O(n log n), escala súper bien.");
        System.out.println("   Mientras que el Bubble Sort se re cuelga con 100k, este los ordena en un par de milisegundos.");
        System.out.println("=======================================");
    }
}
