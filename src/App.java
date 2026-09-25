import actividades.Actividad;
import actividades.Charla;
import actividades.Curso;
import actividades.Estudiante;
import actividades.Inscripcion;
import actividades.Taller;
import certificacion.Certificable;
import excepciones.CupoExcedidoException;
import hilos.EnvioTicketsThread;
import modelo.EventoUniversitario;
import modelo.Sala;

import java.util.List;

/**
 * Clase principal. No está en ningún package (queda en el "paquete por
 * defecto") a propósito, para poder ejecutarla directamente y para poder
 * ver acá, en un solo lugar, cómo se conectan entre sí modelo, actividades,
 * excepciones, certificacion e hilos.
 */
public class App {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  TP2 - Paradigmas de Programacion - UTN FRM");
        System.out.println("==============================================\n");

        // =====================================================================
        // Estudiantes
        // =====================================================================
        Estudiante ana = new Estudiante("50111", "Ana Gomez");
        Estudiante bruno = new Estudiante("50222", "Bruno Diaz");
        Estudiante carla = new Estudiante("50333", "Carla Perez");
        Estudiante diego = new Estudiante("50444", "Diego Suarez");

        // =====================================================================
        // Evento, sala y actividades (incluye el nuevo tipo "Curso" del Ej. 2)
        // =====================================================================
        EventoUniversitario evento = new EventoUniversitario("EVT-1", "Semana de la Tecnologia", 1000.0, false);
        evento.asignarSala(new Sala(1, "Auditorio Principal"));

        evento.crearActividad(1, "Introduccion a Java", "Charla", 10);
        // El taller se crea con cupo = 2 A PROPOSITO, para poder mostrar
        // abajo el caso fallido controlado por CupoExcedidoException.
        evento.crearActividad(2, "Taller de Testing", "Taller", 2);
        evento.crearActividad(3, "Curso de Bases de Datos", "Curso", 10);

        // Ejercicio 3: filtrado tipado usando generics acotados
        List<Charla> charlas = evento.filtrarActividadesPorTipo(Charla.class);
        List<Taller> talleres = evento.filtrarActividadesPorTipo(Taller.class);
        List<Curso> cursos = evento.filtrarActividadesPorTipo(Curso.class);

        Charla charlaJava = charlas.get(0);
        Taller tallerTesting = talleres.get(0);
        Curso cursoBD = cursos.get(0);

        // =====================================================================
        // EJERCICIO 1: inscripciones + try-catch-finally + CupoExcedidoException
        // =====================================================================
        System.out.println("----- EJERCICIO 1: inscripciones y excepciones -----");
        try {
            charlaJava.inscribir(ana);
            System.out.println("Caso exitoso: " + ana.getNombre() + " se inscribio en '" + charlaJava.getTitulo() + "'.");

            tallerTesting.inscribir(bruno);
            tallerTesting.inscribir(carla);
            System.out.println("Caso exitoso: cupo del taller (2) completado con Bruno y Carla.");

            // Esta inscripcion SI dispara CupoExcedidoException: es el "caso
            // fallido controlado" que pide el enunciado.
            tallerTesting.inscribir(diego);

        } catch (CupoExcedidoException e) {
            System.out.println("Caso fallido controlado -> CupoExcedidoException: " + e.getMessage());
        } finally {
            System.out.println("(finally) fin del intento de inscripcion en el taller.\n");
        }

        // Diego y Carla se anotan en el curso, que si tiene lugar
        try {
            cursoBD.inscribir(diego);
            cursoBD.inscribir(carla);
        } catch (CupoExcedidoException e) {
            System.out.println("No se pudo inscribir en el curso: " + e.getMessage());
        }

        evento.mostrarDatos();
        System.out.println();

