package domain;

import dto.ComandoDibujo;
import dto.EstadoJugador;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fachada del dominio de DOPOSoccer. Es la única clase que conoce la interfaz gráfica.
 * Valida las peticiones y las delega al partido. Hay una sola instancia (singleton),
 * que guarda los puntos y las estadísticas de los dos jugadores entre partidos.
 * Nunca entrega objetos del dominio que se puedan cambiar: entrega DTO (ComandoDibujo,
 * EstadoJugador), copias (Estadisticas) o valores, para que no se salten las reglas del juego.
 * Implementa ControlPartido: ofrece las mismas acciones del partido, pero validando primero.
 */
public class DOPOSoccer implements ControlPartido {

    /** Precio en puntos de la primera mejora; cada mejora comprada lo sube en esta misma cantidad. */
    public static final int PRECIO_INICIAL = 25;

    private static DOPOSoccer instancia;

    private Partido partido;
    private int[] puntos;
    private Estadisticas[] estadisticas;
    /** Persistencia de las partidas (se usa a través de la interfaz RepositorioPartida). */
    private RepositorioPartida repositorio;

    /**
     * Crea el juego sin partido, con los dos jugadores en 0 puntos y estadísticas en 1.
     */
    private DOPOSoccer() {
        repositorio = PartidoDAO.getInstance();
        reiniciar();
    }

    /**
     * Retorna la única instancia del juego; la crea la primera vez.
     * @return el juego
     */
    public static DOPOSoccer getInstance() {
        if (instancia == null) instancia = new DOPOSoccer();
        return instancia;
    }

    /**
     * Deja el juego como nuevo: sin partido, 0 puntos y estadísticas en 1.
     */
    public void reiniciar() {
        partido = null;
        puntos = new int[] {0, 0};
        estadisticas = new Estadisticas[] {new Estadisticas(), new Estadisticas()};
    }

    /**
     * Inicia un partido nuevo de jugador contra jugador. Los jugadores conservan los puntos
     * y las estadísticas de los partidos anteriores.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param modo modo de juego (Partido.ModoJuego)
     * @param valor minutos (por tiempo) o goles para ganar (por goles)
     * @throws DOPOSoccerException si el modo o el valor no son válidos, o si algún país no existe
     */
    public void iniciarPartido(Pais pais1, Pais pais2, Partido.ModoJuego modo, int valor) throws DOPOSoccerException {
        iniciarPartido(pais1, pais2, modo, valor, Modalidad.JUGADOR_VS_JUGADOR, null);
    }

    /**
     * Inicia un partido nuevo. Si se da un perfil, el jugador 2 es una máquina con esa estrategia.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param modo modo de juego (Partido.ModoJuego)
     * @param valor minutos (por tiempo) o goles para ganar (por goles)
     * @param perfil perfil de la máquina, o null para jugador contra jugador
     * @throws DOPOSoccerException si el modo o el valor no son válidos, o si algún país no existe
     */
    public void iniciarPartido(Pais pais1, Pais pais2, Partido.ModoJuego modo, int valor,
                               PerfilMaquina perfil) throws DOPOSoccerException {
        Modalidad modalidad = perfil == null ? Modalidad.JUGADOR_VS_JUGADOR : Modalidad.JUGADOR_VS_MAQUINA;
        iniciarPartido(pais1, pais2, modo, valor, modalidad, perfil);
    }

    /**
     * Inicia un partido nuevo con la modalidad dada. En jugador contra máquina, el jugador 2
     * es una máquina con la estrategia del perfil. Los jugadores conservan los puntos y las
     * estadísticas de los partidos anteriores. El valor lo valida cada tipo de partido.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param modo modo de juego (Partido.ModoJuego)
     * @param valor minutos (por tiempo) o goles para ganar (por goles)
     * @param modalidad jugador contra jugador o contra la máquina
     * @param perfil perfil de la máquina (solo se usa contra la máquina)
     * @throws DOPOSoccerException si el modo, la modalidad o el valor no son válidos, si algún país
     *         no existe, o si es contra la máquina y no hay perfil
     */
    public void iniciarPartido(Pais pais1, Pais pais2, Partido.ModoJuego modo, int valor,
                               Modalidad modalidad, PerfilMaquina perfil) throws DOPOSoccerException {
        if (modo == null || modalidad == null) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        if (modalidad.tieneMaquina() && perfil == null) {
            throw new DOPOSoccerException(DOPOSoccerException.PERFIL_INVALIDO);
        }
        Partido nuevo = modo.crearPartido(pais1, pais2, valor);
        if (modalidad.tieneMaquina()) nuevo.ponerMaquina(perfil.crearEstrategia());
        guardarPuntos();
        prepararJugadores(nuevo);
        partido = nuevo;
    }

