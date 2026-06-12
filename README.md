# TP4 — Guía de Laboratorio: Sistema de Análisis de Transacciones Bancarias

---

## 1. Objetivos

Al finalizar este trabajo práctico, el alumno será capaz de:

1. **Identificar violaciones a los principios SOLID** en código legacy, reconociendo patrones como clases "Dios" (God Object), alto acoplamiento y dependencias rígidas.
2. **Implementar algoritmos de búsqueda** — búsqueda lineal `O(n)` y búsqueda binaria `O(log n)` — midiendo empíricamente su rendimiento con `System.nanoTime()`.
3. **Comparar empíricamente algoritmos de ordenamiento** — Bubble Sort `O(n²)` vs `List.sort()` (TimSort `O(n log n)`) —analizando la diferencia entre complejidad cuadrática y logarítmica-lineal.
4. **Refactorizar aplicando SRP, OCP y DIP**, separando una clase monolítica en capas bien definidas: `model`, `service` y `config`.
5. **Aplicar Inyección de Dependencias por constructor** (sin `@Autowired` en campos), respetando el Principio de Inversión de Dependencias (DIP).
6. **Usar Records de Java 21** para DTOs inmutables, aprovechando las ventajas del lenguaje moderno.

---

## 2. Estructura del Proyecto

```
tp4-laboratorio/
├── pom.xml
├── README.md
└── src/main/java/com/bank/analysis/
    ├── inicial/
    │   ├── Transaction.java
    │   ├── TransactionProcessor.java
    │   └── Main.java
    └── solucion/
        ├── model/
        │   ├── Transaction.java
        │   ├── TransactionType.java
        │   └── SearchResult.java
        ├── service/
        │   ├── SearchService.java
        │   ├── SearchServiceImpl.java
        │   ├── SortService.java
        │   ├── SortServiceImpl.java
        │   ├── FilterService.java
        │   └── FilterServiceImpl.java
        ├── report/
        │   └── PerformanceReport.java
        ├── config/
        │   └── AppConfig.java
        └── MainRefactorizado.java
```

### Descripción de paquetes

| Paquete | Propósito |
|---------|-----------|
| `inicial/` | Código legacy con violaciones SOLID que el alumno debe analizar y refactorizar. |
| `solucion/model/` | Entidades refactorizadas: `Transaction` inmutable (campos `final`), `TransactionType` enum, `SearchResult` Record. |
| `solucion/service/` | Servicios separados por responsabilidad: búsqueda, ordenamiento y filtrado. Cada uno con interfaz e implementación (DIP). |
| `solucion/report/` | Servicio generador del reporte de performance comparativo. |
| `solucion/config/` | Configuración Spring Boot con `@Configuration` y `@Bean`. |
| `solucion/MainRefactorizado` | Punto de entrada de la aplicación Spring Boot (`CommandLineRunner`). |

---

## 3. Consignas

### Parte 1 — Análisis de Eficiencia (30%)

El alumno recibe la clase `TransactionProcessor.java` en el paquete `inicial/`. Esta clase contiene los siguientes métodos **vacíos** que debe implementar:

| Método | Firma | Descripción |
|--------|-------|-------------|
| `buscarPorId` | `Transaction buscarPorId(List<Transaction>, Long id)` | Busca una transacción por ID usando búsqueda **lineal** (`O(n)`). Trabaja sobre la lista desordenada. Retorna `null` si no existe. |
| `buscarPorIdBinario` | `Transaction buscarPorIdBinario(List<Transaction>, Long id)` | Busca por ID usando búsqueda **binaria** (`O(log n)`). La lista DEBE estar ordenada previamente por ID. Retorna `null` si no existe. |
| `buscarPorMonto` | `List<Transaction> buscarPorMonto(List<Transaction>, double montoMin, double montoMax)` | Filtra transacciones cuyo monto esté en el rango [min, max] usando búsqueda **lineal**. |
| `ordenarManual` | `void ordenarManual(List<Transaction>)` | Implementa **Bubble Sort**, ordenando por `monto` de menor a mayor. Debe contar comparaciones e intercambios y mostrarlos en consola. |
| `ordenarBuiltIn` | `void ordenarBuiltIn(List<Transaction>)` | Usa `List.sort(Comparator.comparingDouble(Transaction::getMonto))`. Mide el tiempo y lo reporta. |

