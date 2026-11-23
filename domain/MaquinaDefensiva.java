package domain;

/**
 * Estrategia defensiva: su prioridad es defender el arco. Se queda cerca de su arco
 * y solo sale a despejar cuando el balón entra a su mitad de la cancha.
 */
public class MaquinaDefensiva implements EstrategiaMaquina {

    /** Distancia desde su pared a la que la máquina espera defendiendo. */
    public static final double DISTANCIA_ARCO = 150;

    private MaquinaOfensiva despeje;

    /**
     * Crea la estrategia defensiva.
     */
    public MaquinaDefensiva() {
        despeje = new MaquinaOfensiva();
    }

    /**
     * Si el balón está en su mitad, sale a despejarlo; si no, vuelve a su posición frente al arco.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return acción que debe hacer
     */
    @Override
    public Accion decidirAccion(Maquina maquina, Partido partido) {
        Balon balon = partido.getBalon();
        int ancho = partido.getCancha().getAncho();
        if (estaEnSuMitad(maquina, balon.getX(), ancho)) {
            return despeje.decidirAccion(maquina, partido);
        }
        return maquina.accionHacia(posicionDefensa(maquina, ancho));
    }

    /**
     * Indica si una posición está en la mitad de cancha que defiende la máquina.
     * @param maquina máquina que defiende
     * @param x posición horizontal
     * @param ancho ancho de la cancha
     * @return true si está en su mitad
     */
    public boolean estaEnSuMitad(Maquina maquina, double x, int ancho) {
        return (x - ancho / 2.0) * maquina.getSentidoAtaque() < 0;
    }

    /**
     * Calcula dónde espera la máquina cuando defiende.
     * @param maquina máquina que defiende
     * @param ancho ancho de la cancha
     * @return posición horizontal frente a su arco
     */
    public double posicionDefensa(Maquina maquina, int ancho) {
        return maquina.getSentidoAtaque() > 0 ? DISTANCIA_ARCO : ancho - DISTANCIA_ARCO;
    }

    /**
     * @return PerfilMaquina.DEFENSIVA
     */
    @Override
    public PerfilMaquina getPerfil() { return PerfilMaquina.DEFENSIVA; }
}
