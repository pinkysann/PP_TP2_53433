package actividades;

import certificacion.Certificable;

/**
 * Taller extiende de Actividad (una única clase base: herencia simple en
 * Java) e implementa Certificable. Esta combinación es la forma que tiene
 * Java de simular "herencia múltiple": una clase base + varias interfaces.
 */
public class Taller extends Actividad implements Certificable {

    private boolean requiereNotebook;

    public Taller(int id, String titulo, int cupoMaximo, boolean requiereNotebook) {
        super(id, titulo, cupoMaximo);
        this.requiereNotebook = requiereNotebook;
    }

    public boolean isRequiereNotebook() {
        return requiereNotebook;
    }

    @Override
    public double calcularCostoMateriales() {
        return requiereNotebook ? 5000.0 : 1500.0;
    }

    @Override
    public String getTipo() {
        return "Taller";
    }

    @Override
    public String generarCertificado(Estudiante estudiante) {
        return "Certificado emitido por " + ENTIDAD_EMISORA + " a " + estudiante.getNombre()
                + " por participar en el taller '" + getTitulo() + "'.";
    }
}
