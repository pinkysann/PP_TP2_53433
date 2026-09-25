package certificacion;

import actividades.Estudiante;

/**
 * Interfaz que marca a las actividades que PUEDEN emitir un certificado de
 * participación (Taller y Curso la implementan; Charla NO).
 *
 * En una interfaz, todo atributo es implícitamente "public static final"
 * (una constante) y todo método es implícitamente "public abstract"
 * (no hace falta escribir esas palabras clave, Java las agrega solas).
 */
public interface Certificable {

    String ENTIDAD_EMISORA = "UTN - Facultad Regional Mendoza";

    String generarCertificado(Estudiante estudiante);
}
