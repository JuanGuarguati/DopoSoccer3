package domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Registro de errores para los programadores (requisito de diseño "log de errores").
 * Los errores se guardan en el archivo DOPOSoccer.log en la carpeta del proyecto.
 * El archivo se abre una sola vez (la primera vez que se registra un error) y se reutiliza.
 */
public final class Log {

    /** Nombre del archivo de log (sin extensión). */
    public static final String NOMBRE = "DOPOSoccer";

    /** Registrador de Java que escribe en el archivo. */
    private static final Logger LOGGER = Logger.getLogger(NOMBRE);
    /** true cuando ya se intentó abrir el archivo. */
    private static boolean preparado;

    /**
     * No se crean objetos de esta clase.
     */
    private Log() { }

    /**
     * Registra una excepción en el archivo de log.
     * Si no se puede escribir el archivo, el error se muestra en la consola.
     * @param e excepción que se quiere registrar
     */
    public static synchronized void record(Exception e) {
        preparar();
        LOGGER.log(Level.SEVERE, e.toString(), e);
    }

    /**
     * Abre el archivo de log una sola vez. Si no se puede, los errores salen por la consola.
     */
    private static void preparar() {
        if (preparado) return;
        preparado = true;
        try {
            FileHandler archivo = new FileHandler(NOMBRE + ".log", true);
            archivo.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(archivo);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
            System.err.println("No se pudo abrir el log: " + ex.getMessage());
        }
    }
}
