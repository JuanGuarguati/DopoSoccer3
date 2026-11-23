package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de las interfaces ControlPartido, Golpeable y Mejorable.
 */
public class InterfacesTest {

    private DOPOSoccer juego;

    /**
     * Deja el juego sin partido y con los puntos y estadísticas en cero antes de cada prueba.
     */
    @Before
    public void setUp() {
        juego = DOPOSoccer.getInstance();
        juego.reiniciar();
    }

    /**
     * La fachada y el partido se controlan con las mismas llamadas de ControlPartido.
     * @throws DOPOSoccerException si falla el inicio del partido
     */
    @Test
    public void deberiaControlarFachadaYPartidoIgual() throws DOPOSoccerException {
        juego.iniciarPartido(Pais.NIGERIA, Pais.CAMERUN, Partido.ModoJuego.POR_GOLES, 3);
        ControlPartido fachada = juego;
        ControlPartido partido = new PartidoPorGoles(Pais.NIGERIA, Pais.CAMERUN, 3);
        for (ControlPartido control : new ControlPartido[] {fachada, partido}) {
            control.pausar();
            assertTrue(control.estaPausado());
            control.pausar();
            assertFalse(control.estaPausado());
            control.terminar();
            assertTrue(control.estaTerminado());
        }
    }

    /**
     * La fachada valida que haya partido antes de delegar; el partido no lo necesita.
     */
    @Test
    public void deberiaFallarFachadaSinPartido() {
        ControlPartido control = juego;
        try {
            control.pausar();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.PARTIDO_TERMINADO, e.getMessage());
        }
    }

    /**
     * Un Golpeable pierde vida, nunca baja de cero y queda aturdido al llegar a cero.
     */
    @Test
    public void deberiaQuedarAturdidoSinVida() {
        Golpeable golpeable = new Jugador(1, new Camerun(), 200, 600);
        double vidaMax = golpeable.getVidaMax();
        golpeable.recibirDanio(10);
        assertEquals(vidaMax - 10, golpeable.getVida(), 0.001);
        assertFalse(golpeable.estaAturdido());
        golpeable.recibirDanio(vidaMax * 2);
        assertEquals(0, golpeable.getVida(), 0.001);
        assertTrue(golpeable.estaAturdido());
    }

    /**
     * Un Mejorable sube una estadística y cuenta las mejoras compradas.
     * @throws DOPOSoccerException si la estadística ya está en el máximo
     */
    @Test
    public void deberiaContarMejorasDeUnMejorable() throws DOPOSoccerException {
        Mejorable mejorable = new Estadisticas();
        mejorable.mejorar(Estadistica.SPEED);
        mejorable.mejorar(Estadistica.SPEED);
        mejorable.mejorar(Estadistica.KICK);
        assertEquals(3, mejorable.getValor(Estadistica.SPEED));
        assertEquals(2, mejorable.getValor(Estadistica.KICK));
        assertEquals(3, mejorable.getMejorasCompradas());
    }
}
