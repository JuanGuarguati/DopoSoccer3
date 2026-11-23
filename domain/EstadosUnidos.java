package domain;

import java.awt.Color;

/**
 * Personaje de Estados Unidos. Tiro de poder: Ilusión. Efecto: confusión, 8 balones iguales.
 */
public final class EstadosUnidos extends Personaje {

    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {new Color(60, 59, 110), Color.WHITE, new Color(178, 34, 52)};

    /**
     * Crea el personaje de Estados Unidos (daño del tiro 30, efecto de 2.5 segundos).
     */
    public EstadosUnidos() {
        super(Pais.ESTADOS_UNIDOS, 30, 2.5);
    }

    /**
     * Ejecuta el tiro de poder Ilusión.
     * Pendiente: el poder especial todavía no está implementado.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Pendiente: poder especial
    }

    /**
     * Aplica el efecto: confusión, 8 balones iguales.
     * Pendiente: el poder especial todavía no está implementado.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        // Pendiente: poder especial
    }

    /**
     * Colores del uniforme, tomados de la bandera.
     * @return arreglo {cabeza, banda, cuerpo}
     */
    @Override
    public Color[] getColores() {
        return COLORES.clone();
    }

    /**
     * El uniforme de Estados Unidos lleva rayas blancas en el cuerpo.
     * @return true
     */
    @Override
    public boolean tieneRayas() { return true; }
}
