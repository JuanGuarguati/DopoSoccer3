package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de lo agregado en el diseño corregido: singleton, compra de mejoras,
 * separación de jugadores, tiempos de patada y golpe, y texto del tiempo.
 */
public class MejorasTest {

    private DOPOSoccer juego;

    /**
     * Deja el juego como nuevo antes de cada prueba.
     */
    @Before
    public void setUp() {
        juego = DOPOSoccer.getInstance();
        juego.reiniciar();
    }

    /**
     * Gana un partido a 1 gol con el jugador indicado, jugando de verdad (100 puntos para él, 25 para el otro).
     * @param ganador número del jugador que gana
     * @throws DOPOSoccerException si no se puede jugar
     */
    private void ganarPartido(int ganador) throws DOPOSoccerException {
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 1);
        AyudantePruebas.anotarGol(juego, ganador);
        assertTrue(juego.estaTerminado());
    }

    /**
     * Siempre se obtiene la misma instancia del juego.
     */
    @Test
    public void deberiaSerUnaSolaInstancia() {
        assertSame(DOPOSoccer.getInstance(), DOPOSoccer.getInstance());
    }

    /**
     * Sin puntos no se puede comprar una mejora.
     */
    @Test
    public void noDeberiaMejorarSinPuntos() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.mejorarEstadistica(1, Estadistica.SPEED));
        assertEquals(DOPOSoccerException.PUNTOS_INSUFICIENTES, e.getMessage());
    }

    /**
     * Cada mejora cuesta PRECIO_INICIAL por las mejoras compradas más una, y se descuenta de los puntos.
     */
    @Test
    public void deberiaCobrarCadaMejoraMasCara() throws DOPOSoccerException {
        ganarPartido(1);
        assertEquals(100, juego.getPuntos(1));
        assertEquals(25, juego.getPrecioMejora(1));
        juego.mejorarEstadistica(1, Estadistica.KICK);
        assertEquals(75, juego.getPuntos(1));
        assertEquals(2, juego.getEstadisticas(1).getKick());
        assertEquals(50, juego.getPrecioMejora(1));
        juego.mejorarEstadistica(1, Estadistica.SURVIVAL);
        assertEquals(25, juego.getPuntos(1));
    }

    /**
     * No se pueden comprar mejoras mientras el partido está en curso.
     */
    @Test
    public void noDeberiaMejorarConPartidoEnCurso() throws DOPOSoccerException {
        ganarPartido(1);
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.mejorarEstadistica(1, Estadistica.SPEED));
        assertEquals(DOPOSoccerException.PARTIDO_EN_CURSO, e.getMessage());
    }

    /**
     * Una compra que falla no cambia nada: el partido terminado sigue ahí y los puntos no se pierden.
     */
    @Test
    public void unaCompraFallidaNoDeberiaCambiarNada() throws DOPOSoccerException {
        ganarPartido(2);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class,
            () -> juego.mejorarEstadistica(2, null));
        assertEquals(DOPOSoccerException.ESTADISTICA_MAXIMA, e.getMessage());
        assertTrue(juego.hayPartido());
        assertEquals(100, juego.getPuntos(2));
    }

    /**
     * La fachada entrega una copia de las estadísticas: cambiarla no regala mejoras.
     */
    @Test
    public void noDeberiaMejorarCambiandoLaCopia() throws DOPOSoccerException {
        juego.getEstadisticas(1).mejorar(Estadistica.SPEED);
        assertEquals(1, juego.getEstadisticas(1).getSpeed());
    }

    /**
     * Las mejoras compradas se usan en el siguiente partido (survival 2 da vida 110).
     */
    @Test
    public void deberiaUsarLasMejorasEnElSiguientePartido() throws DOPOSoccerException {
        ganarPartido(2);
        juego.mejorarEstadistica(2, Estadistica.SURVIVAL);
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        assertEquals(110, juego.getEstadoJugador(2).vida, 0.001);
        assertEquals(100, juego.getEstadoJugador(1).vida, 0.001);
        assertEquals(75, juego.getPuntos(2));
    }

    /**
     * Si los jugadores quedan uno encima del otro, el partido los separa.
     */
    @Test
    public void deberiaSepararJugadoresQueSePisan() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3);
        Jugador j1 = p.getJugador(1);
        Jugador j2 = p.getJugador(2);
        j1.reiniciar(600, j1.getY());
        j2.reiniciar(610, j2.getY());
        p.actualizar(0.016);
        assertEquals(Jugador.RADIO * 2, j2.getX() - j1.getX(), 0.001);
        assertTrue(j1.getX() < j2.getX());
    }

    /**
     * Patear deja ver la patada un momento y luego se apaga.
     */
    @Test
    public void deberiaMostrarLaPatadaUnMomento() {
        Cancha cancha = new Cancha(1280, 720);
        Jugador j1 = new Jugador(1, new Camerun(), 400, cancha.getNivelPiso() - 50);
        Jugador j2 = new Jugador(2, new Nigeria(), 900, cancha.getNivelPiso() - 50);
        j1.patear(new Balon(1000, 100), j2);
        assertTrue(j1.estaPateando());
        j1.mover(Jugador.DURACION_PATADA + 0.01, cancha);
        assertFalse(j1.estaPateando());
    }

    /**
     * Un golpe que no deja la vida en 0 se muestra pero no aturde.
     */
    @Test
    public void deberiaMarcarGolpeSinAturdir() {
        Jugador j = new Jugador(1, new Camerun(), 400, 600);
        j.recibirDanio(10);
        assertTrue(j.estaGolpeado());
        assertFalse(j.estaAturdido());
        assertEquals(Jugador.DURACION_GOLPE, j.getTiempoGolpe(), 0.001);
    }

    /**
     * Personaje.crear devuelve el personaje del país, y falla si el país es null.
     */
    @Test
    public void deberiaCrearPersonajePorPais() throws DOPOSoccerException {
        assertEquals(Pais.COREA_DEL_SUR, Personaje.crear(Pais.COREA_DEL_SUR).getPais());
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> Personaje.crear(null));
        assertEquals(DOPOSoccerException.PAIS_INVALIDO, e.getMessage());
    }

    /**
     * El texto del tiempo depende del tipo de partido.
     */
    @Test
    public void deberiaArmarTextoDelTiempo() throws DOPOSoccerException {
        assertEquals("01:00", new PartidoPorTiempo(Pais.CAMERUN, Pais.NIGERIA, 1).getTextoTiempo());
        assertEquals("A 3 goles - 00:00", new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3).getTextoTiempo());
    }
}
