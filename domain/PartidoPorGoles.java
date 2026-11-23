package domain;

/**
 * Partido por goles: gana el primero que llegue a un número de goles, sin límite de tiempo.
 */
public class PartidoPorGoles extends Partido {

    private int golesParaGanar;

    /**
     * Crea un partido por goles.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param goles goles necesarios para ganar
     * @throws DOPOSoccerException si algún país no existe o los goles no son mayores que cero
     */
    public PartidoPorGoles(Pais pais1, Pais pais2, int goles) throws DOPOSoccerException {
        super(pais1, pais2);
        if (goles <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        this.golesParaGanar = goles;
    }

    /**
     * Hay ganador cuando algún jugador llega a los goles definidos.
     * @return true si ya hay ganador
     */
    @Override
    public boolean hayGanador() {
        return getGolesMaximos() >= golesParaGanar;
    }

    /**
     * @return goles necesarios para ganar
     */
    public int getGolesParaGanar() {
        return golesParaGanar;
    }

    /**
     * Muestra a cuántos goles se juega y el tiempo jugado.
     * @return texto del tiempo
     */
    @Override
    public String getTextoTiempo() {
        return "A " + golesParaGanar + " goles - " + formato(getTiempoJugado());
    }

    /**
     * @return ModoJuego.POR_GOLES
     */
    @Override
    public ModoJuego getModo() { return ModoJuego.POR_GOLES; }

    /**
     * @return goles necesarios para ganar
     */
    @Override
    public int getValor() { return golesParaGanar; }
}
