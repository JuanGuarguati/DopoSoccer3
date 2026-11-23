package domain;

/**
 * Elemento del partido que puede recibir una patada.
 * La implementan Balon (sale disparado) y Jugador (recibe daño).
 * Cuando un jugador patea, revisa los pateables en orden y patea al primero que alcance.
 */
public interface Pateable {

    /**
     * @param pateador jugador que patea
     * @return true si el pie del pateador alcanza este elemento
     */
    boolean estaAlAlcanceDe(Jugador pateador);

    /**
     * Aplica el efecto de la patada sobre este elemento.
     * @param pateador jugador que patea
     */
    void serPateado(Jugador pateador);
}
