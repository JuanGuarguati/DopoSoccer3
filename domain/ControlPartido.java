package domain;

/**
 * Lo que se puede hacer con un partido mientras se juega: avanzar el tiempo, mover,
 * saltar y patear con un jugador, pausar y terminar.
 * La implementan Partido (hace el trabajo) y DOPOSoccer (la fachada: valida que haya
 * partido y luego le delega). Así la interfaz gráfica y el partido hablan el mismo idioma.
 */
public interface ControlPartido {

    /**
     * Avanza el partido.
     * @param segundos tiempo que pasó desde la última actualización
     */
    void actualizar(double segundos);

    /**
     * Mueve un jugador a la izquierda o a la derecha.
     * @param numero número del jugador (1 o 2)
     * @param direccion Direccion.IZQUIERDA o Direccion.DERECHA
     * @throws DOPOSoccerException si no se puede mover
     */
    void moverJugador(int numero, Direccion direccion) throws DOPOSoccerException;

    /**
     * Hace saltar a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no se puede saltar
     */
    void saltarJugador(int numero) throws DOPOSoccerException;

    /**
     * Hace patear a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no se puede patear
     */
    void patearJugador(int numero) throws DOPOSoccerException;

    /**
     * Pausa el partido o lo reanuda si ya estaba pausado.
     * @throws DOPOSoccerException si no hay partido
     */
    void pausar() throws DOPOSoccerException;

    /**
     * Termina el partido.
     * @throws DOPOSoccerException si no hay partido
     */
    void terminar() throws DOPOSoccerException;

    /**
     * @return true si el partido está en pausa
     * @throws DOPOSoccerException si no hay partido
     */
    boolean estaPausado() throws DOPOSoccerException;

    /**
     * @return true si el partido terminó
     * @throws DOPOSoccerException si no hay partido
     */
    boolean estaTerminado() throws DOPOSoccerException;
}
