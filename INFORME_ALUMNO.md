# Trabajo Práctico 4 - Resumen de lo que hicimos

fuimos corrigiendo en el trabajo, así queda claro qué era lo que estaba mal al principio y cómo lo fuimos acomodando paso a paso.

## Lo que estaba re mal en el código original (TransactionProcessor)

1. **La clase hacía de todo:** El `TransactionProcessor` original era un desastre, básicamente manejaba todo el programa junto: guardaba la lista, filtraba, ordenaba, buscaba y hasta imprimía por consola. Esto es un dolor de cabeza si querés cambiar algo porque terminás rompiendo otra cosa sin querer.
2. **Los filtros eran puro if-else:** Para filtrar por tipo de transacción (depósito, retiro, etc) había un montón de `if` y `else if` encadenados. Si el día de mañana el banco agregaba un tipo de transacción nuevo, teníamos que entrar a modificar ese mismo código. Un asco.
3. **No había separación de cosas:** Estábamos mezclando los datos crudos, la lógica de cómo procesarlos y la parte de mostrarlos en pantalla. Todo atado con alambre usando `new` por todos lados, lo que lo hacía imposible de testear bien.

## Lo que fuimos completando y arreglando

1. **Implementamos los algoritmos a pata:**
   - Hicimos la **búsqueda normal (lineal)** recorriendo el array uno por uno.
   - Hicimos el **ordenamiento Burbuja (Bubble Sort)** comparando los valores de a dos e intercambiándolos, y también agregamos la forma rápida usando el sort que ya trae Java por defecto, para que en las pruebas se vea la diferencia de tiempo (el burbuja es re lento con muchos datos).

2. **Dividimos el código (la refactorización buena):**
   - Rompimos el mamotreto de `TransactionProcessor` y creamos servicios separados: uno para buscar (`SearchService`), otro para ordenar (`SortService`) y otro para filtrar (`FilterService`). Ahora cada uno hace lo suyo y no molesta al resto.
   - En vez de usar strings sueltos como `"DEPOSITO"`, metimos un `Enum` (`TransactionType`) para que quede todo estandarizado y el código no se rompa por escribir mal una letra.

3. **Usamos Spring Boot y arreglamos los if-else:**
   - Armamos los filtros para que se puedan combinar sin tener que hacer esos ifs asquerosos (usando predicados y la API de Streams de Java).
   - Inyectamos los servicios usando los constructores, así Spring se encarga de instanciarlos y nosotros nos olvidamos de andar poniendo `new` por todos lados.

Con todo esto, el código quedó bastante más limpio y se nota fuerte la diferencia cuando le tiramos 100.000 transacciones para ordenar.
