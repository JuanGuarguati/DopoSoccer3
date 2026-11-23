package domain;

import java.awt.Color;

/**
 * Personaje de Camerún. Tiro de poder: Rayo. Efecto: electrocutado, no salta y se mueve al 50 %.
 */
public final class Camerun extends Personaje {

    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {new Color(0, 122, 94), new Color(252, 209, 22), new Color(206, 17, 38)};

    /**
     * Crea el personaje de Camerún (daño del tiro 60, efecto de 3.0 segundos).
     */
    public Camerun() {
        super(Pais.CAMERUN, 60, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Rayo.
     * Pendiente: el poder especial todavía no está implementado.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Pendiente: poder especial
    }

    /**
     * Aplica el efecto: electrocutado, no salta y se mueve al 50 %.
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
}
