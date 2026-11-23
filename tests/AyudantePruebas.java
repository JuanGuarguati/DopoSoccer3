package tests;

import domain.Arco;
import domain.DOPOSoccer;
import domain.DOPOSoccerException;
import domain.Direccion;
import domain.Partido;
import dto.ComandoDibujo;
import static org.junit.Assert.fail;

/**
 * Ayudas para las pruebas: juegan de verdad por la fachada (mover, patear, saltar y
 * actualizar en pasos pequeños) en vez de forzar los goles con anotarGol().
 */
public final class AyudantePruebas {

    /** Duración de un paso de la simulación (igual que el juego). */
    public static final double PASO = 1.0 / 60;
    /** Máximo de pasos para anotar un gol antes de fallar la prueba. */
    public static final int MAX_PASOS = 20000;

    /**
     * No se crean objetos de esta clase.
     */
    private AyudantePruebas() { }

    /**
     * Juega hasta que el jugador indicado anote un gol: persigue el balón, se pone detrás
     * y patea hacia el arco rival. El otro jugador solo salta de vez en cuando.
     * @param juego fachada con un partido en curso
     * @param numero jugador que debe anotar (1 o 2)
     * @throws DOPOSoccerException si no se puede jugar
     */
    public static void anotarGol(DOPOSoccer juego, int numero) throws DOPOSoccerException {
        int otro = numero == 1 ? 2 : 1;
        int golesAntes = juego.getEstadoJugador(numero).goles;
        Direccion haciaArco = numero == 1 ? Direccion.DERECHA : Direccion.IZQUIERDA;
        for (int paso = 0; paso < MAX_PASOS; paso++) {
            if (juego.getEstadoJugador(numero).goles > golesAntes) return;
            if (juego.estaTerminado()) break;
            ComandoDibujo balon = buscar(juego, ComandoDibujo.Forma.BALON, 0);
            ComandoDibujo jugador = buscar(juego, ComandoDibujo.Forma.JUGADOR, numero);
            double centroBalon = balon.x + balon.ancho / 2.0;
            double detras = numero == 1 ? centroBalon - 40 : centroBalon + 40;
            if (jugador.x < detras - 5) {
                juego.moverJugador(numero, Direccion.DERECHA);
            } else if (jugador.x > detras + 5) {
                juego.moverJugador(numero, Direccion.IZQUIERDA);
            } else {
                juego.moverJugador(numero, haciaArco);
                juego.patearJugador(numero);
            }
            if (paso % 40 == 0) juego.saltarJugador(otro);
            juego.actualizar(PASO);
        }
        fail("El jugador " + numero + " no logró anotar");
    }

    /**
     * Avanza un partido en pasos pequeños durante los segundos dados.
     * @param partido partido que se avanza
     * @param segundos tiempo total
     */
    public static void avanzar(Partido partido, double segundos) {
        int pasos = (int) Math.round(segundos / PASO);
        for (int i = 0; i < pasos; i++) partido.actualizar(PASO);
    }

    /**
     * Avanza el juego en pasos pequeños durante los segundos dados.
     * @param juego fachada del juego
     * @param segundos tiempo total
     */
    public static void avanzar(DOPOSoccer juego, double segundos) {
        int pasos = (int) Math.round(segundos / PASO);
        for (int i = 0; i < pasos; i++) juego.actualizar(PASO);
    }

    /**
     * Busca el comando de dibujo de una forma (y del jugador indicado si es JUGADOR).
     * @param juego fachada con partido
     * @param forma forma buscada
     * @param numero número del jugador (se ignora si no es JUGADOR)
     * @return el comando
     * @throws DOPOSoccerException si no hay partido
     */
    public static ComandoDibujo buscar(DOPOSoccer juego, ComandoDibujo.Forma forma, int numero)
            throws DOPOSoccerException {
        for (ComandoDibujo c : juego.getComandosDibujo()) {
            if (c.forma == forma && (forma != ComandoDibujo.Forma.JUGADOR || c.numero == numero)) return c;
        }
        throw new IllegalStateException("No se encontró " + forma);
    }

    /**
     * Pone el balón dentro de un arco y avanza un paso, para que el arco lo detecte como gol.
     * @param partido partido en juego
     * @param arco arco donde entra el balón
     */
    public static void meterEnArco(Partido partido, Arco arco) {
        double x = arco.esIzquierdo() ? arco.getX() + 20 : arco.getX() + arco.getAncho() - 20;
        partido.getBalon().reiniciar(x, arco.getY() + arco.getAlto() - 20);
        partido.actualizar(PASO);
    }
}
