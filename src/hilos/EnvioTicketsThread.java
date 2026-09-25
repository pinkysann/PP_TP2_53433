package hilos;

import actividades.Actividad;
import actividades.Inscripcion;
import modelo.EventoUniversitario;

/**
 * Extiende Thread (en vez de implementar Runnable) porque el enunciado pide
 * una clase pública e independiente, dedicada exclusivamente a esta tarea.
 * Al llamar a .start() (no a .run() directamente) la JVM crea un hilo de
 * ejecución nuevo, separado del hilo principal (el que corre App.main).
 */
public class EnvioTicketsThread extends Thread {

    private EventoUniversitario evento;

    public EnvioTicketsThread(EventoUniversitario evento) {
        this.evento = evento;
    }

    @Override
    public void run() {
        System.out.println("[Hilo envío] Empieza el envío de tickets de '" + evento.getTitulo()
                + "' (hilo: " + Thread.currentThread().getName() + ")");

        for (Actividad actividad : evento.getActividades()) {
            for (Inscripcion inscripcion : actividad.getInscripciones()) {
                if (inscripcion.estaConfirmada() && inscripcion.getTicket() != null) {
                    inscripcion.getTicket().enviarTicket();
                    try {
                        // Simula la demora real de un envío (red, servidor de mail, etc.)
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        System.out.println("[Hilo envío] Terminó de enviar todos los tickets.");
    }
}
