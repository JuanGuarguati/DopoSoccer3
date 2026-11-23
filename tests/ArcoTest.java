package tests;

import domain.*;
import dto.ComandoDibujo;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas del arco: cuándo hay gol, el travesaño, el gol registrado en el partido
 * (interfaz Interactuable) y su comando de dibujo (interfaz Dibujable).
 */
public class ArcoTest {

    private Partido partido;
    private Arco izquierdo;
    private Arco derecho;

    /**
     * Crea un partido a 3 goles y toma sus dos arcos.
     * @throws DOPOSoccerException si no se puede crear el partido
     */
    @Before
    public void setUp() throws DOPOSoccerException {
        partido = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3);
        izquierdo = partido.getCancha().getArcoIzquierdo();
        derecho = partido.getCancha().getArcoDerecho();
    }

    /**
     * Hay gol solo si el balón entra completo y por debajo del travesaño.
     */
    @Test
    public void deberiaDetectarGolEnElArcoIzquierdo() {
        int abajo = izquierdo.getY() + izquierdo.getAlto() - 20;
        assertTrue(izquierdo.esGol(new Balon(20, abajo)));
        assertFalse(izquierdo.esGol(new Balon(izquierdo.getAncho(), abajo)));
        assertFalse(izquierdo.esGol(new Balon(20, izquierdo.getY() - 30)));
    }

    /**
     * Lo mismo en el arco derecho.
     */
    @Test
    public void deberiaDetectarGolEnElArcoDerecho() {
        int abajo = derecho.getY() + derecho.getAlto() - 20;
        int dentro = derecho.getX() + derecho.getAncho() - 20;
        assertTrue(derecho.esGol(new Balon(dentro, abajo)));
        assertFalse(derecho.esGol(new Balon(derecho.getX(), abajo)));
        assertFalse(izquierdo.esGol(new Balon(dentro, abajo)));
    }

    /**
     * El balón toca el travesaño solo cuando está a la altura de su borde superior.
     */
    @Test
    public void deberiaDetectarElTravesano() {
        assertTrue(izquierdo.chocaConTravesano(new Balon(30, izquierdo.getY())));
        assertFalse(izquierdo.chocaConTravesano(new Balon(30, izquierdo.getY() - 50)));
        assertFalse(izquierdo.chocaConTravesano(new Balon(200, izquierdo.getY())));
    }

    /**
     * Al interactuar con un balón que entró, el arco le pide al partido que registre el gol:
     * el arco izquierdo es el del jugador 1, así que el gol es del jugador 2.
     */
    @Test
    public void deberiaRegistrarElGolDelRival() throws DOPOSoccerException {
        Balon balon = partido.getBalon();
        balon.reiniciar(20, izquierdo.getY() + izquierdo.getAlto() - 20);
        Interactuable interactuable = izquierdo;
        interactuable.interactuar(balon, partido);
        assertEquals(1, partido.getJugador(2).getGoles());
        assertEquals(0, partido.getJugador(1).getGoles());
        assertEquals(ConstantesJuego.ANCHO_CANCHA / 2.0, balon.getX(), 0.0001);
    }

    /**
     * Si el balón no entró, interactuar no cambia el marcador.
     */
    @Test
    public void noDeberiaRegistrarGolSiNoEntro() throws DOPOSoccerException {
        Balon balon = partido.getBalon();
        balon.reiniciar(640, 100);
        derecho.interactuar(balon, partido);
        assertEquals(0, partido.getJugador(1).getGoles());
    }

    /**
     * El comando de dibujo tiene la posición del arco y hacia dónde abre.
     */
    @Test
    public void deberiaArmarSuComandoDibujo() {
        ComandoDibujo comando = izquierdo.aComandoDibujo();
        assertEquals(ComandoDibujo.Forma.ARCO, comando.forma);
        assertEquals(izquierdo.getX(), comando.x);
        assertEquals(izquierdo.getAlto(), comando.alto);
        assertTrue(comando.haciaDerecha);
        assertFalse(derecho.aComandoDibujo().haciaDerecha);
    }
}
