package domain;

/**
 * Estrategia equilibrada (nueva): cambia de comportamiento según el marcador.
 * Si va ganando, defiende su arco para cuidar la ventaja; si va empatando o perdiendo, ataca.
 */
public class MaquinaEquilibrada implements EstrategiaMaquina {

    private MaquinaOfensiva ataque;
    private MaquinaDefensiva defensa;

    /**
     * Crea la estrategia equilibrada.
     */
    public MaquinaEquilibrada() {
        ataque = new MaquinaOfensiva();
        defensa = new MaquinaDefensiva();
    }

    /**
     * Defiende si va ganando; si no, ataca.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return acción que debe hacer
     */
    @Override
    public Accion decidirAccion(Maquina maquina, Partido partido) {
        if (vaGanando(maquina, partido)) return defensa.decidirAccion(maquina, partido);
        return ataque.decidirAccion(maquina, partido);
    }

    /**
     * Indica si la máquina tiene más goles que su rival.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return true si va ganando
     */
    public boolean vaGanando(Maquina maquina, Partido partido) {
        return maquina.getGoles() > partido.getRival(maquina).getGoles();
    }

    /**
     * @return PerfilMaquina.EQUILIBRADA
     */
    @Override
    public PerfilMaquina getPerfil() { return PerfilMaquina.EQUILIBRADA; }
}
