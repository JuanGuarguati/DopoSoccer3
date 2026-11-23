package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de las estadísticas del jugador.
 */
public class EstadisticasTest {

    private Estadisticas estadisticas;

    /**
     * Crea estadísticas nuevas antes de cada prueba.
     */
    @Before
    public void setUp() {
        estadisticas = new Estadisticas();
    }

    /**
     * Con survival 1 la vida máxima es 100.
     */
    @Test
    public void deberiaIniciarConVida100() {
        assertEquals(100, estadisticas.getVidaMax(), 0.001);
    }

    /**
     * Con survival 3 la vida máxima es 120 (ejemplo del enunciado).
     */
    @Test
    public void deberiaCalcularVidaConSurvival3() throws DOPOSoccerException {
        estadisticas.mejorar(Estadistica.SURVIVAL);
        estadisticas.mejorar(Estadistica.SURVIVAL);
        assertEquals(120, estadisticas.getVidaMax(), 0.001);
    }

    /**
     * Con kick 5 el multiplicador es 1.4 (ejemplo del enunciado).
     */
    @Test
    public void deberiaCalcularMultiplicadorKick5() throws DOPOSoccerException {
        for (int i = 0; i < 4; i++) estadisticas.mejorar(Estadistica.KICK);
        assertEquals(1.4, estadisticas.getMultiplicadorKick(), 0.001);
    }

    /**
     * Una estadística no puede pasar de 10.
     */
    @Test
    public void noDeberiaMejorarPasandoDe10() throws DOPOSoccerException {
        for (int i = 0; i < 9; i++) estadisticas.mejorar(Estadistica.SPEED);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> estadisticas.mejorar(Estadistica.SPEED));
        assertEquals(DOPOSoccerException.ESTADISTICA_MAXIMA, e.getMessage());
        assertEquals(10, estadisticas.getSpeed());
    }

    /**
     * No se puede mejorar una estadística nula.
     */
    @Test(expected = DOPOSoccerException.class)
    public void noDeberiaMejorarEstadisticaNula() throws DOPOSoccerException {
        estadisticas.mejorar(null);
    }

    /**
     * getValor retorna el valor de cada estadística del enum Estadistica.
     */
    @Test
    public void deberiaConsultarValorPorEnum() throws DOPOSoccerException {
        estadisticas.mejorar(Estadistica.JUMP);
        assertEquals(2, estadisticas.getValor(Estadistica.JUMP));
        assertEquals(1, estadisticas.getValor(Estadistica.KICK));
        assertEquals(1, estadisticas.getMejorasCompradas());
    }

    /**
     * No se pueden crear estadísticas con valores fuera de 1 a 10 (por ejemplo, al abrir un archivo dañado).
     */
    @Test
    public void noDeberiaCrearEstadisticasFueraDeRango() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> new Estadisticas(1, 11, 1, 1));
        assertEquals(DOPOSoccerException.ESTADISTICA_INVALIDA, e.getMessage());
    }
}
