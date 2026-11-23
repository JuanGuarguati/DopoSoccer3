package domain;

/**
 * Elemento del partido que reacciona cuando el balón lo toca.
 * La implementan Jugador (cabezazo: el balón rebota en su cabeza) y Arco (gol).
 * El partido le pide a todos los interactuables que revisen el balón con la misma llamada.
 */
public interface Interactuable {

    /**
     * Revisa si el balón toca el elemento y, si lo toca, aplica el efecto.
     * @param balon balón del partido
     * @param partido partido en curso (por ejemplo, para anotar el gol)
     */
    void interactuar(Balon balon, Partido partido);
}
