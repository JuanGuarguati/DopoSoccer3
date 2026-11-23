package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Partido de DOPOSoccer entre dos jugadores. El jugador 1 defiende el arco izquierdo
 * y el jugador 2 el derecho. Cada tipo de partido define cuándo hay ganador.
 * El jugador 2 puede ser una Maquina (partido contra la máquina).
 * Implementa ControlPartido: es quien de verdad mueve, salta, patea, pausa y termina.
 */
public abstract class Partido implements ControlPartido {

    /**
     * Modos de juego. Cada modo sabe crear su tipo de partido.
     * Reemplaza los textos "tiempo" y "goles".
     */
    public enum ModoJuego {
        /** Gana quien tenga más goles cuando se acaba el tiempo. */
        POR_TIEMPO("Por tiempo (minutos)"),
        /** Gana el primero que llega a un número de goles. */
        POR_GOLES("Por goles");

        private final String nombre;

        /**
         * Crea un modo de juego con su nombre para mostrar.
         * @param nombre nombre del modo
         */
        ModoJuego(String nombre) {
            this.nombre = nombre;
        }

        /**
         * Crea el partido que corresponde a este modo.
         * @param pais1 país del jugador 1
         * @param pais2 país del jugador 2
         * @param valor minutos (por tiempo) o goles para ganar (por goles)
         * @return el partido nuevo
         * @throws DOPOSoccerException si algún país no existe o el valor no es mayor que cero
         */
        public Partido crearPartido(Pais pais1, Pais pais2, int valor) throws DOPOSoccerException {
            if (this == POR_TIEMPO) return new PartidoPorTiempo(pais1, pais2, valor);
            return new PartidoPorGoles(pais1, pais2, valor);
        }

        /**
         * Busca un modo por el nombre de su constante (por ejemplo "POR_GOLES").
         * @param texto nombre de la constante
         * @return el modo
         * @throws DOPOSoccerException si el texto no corresponde a ningún modo
         */
        public static ModoJuego desdeTexto(String texto) throws DOPOSoccerException {
            try {
                return ModoJuego.valueOf(texto.trim());
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
            }
        }

        /**
         * @return nombre del modo para mostrar
         */
        public String getNombre() { return nombre; }

        /**
         * @return el nombre para mostrar (así se ve en los JComboBox)
         */
        @Override
        public String toString() { return nombre; }
    }

    private Cancha cancha;
    private Balon balon;
    private Jugador[] jugadores;
    private double tiempoJugado;
    private boolean pausado;
    private boolean terminado;

    /**
     * Crea un partido 0 - 0 con la cancha, el balón en el centro y los dos jugadores.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @throws DOPOSoccerException si algún país no existe
     */
    public Partido(Pais pais1, Pais pais2) throws DOPOSoccerException {
        cancha = new Cancha(ConstantesJuego.ANCHO_CANCHA, ConstantesJuego.ALTO_CANCHA);
        balon = new Balon(ConstantesJuego.ANCHO_CANCHA / 2.0, ConstantesJuego.ALTURA_SAQUE);
        Personaje personaje1 = Personaje.crear(pais1);
        Personaje personaje2 = Personaje.crear(pais2);
        double piso = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugadores = new Jugador[] {
            new Jugador(1, personaje1, ConstantesJuego.DISTANCIA_INICIAL, piso),
            new Jugador(2, personaje2, ConstantesJuego.ANCHO_CANCHA - ConstantesJuego.DISTANCIA_INICIAL, piso)
        };
    }

    /**
     * Cambia al jugador 2 por una máquina que juega con la estrategia dada.
     * La máquina conserva el personaje y la posición del jugador 2.
     * @param estrategia estrategia con la que juega la máquina
     * @throws DOPOSoccerException si la estrategia es null
     */
    public void ponerMaquina(EstrategiaMaquina estrategia) throws DOPOSoccerException {
        if (estrategia == null) throw new DOPOSoccerException(DOPOSoccerException.PERFIL_INVALIDO);
        Jugador j2 = jugadores[1];
        jugadores[1] = new Maquina(2, j2.getPersonaje(), j2.getX(), j2.getY(), estrategia);
    }

