package tests;

import domain.*;
import dto.ComandoDibujo;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas del balón: gravedad, rebotes en el piso, las paredes y el travesaño,
 * la patada (interfaz Pateable) y su comando de dibujo (interfaz Dibujable).
 */
public class BalonTest {

    private Cancha cancha;
    private Balon balon;

    /**
     * Crea una cancha del tamaño del juego y un balón quieto en el aire.
     */
    @Before
    public void setUp() {
        cancha = new Cancha(ConstantesJuego.ANCHO_CANCHA, ConstantesJuego.ALTO_CANCHA);
        balon = new Balon(640, 100);
    }

    /**
     * La gravedad hace que el balón quieto empiece a caer.
     */
    @Test
    public void deberiaCaerPorLaGravedad() {
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getVelocidadY() > 0);
        assertTrue(balon.getY() > 100);
    }

    /**
     * Al llegar al piso rebota hacia arriba y no se mete en el piso.
     */
    @Test
    public void deberiaRebotarEnElPiso() {
        int piso = cancha.getNivelPiso();
        balon.reiniciar(640, piso - Balon.RADIO - 1);
        balon.recibirPatada(0, 600);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getVelocidadY() < 0);
        assertTrue(balon.getY() + Balon.RADIO <= piso);
    }

    /**
     * Si cae muy despacio deja de rebotar y se queda en el piso.
     */
    @Test
    public void deberiaQuedarseQuietoSiCaeDespacio() {
        int piso = cancha.getNivelPiso();
        balon.reiniciar(640, piso - Balon.RADIO);
        balon.recibirPatada(0, 5);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertEquals(0, balon.getVelocidadY(), 0.0001);
    }

    /**
     * Rebota en la pared izquierda y cambia de sentido.
     */
    @Test
    public void deberiaRebotarEnLaPared() {
        balon.reiniciar(Balon.RADIO + 1, 100);
        balon.recibirPatada(-600, 0);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getVelocidadX() > 0);
        assertTrue(balon.getX() - Balon.RADIO >= 0);
    }

    /**
     * Si llega al travesaño desde arriba, queda encima y sale hacia arriba.
     */
    @Test
    public void deberiaRebotarEncimaDelTravesano() {
        Arco arco = cancha.getArcoIzquierdo();
        balon.reiniciar(30, arco.getY() - Balon.RADIO - 2);
        balon.recibirPatada(0, 300);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getY() <= arco.getY() - Balon.RADIO);
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Si llega al travesaño desde abajo, queda debajo y sale hacia abajo
     * (se decide por la posición anterior, no por la velocidad).
     */
    @Test
    public void deberiaRebotarDebajoDelTravesano() {
        Arco arco = cancha.getArcoIzquierdo();
        balon.reiniciar(30, arco.getY() + Balon.RADIO + 2);
        balon.recibirPatada(0, -400);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getY() >= arco.getY() + Balon.RADIO);
        assertTrue(balon.getVelocidadY() > 0);
    }

    /**
     * Al ser pateado (interfaz Pateable) sale hacia donde mira el pateador y hacia arriba.
     */
    @Test
    public void deberiaSalirHaciaDondeMiraElPateador() throws DOPOSoccerException {
        Jugador derecha = new Jugador(1, Personaje.crear(Pais.CAMERUN), 300, 600);
        Jugador izquierda = new Jugador(2, Personaje.crear(Pais.NIGERIA), 900, 600);
        Pateable pateable = balon;
        pateable.serPateado(derecha);
        assertEquals(Jugador.FUERZA_PATADA_X, balon.getVelocidadX(), 0.0001);
        assertEquals(Jugador.FUERZA_PATADA_Y, balon.getVelocidadY(), 0.0001);
        pateable.serPateado(izquierda);
        assertEquals(-Jugador.FUERZA_PATADA_X, balon.getVelocidadX(), 0.0001);
    }

    /**
     * El comando de dibujo es el cuadro que encierra al balón.
     */
    @Test
    public void deberiaArmarSuComandoDibujo() {
        Dibujable dibujable = balon;
        ComandoDibujo comando = dibujable.aComandoDibujo();
        assertEquals(ComandoDibujo.Forma.BALON, comando.forma);
        assertEquals(640 - Balon.RADIO, comando.x);
        assertEquals(100 - Balon.RADIO, comando.y);
        assertEquals(2 * Balon.RADIO, comando.ancho);
    }

    /**
     * Reiniciar (interfaz Movible) lo deja quieto en la nueva posición.
     */
    @Test
    public void deberiaReiniciarseQuieto() {
        Movible movible = balon;
        balon.recibirPatada(300, -300);
        movible.reiniciar(200, 200);
        assertEquals(0, movible.getVelocidad(), 0.0001);
        assertEquals(200, movible.getX(), 0.0001);
    }
}
