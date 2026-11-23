package domain;

import dto.ComandoDibujo;

/**
 * Balón del partido. Le afecta la gravedad y rebota en el piso, los bordes y los arcos.
 * Es Movible (se mueve), Dibujable (arma su ComandoDibujo) y Pateable (sale disparado con una patada).
 */
public class Balon implements Movible, Dibujable, Pateable {

    /** Radio del balón. */
    public static final int RADIO = 15;
    /** Fracción de velocidad que conserva el balón al rebotar en el piso. */
    public static final double REBOTE_PISO = 0.7;
    /** Fracción de velocidad que conserva el balón al rebotar en una pared. */
    public static final double REBOTE_PARED = 0.8;
    /** Factor de roce que frena el balón en cada actualización. */
    public static final double ROCE = 0.99;
    /** Velocidad vertical por debajo de la cual el balón deja de rebotar en el piso. */
    public static final double VELOCIDAD_MINIMA_REBOTE = 30;

    private double x;
    private double y;
    private double velocidadX;
    private double velocidadY;
    private int radio;
    /** Altura del balón antes del último movimiento (para saber de qué lado llegó al travesaño). */
    private double yAnterior;

    /**
     * Crea un balón quieto en la posición dada.
     * @param x posición horizontal del centro
     * @param y posición vertical del centro
     */
    public Balon(double x, double y) {
        this.x = x;
        this.y = y;
        this.yAnterior = y;
        this.radio = RADIO;
    }

    /**
     * Mueve el balón durante un tiempo (interfaz Movible): aplica la gravedad y avanza en sub-pasos
     * de a lo sumo un radio (así no atraviesa el travesaño, los jugadores ni los bordes aunque vaya
     * muy rápido) y rebota.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha donde se mueve
     */
    @Override
    public void mover(double segundos, Cancha cancha) {
        aplicarGravedad(cancha.getGravedad() * segundos);
        double recorrido = Math.hypot(velocidadX, velocidadY) * segundos;
        int pasos = Math.max(1, (int) Math.ceil(recorrido / radio));
        double paso = segundos / pasos;
        for (int i = 0; i < pasos; i++) {
            yAnterior = y;
            x += velocidadX * paso;
            y += velocidadY * paso;
            rebotar(cancha);
        }
    }

    /**
     * Aumenta la velocidad vertical (hacia abajo).
     * @param gravedad cantidad que se suma a la velocidad vertical
     */
    public void aplicarGravedad(double gravedad) {
        velocidadY += gravedad;
    }

    /**
     * Hace rebotar el balón en el piso, el techo, los bordes y los travesaños.
     * @param cancha cancha donde está el balón
     */
    public void rebotar(Cancha cancha) {
        int piso = cancha.getNivelPiso();
        if (y + radio > piso) {
            y = piso - radio;
            velocidadY = -velocidadY * REBOTE_PISO;
            velocidadX *= ROCE;
            if (Math.abs(velocidadY) < VELOCIDAD_MINIMA_REBOTE) velocidadY = 0;
        }
        if (y - radio < 0) {
            y = radio;
            velocidadY = -velocidadY * REBOTE_PARED;
        }
        if (x - radio < 0) {
            x = radio;
            velocidadX = -velocidadX * REBOTE_PARED;
        }
        if (x + radio > cancha.getAncho()) {
            x = cancha.getAncho() - radio;
            velocidadX = -velocidadX * REBOTE_PARED;
        }
        for (Arco arco : new Arco[] {cancha.getArcoIzquierdo(), cancha.getArcoDerecho()}) {
            if (arco.chocaConTravesano(this)) {
                boolean veniaDeArriba = yAnterior <= arco.getY();
                y = veniaDeArriba ? arco.getY() - radio : arco.getY() + radio;
                velocidadY = -velocidadY * REBOTE_PARED;
            }
        }
    }

    /**
     * Cambia la velocidad del balón por una patada o un cabezazo.
     * @param fuerzaX nueva velocidad horizontal
     * @param fuerzaY nueva velocidad vertical (negativa = hacia arriba)
     */
    public void recibirPatada(double fuerzaX, double fuerzaY) {
        velocidadX = fuerzaX;
        velocidadY = fuerzaY;
    }

    /**
     * Indica si el pie del pateador alcanza el balón (interfaz Pateable).
     * @param pateador jugador que patea
     * @return true si lo alcanza
     */
    @Override
    public boolean estaAlAlcanceDe(Jugador pateador) {
        return pateador.tocaBalon(this);
    }

    /**
     * Pone el balón en una posición y lo hace rebotar contra una superficie (cabeza, cuerpo o pie de un jugador).
     * Si el balón iba hacia la superficie, invierte esa parte de la velocidad y la multiplica por el rebote.
     * @param nuevoX nueva posición horizontal (afuera de la superficie)
     * @param nuevoY nueva posición vertical (afuera de la superficie)
     * @param normalX componente horizontal de la dirección que sale de la superficie (largo 1)
     * @param normalY componente vertical de la dirección que sale de la superficie (largo 1)
     * @param rebote fracción de velocidad que conserva (0 a 1)
     */
    public void rebotarEn(double nuevoX, double nuevoY, double normalX, double normalY, double rebote) {
        x = nuevoX;
        y = nuevoY;
        double haciaSuperficie = velocidadX * normalX + velocidadY * normalY;
        if (haciaSuperficie < 0) {
            velocidadX -= (1 + rebote) * haciaSuperficie * normalX;
            velocidadY -= (1 + rebote) * haciaSuperficie * normalY;
        }
    }

    /**
     * Recibe una patada (interfaz Pateable): sale hacia donde mira el pateador y hacia arriba.
     * @param pateador jugador que patea
     */
    @Override
    public void serPateado(Jugador pateador) {
        double direccion = pateador.estaMirandoDerecha() ? 1 : -1;
        recibirPatada(direccion * Jugador.FUERZA_PATADA_X, Jugador.FUERZA_PATADA_Y);
    }

    /**
     * Arma los datos para dibujar el balón (interfaz Dibujable): el cuadro que lo encierra.
     * @return comando de dibujo del balón
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        return new ComandoDibujo(ComandoDibujo.Forma.BALON, (int) x - radio, (int) y - radio,
            2 * radio, 2 * radio, false);
    }

    /**
     * Pone el balón quieto en una posición (interfaz Movible).
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    @Override
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        yAnterior = y;
        velocidadX = 0;
        velocidadY = 0;
    }

    /**
     * @return posición horizontal del centro
     */
    @Override
    public double getX() { return x; }

    /**
     * @return posición vertical del centro
     */
    @Override
    public double getY() { return y; }

    /**
     * @return radio del balón
     */
    @Override
    public int getRadio() { return radio; }

    /**
     * @return rapidez del balón: módulo de su velocidad (interfaz Movible)
     */
    @Override
    public double getVelocidad() { return Math.hypot(velocidadX, velocidadY); }

    /**
     * @return velocidad horizontal
     */
    public double getVelocidadX() { return velocidadX; }

    /**
     * @return velocidad vertical
     */
    public double getVelocidadY() { return velocidadY; }
}
