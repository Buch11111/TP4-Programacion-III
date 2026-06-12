package com.bank.analysis.solucion.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una transacción bancaria con encapsulamiento adecuado.
 * <p>
 * <b>Mejoras respecto a la versión "sucia" ({@code inicial.Transaction}):</b>
 * <ul>
 * <li>Todos los campos son {@code private final} (inmutabilidad controlada)</li>
 * <li>Usa {@link TransactionType} (enum) en lugar de {@code String} para el
 * tipo</li>
 * <li>Usa {@link LocalDate} en lugar de {@code String} para la fecha</li>
 * <li>Nombres descriptivos: {@code monto} en vez de {@code mnt},
 * {@code fecha} en vez de {@code fec}</li>
 * <li>Implementa correctamente {@code equals()} y {@code hashCode()} basados en
 * el ID</li>
 * <li>Acceso solo mediante getters (encapsulamiento)</li>
 * </ul>
 *
 * @see TransactionType
 */
public class Transaction {

    /** Identificador único de la transacción */
    private final long id;

    /** Monto de la transacción en la moneda local */
    private final double monto;

    /** Tipo de transacción según el enum {@link TransactionType} */
    private final TransactionType tipo;

    /** Fecha en que se realizó la transacción */
    private final LocalDate fecha;

    /** Descripción textual de la transacción */
    private final String descripcion;

    /**
     * Construye una transacción con todos sus atributos.
     *
     * @param id          identificador único
     * @param monto       monto de la transacción (positivo)
     * @param tipo        tipo de transacción
     * @param fecha       fecha de la transacción
     * @param descripcion descripción textual
     * @throws IllegalArgumentException si el monto es negativo
     */
    public Transaction(long id, double monto, TransactionType tipo, LocalDate fecha, String descripcion) {
        if (monto < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo: " + monto);
        }
        this.id = id;
        this.monto = monto;
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
    }

    /**
     * @return el identificador único de la transacción
     */
    public long getId() {
        return id;
    }

    /**
     * @return el monto de la transacción
     */
    public double getMonto() {
        return monto;
    }

    /**
     * @return el tipo de transacción
     */
    public TransactionType getTipo() {
        return tipo;
    }

    /**
     * @return la fecha de la transacción
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * @return la descripción de la transacción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Dos transacciones son iguales si tienen el mismo ID.
     *
     * @param o el objeto a comparar
     * @return {@code true} si ambos objetos representan la misma transacción
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    /**
     * El hash code se calcula exclusivamente a partir del ID.
     *
     * @return código hash basado en el ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representación legible de la transacción.
     *
     * @return cadena con todos los atributos formateados
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", monto=" + String.format("%.2f", monto) +
                ", tipo=" + tipo +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