    /**
     * Cambia la estrategia de la máquina en pleno partido (patrón Strategy).
     * @param perfil nuevo perfil de la máquina
     * @throws DOPOSoccerException si no hay partido en curso, no es contra la máquina o el perfil es null
     */
    public void cambiarEstrategia(PerfilMaquina perfil) throws DOPOSoccerException {
        validarPartido();
        if (perfil == null) throw new DOPOSoccerException(DOPOSoccerException.PERFIL_INVALIDO);
        Maquina maquina = partido.getMaquina();
        if (maquina == null) throw new DOPOSoccerException(DOPOSoccerException.NO_HAY_MAQUINA);
        maquina.setEstrategia(perfil.crearEstrategia());
    }

    /**
     * @return true si hay partido y el jugador 2 es la máquina
     */
    public boolean esContraMaquina() {
        return getModalidad() == Modalidad.JUGADOR_VS_MAQUINA;
    }

    /**
     * @return modalidad del partido actual (jugador contra jugador si no hay partido)
     */
    public Modalidad getModalidad() {
        boolean maquina = partido != null && partido.getMaquina() != null;
        return maquina ? Modalidad.JUGADOR_VS_MAQUINA : Modalidad.JUGADOR_VS_JUGADOR;
    }

    /**
     * @return perfil de la estrategia actual de la máquina
     * @throws DOPOSoccerException si no hay partido o no es contra la máquina
     */
    public PerfilMaquina getPerfilMaquina() throws DOPOSoccerException {
        validarExistePartido();
        Maquina maquina = partido.getMaquina();
        if (maquina == null) throw new DOPOSoccerException(DOPOSoccerException.NO_HAY_MAQUINA);
        return maquina.getEstrategia().getPerfil();
    }

    /**
     * Guarda la partida (puntos, estadísticas y el partido en curso) en un archivo de texto.
     * @param archivo archivo .txt donde se guarda
     * @throws DOPOSoccerException si no se puede escribir el archivo
     */
    public void guardar(File archivo) throws DOPOSoccerException {
        repositorio.guardar(this, archivo);
    }

    /**
     * Abre una partida guardada en un archivo de texto. Si tenía un partido en curso, queda en pausa.
     * @param archivo archivo .txt que se abre
     * @throws DOPOSoccerException si no se puede leer el archivo o no tiene el formato correcto
     */
    public void abrir(File archivo) throws DOPOSoccerException {
        repositorio.abrir(this, archivo);
    }

