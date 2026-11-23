package tests;

import domain.*;
import dto.ComandoDibujo;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Pruebas de aceptación: recorren casos completos usando solo la fachada DOPOSoccer,
 * como lo haría la interfaz gráfica.
 */
public class DOPOSoccerAceptacionTest {

    /** Carpeta temporal para los archivos de partida. */
    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

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
     * Un partido a un gol: el jugador 2 anota jugando, gana y recibe los puntos;
     * luego compra una mejora y la usa en el siguiente partido.
     */
    @Test
    public void deberiaJugarGanarYComprarUnaMejora() throws DOPOSoccerException {
        juego.iniciarPartido(Pais.NIGERIA, Pais.ESTADOS_UNIDOS, Partido.ModoJuego.POR_GOLES, 1);
        AyudantePruebas.anotarGol(juego, 2);
        assertTrue(juego.estaTerminado());
        assertEquals(2, juego.getNumeroGanador());
        assertEquals(ConstantesJuego.PUNTOS_PERDEDOR, juego.getPuntos(1));
        assertEquals(ConstantesJuego.PUNTOS_GANADOR, juego.getPuntos(2));

        int precio = juego.getPrecioMejora(2);
        juego.mejorarEstadistica(2, Estadistica.SPEED);
        assertEquals(ConstantesJuego.PUNTOS_GANADOR - precio, juego.getPuntos(2));
        assertEquals(2, juego.getEstadisticas(2).getSpeed());

        juego.iniciarPartido(Pais.NIGERIA, Pais.ESTADOS_UNIDOS, Partido.ModoJuego.POR_GOLES, 1);
        assertFalse(juego.estaTerminado());
        assertEquals(0, juego.getEstadoJugador(2).goles);
    }

    /**
     * Contra la máquina: la máquina se mueve sola, se le cambia la estrategia
     * en pleno partido y sigue jugando.
     */
    @Test
    public void deberiaJugarContraLaMaquinaYCambiarSuEstrategia() throws DOPOSoccerException {
        juego.iniciarPartido(Pais.CAMERUN, Pais.COREA_DEL_SUR, Partido.ModoJuego.POR_TIEMPO, 2,
            PerfilMaquina.OFENSIVA);
        assertTrue(juego.esContraMaquina());
        int antes = AyudantePruebas.buscar(juego, ComandoDibujo.Forma.JUGADOR, 2).x;
        AyudantePruebas.avanzar(juego, 2);
        assertNotEquals(antes, AyudantePruebas.buscar(juego, ComandoDibujo.Forma.JUGADOR, 2).x);

        juego.cambiarEstrategia(PerfilMaquina.DEFENSIVA);
        assertEquals(PerfilMaquina.DEFENSIVA, juego.getPerfilMaquina());
        AyudantePruebas.avanzar(juego, 1);
        assertFalse(juego.estaTerminado());
    }

    /**
     * En pausa nada se mueve; al reanudar el balón sigue cayendo.
     */
    @Test
    public void deberiaPausarYReanudar() throws DOPOSoccerException {
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_TIEMPO, 2);
        juego.pausar();
        assertTrue(juego.estaPausado());
        int y = AyudantePruebas.buscar(juego, ComandoDibujo.Forma.BALON, 0).y;
        String tiempo = juego.getTextoTiempo();
        AyudantePruebas.avanzar(juego, 1);
        assertEquals(y, AyudantePruebas.buscar(juego, ComandoDibujo.Forma.BALON, 0).y);
        assertEquals(tiempo, juego.getTextoTiempo());

        juego.pausar();
        assertFalse(juego.estaPausado());
        AyudantePruebas.avanzar(juego, 0.2);
        assertTrue(AyudantePruebas.buscar(juego, ComandoDibujo.Forma.BALON, 0).y > y);
    }

    /**
     * Se guarda un partido contra la máquina, se cierra el juego y al abrirlo
     * sigue en pausa, con la misma máquina, y se puede seguir jugando.
     */
    @Test
    public void deberiaGuardarAbrirYSeguirJugando() throws Exception {
        juego.iniciarPartido(Pais.ESTADOS_UNIDOS, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3,
            PerfilMaquina.EXPERTA);
        AyudantePruebas.avanzar(juego, 1);
        File archivo = new File(carpeta.getRoot(), "aceptacion.txt");
        juego.guardar(archivo);

        juego.reiniciar();
        assertFalse(juego.hayPartido());
        juego.abrir(archivo);
        assertTrue(juego.estaPausado());
        assertEquals(PerfilMaquina.EXPERTA, juego.getPerfilMaquina());
        juego.pausar();
        AyudantePruebas.avanzar(juego, 1);
        assertFalse(juego.estaPausado());
    }

    /**
     * Terminar a mano cierra el partido y ya no se puede mover a nadie.
     */
    @Test
    public void deberiaTerminarElPartidoAMano() throws DOPOSoccerException {
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 3);
        juego.terminar();
        assertTrue(juego.estaTerminado());
        assertThrows(DOPOSoccerException.class, () -> juego.moverJugador(1, Direccion.DERECHA));
    }
}
