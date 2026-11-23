package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Objeto de acceso a datos (DAO) de DOPOSoccer: guarda y abre partidas en archivos de texto (.txt).
 * Implementa RepositorioPartida y hay una sola instancia (Singleton).
 * Cada línea del archivo es "clave=valor". Las líneas que empiezan con # son comentarios.
 * Errores: PersistenciaException si no se puede leer o escribir el archivo, y
 * FormatoArchivoException si el archivo está mal escrito (las dos se registran en el log).
 */
public final class PartidoDAO implements RepositorioPartida {

    /** Extensión de los archivos de partida. */
    public static final String EXTENSION = ".txt";
    /** Primera línea de todo archivo de partida; sirve para reconocerlo. */
    public static final String ENCABEZADO = "# Partida de DOPOSoccer";
    /** Valor que se escribe cuando el partido es jugador contra jugador. */
    public static final String SIN_MAQUINA = "NINGUNO";
    /** Clave interna donde se guarda el nombre del archivo leído (empieza con # para no chocar con el archivo). */
    private static final String CLAVE_ARCHIVO = "#archivo";

    /** Única instancia del DAO. */
    private static PartidoDAO instancia;

    /**
     * Constructor privado: se usa getInstance().
     */
    private PartidoDAO() { }

    /**
     * Retorna la única instancia del DAO; la crea la primera vez.
     * @return el DAO
     */
    public static PartidoDAO getInstance() {
        if (instancia == null) instancia = new PartidoDAO();
        return instancia;
    }

    /**
     * Guarda la partida: puntos y estadísticas de los dos jugadores y, si hay un partido sin
     * terminar, su modo, valor, países, perfil de la máquina, goles y tiempo jugado.
     * @param juego juego que se guarda
     * @param archivo archivo donde se escribe (si no termina en .txt se le agrega)
     * @throws DOPOSoccerException PersistenciaException si el archivo es null o no se puede escribir
     */
    @Override
    public void guardar(DOPOSoccer juego, File archivo) throws DOPOSoccerException {
        if (archivo == null) {
            throw new PersistenciaException("guardar", DOPOSoccerException.ARCHIVO_NO_GUARDADO, null);
        }
        File destino = conExtension(archivo);
        StringBuilder texto = new StringBuilder(ENCABEZADO + "\n");
        for (int numero = 1; numero <= 2; numero++) {
            texto.append("puntos").append(numero).append('=').append(juego.getPuntos(numero)).append('\n');
            texto.append("estadisticas").append(numero).append('=')
                 .append(aTexto(juego.getEstadisticas(numero))).append('\n');
        }
        Partido partido = juego.getPartido();
        boolean enCurso = partido != null && !partido.estaTerminado();
        texto.append("partido=").append(enCurso ? "si" : "no").append('\n');
        if (enCurso) {
            Maquina maquina = partido.getMaquina();
            texto.append("modo=").append(partido.getModo().name()).append('\n');
            texto.append("valor=").append(partido.getValor()).append('\n');
            texto.append("pais1=").append(partido.getJugador(1).getPais().name()).append('\n');
            texto.append("pais2=").append(partido.getJugador(2).getPais().name()).append('\n');
            texto.append("maquina=").append(maquina == null ? SIN_MAQUINA : maquina.getEstrategia().getPerfil().name()).append('\n');
            texto.append("goles1=").append(partido.getJugador(1).getGoles()).append('\n');
            texto.append("goles2=").append(partido.getJugador(2).getGoles()).append('\n');
            texto.append("tiempo=").append(partido.getTiempoJugado()).append('\n');
        }
        try (BufferedWriter salida = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            salida.write(texto.toString());
        } catch (IOException e) {
            throw new PersistenciaException("guardar", DOPOSoccerException.ARCHIVO_NO_GUARDADO, e);
        }
    }

    /**
     * Abre una partida guardada y la carga en el juego. Primero valida todo el archivo;
     * si algo está mal no cambia nada del juego.
     * @param juego juego donde se carga la partida
     * @param archivo archivo .txt que se lee
     * @throws DOPOSoccerException PersistenciaException si no se puede leer el archivo,
     *         FormatoArchivoException si su contenido no es válido
     */
    @Override
    public void abrir(DOPOSoccer juego, File archivo) throws DOPOSoccerException {
        Map<String, String> datos = leer(archivo);
        int[] puntos = {entero(datos, "puntos1"), entero(datos, "puntos2")};
        Estadisticas[] estadisticas = {estadisticas(datos, "estadisticas1"), estadisticas(datos, "estadisticas2")};
        if (puntos[0] < 0 || puntos[1] < 0) throw invalido(datos, "puntos negativos");
        Partido partido = null;
        if ("si".equals(valor(datos, "partido"))) {
            Partido.ModoJuego modo = Partido.ModoJuego.desdeTexto(valor(datos, "modo"));
            partido = modo.crearPartido(Pais.desdeTexto(valor(datos, "pais1")),
                Pais.desdeTexto(valor(datos, "pais2")), entero(datos, "valor"));
            String maquina = valor(datos, "maquina");
            if (!SIN_MAQUINA.equals(maquina)) {
                partido.ponerMaquina(PerfilMaquina.desdeTexto(maquina).crearEstrategia());
            }
            partido.restaurar(entero(datos, "goles1"), entero(datos, "goles2"), decimal(datos, "tiempo"));
        }
        juego.restaurar(puntos, estadisticas, partido);
    }

