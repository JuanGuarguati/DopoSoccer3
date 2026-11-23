package domain;

/**
 * Jugador controlado por la máquina (contexto del patrón Strategy).
 * No decide por sí misma: en cada turno le pregunta a su EstrategiaMaquina qué hacer
 * y ejecuta esa acción. La estrategia se puede cambiar en cualquier momento del partido.
 */
public class Maquina extends Jugador {

    /** Distancia horizontal a la que la máquina considera que ya llegó a su objetivo. */
    public static final double TOLERANCIA = 6;

    private EstrategiaMaquina estrategia;

    /**
     * Crea un jugador máquina con una estrategia.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     * @param estrategia estrategia con la que juega
     */
    public Maquina(int numero, Personaje personaje, double x, double y, EstrategiaMaquina estrategia) {
        super(numero, personaje, x, y);
        this.estrategia = estrategia;
    }

    /**
     * Turno de la máquina: pide la acción a su estrategia y la ejecuta.
     * Si está aturdida no hace nada.
     * @param partido partido en curso
     */
    @Override
    public void jugar(Partido partido) {
        if (estaAturdido()) return;
        ejecutar(estrategia.decidirAccion(this, partido), partido);
    }

    /**
     * Ejecuta una acción sobre el partido.
     * @param accion acción que se quiere hacer
     * @param partido partido en curso
     */
    public void ejecutar(Accion accion, Partido partido) {
        switch (accion) {
            case MOVER_IZQUIERDA: moverIzquierda(); break;
            case MOVER_DERECHA: moverDerecha(); break;
            case SALTAR: saltar(); break;
            case PATEAR: patear(partido.getBalon(), partido.getRival(this)); break;
            default: break;
        }
    }

    /**
     * Calcula el movimiento para acercarse a una posición horizontal.
     * @param objetivoX posición a la que quiere llegar
     * @return MOVER_IZQUIERDA, MOVER_DERECHA o QUEDARSE si ya llegó
     */
    public Accion accionHacia(double objetivoX) {
        if (objetivoX < getX() - TOLERANCIA) return Accion.MOVER_IZQUIERDA;
        if (objetivoX > getX() + TOLERANCIA) return Accion.MOVER_DERECHA;
        return Accion.QUEDARSE;
    }

    /**
     * Sentido en el que ataca: el jugador 1 ataca hacia la derecha (+1) y el jugador 2 hacia la izquierda (-1).
     * @return +1 o -1
     */
    public int getSentidoAtaque() {
        return getNumero() == 1 ? 1 : -1;
    }

    /**
     * @return true si está mirando hacia el arco que ataca
     */
    public boolean miraHaciaAtaque() {
        return estaMirandoDerecha() == (getSentidoAtaque() > 0);
    }

    /**
     * @return la acción de moverse un paso hacia el arco que ataca
     */
    public Accion accionHaciaAtaque() {
        return getSentidoAtaque() > 0 ? Accion.MOVER_DERECHA : Accion.MOVER_IZQUIERDA;
    }

    /**
     * Cambia la estrategia con la que juega la máquina (se puede hacer en pleno partido).
     * @param estrategia nueva estrategia
     * @throws DOPOSoccerException si la estrategia es null
     */
    public void setEstrategia(EstrategiaMaquina estrategia) throws DOPOSoccerException {
        if (estrategia == null) throw new DOPOSoccerException(DOPOSoccerException.PERFIL_INVALIDO);
        this.estrategia = estrategia;
    }

    /**
     * @return estrategia actual de la máquina
     */
    public EstrategiaMaquina getEstrategia() { return estrategia; }

    /**
     * @return true, porque este jugador lo controla la máquina
     */
    @Override
    public boolean esMaquina() { return true; }

    /**
     * @return nombre del perfil de la estrategia actual (por ejemplo "Ofensiva")
     */
    @Override
    public String getNombrePerfil() { return estrategia.getPerfil().getNombre(); }
}