    /**
     * Retorna los puntos acumulados de un jugador en todos los partidos jugados.
     * @param numero número del jugador (1 o 2)
     * @return puntos acumulados
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public int getPuntos(int numero) throws DOPOSoccerException {
        validarNumero(numero);
        if (partido != null) return partido.getJugador(numero).getPuntos();
        return puntos[numero - 1];
    }

    /**
     * Retorna una copia de las estadísticas de un jugador (las que usará en el próximo partido).
     * Es una copia para que nadie las cambie sin pagar la mejora.
     * @param numero número del jugador (1 o 2)
     * @return copia de las estadísticas del jugador
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public Estadisticas getEstadisticas(int numero) throws DOPOSoccerException {
        validarNumero(numero);
        return estadisticas[numero - 1].copiar();
    }

    /**
     * Retorna lo que cuesta la siguiente mejora de un jugador:
     * PRECIO_INICIAL por cada mejora ya comprada más uno.
     * @param numero número del jugador (1 o 2)
     * @return precio en puntos
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public int getPrecioMejora(int numero) throws DOPOSoccerException {
        validarNumero(numero);
        return PRECIO_INICIAL * (estadisticas[numero - 1].getMejorasCompradas() + 1);
    }

    /**
     * Compra una mejora de estadística con los puntos del jugador. Solo se puede sin partido en curso.
     * Primero valida todo (puntos y estadística); si algo falla, el juego queda como estaba.
     * @param numero número del jugador (1 o 2)
     * @param estadistica estadística que se mejora (enum Estadistica)
     * @throws DOPOSoccerException si hay un partido en curso, el número no es válido,
     *         no le alcanzan los puntos, o la estadística es null o ya está en 10
     */
    public void mejorarEstadistica(int numero, Estadistica estadistica) throws DOPOSoccerException {
        validarNumero(numero);
        if (partido != null && !partido.estaTerminado()) {
            throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_EN_CURSO);
        }
        int precio = getPrecioMejora(numero);
        if (getPuntos(numero) < precio) throw new DOPOSoccerException(DOPOSoccerException.PUNTOS_INSUFICIENTES);
        estadisticas[numero - 1].mejorar(estadistica);
        guardarPuntos();
        partido = null;
        puntos[numero - 1] -= precio;
    }

    /**
     * Avanza el partido en curso. Si no hay partido, no hace nada.
     * @param segundos tiempo que pasó desde la última actualización
     */
    @Override
    public void actualizar(double segundos) {
        if (partido != null) partido.actualizar(segundos);
    }

    /**
     * Mueve un jugador.
     * @param numero número del jugador (1 o 2)
     * @param direccion Direccion.IZQUIERDA o Direccion.DERECHA
     * @throws DOPOSoccerException si no hay partido en curso, o el número o la dirección no son válidos
     */
    @Override
    public void moverJugador(int numero, Direccion direccion) throws DOPOSoccerException {
        validarPartido();
        partido.moverJugador(numero, direccion);
    }

    /**
     * Hace saltar a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no hay partido en curso o el número no es válido
     */
    @Override
    public void saltarJugador(int numero) throws DOPOSoccerException {
        validarPartido();
        partido.saltarJugador(numero);
    }

    /**
     * Hace patear a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no hay partido en curso o el número no es válido
     */
    @Override
    public void patearJugador(int numero) throws DOPOSoccerException {
        validarPartido();
        partido.patearJugador(numero);
    }

    /**
     * Pausa o reanuda el partido.
     * @throws DOPOSoccerException si no hay partido en curso
     */
    @Override
    public void pausar() throws DOPOSoccerException {
        validarPartido();
        partido.pausar();
    }

    /**
     * Termina el partido en curso.
     * @throws DOPOSoccerException si no hay partido en curso
     */
    @Override
    public void terminar() throws DOPOSoccerException {
        validarPartido();
        partido.terminar();
    }

    /**
     * @return true si hay un partido (en curso o recién terminado)
     */
    public boolean hayPartido() {
        return partido != null;
    }

    /**
     * @return true si el partido está en pausa
     * @throws DOPOSoccerException si no hay partido
     */
    @Override
    public boolean estaPausado() throws DOPOSoccerException {
        validarExistePartido();
        return partido.estaPausado();
    }

    /**
     * @return true si el partido terminó
     * @throws DOPOSoccerException si no hay partido
     */
    @Override
    public boolean estaTerminado() throws DOPOSoccerException {
        validarExistePartido();
        return partido.estaTerminado();
    }

    /**
     * Retorna el estado de un jugador para mostrarlo (DTO de solo lectura).
     * @param numero número del jugador (1 o 2)
     * @return estado del jugador
     * @throws DOPOSoccerException si no hay partido o el número no es válido
     */
    public EstadoJugador getEstadoJugador(int numero) throws DOPOSoccerException {
        validarExistePartido();
        return partido.getJugador(numero).aEstado();
    }

    /**
     * @return número del jugador que va ganando (1 o 2), o 0 si van empatados
     * @throws DOPOSoccerException si no hay partido
     */
    public int getNumeroGanador() throws DOPOSoccerException {
        validarExistePartido();
        Jugador ganador = partido.getGanador();
        return ganador == null ? 0 : ganador.getNumero();
    }

    /**
     * Retorna los datos para dibujar el partido (DTO), en el orden en que se pintan:
     * cancha, arcos, balón y jugadores. La presentación los pinta sin tocar el dominio.
     * @return lista de comandos de dibujo
     * @throws DOPOSoccerException si no hay partido
     */
    public List<ComandoDibujo> getComandosDibujo() throws DOPOSoccerException {
        validarExistePartido();
        List<ComandoDibujo> comandos = new ArrayList<>();
        for (Dibujable d : partido.getDibujables()) comandos.add(d.aComandoDibujo());
        return comandos;
    }

    /**
     * @return texto del tiempo según el tipo de partido
     * @throws DOPOSoccerException si no hay partido
     */
    public String getTextoTiempo() throws DOPOSoccerException {
        validarExistePartido();
        return partido.getTextoTiempo();
    }

    /**
     * Retorna el partido actual. Solo lo usa el PartidoDAO (mismo paquete) para guardar.
     * @return el partido, o null si no hay
     */
    Partido getPartido() {
        return partido;
    }

    /**
     * Deja el juego como estaba en una partida guardada. Solo lo usa el PartidoDAO (mismo paquete).
     * Si hay un partido sin terminar, queda en pausa.
     * @param puntosGuardados puntos de los jugadores 1 y 2
     * @param estadisticasGuardadas estadísticas de los jugadores 1 y 2
     * @param guardado partido guardado, o null si no había partido en curso
     * @throws DOPOSoccerException si no se puede leer un jugador
     */
    void restaurar(int[] puntosGuardados, Estadisticas[] estadisticasGuardadas, Partido guardado)
            throws DOPOSoccerException {
        puntos = puntosGuardados;
        estadisticas = estadisticasGuardadas;
        partido = null;
        if (guardado != null) {
            prepararJugadores(guardado);
            if (!guardado.estaPausado()) guardado.pausar();
            partido = guardado;
        }
    }

    /**
     * Les pasa a los jugadores de un partido los puntos y las estadísticas acumulados.
     * @param nuevo partido que va a empezar
     * @throws DOPOSoccerException si no se puede leer un jugador
     */
    private void prepararJugadores(Partido nuevo) throws DOPOSoccerException {
        for (int numero = 1; numero <= 2; numero++) {
            Jugador j = nuevo.getJugador(numero);
            j.sumarPuntos(puntos[numero - 1]);
            j.setEstadisticas(estadisticas[numero - 1]);
        }
    }

    /**
     * Guarda los puntos que tienen los jugadores del partido actual para pasarlos al siguiente.
     * @throws DOPOSoccerException si no se puede leer un jugador
     */
    private void guardarPuntos() throws DOPOSoccerException {
        if (partido != null) {
            for (int numero = 1; numero <= puntos.length; numero++) {
                puntos[numero - 1] = partido.getJugador(numero).getPuntos();
            }
        }
    }

    /**
     * Verifica que haya un partido en curso (que exista y no haya terminado).
     * @throws DOPOSoccerException si no hay partido o ya terminó
     */
    private void validarPartido() throws DOPOSoccerException {
        if (partido == null || partido.estaTerminado()) {
            throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_TERMINADO);
        }
    }

    /**
     * Verifica que exista un partido, aunque ya haya terminado.
     * @throws DOPOSoccerException si no hay partido
     */
    private void validarExistePartido() throws DOPOSoccerException {
        if (partido == null) throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_TERMINADO);
    }

    /**
     * Verifica que el número de jugador sea 1 o 2.
     * @param numero número del jugador
     * @throws DOPOSoccerException si no es 1 ni 2
     */
    private void validarNumero(int numero) throws DOPOSoccerException {
        if (numero != 1 && numero != 2) throw new DOPOSoccerException(DOPOSoccerException.JUGADOR_INVALIDO);
    }
}
