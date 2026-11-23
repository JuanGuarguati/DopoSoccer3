package domain;

/**
 * Error cuando el archivo de una partida está mal escrito: no tiene el encabezado,
 * le falta una clave o un valor no es válido. Se registra solo en el log al crearse.
 */
public class FormatoArchivoException extends DOPOSoccerException {

    /** Nombre del archivo con el error. */
    private final String archivo;
    /** Explicación del error (por ejemplo, la clave que falta). */
    private final String razon;

    /**
     * Crea el error y lo registra en el log.
     * @param archivo nombre del archivo
     * @param mensaje mensaje para el usuario (constante de DOPOSoccerException)
     * @param razon explicación del error para el programador
     */
    public FormatoArchivoException(String archivo, String mensaje, String razon) {
        super(mensaje);
        this.archivo = archivo;
        this.razon = razon;
        Log.record(this);
    }

    /**
     * @return nombre del archivo con el error
     */
    public String getArchivo() { return archivo; }

    /**
     * @return explicación del error
     */
    public String getRazon() { return razon; }

    /**
     * @return mensaje para el usuario seguido de la razón, para el log
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getMessage() + " [" + archivo + ": " + razon + "]";
    }
}
