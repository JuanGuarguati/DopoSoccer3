package domain;

/**
 * Elemento del partido que se mueve por la cancha (jugadores y balón).
 * Permite que el partido mueva a todos de la misma forma (polimorfismo).
 */
public interface Movible {

    /**
     * Mueve el elemento durante un tiempo, con la gravedad y los límites de la cancha.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha donde se mueve
     */
    void mover(double segundos, Cancha cancha);

    /**
     * Pone el elemento quieto en una posición.
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    void reiniciar(double x, double y);

    /**
     * @return posición horizontal del centro
     */
    double getX();

    /**
     * @return posición vertical del centro
     */
    double getY();

    /**
     * @return rapidez del elemento (píxeles por paso en el jugador, módulo de la velocidad en el balón)
     */
    double getVelocidad();

    /**
     * @return radio del elemento
     */
    int getRadio();
}