#### 1.1 Reglas para las mediciones con `System.nanoTime()`

```java
// ¡ASÍ SE MIDE CORRECTAMENTE!
List<Transaction> datos = generarDatos(tamaño);

// Warm-up de la JVM (ejecutar una vez sin medir)
ordenarBuiltIn(new ArrayList<>(datos));

// Medición real
long inicio = System.nanoTime();
ordenarBuiltIn(new ArrayList<>(datos)); // copia fresca
long fin = System.nanoTime();
long duracionNs = fin - inicio;
```

**IMPORTANTE:**
- El warm-up es obligatorio para que el JIT compile y optimice el código.
- Medir solo el algoritmo, no la generación de datos ni la impresión en consola.
- Usar una copia fresca de los datos en cada iteración para evitar efectos de caché.
- Los tamaños de entrada a probar son: **100**, **1.000**, **10.000** y **100.000** transacciones.

#### 1.2 Reporte comparativo esperado

El alumno debe generar una tabla con el siguiente formato:

```
+------------------+---------------------+---------------------+------------------+------------------+
| Tamaño entrada   | Búsqueda Lineal (ns)| Búsqueda Binaria(ns)| Bubble Sort (ns) | Built-in Sort(ns)|
+------------------+---------------------+---------------------+------------------+------------------+
| 100              |              XX,XXX |              XX,XXX |         XX,XXX   |         XX,XXX   |
| 1.000            |             XXX,XXX |              XX,XXX |      X,XXX,XXX   |         XX,XXX   |
| 10.000           |           X,XXX,XXX |              XX,XXX |    XX,XXX,XXX    |        XXX,XXX   |
| 100.000          |          XX,XXX,XXX |              XX,XXX | X,XXX,XXX,XXX    |      X,XXX,XXX   |
+------------------+---------------------+---------------------+------------------+------------------+
```

Además debe incluir una **justificación teórica** de la complejidad temporal (notación Big O) para cada algoritmo, explicando por qué los tiempos medidos se corresponden (o no) con la teoría.

---

### Parte 2 — Refactorización SOLID (40%)

#### 2.1 Análisis del código inicial

El código en el paquete `inicial/` presenta las siguientes violaciones a los principios SOLID:

##### SRP — Principio de Responsabilidad Única (VIOLADO)

La clase `TransactionProcessor` hace **TODO**:
- Búsqueda de transacciones
- Ordenamiento
- Filtrado
- Generación de reportes
- Entrada/Salida por consola

Una clase debe tener **una sola razón para cambiar**. Si cambia la lógica de búsqueda, la forma de ordenar, o el formato del reporte, esta clase debe modificarse.

##### OCP — Principio Abierto/Cerrado (VIOLADO)

Los filtros están implementados con `if-else` encadenados:

```java
if (tipo.equals("DEPOSITO")) { ... }
else if (tipo.equals("RETIRO")) { ... }
else if (monto > 1000) { ... }
```

Agregar un nuevo criterio de filtro requiere **modificar el código existente**, cuando debería poder **extenderse** sin modificarlo.

##### DIP — Principio de Inversión de Dependencias (VIOLADO)

Se instancian dependencias con `new`:

```java
TransactionProcessor processor = new TransactionProcessor();
```

Esto genera **alto acoplamiento**. El código depende de implementaciones concretas, no de abstracciones. No se pueden intercambiar implementaciones fácilmente.

##### Otros problemas detectables

- Uso de `String` para el tipo de transacción (propenso a errores de tipeo, sin validación en compilación).
- Campos públicos sin encapsulamiento.
- Métodos con múltiples responsabilidades (mezclan lógica de negocio con I/O).
- Números mágicos sin constantes con nombre.

#### 2.2 Tareas de refactorización

El alumno debe refactorizar el código en el paquete `solucion/` aplicando las siguientes transformaciones:

1. **Separar `TransactionProcessor` en múltiples servicios especializados:**
   - `SearchService` / `SearchServiceImpl`: toda la lógica de búsqueda.
   - `SortService` / `SortServiceImpl`: toda la lógica de ordenamiento y medición.
   - `FilterService` / `FilterServiceImpl`: toda la lógica de filtrado, aplicando patrón Strategy para filtros combinables.

