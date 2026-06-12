package com.bank.analysis.inicial;

import java.util.Objects;

/**
 * Representa una transacción bancaria.
 * <p>
 * <b>⚠ CÓDIGO SUCIO - CON MALAS PRÁCTICAS INTENCIONALES:</b>
 * Esta clase viola intencionalmente principios de encapsulamiento y buenas
 * prácticas
 * para que los estudiantes identifiquen los problemas y los corrijan en la
 * versión refactorizada.
 * <ul>
 * <li>Todos los campos son públicos (sin encapsulamiento)</li>
 * <li>Usa {@code String} para el tipo en lugar de un {@code enum}</li>
 * <li>Campos no finales (mutables sin control)</li>
 * <li>Nombres poco descriptivos (mnt, fec, desc)</li>
 * <li>La fecha se almacena como String en lugar de {@code LocalDate}</li>
 * </ul>
 */
public class Transaction {

    /** Identificador único de la transacción */
    public long id;

    /** Monto de la transacción (nomenclatura poco clara: mnt en vez de monto) */
    public double mnt;

    /**
     * Tipo de transacción como String.
     * Valores posibles: "DEPOSITO", "RETIRO", "TRANSFERENCIA", "PAGO"
     */
    public String tipo;

    /**
     * Fecha de la transacción en formato "yyyy-MM-dd" (String en vez de LocalDate)
     */
    public String fec;

    /** Descripción de la transacción (nomenclatura poco clara) */
    public String desc;

    /**
     * Constructor con todos los campos.
     *
     * @param id   identificador de la transacción
     * @param mnt  monto de la transacción
     * @param tipo tipo de transacción como String
     * @param fec  fecha como String en formato "yyyy-MM-dd"
     * @param desc descripción
     */
    public Transaction(long id, double mnt, String tipo, String fec, String desc) {
        this.id = id;
        this.mnt = mnt;
        this.tipo = tipo;
        this.fec = fec;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", mnt=" + mnt + ", tipo='" + tipo + "', fec='" + fec + "', desc='" + desc
                + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
