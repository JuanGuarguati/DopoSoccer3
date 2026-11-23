package tests;

import domain.*;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;

/**
 * Pruebas de la interfaz Movible (Jugador y Balon) y de los enums Pais y Direccion.
 */
public class MovibleTest {

    /**
     * Jugador y Balon son Movible y se pueden mover con la misma llamada.
     */
    @Test
    public void deberiaMoverJugadorYBalonComoMovible() {
        Cancha cancha = new Cancha(1280, 720);
        Movible balon = new Balon(640, 100);
        Movible jugador = new Jugador(1, new Camerun(), 200, 100);
        double yBalon = balon.getY();
        double yJugador = jugador.getY();
        balon.mover(0.1, cancha);
        jugador.mover(0.1, cancha);
        assertTrue(balon.getY() > yBalon);
        assertTrue(jugador.getY() > yJugador);
    }

    /**
     * La velocidad del jugador es su paso según speed y la del balón es el módulo de su velocidad.
     */
    @Test
    public void deberiaCalcularVelocidad() {
        Jugador jugador = new Jugador(1, new Camerun(), 200, 600);
        assertEquals(Estadisticas.VELOCIDAD_BASE, jugador.getVelocidad(), 0.001);
        Balon balon = new Balon(640, 100);
        balon.recibirPatada(300, -400);
        assertEquals(500, balon.getVelocidad(), 0.001);
    }

    /**
     * El partido tiene tres elementos Movible: el balón y los dos jugadores.
     */
    @Test
    public void deberiaTenerTresMovibles() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3);
        List<Movible> movibles = p.getMovibles();
        assertEquals(3, movibles.size());
        assertSame(p.getBalon(), movibles.get(0));
        assertSame(p.getJugador(2), movibles.get(2));
    }

    /**
     * Moverse con el enum Direccion cambia la posición hacia ese lado.
     */
    @Test
    public void deberiaMoverseConDireccion() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3);
        double x = p.getJugador(1).getX();
        p.moverJugador(1, Direccion.DERECHA);
        assertTrue(p.getJugador(1).getX() > x);
        p.moverJugador(1, Direccion.IZQUIERDA);
        assertEquals(x, p.getJugador(1).getX(), 0.001);
    }

    /**
     * El enum Pais crea el personaje correcto y se puede leer desde texto.
     */
    @Test
    public void deberiaUsarEnumPais() throws DOPOSoccerException {
        assertTrue(Personaje.crear(Pais.NIGERIA) instanceof Nigeria);
        assertEquals(Pais.CAMERUN, Pais.desdeTexto("CAMERUN"));
        assertEquals("Estados Unidos", Pais.ESTADOS_UNIDOS.toString());
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> Pais.desdeTexto("BRASIL"));
        assertEquals(DOPOSoccerException.PAIS_INVALIDO, e.getMessage());
    }
}
