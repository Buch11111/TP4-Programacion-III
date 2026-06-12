# Reporte Final Detallado: Sistema de Análisis de Transacciones Bancarias

Este documento detalla exhaustivamente la resolución de todas las consignas del Trabajo Práctico 4, abordando tanto la algoritmia de bajo nivel como la reestructuración arquitectónica hacia los estándares modernos de ingeniería de software (SOLID y Spring Boot).

---

## Parte 1: Análisis de Eficiencia Algorítmica

El objetivo de esta sección fue implementar algoritmos clásicos de búsqueda y ordenamiento desde cero, y comparar su rendimiento empírico frente a soluciones nativas de Java, comprendiendo el impacto de la complejidad teórica (Notación Big O).

### 1. Algoritmos de Búsqueda Implementados
- **Búsqueda Lineal ($\mathcal{O}(n)$):** Se implementó un algoritmo secuencial iterativo. Este enfoque recorre elemento por elemento comparando el ID buscado. Se comprobó que el tiempo de ejecución crece de forma estrictamente proporcional a la cantidad de elementos. En el peor escenario (el elemento está al final o no existe), realiza $n$ comparaciones.
- **Búsqueda Binaria ($\mathcal{O}(\log n)$):** Se implementó un algoritmo de divide y vencerás. Requiriendo que la lista de entrada esté previamente ordenada, el algoritmo verifica el punto medio y descarta sistemáticamente el 50% de las posibilidades restantes. Esta aproximación redujo drásticamente el costo de búsqueda en listados masivos (ej. 100,000 elementos).

### 2. Algoritmos de Ordenamiento Implementados
- **Bubble Sort ($\mathcal{O}(n^2)$):** Se implementó el ordenamiento por burbuja interactuando con dos bucles anidados. Se incluyó una optimización de "parada temprana" (si un recorrido finaliza sin intercambios, el proceso se aborta). A pesar de esta optimización, se demostró que el rendimiento es insostenible para volúmenes mayores a 10,000 registros, superando fácilmente el límite de procesamiento razonable (causando degradación extrema del rendimiento).
- **Built-in Sort / TimSort ($\mathcal{O}(n \log n)$):** Se implementó un enlazador al `List.sort()` nativo de Java. Éste utiliza TimSort por debajo (una combinación híbrida de Merge Sort y Insertion Sort), garantizando tiempos por debajo de un segundo incluso ordenando cientos de miles de registros.

### 3. Técnicas de Medición de Rendimiento
Para las métricas de tiempo se empleó estrictamente `System.nanoTime()` (alta precisión). Para evitar resultados viciados, la medición se estructuró siguiendo dos reglas clave:
1. **JVM Warm-Up:** Se incluyó un bloque previo de ejecuciones iterativas ficticias (miles de vueltas sin registrar métricas) para obligar a la máquina virtual de Java a ejecutar el compilador "Just-In-Time" (JIT) y estabilizar la caché.
2. **Aislamiento de Entorno:** Se midió rigurosamente sólo el segmento algorítmico (descartando instanciación de clases extrañas, I/O o recolección de basura predecible).

---

## Parte 2: Refactorización Arquitectónica y Principios SOLID

El código origen (paquete `inicial`) presentaba el antipatrón *God Class* (Clase Dios) en el componente `TransactionProcessor`. Procedimos a demoler esta estructura y redistribuirla para cumplir con los lineamientos de Clean Architecture.

### Cumplimiento del SRP (Principio de Responsabilidad Única)
Se fragmentó el `TransactionProcessor` creando servicios atómicos y dedicados. Cada uno tiene exactamente una razón para cambiar:
- `SearchService`: Encapsula los algoritmos de búsqueda.
- `SortService`: Encapsula las rutinas de ordenamiento matemático.
- `FilterService`: Centraliza la lógica para tamizar y segmentar las transacciones.
- `PerformanceReport`: Se responsabiliza exclusivamente de formatear métricas y generar salidas I/O tabulares.

### Cumplimiento del OCP (Principio Abierto/Cerrado) y Patrón Strategy
Los filtros originales se encontraban acoplados dentro de un método altamente viciado por sentencias `if-else` encadenadas. Esto se rediseñó utilizando **composición de predicados** (Streams de Java 8+) y el **Patrón Strategy**.
A través de la interfaz funcional `FiltroPredicate` y el método `.filter()` de la Stream API, la clase está completamente *cerrada a modificación pero abierta a extensión*: si el dominio bancario requiere filtrar por nuevos criterios mañana, basta con inyectar un nuevo `Predicate` sin alterar la clase base `FilterService`.

### Encapsulamiento Fuerte e Inmutabilidad
- Las entidades `Transaction` mutaron de ser simples contenedores con campos públicos a clases rigurosamente inmutables (todos sus campos son `private final`). 
- Se deprecó el uso indiscriminado de `String` (propenso a errores tipográficos) para clasificar tipos de operación y se introdujo el Enum `TransactionType`.
- Se introdujo el uso formal de los nuevos **Records de Java 21**. Creamos `public record SearchResult(...)` para movilizar las respuestas desde los servicios hasta la capa de impresión. Este DTO (Data Transfer Object) inmutable transporta el hallazgo, los nanosegundos invertidos y las iteraciones consumidas de forma compacta.

---

## Parte 3: Modernización con Spring Boot e Inyección de Dependencias

Finalmente, el flujo base de Java SE migró hacia un contenedor web embebido administrado mediante **Spring Boot**.

### Orquestación de Beans
El `Main.java` monolítico desapareció para dar espacio a `MainRefactorizado.java`, anotado con `@SpringBootApplication`. Implementamos `CommandLineRunner` para asegurar que todo el set de demostraciones algorítmicas se lance apenas finaliza la construcción del Application Context.

### Cumplimiento del DIP (Principio de Inversión de Dependencias)
Ningún servicio instancia a otros servicios haciendo un llamado rústico con la palabra reservada `new`. Las responsabilidades se han abstraído; los controladores y reportes hablan mediante contratos (`SearchService` interface) y no mediante concreciones (`SearchServiceImpl`).

### Inyección de Dependencias Exclusiva por Constructor
Por normativa corporativa y buenas prácticas de seguridad, el uso de inyección mediante reflexividad en los atributos (`@Autowired` sobre campos privados) ha sido vetado de la aplicación.
En su lugar, todas las dependencias cruzadas (por ejemplo, `PerformanceReport` solicitando los 3 servicios de base) son inyectadas en el constructor e instanciadas a campos `final`. Esto previene referencias circulares en el proceso de Boot y garantiza testabilidad 100% independiente del ecosistema de Spring para pruebas unitarias limpias.
