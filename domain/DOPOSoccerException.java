package domain;

/**
 * Excepción propia del juego DOPOSoccer (base de todas las excepciones del dominio).
 * Los mensajes para el usuario están encapsulados como constantes de la clase.
 * Subclases: PersistenciaException (error de lectura o escritura) y
 * FormatoArchivoException (archivo mal escrito); las dos se registran solas en el log.
 */
public class DOPOSoccerException extends Exception {

    /** Mensaje cuando el número de jugador no es 1 ni 2. */
    public static final String JUGADOR_INVALIDO = "El número de jugador debe ser 1 o 2.";
    /** Mensaje cuando no hay un partido en curso. */
    public static final String PARTIDO_TERMINADO = "No hay un partido en curso.";
    /** Mensaje cuando el país no existe. */
    public static final String PAIS_INVALIDO = "El país seleccionado no existe. Opciones: Corea del Sur, Camerún, Nigeria, Estados Unidos.";
    /** Mensaje cuando el perfil de la máquina no existe. */
    public static final String PERFIL_INVALIDO = "El perfil de la máquina no existe.";
    /** Mensaje cuando se pide algo de la máquina y el partido no es contra la máquina. */
    public static final String NO_HAY_MAQUINA = "Este partido no es contra la máquina.";
    /** Mensaje cuando el tipo o el valor del partido no son válidos. */
    public static final String VALOR_INVALIDO = "Debe elegir un modo de juego (por tiempo o por goles) y el valor debe ser mayor que cero.";
    /** Mensaje cuando la dirección no es izquierda ni derecha. */
    public static final String DIRECCION_INVALIDA = "Debe indicar una dirección: izquierda o derecha.";
    /** Mensaje cuando el jugador no tiene puntos para pagar una mejora. */
    public static final String PUNTOS_INSUFICIENTES = "El jugador no tiene puntos suficientes para esta mejora.";
    /** Mensaje cuando se intenta mejorar estadísticas con un partido en curso. */
    public static final String PARTIDO_EN_CURSO = "No se puede hacer esto mientras hay un partido en curso.";
    /** Mensaje cuando la estadística no existe o ya está en el máximo. */
    public static final String ESTADISTICA_MAXIMA = "La estadística no existe o ya está en su valor máximo (10).";
    /** Mensaje cuando una estadística leída de un archivo no está entre 1 y 10. */
    public static final String ESTADISTICA_INVALIDA = "Las estadísticas deben estar entre 1 y 10.";
    /** Mensaje cuando no se puede escribir el archivo de la partida. */
    public static final String ARCHIVO_NO_GUARDADO = "No se pudo guardar la partida en el archivo.";
    /** Mensaje cuando no se puede leer el archivo de la partida. */
    public static final String ARCHIVO_NO_ABIERTO = "No se pudo abrir el archivo de la partida.";
    /** Mensaje cuando el archivo no tiene el formato de una partida de DOPOSoccer. */
    public static final String ARCHIVO_INVALIDO = "El archivo no es una partida válida de DOPOSoccer.";

    /**
     * Crea una excepción del juego con el mensaje dado.
     * @param message mensaje que describe el error
     */
    public DOPOSoccerException(String message) {
        super(message);
    }

    /**
     * Crea una excepción del juego con el mensaje dado y el error que la causó.
     * @param message mensaje que describe el error
     * @param causa error original
     */
    public DOPOSoccerException(String message, Throwable causa) {
        super(message, causa);
    }
}
