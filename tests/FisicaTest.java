package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de la física de los choques: el balón no atraviesa la cabeza, el cuerpo ni el pie
 * de los cabezones, ni el travesaño aunque vaya muy rápido. También prueba la barra de poder
 * (se llena con el tiempo) y el pie que gira al patear.
 */
public class FisicaTest {

    private Cancha cancha;
    private Balon balon;
    private Jugador jugador;
    private double suelo;

    /**
     * Crea una cancha, un balón y un jugador de Camerún parado en el piso, mirando a la derecha.
     */
    @Before
    public void setUp() {
        cancha = new Cancha(ConstantesJuego.ANCHO_CANCHA, ConstantesJuego.ALTO_CANCHA);
        suelo = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugador = new Jugador(1, Pais.CAMERUN.crearPersonaje(), 640, suelo);
        balon = new Balon(100, 100);
    }

    /**
     * Mueve el balón y lo hace chocar con el jugador durante varios pasos.
     * @param pasos cantidad de pasos de 1/60 s
     */
    private void simular(int pasos) {
        for (int i = 0; i < pasos; i++) {
            balon.mover(AyudantePruebas.PASO, cancha);
            jugador.interactuar(balon, null);
        }
    }

    /**
     * Un balón que va rodando hacia el cuerpo (debajo de la cabeza) rebota y no lo atraviesa.
     */
    @Test
    public void noDeberiaAtravesarElCuerpo() {
        balon.reiniciar(560, cancha.getNivelPiso() - Balon.RADIO);
        balon.recibirPatada(700, 0);
        for (int i = 0; i < 60; i++) {
            simular(1);
            assertTrue("el balón quedó dentro o detrás del cuerpo", balon.getX() < jugador.getX());
        }
        assertTrue(balon.getVelocidadX() < 0);
    }

    /**
     * Un balón que llega por detrás también choca con la espalda.
     */
    @Test
    public void noDeberiaAtravesarLaEspalda() {
        balon.reiniciar(720, cancha.getNivelPiso() - Balon.RADIO);
        balon.recibirPatada(-700, 0);
        for (int i = 0; i < 60; i++) {
            simular(1);
            assertTrue(balon.getX() > jugador.getX());
        }
        assertTrue(balon.getVelocidadX() > 0);
    }

    /**
     * Un balón que cae sobre el pie rebota hacia arriba y no se mete en él.
     */
    @Test
    public void noDeberiaAtravesarElPie() {
        double puntaPie = jugador.getX() + Jugador.RADIO / 2.0 + Jugador.LARGO_PIE;
        double xBalon = puntaPie - 2;
        balon.reiniciar(xBalon, cancha.getNivelPiso() - 120);
        balon.recibirPatada(0, 400);
        double topePie = cancha.getNivelPiso() - Jugador.ALTO_PIE;
        boolean reboto = false;
        for (int i = 0; i < 40; i++) {
            simular(1);
            if (Math.abs(balon.getX() - xBalon) < 1) {
                assertTrue("el balón se metió en el pie", balon.getY() + Balon.RADIO <= topePie + 1);
            }
            if (balon.getVelocidadY() < 0) reboto = true;
        }
        assertTrue(reboto);
    }

    /**
     * Si el jugador camina hacia un balón quieto, lo empuja: el balón nunca queda dentro del cuerpo.
     */
    @Test
    public void deberiaEmpujarElBalonAlCaminar() {
        balon.reiniciar(690, cancha.getNivelPiso() - Balon.RADIO);
        for (int i = 0; i < 30; i++) {
            jugador.moverDerecha();
            jugador.mover(AyudantePruebas.PASO, cancha);
            simular(1);
            assertTrue(balon.getX() - Balon.RADIO >= jugador.getX() + Jugador.RADIO / 2.0 - 0.001);
        }
    }

    /**
     * La cabeza saca el balón afuera: después del cabezazo no queda montado sobre ella.
     */
    @Test
    public void deberiaSacarElBalonDeLaCabeza() {
        balon.reiniciar(jugador.getX() + 5, jugador.getY() - 20);
        jugador.interactuar(balon, null);
        double distancia = Math.hypot(balon.getX() - jugador.getX(), balon.getY() - jugador.getY());
        assertTrue(distancia >= Jugador.RADIO + Balon.RADIO - 0.001);
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Aunque el balón caiga muy rápido, no atraviesa el travesaño (el movimiento va en sub-pasos).
     */
    @Test
    public void noDeberiaAtravesarElTravesanoMuyRapido() {
        Arco arco = cancha.getArcoIzquierdo();
        balon.reiniciar(arco.getX() + arco.getAncho() / 2.0, arco.getY() - 40);
        balon.recibirPatada(0, 4800);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getY() < arco.getY());
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Un tiro muy rápido contra la pared no se sale de la cancha.
     */
    @Test
    public void noDeberiaSalirseDeLaCancha() {
        balon.reiniciar(1200, 300);
        balon.recibirPatada(6000, 0);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getX() + Balon.RADIO <= cancha.getAncho());
    }

    /**
     * El pie sigue sirviendo para patear: el balón al alcance sale disparado.
     */
    @Test
    public void deberiaSeguirPateando() {
        Jugador rival = new Jugador(2, Pais.NIGERIA.crearPersonaje(), 1000, suelo);
        balon.reiniciar(jugador.getX() + 45, cancha.getNivelPiso() - Balon.RADIO);
        jugador.patear(balon, rival);
        assertTrue(balon.getVelocidadX() > 0);
        assertTrue(balon.getVelocidadY() < 0);
    }


    /**
     * La barra de poder empieza vacía, se llena en SEGUNDOS_BARRA y no pasa de llena.
     */
    @Test
    public void deberiaLlenarLaBarraDePoder() {
        assertEquals(0, jugador.getBarraPoder(), 1e-9);
        for (int i = 0; i < 60 * 5; i++) jugador.mover(AyudantePruebas.PASO, cancha);
        assertEquals(0.5, jugador.getBarraPoder(), 0.01);
        for (int i = 0; i < 60 * 10; i++) jugador.mover(AyudantePruebas.PASO, cancha);
        assertEquals(1, jugador.getBarraPoder(), 1e-9);
        assertEquals(1, jugador.aEstado().barraPoder, 1e-9);
    }

    /**
     * El pie siempre se dibuja: sin patear el avance es negativo y al patear va de 0 a 1.
     */
    @Test
    public void deberiaMoverElPieAlPatear() {
        assertTrue(jugador.aComandoDibujo().avancePatada < 0);
        jugador.patear(balon, new Jugador(2, Pais.NIGERIA.crearPersonaje(), 1000, suelo));
        jugador.mover(AyudantePruebas.PASO, cancha);
        double avance = jugador.aComandoDibujo().avancePatada;
        assertTrue(avance > 0 && avance < 1);
    }
}