2. **Encapsular `Transaction`:**
   - Todos los campos deben ser `private final`.
   - Exponer solo getters (sin setters).
   - Implementar `equals()`, `hashCode()` y `toString()`.

3. **Reemplazar `String type` por el enum `TransactionType`:**
   ```java
   public enum TransactionType {
       DEPOSITO("Depósito"),
       RETIRO("Retiro"),
       TRANSFERENCIA("Transferencia"),
       PAGO("Pago");

       private final String descripcion;
       // constructor, getter
   }
   ```

4. **Aplicar patrón Strategy en `FilterService`:**
   - Crear una interfaz funcional `FiltroPredicate` con método `boolean cumple(Transaction t)`.
   - Implementar filtros concretos: `FiltroPorTipo`, `FiltroPorRangoMonto`, `FiltroPorFecha`.
   - `FilterService` recibe una lista de filtros y los aplica encadenados con `Stream.filter()`.

5. **Crear `SearchResult` como Record inmutable de Java 21:**
   ```java
   public record SearchResult(Transaction transaction,
                              long tiempoBusquedaNs,
                              int comparacionesRealizadas) {}
   ```

#### 2.3 Arquitectura refactorizada (diagrama ASCII)

```
┌──────────────────────────────────────────────────────────────────┐
│                    MainRefactorizado                              │
│              (CommandLineRunner - Spring Boot)                    │
└────────────┬──────────────┬──────────────┬───────────────────────┘
             │              │              │
             ▼              ▼              ▼
    ┌────────────┐  ┌────────────┐  ┌────────────┐
    │SearchService│  │SortService │  │FilterService│
    │ (interface) │  │ (interface)│  │ (interface) │
    └──────┬─────┘  └──────┬─────┘  └──────┬─────┘
           │               │               │
           ▼               ▼               ▼
    ┌────────────┐  ┌────────────┐  ┌────────────┐
    │SearchService│ │SortService │  │FilterService│
    │   Impl     │  │   Impl     │  │   Impl      │
    └────────────┘  └────────────┘  └────────────┘
           │               │               │
           └───────────────┼───────────────┘
                           │
                           ▼
                   ┌───────────────┐
                   │  Transaction  │
                   │  (model)      │
                   │  + SearchResult│
                   │  (record DTO) │
                   └───────────────┘
                           ▲
                           │
                   ┌───────────────┐
                   │  AppConfig    │
                   │ (@Config +    │
                   │  @Bean)       │
                   └───────────────┘
```

**Flujo de dependencias:**
- `MainRefactorizado` depende de interfaces (`SearchService`, `SortService`, `FilterService`), **nunca** de implementaciones concretas.
- `AppConfig` instancia las implementaciones y las expone como `@Bean`.
- Los servicios dependen de `Transaction` (modelo), no al revés.
- `PerformanceReport` recibe los servicios por inyección de dependencias.

---

### Parte 3 — Spring Boot & Inyección de Dependencias (30%)

#### 3.1 Transformación a aplicación Spring Boot

El proyecto refactorizado debe convertirse en una aplicación Spring Boot que se ejecute por consola (sin web):

1. **Clase principal `MainRefactorizado`:**
   - Anotada con `@SpringBootApplication`.
   - Implementa `CommandLineRunner`.
   - Obtiene los beans del `ApplicationContext` (o los recibe por constructor del runner).
   - Ejecuta las pruebas de performance y muestra el reporte.

2. **Servicios como beans de Spring:**
   - `@Service` en cada implementación (`SearchServiceImpl`, `SortServiceImpl`, `FilterServiceImpl`).
   - `PerformanceReport` también es `@Service`.

3. **Configuración con `AppConfig`:**
   - `@Configuration`
   - Puede declarar beans adicionales si es necesario (ej. generadores de datos de prueba).
   - Centraliza la creación de beans complejos.