        // ---- Persistencia (serializacion / deserializacion) ----
        System.out.println("----- Persistencia del evento -----");
        try {
            boolean guardado = evento.persistirEvento();
            System.out.println(guardado
                    ? "Evento persistido correctamente en disco (carpeta 'eventos/')."
                    : "No se pudo persistir el evento (ver mensaje anterior).");

            EventoUniversitario recuperado = EventoUniversitario.recuperarEvento(evento.getId());
            if (recuperado != null) {
                System.out.println("Evento recuperado desde archivo:");
                recuperado.mostrarDatos();
            }

            // Caso fallido controlado de persistencia: pedimos un id que no existe
            EventoUniversitario.recuperarEvento("ID-QUE-NO-EXISTE");

        } finally {
            System.out.println("(finally) fin del flujo de persistencia.\n");
        }

        // =====================================================================
        // EJERCICIO 2: certificados (interfaz Certificable)
        // =====================================================================
        System.out.println("----- EJERCICIO 2: certificados -----");
        charlaJava.getInscripciones().get(0).confirmar();   // Charla: no genera certificado
        tallerTesting.getInscripciones().get(0).confirmar(); // Bruno, en el taller
        cursoBD.getInscripciones().get(0).confirmar();       // Diego, en el curso

        emitirCertificadosSiCorresponde(tallerTesting);
        emitirCertificadosSiCorresponde(cursoBD);
        emitirCertificadosSiCorresponde(charlaJava);
        System.out.println();

        // =====================================================================
        // EJERCICIO 3: generics acotados + wildcards
        // =====================================================================
        System.out.println("----- EJERCICIO 3: genericos y wildcards -----");
        System.out.println("Cantidad de charlas: " + charlas.size() + " (List<Charla>)");
        System.out.println("Cantidad de talleres: " + talleres.size() + " (List<Taller>)");
        System.out.println("Cantidad de cursos: " + cursos.size() + " (List<Curso>)");

        System.out.println("Costo materiales charlas: $" + evento.calcularCostoMateriales(charlas));
        System.out.println("Costo materiales talleres: $" + evento.calcularCostoMateriales(talleres));
        System.out.println("Costo materiales cursos: $" + evento.calcularCostoMateriales(cursos));
        System.out.println();

        // =====================================================================
        // EJERCICIO 4: tickets de acceso (clase anidada) + hilo de envio
        // =====================================================================
        System.out.println("----- EJERCICIO 4: tickets e hilos -----");

        // Un ticket por cada inscripcion ya confirmada
        for (Actividad a : evento.getActividades()) {
            for (Inscripcion i : a.getInscripciones()) {
                if (i.estaConfirmada()) {
                    i.generarTicket();
                }
            }
        }

        EnvioTicketsThread hiloEnvio = new EnvioTicketsThread(evento);
        hiloEnvio.start(); // arranca un hilo de ejecucion nuevo, en paralelo

        // Mientras el hilo secundario envia los tickets, el hilo principal
        // sigue trabajando: esto demuestra que hay DOS flujos de ejecucion.
        for (int vuelta = 1; vuelta <= 3; vuelta++) {
            System.out.println("[Hilo principal] (" + Thread.currentThread().getName()
                    + ") vuelta " + vuelta + " - evento '" + evento.getTitulo() + "', "
                    + evento.getActividades().size() + " actividades.");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            // join(): el hilo principal espera aca a que termine el hilo de
            // envio antes de imprimir el cierre del programa.
            hiloEnvio.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nCantidad total de eventos creados en el sistema: " + EventoUniversitario.getCantidadEventos());
        System.out.println("Programa finalizado.");
    }

    /**
     * Usa "instanceof" con pattern matching para saber, en tiempo de
     * ejecucion, si la actividad recibida implementa Certificable, sin
     * importar si es un Taller, un Curso, o un futuro nuevo tipo.
     */
    private static void emitirCertificadosSiCorresponde(Actividad actividad) {
        if (actividad instanceof Certificable certificable) {
            for (Inscripcion i : actividad.getInscripciones()) {
                if (i.estaConfirmada()) {
                    System.out.println(certificable.generarCertificado(i.getEstudiante()));
                }
            }
        } else {
            System.out.println("'" + actividad.getTitulo() + "' (" + actividad.getTipo() + ") no es certificable.");
        }
    }
}
