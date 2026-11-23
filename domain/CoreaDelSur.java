package domain;

import java.awt.Color;

/**
 * Personaje de Corea del Sur. Tiro de poder: Aura Azul. Efecto: empuje hacia su propio arco.
 */
public final class CoreaDelSur extends Personaje {

    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {Color.WHITE, new Color(205, 46, 58), new Color(0, 71, 160)};

    /**
     * Crea el personaje de Corea del Sur (daño del tiro 40, efecto de 1.5 segundos).
     */
    public CoreaDelSur() {
        super(Pais.COREA_DEL_SUR, 40, 1.5);
    }

    /**
     * Ejecuta el tiro de poder Aura Azul.
     * Pendiente: el poder especial todavía no está implementado.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Pendiente: poder especial
    }

    /**
     * Aplica el efecto: empuje hacia su propio arco.
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
