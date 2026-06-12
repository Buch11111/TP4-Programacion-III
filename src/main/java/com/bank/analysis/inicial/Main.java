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
    }
}
