package excepciones;

/**
 * Excepción CHEQUEADA (checked): hereda directamente de Exception, no de
 * RuntimeException. Esto obliga a quien llame a Actividad.inscribir(...) a
 * atraparla con try-catch o a propagarla con throws; el compilador lo exige.
 *
 * Se lanza cuando se intenta inscribir a un estudiante en una actividad que
 * ya alcanzó su cupoMaximo.
 */
public class CupoExcedidoException extends Exception {

    public CupoExcedidoException(String mensaje) {
        // super(mensaje) guarda el mensaje en el objeto Throwable para que
        // luego podamos recuperarlo con e.getMessage() en el catch.
        super(mensaje);
    }
}