4. **Inyección por constructor (OBLIGATORIO):**

   ```java
   // CORRECTO - Inyección por constructor
   @Service
   public class SearchServiceImpl implements SearchService {
       private final SortService sortService; // final = inmutable post-construcción

       public SearchServiceImpl(SortService sortService) { // Spring inyecta automáticamente
           this.sortService = sortService;
       }
   }
   ```

   ```java
   // PROHIBIDO - Inyección por campo
   @Service
   public class SearchServiceImpl implements SearchService {
       @Autowired  // ❌ NO USAR
       private SortService sortService;
   }
   ```

#### 3.2 `PerformanceReport` como servicio

```java
@Service
public class PerformanceReport {
    private final SearchService searchService;
    private final SortService sortService;
    private final FilterService filterService;

    // Constructor injection
    public PerformanceReport(SearchService searchService,
                             SortService sortService,
                             FilterService filterService) {
        this.searchService = searchService;
        this.sortService = sortService;
        this.filterService = filterService;
    }

    public void generarReporteCompleto(int[] tamanios) {
        for (int n : tamanios) {
            List<Transaction> datos = generarDatos(n);
            // Warm-up + medición...
            // Imprimir tabla formateada
        }
    }
}
```

---

## 4. Criterios de Evaluación

| Criterio | Peso | Descripción |
|----------|------|-------------|
| **Corrección de algoritmos** | 20% | La búsqueda lineal, binaria y los ordenamientos producen resultados correctos. Bubble Sort ordena efectivamente; la búsqueda binaria encuentra (o no) el elemento correcto. |
| **Mediciones con `nanoTime()`** | 15% | Uso correcto de `System.nanoTime()`: warm-up de la JVM, medición exclusiva del algoritmo, cálculos de diferencia correctos. |
| **Análisis Big O** | 15% | Justificación teórica de la complejidad temporal para cada método implementado. Explicación de por qué la búsqueda binaria requiere lista ordenada. Comparación `O(n²)` vs `O(n log n)`. |
| **Refactorización SOLID** | 20% | Separación correcta de responsabilidades: cada servicio tiene una única razón de cambio. `FilterService` usa patrón Strategy (OCP). Las dependencias son interfaces, no implementaciones concretas (DIP). |
| **Inyección de Dependencias** | 10% | Constructor injection en todos los servicios. Cero usos de `@Autowired` en campos. Los beans se crean y cablean correctamente. |
| **JavaDoc** | 10% | Todas las clases públicas, interfaces y métodos públicos están documentados con JavaDoc estándar (`@param`, `@return`, `@throws`, `@since`). |
| **Código limpio** | 10% | Nombres significativos en español o inglés consistente. Sin números mágicos (usar constantes). Formato consistente. Sin código comentado ni métodos vacíos. |

---

## 5. Formato de Entrega

1. **Proyecto Maven compilable:** debe ejecutarse `mvn clean package` sin errores.
2. **Rama `main`:** debe contener el código inicial en el paquete `inicial/` compilando correctamente.
3. **Solución:** el paquete `solucion/` debe compilar y ejecutar correctamente con `mvn spring-boot:run`, mostrando en consola el reporte de performance completo.
4. **Código fuente documentado:** todas las clases deben tener JavaDoc.
5. **Entrega:** repositorio Git (URL del repositorio) o archivo `.zip` con el proyecto completo.

---

## 6. Recursos y Referencias

- **Principios SOLID** — Robert C. Martin. *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall, 2017.
- **Java Stream API** — [java.util.stream.Stream](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)
- **Comparator** — [java.util.Comparator](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html)
- **System.nanoTime()** — [JavaDoc oficial](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html#nanoTime())
- **Spring Boot Reference Documentation** — [Core Features: Dependency Injection](https://docs.spring.io/spring-boot/reference/features/spring-application.html)
- **Big O Cheat Sheet** — [bigocheatsheet.com](https://www.bigocheatsheet.com/)
- **Java Records (JEP 395)** — [OpenJDK](https://openjdk.org/jeps/395)

---

> **Nota para el alumno:** Este trabajo práctico está diseñado para resolverse en **2 semanas**. Se recomienda comenzar por la Parte 1 (algoritmos) para entender el dominio, luego refactorizar (Parte 2), y finalmente integrar Spring Boot (Parte 3). La Parte 2 es la de mayor peso en la evaluación y requiere una comprensión sólida de los principios SOLID.