    /**
     * Avanza el partido: turno de cada jugador (la máquina decide aquí), movimiento de todos
     * los elementos Movible, reacción de todos los Interactuable al balón (cabezazos y goles),
     * separación de los jugadores y ganador.
     * No hace nada si está pausado o terminado.
     * @param segundos tiempo que pasó desde la última actualización
     */
    @Override
    public void actualizar(double segundos) {
        if (pausado || terminado) return;
        tiempoJugado += segundos;
        for (Jugador j : jugadores) {
            j.jugar(this);
        }
        for (Movible m : getMovibles()) {
            m.mover(segundos, cancha);
        }
        for (Interactuable i : getInteractuables()) {
            i.interactuar(balon, this);
        }
        separarJugadores();
        if (hayGanador()) {
            finalizar();
        }
    }

    /**
     * Mueve un jugador a la izquierda o a la derecha. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @param direccion Direccion.IZQUIERDA o Direccion.DERECHA
     * @throws DOPOSoccerException si el número no es válido o la dirección es null
     */
    @Override
    public void moverJugador(int numero, Direccion direccion) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        if (direccion == null) throw new DOPOSoccerException(DOPOSoccerException.DIRECCION_INVALIDA);
        if (pausado) return;
        if (direccion == Direccion.IZQUIERDA) {
            j.moverIzquierda();
        } else {
            j.moverDerecha();
        }
    }

    /**
     * Hace saltar a un jugador. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si el número no es válido
     */
    @Override
    public void saltarJugador(int numero) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        if (pausado) return;
        j.saltar();
    }

    /**
     * Hace patear a un jugador contra el balón o el rival. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si el número no es válido
     */
    @Override
    public void patearJugador(int numero) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        if (pausado) return;
        j.patear(balon, getRival(j));
    }

    /**
     * Registra un gol en un arco. Gol en el arco izquierdo es del jugador 2 y gol en el
     * derecho es del jugador 1. Después del gol se reinician las posiciones.
     * @param arco arco donde entró el balón
     */
    public void registrarGol(Arco arco) {
        Jugador autor = arco.esIzquierdo() ? jugadores[1] : jugadores[0];
        autor.anotarGol();
        reiniciarPosiciones();
    }

    /**
     * Pone a los jugadores en su posición inicial y deja caer el balón desde el centro.
     */
    public void reiniciarPosiciones() {
        double piso = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugadores[0].reiniciar(ConstantesJuego.DISTANCIA_INICIAL, piso);
        jugadores[1].reiniciar(ConstantesJuego.ANCHO_CANCHA - ConstantesJuego.DISTANCIA_INICIAL, piso);
        balon.reiniciar(ConstantesJuego.ANCHO_CANCHA / 2.0, ConstantesJuego.ALTURA_SAQUE);
    }

    /**
     * Pausa el partido o lo reanuda si ya estaba pausado.
     */
    @Override
    public void pausar() {
        pausado = !pausado;
    }

    /**
     * Termina el partido en cualquier momento y entrega los puntos si hay ganador.
     */
    @Override
    public void terminar() {
        if (!terminado) finalizar();
    }

    /**
     * Indica si se cumplió la condición para ganar según el tipo de partido.
     * @return true si ya hay ganador
     */
    public abstract boolean hayGanador();

    /**
     * Texto del tiempo que muestra la interfaz; depende del tipo de partido.
     * @return texto del tiempo
     */
    public abstract String getTextoTiempo();

    /**
     * @return modo de juego de este partido
     */
    public abstract ModoJuego getModo();

    /**
     * @return minutos (por tiempo) o goles para ganar (por goles) con que se creó el partido
     */
    public abstract int getValor();

    /**
     * Deja el partido como estaba en una partida guardada: goles de cada jugador y tiempo jugado.
     * @param goles1 goles del jugador 1
     * @param goles2 goles del jugador 2
     * @param tiempo segundos jugados
     */
    public void restaurar(int goles1, int goles2, double tiempo) {
        jugadores[0].setGoles(goles1);
        jugadores[1].setGoles(goles2);
        tiempoJugado = Math.max(0, tiempo);
    }

    /**
     * Retorna los elementos que se mueven en la cancha: el balón y los dos jugadores.
     * @return lista de elementos Movible
     */
    public List<Movible> getMovibles() {
        List<Movible> movibles = new ArrayList<>();
        movibles.add(balon);
        for (Jugador j : jugadores) movibles.add(j);
        return movibles;
    }

    /**
     * Retorna los elementos que reaccionan al balón: los dos jugadores (cabezazo)
     * y los dos arcos (gol).
     * @return lista de elementos Interactuable
     */
    public List<Interactuable> getInteractuables() {
        List<Interactuable> interactuables = new ArrayList<>();
        for (Jugador j : jugadores) interactuables.add(j);
        interactuables.add(cancha.getArcoIzquierdo());
        interactuables.add(cancha.getArcoDerecho());
        return interactuables;
    }

    /**
     * Retorna los elementos que se dibujan, en el orden en que se pintan:
     * cancha, arcos, balón y jugadores.
     * @return lista de elementos Dibujable
     */
    public List<Dibujable> getDibujables() {
        List<Dibujable> dibujables = new ArrayList<>();
        dibujables.add(cancha);
        dibujables.add(cancha.getArcoIzquierdo());
        dibujables.add(cancha.getArcoDerecho());
        dibujables.add(balon);
        for (Jugador j : jugadores) dibujables.add(j);
        return dibujables;
    }

    /**
     * Retorna el rival de un jugador.
     * @param jugador jugador del partido
     * @return el otro jugador
     */
    public Jugador getRival(Jugador jugador) {
        return jugador == jugadores[0] ? jugadores[1] : jugadores[0];
    }

    /**
     * @return la máquina del partido, o null si es jugador contra jugador
     */
    public Maquina getMaquina() {
        for (Jugador j : jugadores) {
            if (j.esMaquina()) return (Maquina) j;
        }
        return null;
    }

    /**
     * @return la mayor cantidad de goles que tiene un jugador
     */
    protected int getGolesMaximos() {
        return Math.max(jugadores[0].getGoles(), jugadores[1].getGoles());
    }

    /**
     * Retorna el jugador con más goles.
     * @return el ganador, o null si están empatados
     */
    public Jugador getGanador() {
        if (jugadores[0].getGoles() > jugadores[1].getGoles()) return jugadores[0];
        if (jugadores[1].getGoles() > jugadores[0].getGoles()) return jugadores[1];
        return null;
    }

    /**
     * Retorna un jugador por su número.
     * @param numero número del jugador (1 o 2)
     * @return el jugador
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public Jugador getJugador(int numero) throws DOPOSoccerException {
        if (numero != 1 && numero != 2) throw new DOPOSoccerException(DOPOSoccerException.JUGADOR_INVALIDO);
        return jugadores[numero - 1];
    }

    /**
     * @return balón del partido
     */
    public Balon getBalon() { return balon; }

    /**
     * @return cancha del partido
     */
    public Cancha getCancha() { return cancha; }

    /**
     * @return true si el partido terminó
     */
    @Override
    public boolean estaTerminado() { return terminado; }

    /**
     * @return true si el partido está en pausa
     */
    @Override
    public boolean estaPausado() { return pausado; }

    /**
     * @return segundos jugados
     */
    public double getTiempoJugado() { return tiempoJugado; }

    /**
     * Convierte segundos a texto mm:ss.
     * @param segundos segundos
     * @return texto mm:ss
     */
    protected String formato(double segundos) {
        int s = (int) Math.ceil(segundos);
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    /**
     * Si los dos jugadores se pisan (se cruzan a la misma altura), los aparta
     * la mitad de lo que se pisan cada uno, para que no queden uno dentro del otro.
     */
    private void separarJugadores() {
        Jugador j1 = jugadores[0];
        Jugador j2 = jugadores[1];
        double dx = j2.getX() - j1.getX();
        double minimo = Jugador.RADIO * 2;
        if (Math.abs(j2.getY() - j1.getY()) >= minimo || Math.abs(dx) >= minimo) return;
        double falta = minimo - Math.abs(dx);
        double lado = dx >= 0 ? 1 : -1;
        j1.desplazar(-lado * falta / 2, cancha);
        j2.desplazar(lado * falta / 2, cancha);
    }

    /**
     * Marca el partido como terminado y entrega 100 puntos al ganador y 25 al perdedor.
     */
    private void finalizar() {
        terminado = true;
        Jugador ganador = getGanador();
        if (ganador != null) {
            ganador.sumarPuntos(ConstantesJuego.PUNTOS_GANADOR);
            getRival(ganador).sumarPuntos(ConstantesJuego.PUNTOS_PERDEDOR);
        }
    }
}
