package domain;

/**
 * Poder especial de un país: el tiro de poder y el efecto que deja en el rival.
 * Lo implementa Personaje y cada país (Corea del Sur, Camerún, Nigeria y Estados Unidos)
 * define el suyo.
 */
public interface PoderEspecial {

    /**
     * Ejecuta el tiro de poder sobre el balón.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    void tiroDePoder(Balon balon, boolean haciaDerecha);

    /**
     * Aplica al rival el efecto del tiro de poder.
     * @param rival jugador que recibe el efecto
     */
    void aplicarEfecto(Jugador rival);

    /**
     * @return daño base del tiro de poder
     */
    int getDanioTiro();

    /**
     * @return duración del efecto en segundos
     */
    double getDuracionEfecto();
}
