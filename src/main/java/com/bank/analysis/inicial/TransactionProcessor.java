package com.bank.analysis.inicial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TransactionProcessor {

    /** Lista interna de transacciones (almacenamiento + lógica todo junto) */
    private List<Transaction> transacciones;

    /** Generador de números aleatorios */
    private Random random;

    /** Tipos de transacción hardcodeados como Strings (debería ser un enum) */
    private static final String[] TIPOS = { "DEPOSITO", "RETIRO", "TRANSFERENCIA", "PAGO" };

    /** Descripciones de ejemplo */
    private static final String[] DESCS = {
            "Pago de servicios", "Transferencia recibida", "Depósito en cajero",
            "Retiro en sucursal", "Compra con tarjeta", "Pago de nómina",
            "Transferencia enviada", "Depósito en ventanilla", "Retiro en cajero",
            "Pago de factura", "Compra en línea", "Pago recurrente",
            "Depósito de cheque", "Transferencia entre cuentas", "Pago de préstamo",
            "Retiro sin tarjeta", "Depósito móvil", "Pago de impuestos",
            "Transferencia internacional", "Abono de intereses"
    };

    /**
     * Constructor: genera 20 transacciones hardcodeadas para que los tests
     * tengan datos predecibles. También genera aleatorias.
     */
    public TransactionProcessor() {
        this.transacciones = new ArrayList<>();
        this.random = new Random(42); // Semilla fija para reproducibilidad

        // Datos predecibles para tests
        transacciones.add(new Transaction(1001, 1500.00, "DEPOSITO", "2024-01-15", "Depósito en cajero"));
        transacciones.add(new Transaction(1002, 500.00, "RETIRO", "2024-01-16", "Retiro en sucursal"));
        transacciones.add(new Transaction(1003, 2500.00, "TRANSFERENCIA", "2024-01-17", "Transferencia recibida"));
        transacciones.add(new Transaction(1004, 350.00, "PAGO", "2024-01-18", "Pago de servicios"));
        transacciones.add(new Transaction(1005, 10000.00, "DEPOSITO", "2024-01-19", "Depósito en ventanilla"));
        transacciones.add(new Transaction(1006, 200.00, "RETIRO", "2024-01-20", "Retiro en cajero"));
        transacciones.add(new Transaction(1007, 5000.00, "TRANSFERENCIA", "2024-01-21", "Transferencia enviada"));
        transacciones.add(new Transaction(1008, 750.00, "PAGO", "2024-01-22", "Pago de factura"));
        transacciones.add(new Transaction(1009, 3200.00, "DEPOSITO", "2024-01-23", "Depósito móvil"));
        transacciones.add(new Transaction(1010, 1800.00, "RETIRO", "2024-01-24", "Retiro sin tarjeta"));
        transacciones.add(new Transaction(1011, 4200.00, "TRANSFERENCIA", "2024-02-01", "Transferencia internacional"));
        transacciones.add(new Transaction(1012, 900.00, "PAGO", "2024-02-02", "Pago de impuestos"));
        transacciones.add(new Transaction(1013, 6700.00, "DEPOSITO", "2024-02-03", "Depósito de cheque"));
        transacciones.add(new Transaction(1014, 150.00, "RETIRO", "2024-02-04", "Retiro en cajero"));
        transacciones.add(new Transaction(1015, 8900.00, "TRANSFERENCIA", "2024-02-05", "Transferencia entre cuentas"));
        transacciones.add(new Transaction(1016, 430.00, "PAGO", "2024-02-06", "Pago recurrente"));
        transacciones.add(new Transaction(1017, 11000.00, "DEPOSITO", "2024-02-07", "Abono de intereses"));
        transacciones.add(new Transaction(1018, 600.00, "RETIRO", "2024-02-08", "Retiro en sucursal"));
        transacciones.add(new Transaction(1019, 7700.00, "TRANSFERENCIA", "2024-02-09", "Transferencia recibida"));
        transacciones.add(new Transaction(1020, 280.00, "PAGO", "2024-02-10", "Compra en línea"));
    }

    /**
     * Genera transacciones aleatorias y las agrega a la lista.
     *
     * @param cantidad número de transacciones a generar
     */
    public void generarTransacciones(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            long id = 2000 + i; // IDs a partir de 2000 para no colisionar con los hardcodeados
            double monto = Math.round((100.0 + random.nextDouble() * 50000.0) * 100.0) / 100.0;
            String tipo = TIPOS[random.nextInt(TIPOS.length)];
            int anio = 2024;
            int mes = 1 + random.nextInt(12);
            int dia = 1 + random.nextInt(28);
            String fecha = String.format("%d-%02d-%02d", anio, mes, dia);
            String descripcion = DESCS[random.nextInt(DESCS.length)];
            transacciones.add(new Transaction(id, monto, tipo, fecha, descripcion));
        }
    }

    /**
     * Obtiene la lista completa de transacciones.
     *
     * @return lista de transacciones
     */
    public List<Transaction> getTransacciones() {
        return transacciones;
    }

    // ==================== MÉTODOS QUE LOS ESTUDIANTES DEBEN IMPLEMENTAR
    // ====================

    /**
     * Busca una transacción por ID usando búsqueda lineal (recorrido secuencial).
     * Los estudiantes deben implementar este método recorriendo la lista no
     * ordenada.
     * <p>
     * <b>Complejidad esperada:</b> O(n)
     *
     * @param id el identificador a buscar
     * @return la transacción encontrada, o {@code null} si no existe
     */
    // TODO: Implementar búsqueda lineal sobre la lista no ordenada.
    // Recorrer transacciones elemento por elemento comparando el id.
    // Retornar null si no se encuentra.
    public Transaction buscarPorId(long id) {
        for (Transaction t : transacciones) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    public Transaction buscarPorIdBinario(long id) {
        int izquierda = 0;
        int derecha = transacciones.size() - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            Transaction t = transacciones.get(medio);
            if (t.id == id) {
                return t;
            } else if (t.id < id) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null;
    }

    /**
     * Busca transacciones cuyo monto esté dentro del rango especificado [min, max].
     * Los estudiantes deben implementar este método.
     *
     * @param montoMin monto mínimo (inclusive)
     * @param montoMax monto máximo (inclusive)
     * @return lista de transacciones cuyo monto está en el rango
     */
    // TODO: Implementar búsqueda por rango de monto.
    // Recorrer la lista y agregar las transacciones que cumplan:
    // mnt >= montoMin && mnt <= montoMax
    public List<Transaction> buscarPorMonto(double montoMin, double montoMax) {
        List<Transaction> resultado = new ArrayList<>();
        for (Transaction t : transacciones) {
            if (t.mnt >= montoMin && t.mnt <= montoMax) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Busca transacciones por tipo usando una cadena de if-else.
     * <p>
     * <b>⚠ VIOLACIÓN DEL PRINCIPIO ABIERTO/CERRADO (OCP):</b>
     * Para agregar un nuevo tipo de transacción, hay que modificar este método.
     * Esto demuestra exactamente lo que NO se debe hacer.
     *
     * @param tipo el tipo de transacción como String
     * @return lista de transacciones del tipo especificado
     */
    public List<Transaction> buscarPorTipo(String tipo) {
        List<Transaction> resultado = new ArrayList<>();
        for (Transaction t : transacciones) {
            if (t.tipo == null && tipo == null) {
                resultado.add(t);
            } else if (t.tipo != null && t.tipo.equals(tipo)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Ordena la lista de transacciones por monto usando el algoritmo Bubble Sort.
     * Los estudiantes deben implementar este método.
     * <p>
     * <b>Complejidad esperada:</b> O(n²)
     *
     * @param ascendente {@code true} para orden ascendente, {@code false} para
     *                   descendente
     */
    // TODO: Implementar ordenamiento manual con Bubble Sort.
    // Crear una copia de la lista de transacciones.
    // Aplicar el algoritmo Bubble Sort comparando los montos (mnt).
    // Si ascendente es true, ordenar de menor a mayor.
    // Si ascendente es false, ordenar de mayor a menor.
    // Retornar la lista ordenada (no modificar la original directamente).
    public List<Transaction> ordenarManual(boolean ascendente) {
        List<Transaction> copia = new ArrayList<>(transacciones);
        int n = copia.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                boolean cambiar = false;
                if (ascendente) {
                    if (copia.get(j).mnt > copia.get(j + 1).mnt) {
                        cambiar = true;
                    }
                } else {
                    if (copia.get(j).mnt < copia.get(j + 1).mnt) {
                        cambiar = true;
                    }
                }
                if (cambiar) {
                    Transaction temp = copia.get(j);
                    copia.set(j, copia.get(j + 1));
                    copia.set(j + 1, temp);
                }
            }
        }
        return copia;
    }

    /**
     * Ordena la lista de transacciones por monto usando el método
     * {@link List#sort(Comparator)}.
     * Los estudiantes deben implementar este método.
     *
     * @param ascendente {@code true} para orden ascendente, {@code false} para
     *                   descendente
     * @return nueva lista ordenada
     */
    // TODO: Implementar ordenamiento con List.sort() y Comparator.
    // Crear una copia de la lista de transacciones.
    // Usar Comparator.comparingDouble(t -> t.mnt) para comparar por monto.
    // Si ascendente es false, usar .reversed() en el comparador.
    // Llamar a list.sort(comparator) y retornar la lista.
    public List<Transaction> ordenarBuiltIn(boolean ascendente) {
        List<Transaction> copia = new ArrayList<>(transacciones);
        Comparator<Transaction> comp = Comparator.comparingDouble(t -> t.mnt);
        if (!ascendente) {
            comp = comp.reversed();
        }
        copia.sort(comp);
        return copia;
    }

    /**
     * Filtro avanzado que combina múltiples criterios.
     * <p>
     * <b>⚠ VIOLACIÓN DEL PRINCIPIO ABIERTO/CERRADO (OCP):</b>
     * Este método usa una horrible cadena de if-else que combina los criterios
     * manualmente. Agregar un nuevo criterio requiere modificar este código.
     * <p>
     * <b>NOTA PARA ESTUDIANTES:</b> Este es el tipo de código que deben aprender a
     * refactorizar usando el patrón Strategy o composición de predicados.
     *
     * @param tipo       tipo de transacción (puede ser null para ignorar)
     * @param montoMin   monto mínimo (puede ser null para ignorar)
     * @param montoMax   monto máximo (puede ser null para ignorar)
     * @param fechaDesde fecha mínima como String (puede ser null para ignorar)
     * @param fechaHasta fecha máxima como String (puede ser null para ignorar)
     * @return lista de transacciones que cumplen todos los criterios no nulos
     */
    public List<Transaction> filtrarAvanzado(String tipo, Double montoMin, Double montoMax,
            String fechaDesde, String fechaHasta) {
        List<Transaction> resultado = new ArrayList<>();

        for (Transaction t : transacciones) {
            boolean cumple = true;

            // Violación OCP: if-else encadenados para cada combinación de criterios
            if (tipo != null && !tipo.isEmpty()) {
                if (!tipo.equals(t.tipo)) {
                    cumple = false;
                }
            }

            if (cumple && montoMin != null) {
                if (t.mnt < montoMin) {
                    cumple = false;
                }
            }

            if (cumple && montoMax != null) {
                if (t.mnt > montoMax) {
                    cumple = false;
                }
            }

            if (cumple && fechaDesde != null && !fechaDesde.isEmpty()) {
                // Comparación de strings — frágil y propensa a errores
                if (t.fec.compareTo(fechaDesde) < 0) {
                    cumple = false;
                }
            }

            if (cumple && fechaHasta != null && !fechaHasta.isEmpty()) {
                if (t.fec.compareTo(fechaHasta) > 0) {
                    cumple = false;
                }
            }

            if (cumple) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    /**
     * Imprime la lista de transacciones en consola.
     * Mezcla lógica de presentación con lógica de negocio (otra violación).
     *
     * @param lista      lista de transacciones a imprimir
     * @param maxMostrar máximo de elementos a mostrar (-1 para todos)
     */
    public void imprimir(List<Transaction> lista, int maxMostrar) {
        int count = 0;
        for (Transaction t : lista) {
            if (maxMostrar > 0 && count >= maxMostrar) {
                System.out.println("... y " + (lista.size() - maxMostrar) + " más");
                break;
            }
            System.out.println(t);
            count++;
        }
    }

    /**
     * Cuenta transacciones por tipo.
     * Otro ejemplo de violación OCP: if-else para cada tipo.
     */
    public void contarPorTipo() {
        int depositos = 0, retiros = 0, transferencias = 0, pagos = 0;

        for (Transaction t : transacciones) {
            if ("DEPOSITO".equals(t.tipo)) {
                depositos++;
            } else if ("RETIRO".equals(t.tipo)) {
                retiros++;
            } else if ("TRANSFERENCIA".equals(t.tipo)) {
                transferencias++;
            } else if ("PAGO".equals(t.tipo)) {
                pagos++;
            }
        }

        System.out.println("=== Conteo por tipo ===");
        System.out.println("DEPOSITOS: " + depositos);
        System.out.println("RETIROS: " + retiros);
        System.out.println("TRANSFERENCIAS: " + transferencias);
        System.out.println("PAGOS: " + pagos);
    }

    /**
     * Calcula el balance total (suma de montos, DEPOSITO positivo, RETIRO/PAGO
     * negativo).
     *
     * @return balance total calculado
     */
    public double calcularBalance() {
        double total = 0;
        for (Transaction t : transacciones) {
            if ("DEPOSITO".equals(t.tipo) || "TRANSFERENCIA".equals(t.tipo)) {
                total += t.mnt;
            } else {
                total -= t.mnt;
            }
        }
        return total;
    }
}
