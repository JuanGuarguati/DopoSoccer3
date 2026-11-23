package domain;

import java.awt.Color;

/**
 * Personaje de Nigeria. Tiro de poder: Tornado. Efecto: inconsciente, no se mueve, no salta ni patea.
 */
public final class Nigeria extends Personaje {

    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {Color.WHITE, new Color(0, 135, 81), new Color(0, 135, 81)};

    /**
     * Crea el personaje de Nigeria (daño del tiro 40, efecto de 3.0 segundos).
     */
    public Nigeria() {
        super(Pais.NIGERIA, 40, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Tornado.
     * Pendiente: el poder especial todavía no está implementado.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Pendiente: poder especial
    }

    /**
     * Aplica el efecto: inconsciente, no se mueve, no salta ni patea.
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
