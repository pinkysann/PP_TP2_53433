package modelo;

import actividades.Actividad;
import actividades.Charla;
import actividades.Curso;
import actividades.Taller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventoUniversitario implements Serializable {

    // Atributo estático: es UNO SOLO, compartido por todas las instancias,
    // y lleva la cuenta de cuántos eventos se crearon en total.
    private static int contador = 0;

    // Carpeta donde se van a guardar los .dat de cada evento persistido.
    private static final String CARPETA_PERSISTENCIA = "eventos";

    private final String id; // final: se asigna una sola vez, en el constructor
    private String titulo;
    private double costoBase;
    private boolean gratuito;

    private Sala sala;                     // relación "agrega" (1)
    private List<Actividad> actividades;   // relación "compone" (1..*)

    public EventoUniversitario(String id, String titulo, double costoBase, boolean gratuito) {
        this.id = id;
        this.titulo = titulo;
        this.costoBase = costoBase;
        this.gratuito = gratuito;
        this.actividades = new ArrayList<>();
        contador++;
    }

    /** Constructor de copia: crea un evento nuevo a partir de otro existente. */
    public EventoUniversitario(EventoUniversitario otro) {
        this.id = otro.id;
        this.titulo = otro.titulo;
        this.costoBase = otro.costoBase;
        this.gratuito = otro.gratuito;
        this.sala = otro.sala;
        this.actividades = new ArrayList<>(otro.actividades);
        contador++;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Sala getSala() {
        return sala;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public static int getCantidadEventos() {
        return contador;
    }

    public double calcularCostoEstimado() {
        if (gratuito) {
            return 0.0;
        }
        double totalMateriales = 0.0;
        for (Actividad a : actividades) {
            totalMateriales += a.calcularCostoMateriales();
        }
        return costoBase + totalMateriales;
    }

    public void asignarSala(Sala sala) {
        this.sala = sala;
    }

    /** Sobrecarga con cupo por defecto (20). */
    public void crearActividad(int id, String titulo, String tipo) {
        crearActividad(id, titulo, tipo, 20);
    }

    public void crearActividad(int id, String titulo, String tipo, int cupoMaximo) {
        Actividad nueva;
        switch (tipo) {
            case "Charla":
                nueva = new Charla(id, titulo, cupoMaximo, "A confirmar");
                break;
            case "Taller":
                nueva = new Taller(id, titulo, cupoMaximo, false);
                break;
            case "Curso":
                nueva = new Curso(id, titulo, cupoMaximo, 1);
                break;
            default:
                throw new IllegalArgumentException("Tipo de actividad desconocido: " + tipo);
        }
        actividades.add(nueva);
    }

    public void mostrarDatos() {
        System.out.println("=== Evento: " + titulo + " (id=" + id + ") ===");
        System.out.println("Costo base: $" + costoBase + " | Gratuito: " + gratuito);
        System.out.println("Costo estimado total: $" + calcularCostoEstimado());
        System.out.println("Sala: " + (sala != null ? sala.getNombre() : "sin asignar"));
        System.out.println("Actividades (" + actividades.size() + "):");
        for (Actividad a : actividades) {
            a.mostrarIdentificacion();
        }
    }

    // =====================================================================
    // PERSISTENCIA (Ejercicio 1): serialización / deserialización de objetos
    // =====================================================================

    /**
     * Guarda el evento completo (con su Sala y todas sus Actividades, que
     * a su vez tienen sus Inscripciones) en un archivo binario .dat.
     * Devuelve true si pudo persistir, false si algo falló (y ya imprimió
     * por consola cuál fue el problema concreto).
     */
    public boolean persistirEvento() {
        File carpeta = new File(CARPETA_PERSISTENCIA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        File archivo = new File(carpeta, id + ".dat");

        // try-with-resources: cierra automáticamente los streams al salir
        // del bloque, incluso si se produce una excepción.
        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(this);
            return true;

        } catch (FileNotFoundException e) {
            // No se pudo crear/abrir el archivo (permisos, ruta inválida, etc.)
            System.out.println("No se pudo crear el archivo de persistencia: " + e.getMessage());
        } catch (NotSerializableException e) {
            // Algún objeto referenciado desde el evento NO implementa Serializable
            System.out.println("Hay un objeto dentro del evento que no es serializable: " + e.getMessage());
        } catch (IOException e) {
            // Cualquier otro problema de entrada/salida durante la escritura
            System.out.println("Error de E/S al persistir el evento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Recupera un evento previamente persistido a partir de su id.
     * Devuelve null si no existe o si ocurrió algún error (y lo informa
     * por consola de forma específica, catch por catch).
     */
    public static EventoUniversitario recuperarEvento(String id) {
        File archivo = new File(CARPETA_PERSISTENCIA, id + ".dat");

        try (FileInputStream fis = new FileInputStream(archivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (EventoUniversitario) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No existe un evento persistido con id '" + id + "'.");
        } catch (InvalidClassException e) {
            // La versión de la clase guardada no coincide con la actual
            System.out.println("La clase serializada no coincide con la versión actual: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró la clase del objeto guardado: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de E/S al recuperar el evento: " + e.getMessage());
        }
        return null;
    }

    // =====================================================================
    // GENÉRICOS (Ejercicio 3): método parametrizado acotado + wildcard
    // =====================================================================

    /**
     * <T extends Actividad>: T puede ser CUALQUIER subtipo de Actividad
     * (Charla, Taller, Curso...), pero no cualquier clase. tipo.isInstance(a)
     * filtra en tiempo de ejecución; el "cast" a T es seguro porque ya
     * verificamos el tipo real del objeto antes de hacerlo.
     */
    @SuppressWarnings("unchecked")
    public <T extends Actividad> List<T> filtrarActividadesPorTipo(Class<T> tipo) {
        List<T> resultado = new ArrayList<>();
        for (Actividad a : actividades) {
            if (tipo.isInstance(a)) {
                resultado.add((T) a);
            }
        }
        return resultado;
    }

    /**
     * List<? extends Actividad>: acepta una lista de Actividad, o una lista
     * de CUALQUIER subtipo suyo (List<Charla>, List<Taller>, List<Curso>...),
     * algo que List<Actividad> por sí sola NO aceptaría (los genéricos son
     * invariantes). Como solo LEEMOS de la lista (no insertamos), el
     * comodín "? extends" es la elección correcta.
     */
    public double calcularCostoMateriales(List<? extends Actividad> actividadesAEvaluar) {
        double total = 0.0;
        for (Actividad a : actividadesAEvaluar) {
            total += a.calcularCostoMateriales();
        }
        return total;
    }
}
