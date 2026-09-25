package actividades;

import excepciones.CupoExcedidoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta: define el comportamiento común a toda actividad de un
 * evento (Charla, Taller, Curso), pero no se puede instanciar directamente
 * (new Actividad(...) no compila). Cada subclase debe decidir CÓMO calcula
 * su costo de materiales y CUÁL es su tipo.
 */
public abstract class Actividad implements Serializable {

    // Constante de clase: cantidad mínima de inscriptos para que la
    // actividad se considere "viable". static porque es la misma para
    // todas las actividades; final porque no cambia una vez definida.
    public static final int CUPO_MINIMO = 3;

    private int id;
    private String titulo;
    private int cupoMaximo;
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Actividad(int id, String titulo, int cupoMaximo) {
        this.id = id;
        this.titulo = titulo;
        this.cupoMaximo = cupoMaximo;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public boolean alcanzoCupoMinimo() {
        return inscripciones.size() >= CUPO_MINIMO;
    }

    /**
     * Intenta inscribir a un estudiante. Si ya no hay lugar, en vez de
     * devolver null o un booleano (lo que obligaría a validar "a mano" en
     * cada lugar donde se llame), LANZA una excepción chequeada: quien
     * llame a este método está obligado por el compilador a manejarla.
     */
    public Inscripcion inscribir(Estudiante estudiante) throws CupoExcedidoException {
        if (inscripciones.size() >= cupoMaximo) {
            throw new CupoExcedidoException(
                    "No hay cupo en '" + titulo + "' (máximo " + cupoMaximo + " inscriptos).");
        }
        Inscripcion inscripcion = new Inscripcion(estudiante);
        inscripciones.add(inscripcion);
        return inscripcion;
    }

    public void mostrarInscripciones() {
        System.out.println("Inscriptos en '" + titulo + "':");
        if (inscripciones.isEmpty()) {
            System.out.println("  (sin inscriptos)");
        }
        for (Inscripcion i : inscripciones) {
            System.out.println("  - " + i);
        }
    }

    /**
     * "final": ninguna subclase puede sobreescribir este método. Tiene
     * sentido porque mostrar el tipo + id + título debe comportarse igual
     * para cualquier actividad, sin importar cuál sea su subtipo.
     */
    public final void mostrarIdentificacion() {
        System.out.println("[" + getTipo() + "] #" + id + " - " + titulo);
    }

    // Métodos abstractos: cada subclase (Charla/Taller/Curso) los implementa
    // a su manera. Es el corazón del polimorfismo: el mismo mensaje
    // "calcularCostoMateriales()" produce un resultado distinto según el
    // tipo real del objeto en tiempo de ejecución.
    public abstract double calcularCostoMateriales();

    public abstract String getTipo();
}
