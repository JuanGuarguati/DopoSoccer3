package tests;

import domain.*;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Pruebas de la persistencia con PartidoDAO: guardar y abrir partidas en archivos .txt.
 */
public class PartidoDAOTest {

    /** Carpeta temporal que JUnit crea y borra en cada prueba. */
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
     * Escribe un archivo de texto en la carpeta temporal.
     * @param nombre nombre del archivo
     * @param contenido texto del archivo
     * @return el archivo
     * @throws IOException si no se puede escribir
     */
    private File escribir(String nombre, String contenido) throws IOException {
        File archivo = carpeta.newFile(nombre);
        try (FileWriter salida = new FileWriter(archivo)) {
            salida.write(contenido);
        }
        return archivo;
    }

    /**
     * Guardar y abrir deja los puntos, las estadísticas y el partido (con sus goles) como estaban.
     */
    @Test
    public void deberiaGuardarYAbrirLaPartida() throws Exception {
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_GOLES, 1);
        AyudantePruebas.anotarGol(juego, 1);
        juego.mejorarEstadistica(1, Estadistica.KICK);
        juego.iniciarPartido(Pais.COREA_DEL_SUR, Pais.ESTADOS_UNIDOS, Partido.ModoJuego.POR_TIEMPO, 3);
        AyudantePruebas.anotarGol(juego, 1);
        String tiempo = juego.getTextoTiempo();
        File archivo = new File(carpeta.getRoot(), "partida.txt");
        juego.guardar(archivo);

        juego.reiniciar();
        juego.abrir(archivo);
        assertEquals(75, juego.getPuntos(1));
        assertEquals(25, juego.getPuntos(2));
        assertEquals(2, juego.getEstadisticas(1).getKick());
        assertTrue(juego.hayPartido());
        assertTrue(juego.estaPausado());
        assertEquals(Pais.COREA_DEL_SUR.getNombre(), juego.getEstadoJugador(1).pais);
        assertEquals(1, juego.getEstadoJugador(1).goles);
        assertFalse(juego.esContraMaquina());
        assertEquals(tiempo, juego.getTextoTiempo());
    }

    /**
     * Guardar y abrir un partido contra la máquina conserva la máquina y su perfil.
     */
    @Test
    public void deberiaGuardarYAbrirLaMaquina() throws Exception {
        juego.iniciarPartido(Pais.NIGERIA, Pais.CAMERUN, Partido.ModoJuego.POR_TIEMPO, 2,
            PerfilMaquina.DEFENSIVA);
        AyudantePruebas.avanzar(juego, 5);
        String tiempo = juego.getTextoTiempo();
        File archivo = new File(carpeta.getRoot(), "maquina.txt");
        juego.guardar(archivo);

        juego.reiniciar();
        juego.abrir(archivo);
        assertTrue(juego.esContraMaquina());
        assertEquals(PerfilMaquina.DEFENSIVA, juego.getPerfilMaquina());
        assertTrue(juego.getEstadoJugador(2).maquina);
        assertEquals(tiempo, juego.getTextoTiempo());
    }

    /**
     * Si no hay partido en curso solo se guardan los puntos y las estadísticas.
     */
    @Test
    public void deberiaGuardarSinPartido() throws Exception {
        File archivo = new File(carpeta.getRoot(), "solo_puntos.txt");
        juego.guardar(archivo);
        juego.abrir(archivo);
        assertFalse(juego.hayPartido());
        assertEquals(0, juego.getPuntos(1));
    }

    /**
     * Si el nombre no termina en .txt, se le agrega la extensión.
     */
    @Test
    public void deberiaAgregarExtensionTxt() throws DOPOSoccerException {
        juego.guardar(new File(carpeta.getRoot(), "sin_extension"));
        assertTrue(new File(carpeta.getRoot(), "sin_extension.txt").isFile());
    }

    /**
     * Abrir un archivo que no existe lanza DOPOSoccerException.
     */
    @Test
    public void noDeberiaAbrirArchivoInexistente() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.abrir(new File(carpeta.getRoot(), "no_existe.txt")));
        assertEquals(DOPOSoccerException.ARCHIVO_NO_ABIERTO, e.getMessage());
    }

    /**
     * Un archivo que no es de DOPOSoccer se rechaza y el juego no cambia.
     */
    @Test
    public void noDeberiaAbrirArchivoAjeno() throws Exception {
        File archivo = escribir("ajeno.txt", "hola\nmundo\n");
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.abrir(archivo));
        assertEquals(DOPOSoccerException.ARCHIVO_INVALIDO, e.getMessage());
        assertFalse(juego.hayPartido());
    }

    /**
     * Un archivo con estadísticas fuera de rango se rechaza.
     */
    @Test
    public void noDeberiaAbrirEstadisticasDaniadas() throws Exception {
        File archivo = escribir("daniado.txt", PartidoDAO.ENCABEZADO + "\npuntos1=0\npuntos2=0\n"
            + "estadisticas1=1,1,1,99\nestadisticas2=1,1,1,1\npartido=no\n");
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> juego.abrir(archivo));
        assertEquals(DOPOSoccerException.ESTADISTICA_INVALIDA, e.getMessage());
    }

    /**
     * Un archivo mal escrito lanza FormatoArchivoException (subclase de DOPOSoccerException)
     * con el nombre del archivo y la razón.
     */
    @Test
    public void deberiaInformarFormatoInvalido() throws Exception {
        File archivo = escribir("sin_clave.txt", PartidoDAO.ENCABEZADO + "\npuntos1=0\n");
        FormatoArchivoException e = assertThrows(FormatoArchivoException.class, () -> juego.abrir(archivo));
        assertEquals(DOPOSoccerException.ARCHIVO_INVALIDO, e.getMessage());
        assertEquals("sin_clave.txt", e.getArchivo());
        assertTrue(e.getRazon().contains("puntos2"));
    }

    /**
     * El DAO es un Singleton y la fachada lo usa por la interfaz RepositorioPartida.
     */
    @Test
    public void deberiaSerUnSoloDAO() {
        assertSame(PartidoDAO.getInstance(), PartidoDAO.getInstance());
        assertTrue(PartidoDAO.getInstance() instanceof RepositorioPartida);
    }

    /**
     * Si no se puede escribir el archivo (la carpeta no existe), lanza DOPOSoccerException.
     */
    @Test
    public void noDeberiaGuardarEnCarpetaInexistente() {
        PersistenciaException e = assertThrows(PersistenciaException.class,
            () -> juego.guardar(new File(carpeta.getRoot(), "no/existe/partida.txt")));
        assertEquals(DOPOSoccerException.ARCHIVO_NO_GUARDADO, e.getMessage());
        assertEquals("guardar", e.getOperacion());
    }
}
