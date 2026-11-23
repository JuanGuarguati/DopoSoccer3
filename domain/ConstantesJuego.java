package domain;

/**
 * Constantes del juego que usan varias clases del dominio (y la presentación):
 * tamaño de la cancha, saque, puntos y ritmo del juego.
 */
public final class ConstantesJuego {

    /** Ancho lógico de la cancha. */
    public static final int ANCHO_CANCHA = 1280;
    /** Alto lógico de la cancha. */
    public static final int ALTO_CANCHA = 720;
    /** Distancia desde cada pared a la que empiezan los jugadores. */
    public static final double DISTANCIA_INICIAL = 200;
    /** Altura desde la que cae el balón al empezar y después de cada gol. */
    public static final double ALTURA_SAQUE = 100;
    /** Puntos que recibe el ganador del partido. */
    public static final int PUNTOS_GANADOR = 100;
    /** Puntos que recibe el perdedor del partido. */
    public static final int PUNTOS_PERDEDOR = 25;
    /** Pasos del juego por segundo (el ciclo de la presentación debe usar el mismo valor). */
    public static final int PASOS_POR_SEGUNDO = 60;

    /**
     * No se crean objetos de esta clase: solo guarda constantes.
     */
    private ConstantesJuego() { }
}
