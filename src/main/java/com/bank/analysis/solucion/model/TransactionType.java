package com.bank.analysis.solucion.model;

/**
 * Enumeración que define los tipos de transacción bancaria soportados por el
 * sistema.
 * <p>
 * <b>Propósito educativo:</b> El uso de un {@code enum} en lugar de
 * {@code String} para representar el tipo de transacción demuestra:
 * <ul>
 * <li><b>Type safety:</b> El compilador garantiza que solo se usen valores
 * válidos</li>
 * <li><b>Encapsulamiento:</b> Cada constante puede tener atributos y
 * comportamiento asociado</li>
 * <li><b>Principio Abierto/Cerrado (OCP):</b> Agregar un nuevo tipo requiere
 * solo agregar una constante al enum, no modificar cadenas de if-else
 * dispersas</li>
 * </ul>
 *
 * @see Transaction
 */
public enum TransactionType {

    /** Depósito de dinero en una cuenta */
    DEPOSITO("Depósito"),

    /** Retiro de dinero de una cuenta */
    RETIRO("Retiro"),

    /** Transferencia entre cuentas */
    TRANSFERENCIA("Transferencia"),

    /** Pago de servicios o facturas */
    PAGO("Pago");

    /** Descripción en español del tipo de transacción */
    private final String descripcion;

    /**
     * Constructor privado del enum.
     *
     * @param descripcion descripción legible en español
     */
    TransactionType(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción en español del tipo de transacción.
     *
     * @return descripción legible
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Convierte de forma segura un String a {@link TransactionType}.
     * Útil para migrar desde el código sucio que usaba Strings.
     *
     * @param tipo nombre del tipo (case-insensitive)
     * @return el enum correspondiente
     * @throws IllegalArgumentException si el tipo no es válido
     */
    public static TransactionType fromString(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo no puede ser nulo o vacío");
        }
        try {
            return TransactionType.valueOf(tipo.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de transacción inválido: '" + tipo
                            + "'. Valores válidos: DEPOSITO, RETIRO, TRANSFERENCIA, PAGO");
        }
    }
}
