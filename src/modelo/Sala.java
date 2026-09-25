package modelo;

import java.io.Serializable;

/**
 * Representa la sala física donde se dicta un EventoUniversitario.
 * Implementa Serializable porque EventoUniversitario la compone (relación "agrega")
 * y, para poder persistir un evento completo, TODOS los objetos que cuelgan de él
 * (composición) también deben ser serializables. Si Sala no implementara esta
 * interfaz, al intentar guardar el evento Java lanzaría NotSerializableException.
 */
public class Sala implements Serializable {

    private int id;
    private String nombre;

    public Sala(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "Sala{id=" + id + ", nombre='" + nombre + "'}";
    }
}
