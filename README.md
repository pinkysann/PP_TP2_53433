# PP_TP2_53433 — Paradigmas de Programación (UTN FRM)

Trabajo Práctico 2 — Unidad 2: Organización, reutilización y recursos avanzados en POO.
Implementación en Java del modelo `EventoUniversitario`, escalado a través de los 4 ejercicios del TP.

## Estructura del proyecto

```
src/
 ├── App.java                       (clase principal, paquete por defecto)
 ├── modelo/
 │    ├── EventoUniversitario.java  (persistencia + genéricos)
 │    └── Sala.java
 ├── actividades/
 │    ├── Actividad.java            (clase abstracta)
 │    ├── Charla.java
 │    ├── Taller.java
 │    ├── Curso.java
 │    ├── Estudiante.java
 │    └── Inscripcion.java          (contiene la clase anidada TicketDeAcceso)
 ├── excepciones/
 │    └── CupoExcedidoException.java
 ├── certificacion/
 │    └── Certificable.java         (interfaz)
 └── hilos/
      └── EnvioTicketsThread.java
```

## Qué resuelve cada ejercicio (y dónde está el código)

**Ejercicio 1 — Excepciones + persistencia**
- `Actividad.inscribir()` lanza `CupoExcedidoException` (excepción chequeada, hereda de `Exception`) cuando se supera el cupo máximo.
- `EventoUniversitario.persistirEvento()` / `recuperarEvento()` usan `ObjectOutputStream` / `ObjectInputStream` para serializar el evento completo a un archivo `.dat` (carpeta `eventos/`).
- En `App.java`, un bloque `try-catch-finally` inscribe estudiantes (con un caso exitoso y un caso fallido a propósito) y otro bloque hace lo mismo con la persistencia, capturando cada excepción por separado (`FileNotFoundException`, `NotSerializableException`, `InvalidClassException`, `ClassNotFoundException`, `IOException`).

**Ejercicio 2 — Certificados (interfaces)**
- `Certificable` es una interfaz con el método `generarCertificado(Estudiante)`.
- La implementan `Taller` y `Curso` (nuevo tipo de actividad agregado en este ejercicio). `Charla` **no** la implementa, por eso nunca emite certificado.
- En `App.java`, el método `emitirCertificadosSiCorresponde()` usa `instanceof Certificable` para decidir, en tiempo de ejecución, si una actividad puede certificar o no — sin necesidad de un `if` por cada subtipo.

**Ejercicio 3 — Genéricos acotados y wildcards**
- `<T extends Actividad> List<T> filtrarActividadesPorTipo(Class<T> tipo)`: devuelve una lista ya tipada (`List<Charla>`, `List<Taller>`, `List<Curso>`) filtrando por el tipo concreto pedido.
- `double calcularCostoMateriales(List<? extends Actividad> actividades)`: acepta tanto `List<Actividad>` como cualquier `List<Charla>`, `List<Taller>`, etc. (algo que `List<Actividad>` sola no permitiría, porque los genéricos en Java son invariantes).

**Ejercicio 4 — Clases anidadas e hilos**
- `TicketDeAcceso` es una clase anidada **miembro no estática** dentro de `Inscripcion` (un ticket no existe sin una inscripción concreta). Solo se puede generar si la inscripción está confirmada.
- `EnvioTicketsThread` (paquete `hilos`) extiende `Thread` y envía todos los tickets de las inscripciones confirmadas de un evento en un hilo separado.
- En `App.java` se ve cómo, mientras `hiloEnvio.start()` corre en paralelo, el hilo `main` sigue imprimiendo información — evidenciando los dos flujos de ejecución — y al final se usa `hiloEnvio.join()` para esperar a que termine antes de cerrar el programa.

## Cómo abrirlo en IntelliJ IDEA

1. `File` → `New` → `Project from Existing Sources...` y elegí la carpeta `PP_TP2_53433`.
2. Cuando pregunte, marcá la carpeta `src` como **Sources Root** (clic derecho sobre `src` → `Mark Directory as` → `Sources Root`, si no quedó marcada sola).
3. SDK: Java 17 o superior (se probó con Java 21). El código usa *pattern matching* de `instanceof` (Java 16+) y `switch` sobre `String`.
4. Ejecutar la clase `App` (botón ▶️ al lado de `public class App`).

## Salida por consola

Ya se compiló y ejecutó para verificar que no tenga errores — el archivo `salida_consola.txt` incluido tiene una corrida completa de ejemplo. Para la entrega necesitás una **captura de pantalla** (imagen) de una ejecución tuya desde IntelliJ, no el `.txt`.


