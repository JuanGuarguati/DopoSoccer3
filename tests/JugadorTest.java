package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas del jugador: daño, aturdimiento y movimiento.
 */
public class JugadorTest {

    private Jugador jugador1;
    private Jugador jugador2;
    private Cancha cancha;

    /**
     * Crea dos jugadores cerca uno del otro antes de cada prueba.
     */
    @Before
    public void setUp() {
        cancha = new Cancha(1280, 720);
        double piso = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugador1 = new Jugador(1, new Camerun(), 400, piso);
        jugador2 = new Jugador(2, new Nigeria(), 450, piso);
    }

    /**
     * El jugador empieza con 0 goles y vida máxima.
     */
    @Test
    public void deberiaIniciarConCeroGolesYVidaMaxima() {
        assertEquals(0, jugador1.getGoles());
        assertEquals(100, jugador1.getVida(), 0.001);
    }

    /**
     * Con kick 5 la patada al rival hace 10 x 1.4 = 14 de daño (ejemplo del enunciado).
     */
    @Test
    public void deberiaHacerDanioPatadaConKick5() throws DOPOSoccerException {
        for (int i = 0; i < 4; i++) jugador1.getEstadisticas().mejorar(Estadistica.KICK);
        Balon lejos = new Balon(900, 100);
        jugador1.patear(lejos, jugador2);
        assertEquals(100 - 14, jugador2.getVida(), 0.001);
    }

    /**
     * Si la patada alcanza el balón, solo lo impulsa y no hace daño.
     */
    @Test
    public void deberiaImpulsarBalonSinHacerDanio() {
        Balon balon = new Balon(jugador1.getX() + 40, jugador1.getY() + Jugador.RADIO);
        jugador1.patear(balon, jugador2);
        assertEquals(100, jugador2.getVida(), 0.001);
        assertTrue(balon.getVelocidadX() > 0);
    }

    /**
     * La vida nunca queda negativa y al llegar a 0 el jugador queda aturdido.
     */
    @Test
    public void noDeberiaQuedarVidaNegativa() {
        jugador2.recibirDanio(500);
        assertEquals(0, jugador2.getVida(), 0.001);
        assertTrue(jugador2.estaAturdido());
    }

    /**
     * Aturdido no se puede mover; a los 3 segundos vuelve con vida máxima.
     */
    @Test
    public void deberiaRecuperarseDespuesDeTresSegundos() {
        jugador2.recibirDanio(100);
        double x = jugador2.getX();
        jugador2.moverDerecha();
        assertEquals(x, jugador2.getX(), 0.001);
        jugador2.mover(3.1, cancha);
        assertFalse(jugador2.estaAturdido());
        assertEquals(100, jugador2.getVida(), 0.001);
    }

    /**
     * Al saltar el jugador sube y luego vuelve al suelo.
     */
    @Test
    public void deberiaSaltarYVolverAlSuelo() {
        double suelo = jugador1.getY();
        jugador1.saltar();
        jugador1.mover(0.1, cancha);
        assertTrue(jugador1.getY() < suelo);
        for (int i = 0; i < 100; i++) jugador1.mover(0.05, cancha);
        assertEquals(suelo, jugador1.getY(), 0.001);
    }
}
