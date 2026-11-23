package domain;

/**
 * Estrategia ofensiva: su prioridad es anotar. Siempre va a buscar el balón,
 * se pone detrás de él y lo patea hacia el arco rival.
 */
public class MaquinaOfensiva implements EstrategiaMaquina {

    /** Altura por encima de la cabeza a partir de la cual salta a cabecear. */
    public static final double ALTURA_CABEZAZO = 60;
    /** Distancia horizontal al balón para intentar el cabezazo. */
    public static final double DISTANCIA_CABEZAZO = 70;

    /**
     * Decide la acción para atacar: patea si toca el balón mirando al arco rival,
     * salta si el balón viene alto, y si no, se acomoda detrás del balón.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return acción que debe hacer
     */
    @Override
    public Accion decidirAccion(Maquina maquina, Partido partido) {
        Balon balon = partido.getBalon();
        if (maquina.tocaBalon(balon)) {
            return maquina.miraHaciaAtaque() ? Accion.PATEAR : maquina.accionHaciaAtaque();
        }
        if (debeCabecear(maquina, balon)) return Accion.SALTAR;
        double detras = balon.getX() - maquina.getSentidoAtaque() * Jugador.RADIO * 2;
        return maquina.accionHacia(detras);
    }

    /**
     * Indica si el balón está alto y cerca, para saltar a cabecearlo.
     * @param maquina máquina que va a actuar
     * @param balon balón del partido
     * @return true si debe saltar
     */
    public boolean debeCabecear(Maquina maquina, Balon balon) {
        boolean alto = balon.getY() < maquina.getY() - ALTURA_CABEZAZO;
        boolean cerca = Math.abs(balon.getX() - maquina.getX()) < DISTANCIA_CABEZAZO;
        return alto && cerca && maquina.estaEnSuelo();
    }

    /**
     * @return PerfilMaquina.OFENSIVA
     */
    @Override
    public PerfilMaquina getPerfil() { return PerfilMaquina.OFENSIVA; }
}
