package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas del partido y de la fachada DOPOSoccer.
 */
public class PartidoTest {

    /**
     * Retorna el juego (singleton) como nuevo, para que cada prueba empiece igual.
     * @return el juego sin partido, con 0 puntos y estadísticas en 1
     */
    private DOPOSoccer nuevoJuego() {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.reiniciar();
        return juego;
    }

    /**
     * El partido empieza 0 - 0 y sin ganador.
     */
    @Test
    public void deberiaIniciarCeroACero() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        assertEquals(0, juego.getEstadoJugador(1).goles);
        assertEquals(0, juego.getEstadoJugador(2).goles);
        assertEquals(0, juego.getNumeroGanador());
        assertFalse(juego.estaTerminado());
    }

    /**
     * En partido a 3 goles, quien llegue a 3 gana y recibe 100 puntos; el otro 25.
     */
    @Test
    public void deberiaGanarPorGolesYRepartirPuntos() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Pais.COREA_DEL_SUR, Pais.ESTADOS_UNIDOS, 3);
        for (int i = 0; i < 3; i++) AyudantePruebas.meterEnArco(p, p.getCancha().getArcoDerecho());
        assertTrue(p.estaTerminado());
        assertSame(p.getJugador(1), p.getGanador());
        assertEquals(100, p.getJugador(1).getPuntos());
        assertEquals(25, p.getJugador(2).getPuntos());
    }

    /**
     * Si el tiempo se acaba con empate, entra en muerte súbita y el siguiente gol gana.
     */
    @Test
    public void deberiaEntrarEnMuerteSubita() throws DOPOSoccerException {
        PartidoPorTiempo p = new PartidoPorTiempo(Pais.CAMERUN, Pais.NIGERIA, 1);
        AyudantePruebas.avanzar(p, 61);
        assertFalse(p.estaTerminado());
        assertTrue(p.estaEnMuerteSubita());
        assertFalse(p.hayGanador());
        assertTrue(p.estaEnMuerteSubita());
        AyudantePruebas.meterEnArco(p, p.getCancha().getArcoIzquierdo());
        assertTrue(p.estaTerminado());
        assertSame(p.getJugador(2), p.getGanador());
    }

    /**
     * Un balón dentro del arco derecho es gol del jugador 1 y todo vuelve al centro.
     */
    @Test
    public void deberiaContarGolYReiniciarPosiciones() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 5);
        Cancha c = p.getCancha();
        AyudantePruebas.meterEnArco(p, c.getArcoDerecho());
        assertEquals(1, p.getJugador(1).getGoles());
        assertEquals(c.getAncho() / 2.0, p.getBalon().getX(), 0.001);
    }

    /**
     * En pausa el tiempo no avanza.
     */
    @Test
    public void noDeberiaAvanzarEnPausa() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_TIEMPO, 2);
        juego.pausar();
        juego.actualizar(5);
        assertEquals("02:00", juego.getTextoTiempo());
    }

    /**
     * No se puede iniciar un partido con 0 goles.
     */
    @Test
    public void deberiaLanzarExcepcionConCeroGoles() {
        DOPOSoccer juego = nuevoJuego();
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class,
            () -> juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 0));
        assertEquals(DOPOSoccerException.VALOR_INVALIDO, e.getMessage());
    }

    /**
     * No se puede iniciar un partido sin país.
     */
    @Test
    public void deberiaLanzarExcepcionConPaisInvalido() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> nuevoJuego().iniciarPartido(null, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3));
        assertEquals(DOPOSoccerException.PAIS_INVALIDO, e.getMessage());
    }

    /**
     * No se puede mover un jugador que no existe.
     */
    @Test
    public void deberiaLanzarExcepcionConJugadorInvalido() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class,
            () -> juego.moverJugador(3, Direccion.DERECHA));
        assertEquals(DOPOSoccerException.JUGADOR_INVALIDO, e.getMessage());
    }

    /**
     * No se puede mover sin dirección.
     */
    @Test
    public void deberiaLanzarExcepcionConDireccionInvalida() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.moverJugador(1, null));
        assertEquals(DOPOSoccerException.DIRECCION_INVALIDA, e.getMessage());
    }

    /**
     * Sin partido en curso no se puede jugar.
     */
    @Test
    public void deberiaLanzarExcepcionSinPartido() {
        DOPOSoccer juego = nuevoJuego();
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.patearJugador(1));
        assertEquals(DOPOSoccerException.PARTIDO_TERMINADO, e.getMessage());
    }

    /**
     * En pausa los jugadores no se mueven, no saltan y no patean (el rival no pierde vida).
     */
    @Test
    public void noDeberiaPermitirAccionesEnPausa() throws DOPOSoccerException {
        Partido p = new PartidoPorTiempo(Pais.CAMERUN, Pais.NIGERIA, 2);
        Jugador j1 = p.getJugador(1);
        Jugador j2 = p.getJugador(2);
        while (j2.getX() - j1.getX() > 60) p.moverJugador(2, Direccion.IZQUIERDA);
        assertTrue(j1.alcanzaRival(j2));
        double x = j1.getX();
        double y = j1.getY();
        double vida = j2.getVida();
        p.pausar();
        p.moverJugador(1, Direccion.DERECHA);
        p.saltarJugador(1);
        p.patearJugador(1);
        p.pausar();
        p.actualizar(0.016);
        assertEquals(x, j1.getX(), 0.001);
        assertEquals(y, j1.getY(), 0.001);
        assertEquals(vida, j2.getVida(), 0.001);
    }

    /**
     * Los puntos de los jugadores se mantienen de un partido al siguiente.
     */
    @Test
    public void deberiaMantenerPuntosEntrePartidos() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.COREA_DEL_SUR, Pais.CAMERUN, Partido.ModoJuego.POR_GOLES, 1);
        AyudantePruebas.anotarGol(juego, 2);
        assertEquals(25, juego.getPuntos(1));
        assertEquals(100, juego.getPuntos(2));
        juego.iniciarPartido(Pais.NIGERIA, Pais.ESTADOS_UNIDOS, Partido.ModoJuego.POR_GOLES, 1);
        assertEquals(25, juego.getPuntos(1));
        assertEquals(100, juego.getPuntos(2));
        AyudantePruebas.anotarGol(juego, 1);
        assertEquals(125, juego.getPuntos(1));
        assertEquals(125, juego.getPuntos(2));
    }

    /**
     * No se puede iniciar un partido sin modo de juego.
     */
    @Test
    public void deberiaLanzarExcepcionSinModo() {
        DOPOSoccer juego = nuevoJuego();
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class,
            () -> juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, null, 3));
        assertEquals(DOPOSoccerException.VALOR_INVALIDO, e.getMessage());
    }

    /**
     * Contra la máquina hace falta un perfil.
     */
    @Test
    public void deberiaPedirPerfilContraLaMaquina() {
        DOPOSoccer juego = nuevoJuego();
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class,
            () -> juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3,
                Modalidad.JUGADOR_VS_MAQUINA, null));
        assertEquals(DOPOSoccerException.PERFIL_INVALIDO, e.getMessage());
    }

    /**
     * La modalidad (enum del dominio) dice si el jugador 2 es la máquina.
     */
    @Test
    public void deberiaSaberLaModalidad() throws DOPOSoccerException {
        DOPOSoccer juego = nuevoJuego();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3,
            Modalidad.JUGADOR_VS_MAQUINA, PerfilMaquina.TORPE);
        assertEquals(Modalidad.JUGADOR_VS_MAQUINA, juego.getModalidad());
        assertTrue(juego.getEstadoJugador(2).maquina);
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        assertEquals(Modalidad.JUGADOR_VS_JUGADOR, juego.getModalidad());
    }

    /**
     * Cada modo de juego (enum ModoJuego) crea su tipo de partido.
     */
    @Test
    public void deberiaCrearPartidoSegunModo() throws DOPOSoccerException {
        Partido t = Partido.ModoJuego.POR_TIEMPO.crearPartido(Pais.CAMERUN, Pais.NIGERIA, 2);
        Partido g = Partido.ModoJuego.POR_GOLES.crearPartido(Pais.CAMERUN, Pais.NIGERIA, 4);
        assertTrue(t instanceof PartidoPorTiempo);
        assertTrue(g instanceof PartidoPorGoles);
        assertEquals(Partido.ModoJuego.POR_TIEMPO, t.getModo());
        assertEquals(2, t.getValor());
        assertEquals(4, g.getValor());
    }
}
