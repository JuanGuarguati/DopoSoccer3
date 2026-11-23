package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas del patrón Strategy de la máquina: cada estrategia, el cambio de estrategia
 * en pleno partido y que la máquina juegue sola.
 */
public class EstrategiaTest {

    private Partido partido;
    private Maquina maquina;

    /**
     * Crea un partido a 3 goles donde el jugador 2 es una máquina ofensiva.
     * @throws DOPOSoccerException si no se puede crear el partido
     */
    @Before
    public void setUp() throws DOPOSoccerException {
        partido = new PartidoPorGoles(Pais.CAMERUN, Pais.NIGERIA, 3);
        partido.ponerMaquina(new MaquinaOfensiva());
        maquina = partido.getMaquina();
    }

    /**
     * Pone el balón quieto en una posición.
     * @param x posición horizontal
     * @param y posición vertical
     */
    private void balonEn(double x, double y) {
        partido.getBalon().reiniciar(x, y);
    }

    /**
     * El jugador 2 pasa a ser una máquina con el mismo personaje y la misma posición.
     */
    @Test
    public void deberiaPonerMaquinaComoJugador2() throws DOPOSoccerException {
        assertTrue(partido.getJugador(2).esMaquina());
        assertFalse(partido.getJugador(1).esMaquina());
        assertEquals(Pais.NIGERIA, maquina.getPais());
        assertEquals(PerfilMaquina.OFENSIVA, maquina.getEstrategia().getPerfil());
    }

    /**
     * La ofensiva va hacia el balón y, si lo toca mirando al arco rival, patea hacia ese arco.
     */
    @Test
    public void ofensivaDeberiaBuscarYPatearElBalon() {
        balonEn(640, 100);
        assertEquals(Accion.MOVER_IZQUIERDA, maquina.getEstrategia().decidirAccion(maquina, partido));
        balonEn(maquina.getX() - 40, maquina.getY() + Jugador.RADIO);
        assertEquals(Accion.PATEAR, maquina.getEstrategia().decidirAccion(maquina, partido));
        maquina.jugar(partido);
        assertTrue(partido.getBalon().getVelocidadX() < 0);
    }

    /**
     * La defensiva vuelve a su arco si el balón está lejos y sale a despejar si entra a su mitad.
     */
    @Test
    public void defensivaDeberiaCuidarSuArco() {
        MaquinaDefensiva defensiva = new MaquinaDefensiva();
        balonEn(300, 100);
        assertEquals(Accion.MOVER_DERECHA, defensiva.decidirAccion(maquina, partido));
        balonEn(900, 100);
        assertEquals(Accion.MOVER_IZQUIERDA, defensiva.decidirAccion(maquina, partido));
    }

    /**
     * La experta patea al rival cuando lo tiene cerca y el balón está lejos.
     */
    @Test
    public void expertaDeberiaPatearAlRivalCercano() throws DOPOSoccerException {
        maquina.setEstrategia(new MaquinaExperta());
        Jugador rival = partido.getJugador(1);
        rival.reiniciar(maquina.getX() - 60, maquina.getY());
        balonEn(300, 100);
        assertEquals(Accion.PATEAR, maquina.getEstrategia().decidirAccion(maquina, partido));
        maquina.jugar(partido);
        assertTrue(rival.getVida() < rival.getVidaMax());
    }

    /**
     * La equilibrada (nueva) ataca cuando va empatando y defiende cuando va ganando.
     */
    @Test
    public void equilibradaDeberiaDependerDelMarcador() {
        MaquinaEquilibrada equilibrada = new MaquinaEquilibrada();
        balonEn(300, 100);
        assertFalse(equilibrada.vaGanando(maquina, partido));
        assertEquals(Accion.MOVER_IZQUIERDA, equilibrada.decidirAccion(maquina, partido));
        AyudantePruebas.meterEnArco(partido, partido.getCancha().getArcoIzquierdo());
        balonEn(300, 100);
        assertTrue(equilibrada.vaGanando(maquina, partido));
        assertEquals(Accion.MOVER_DERECHA, equilibrada.decidirAccion(maquina, partido));
    }

    /**
     * La torpe escoge acciones al azar; con la misma semilla escoge las mismas.
     */
    @Test
    public void torpeDeberiaEscogerAlAzar() {
        MaquinaTorpe a = new MaquinaTorpe(7);
        MaquinaTorpe b = new MaquinaTorpe(7);
        for (int i = 0; i < 30; i++) {
            Accion accion = a.decidirAccion(maquina, partido);
            assertNotNull(accion);
            assertEquals(accion, b.decidirAccion(maquina, partido));
        }
    }

    /**
     * Cada perfil del enum PerfilMaquina crea la estrategia que le corresponde.
     */
    @Test
    public void cadaPerfilDeberiaCrearSuEstrategia() {
        for (PerfilMaquina perfil : PerfilMaquina.values()) {
            assertEquals(perfil, perfil.crearEstrategia().getPerfil());
        }
        assertTrue(PerfilMaquina.EQUILIBRADA.crearEstrategia() instanceof MaquinaEquilibrada);
    }

    /**
     * La máquina juega sola cuando el partido se actualiza.
     */
    @Test
    public void maquinaDeberiaJugarSolaAlActualizar() {
        double x = maquina.getX();
        for (int i = 0; i < 10; i++) partido.actualizar(0.016);
        assertTrue(maquina.getX() < x);
    }

    /**
     * Desde la fachada se puede cambiar la estrategia de la máquina en pleno partido.
     */
    @Test
    public void deberiaCambiarEstrategiaEnEjecucion() throws DOPOSoccerException {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.reiniciar();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3, PerfilMaquina.TORPE);
        assertTrue(juego.esContraMaquina());
        assertEquals(PerfilMaquina.TORPE, juego.getPerfilMaquina());
        juego.cambiarEstrategia(PerfilMaquina.EXPERTA);
        assertEquals(PerfilMaquina.EXPERTA, juego.getPerfilMaquina());
    }

    /**
     * No se puede cambiar la estrategia si el partido es jugador contra jugador.
     */
    @Test
    public void noDeberiaCambiarEstrategiaSinMaquina() throws DOPOSoccerException {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.reiniciar();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.cambiarEstrategia(PerfilMaquina.OFENSIVA));
        assertEquals(DOPOSoccerException.NO_HAY_MAQUINA, e.getMessage());
    }
}
