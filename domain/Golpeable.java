package domain;

/**
 * Elemento que tiene vida y puede recibir daño (patadas y tiros de poder).
 * La implementa Jugador (y por herencia Maquina). Cuando la vida llega a cero queda aturdido.
 */
public interface Golpeable {

    /**
     * Resta vida. La vida nunca queda menor que cero.
     * @param danio cantidad de vida que se resta
     */
    void recibirDanio(double danio);

    /**
     * @return vida actual
     */
    double getVida();

    /**
     * @return vida máxima
     */
    double getVidaMax();

    /**
     * @return true si quedó sin vida y está aturdido
     */
    boolean estaAturdido();
}
