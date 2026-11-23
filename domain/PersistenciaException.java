package domain;

/**
 * Error al guardar o abrir una partida (problema de lectura o escritura del archivo).
 * Se registra solo en el log al crearse.
 */
public class PersistenciaException extends DOPOSoccerException {

    /** Operación que falló: guardar o abrir. */
    private final String operacion;

    /**
     * Crea el error y lo registra en el log.
     * @param operacion operación que falló ("guardar" o "abrir")
     * @param mensaje mensaje para el usuario (constante de DOPOSoccerException)
     * @param causa error original de entrada/salida (puede ser null)
     */
    public PersistenciaException(String operacion, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.operacion = operacion;
        Log.record(this);
    }

    /**
     * @return operación que falló ("guardar" o "abrir")
     */
    public String getOperacion() { return operacion; }
}