    /**
     * Lee todas las líneas "clave=valor" del archivo.
     * @param archivo archivo que se lee
     * @return mapa con cada clave y su valor
     * @throws DOPOSoccerException PersistenciaException si no se puede leer,
     *         FormatoArchivoException si no empieza con el encabezado o una línea no es "clave=valor"
     */
    private Map<String, String> leer(File archivo) throws DOPOSoccerException {
        if (archivo == null || !archivo.isFile()) {
            throw new PersistenciaException("abrir", DOPOSoccerException.ARCHIVO_NO_ABIERTO, null);
        }
        Map<String, String> datos = new HashMap<>();
        datos.put(CLAVE_ARCHIVO, archivo.getName());
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String primera = entrada.readLine();
            if (primera == null || !primera.trim().equals(ENCABEZADO)) {
                throw invalido(datos, "falta el encabezado");
            }
            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                int igual = linea.indexOf('=');
                if (igual <= 0) throw invalido(datos, "línea sin '=': " + linea);
                datos.put(linea.substring(0, igual).trim(), linea.substring(igual + 1).trim());
            }
        } catch (IOException e) {
            throw new PersistenciaException("abrir", DOPOSoccerException.ARCHIVO_NO_ABIERTO, e);
        }
        return datos;
    }

    /**
     * Retorna el valor de una clave obligatoria.
     * @param datos datos leídos del archivo
     * @param clave clave que se busca
     * @return el valor
     * @throws DOPOSoccerException si la clave no está
     */
    private String valor(Map<String, String> datos, String clave) throws DOPOSoccerException {
        String valor = datos.get(clave);
        if (valor == null) throw invalido(datos, "falta la clave " + clave);
        return valor;
    }

    /**
     * Retorna el valor entero de una clave.
     * @param datos datos leídos del archivo
     * @param clave clave que se busca
     * @return el número
     * @throws DOPOSoccerException si la clave no está o no es un número entero
     */
    private int entero(Map<String, String> datos, String clave) throws DOPOSoccerException {
        try {
            return Integer.parseInt(valor(datos, clave));
        } catch (NumberFormatException e) {
            throw invalido(datos, clave + " no es un número entero");
        }
    }

    /**
     * Retorna el valor decimal de una clave.
     * @param datos datos leídos del archivo
     * @param clave clave que se busca
     * @return el número
     * @throws DOPOSoccerException si la clave no está o no es un número
     */
    private double decimal(Map<String, String> datos, String clave) throws DOPOSoccerException {
        try {
            return Double.parseDouble(valor(datos, clave));
        } catch (NumberFormatException e) {
            throw invalido(datos, clave + " no es un número");
        }
    }

    /**
     * Convierte el texto "speed,kick,jump,survival" en estadísticas.
     * @param datos datos leídos del archivo
     * @param clave clave de las estadísticas
     * @return las estadísticas
     * @throws DOPOSoccerException si no hay cuatro números o alguno no está entre 1 y 10
     */
    private Estadisticas estadisticas(Map<String, String> datos, String clave) throws DOPOSoccerException {
        String[] partes = valor(datos, clave).split(",");
        if (partes.length != 4) throw invalido(datos, clave + " debe tener cuatro números");
        try {
            return new Estadisticas(Integer.parseInt(partes[0].trim()), Integer.parseInt(partes[1].trim()),
                Integer.parseInt(partes[2].trim()), Integer.parseInt(partes[3].trim()));
        } catch (NumberFormatException e) {
            throw invalido(datos, clave + " tiene un valor que no es número");
        }
    }

    /**
     * Crea el error de formato del archivo que se está leyendo.
     * @param datos datos leídos (de ahí sale el nombre del archivo)
     * @param razon explicación del error
     * @return la excepción (ya registrada en el log)
     */
    private FormatoArchivoException invalido(Map<String, String> datos, String razon) {
        return new FormatoArchivoException(datos.get(CLAVE_ARCHIVO), DOPOSoccerException.ARCHIVO_INVALIDO, razon);
    }

    /**
     * Convierte estadísticas en el texto "speed,kick,jump,survival".
     * @param e estadísticas
     * @return texto con los cuatro valores separados por comas
     */
    private String aTexto(Estadisticas e) {
        return e.getSpeed() + "," + e.getKick() + "," + e.getJump() + "," + e.getSurvival();
    }

    /**
     * Agrega la extensión .txt si el archivo no la tiene.
     * @param archivo archivo elegido
     * @return el archivo con extensión .txt
     */
    private File conExtension(File archivo) {
        if (archivo.getName().toLowerCase().endsWith(EXTENSION)) return archivo;
        return new File(archivo.getParentFile(), archivo.getName() + EXTENSION);
    }
}
