package domain;

import dto.ComandoDibujo;

/**
 * Cancha del partido: tamaño, piso, gravedad y los dos arcos.
 * Las medidas son lógicas (en píxeles); la interfaz las escala a la resolución elegida.
 * Es Dibujable (arma su ComandoDibujo con el cielo y el pasto).
 */
public class Cancha implements Dibujable {

    /** Altura del piso medida desde el borde inferior de la cancha. */
    public static final int ALTO_PISO = 40;
    /** Gravedad de la cancha en unidades por segundo al cuadrado. */
    public static final double GRAVEDAD = 900;

    private int ancho;
    private int alto;
    private int nivelPiso;
    private Arco arcoIzquierdo;
    private Arco arcoDerecho;

    /**
     * Crea una cancha con sus dos arcos.
     * @param ancho ancho de la cancha
     * @param alto alto de la cancha
     */
    public Cancha(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        this.nivelPiso = alto - ALTO_PISO;
        arcoIzquierdo = new Arco(0, nivelPiso - Arco.ALTO, true);
        arcoDerecho = new Arco(ancho - Arco.ANCHO, nivelPiso - Arco.ALTO, false);
    }

    /**
     * Arma los datos para dibujar el cielo y el pasto (interfaz Dibujable).
     * @return comando de dibujo de la cancha (y es el nivel del piso)
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        return new ComandoDibujo(ComandoDibujo.Forma.CANCHA, 0, nivelPiso, ancho, alto, false);
    }

    /**
     * @return gravedad de la cancha
     */
    public double getGravedad() { return GRAVEDAD; }

    /**
     * @return altura del piso
     */
    public int getNivelPiso() { return nivelPiso; }

    /**
     * @return ancho de la cancha
     */
    public int getAncho() { return ancho; }

    /**
     * @return alto de la cancha
     */
    public int getAlto() { return alto; }

    /**
     * @return arco del lado izquierdo (lo defiende el jugador 1)
     */
    public Arco getArcoIzquierdo() { return arcoIzquierdo; }

    /**
     * @return arco del lado derecho (lo defiende el jugador 2)
     */
    public Arco getArcoDerecho() { return arcoDerecho; }
}
