package domain;

import dto.ComandoDibujo;

/**
 * Arco (portería) de un lado de la cancha.
 * (x, y) es la esquina superior izquierda; y es la altura del travesaño.
 * Es Interactuable (detecta el gol) y Dibujable (arma su ComandoDibujo).
 */
public class Arco implements Interactuable, Dibujable {

    /** Ancho del arco en unidades de la cancha. */
    public static final int ANCHO = 60;
    /** Alto del arco en unidades de la cancha. */
    public static final int ALTO = 150;

    private int x;
    private int y;
    private int ancho;
    private int alto;
    private boolean esIzquierdo;

    /**
     * Crea un arco.
     * @param x posición horizontal de la esquina superior izquierda
     * @param y altura del travesaño
     * @param esIzquierdo true si es el arco del lado izquierdo
     */
    public Arco(int x, int y, boolean esIzquierdo) {
        this.x = x;
        this.y = y;
        this.ancho = ANCHO;
        this.alto = ALTO;
        this.esIzquierdo = esIzquierdo;
    }

    /**
     * Revisa si el balón entró a este arco (interfaz Interactuable) y, si entró,
     * le pide al partido que registre el gol.
     * @param balon balón del partido
     * @param partido partido en curso
     */
    @Override
    public void interactuar(Balon balon, Partido partido) {
        if (esGol(balon)) partido.registrarGol(this);
    }

    /**
     * Arma los datos para dibujar el arco (interfaz Dibujable).
     * @return comando de dibujo del arco (haciaDerecha es true en el arco izquierdo)
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        return new ComandoDibujo(ComandoDibujo.Forma.ARCO, x, y, ancho, alto, esIzquierdo);
    }

    /**
     * Indica si el balón entró completamente al arco por debajo del travesaño.
     * @param balon balón del partido
     * @return true si es gol
     */
    public boolean esGol(Balon balon) {
        double r = balon.getRadio();
        boolean debajo = balon.getY() - r > y;
        if (esIzquierdo) {
            return debajo && balon.getX() + r < x + ancho;
        }
        return debajo && balon.getX() - r > x;
    }

    /**
     * Indica si el balón está tocando el travesaño (la parte de arriba del arco, incluida su punta).
     * @param balon balón del partido
     * @return true si lo toca
     */
    public boolean chocaConTravesano(Balon balon) {
        boolean dentroEnX = balon.getX() >= x && balon.getX() <= x + ancho;
        double punta = esIzquierdo ? x + ancho : x;
        boolean tocaPunta = Math.hypot(balon.getX() - punta, balon.getY() - y) <= balon.getRadio();
        return (dentroEnX && Math.abs(balon.getY() - y) <= balon.getRadio()) || tocaPunta;
    }

    /**
     * @return posición x del arco
     */
    public int getX() { return x; }

    /**
     * @return altura del travesaño
     */
    public int getY() { return y; }

    /**
     * @return ancho del arco
     */
    public int getAncho() { return ancho; }

    /**
     * @return alto del arco
     */
    public int getAlto() { return alto; }

    /**
     * @return true si es el arco izquierdo
     */
    public boolean esIzquierdo() { return esIzquierdo; }
}
