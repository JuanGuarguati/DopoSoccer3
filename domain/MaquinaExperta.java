package domain;

/**
 * Estrategia experta: lee el partido. Patea al rival cuando lo tiene cerca y el balón
 * está lejos, se interpone cuando el balón va rápido hacia su arco y, en lo demás, ataca.
 */
public class MaquinaExperta implements EstrategiaMaquina {

    /** Distancia al balón a partir de la cual prefiere patear al rival. */
    public static final double BALON_LEJOS = 150;
    /** Velocidad del balón hacia su arco a partir de la cual se considera peligro. */
    public static final double VELOCIDAD_PELIGRO = 200;

    private MaquinaOfensiva ataque;
    private MaquinaDefensiva defensa;

    /**
     * Crea la estrategia experta.
     */
    public MaquinaExperta() {
        ataque = new MaquinaOfensiva();
        defensa = new MaquinaDefensiva();
    }

    /**
     * Decide la acción según la situación del partido.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return acción que debe hacer
     */
    @Override
    public Accion decidirAccion(Maquina maquina, Partido partido) {
        Balon balon = partido.getBalon();
        Jugador rival = partido.getRival(maquina);
        double distanciaBalon = Math.abs(balon.getX() - maquina.getX());
        if (!maquina.tocaBalon(balon) && maquina.alcanzaRival(rival)
                && !rival.estaAturdido() && distanciaBalon > BALON_LEJOS) {
            return Accion.PATEAR;
        }
        int ancho = partido.getCancha().getAncho();
        boolean haciaSuArco = balon.getVelocidadX() * maquina.getSentidoAtaque() < -VELOCIDAD_PELIGRO;
        if (haciaSuArco && defensa.estaEnSuMitad(maquina, balon.getX(), ancho)
                && !maquina.tocaBalon(balon)) {
            double cubrir = balon.getX() - maquina.getSentidoAtaque() * Jugador.RADIO;
            return maquina.accionHacia(cubrir);
        }
        return ataque.decidirAccion(maquina, partido);
    }

    /**
     * @return PerfilMaquina.EXPERTA
     */
    @Override
    public PerfilMaquina getPerfil() { return PerfilMaquina.EXPERTA; }
}
