package com.bank.analysis.solucion.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bank.analysis.solucion.model.Transaction;
import com.bank.analysis.solucion.model.TransactionType;

/**
 * Utilidad para generar transacciones de prueba con datos aleatorios
 * controlados.
 * <p>
 * <b>Propósito educativo:</b> Separar la generación de datos de la lógica de
 * negocio. En el código "sucio", la generación de datos estaba acoplada al
 * {@code TransactionProcessor}. Aquí es un componente independiente.
 * <p>
 * Usa una semilla fija ({@code Random(42)}) para garantizar reproducibilidad
 * en las pruebas y reportes.
 */
public class TransactionDataGenerator {

    private static final String[] DESCRIPCIONES = {
            "Pago de servicios", "Transferencia recibida", "Depósito en cajero",
            "Retiro en sucursal", "Compra con tarjeta", "Pago de nómina",
            "Transferencia enviada", "Depósito en ventanilla", "Retiro en cajero",
            "Pago de factura", "Compra en línea", "Pago recurrente",
            "Depósito de cheque", "Transferencia entre cuentas", "Pago de préstamo",
            "Retiro sin tarjeta", "Depósito móvil", "Pago de impuestos",
            "Transferencia internacional", "Abono de intereses"
    };

    private final Random random;

    /**
     * Constructor que inicializa el generador con una semilla fija.
     */
    public TransactionDataGenerator() {
        this.random = new Random(42);
    }

    /**
     * Genera una lista de transacciones aleatorias.
     *
     * @param cantidad    número de transacciones a generar
     * @param idInicial   ID inicial (los IDs serán consecutivos desde este valor)
     * @return lista de transacciones generadas
     */
    public List<Transaction> generar(int cantidad, long idInicial) {
        List<Transaction> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            lista.add(generarAleatoria(random, idInicial + i));
        }
        return lista;
    }

    /**
     * Genera una lista de transacciones predecibles para testing (datos fijos con
     * semilla).
     *
     * @param cantidad número de transacciones
     * @return lista de transacciones
     */
    public List<Transaction> generarDatosPredecibles(int cantidad) {
        return generar(cantidad, 1001);
    }

    /**
     * Genera una única transacción aleatoria.
     * Método estático para poder ser usado desde {@link com.bank.analysis.solucion.report.PerformanceReport}.
     *
     * @param random generador de números aleatorios
     * @param id     identificador de la transacción
     * @return nueva transacción con datos aleatorios
     */
    public static Transaction generarAleatoria(Random random, long id) {
        double monto = Math.round((100.0 + random.nextDouble() * 50000.0) * 100.0) / 100.0;
        TransactionType tipo = TransactionType.values()[random.nextInt(TransactionType.values().length)];
        int anio = 2024;
        int mes = 1 + random.nextInt(12);
        int dia = 1 + random.nextInt(28);
        LocalDate fecha = LocalDate.of(anio, mes, dia);
        String descripcion = DESCRIPCIONES[random.nextInt(DESCRIPCIONES.length)];
        return new Transaction(id, monto, tipo, fecha, descripcion);
    }
}
