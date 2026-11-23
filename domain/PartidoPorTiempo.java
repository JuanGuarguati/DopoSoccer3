package domain;

/**
 * Partido por tiempo: dura un número de minutos. Gana quien tenga más goles al final.
 * Si hay empate, entra en muerte súbita y gana el primero que anote.
 */
public class PartidoPorTiempo extends Partido {

    private double duracion;

    /**
     * Crea un partido por tiempo.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param minutos duración del partido en minutos
     * @throws DOPOSoccerException si algún país no existe o los minutos no son mayores que cero
     */
    public PartidoPorTiempo(Pais pais1, Pais pais2, int minutos) throws DOPOSoccerException {
        super(pais1, pais2);
        if (minutos <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        this.duracion = minutos * 60.0;
    }

    /**
     * Hay ganador cuando se acabó el tiempo y un jugador tiene más goles.
     * Solo consulta: no cambia el estado del partido.
     * @return true si ya hay ganador
     */
    @Override
    public boolean hayGanador() {
        return seAcaboElTiempo() && getGanador() != null;
    }

    /**
     * @return true si ya pasó la duración del partido
     */
    private boolean seAcaboElTiempo() {
        return getTiempoJugado() >= duracion;
    }

    /**
     * @return segundos que faltan (0 si ya se acabó el tiempo)
     */
    public double getTiempoRestante() {
        return Math.max(0, duracion - getTiempoJugado());
    }

    /**
     * Muerte súbita: se acabó el tiempo y van empatados; el siguiente gol gana.
     * @return true si el partido está en muerte súbita
     */
    public boolean estaEnMuerteSubita() {
        return seAcaboElTiempo() && getGanador() == null;
    }

    /**
     * Muestra el tiempo que falta, o "MUERTE SÚBITA" si se acabó con empate.
     * @return texto del tiempo
     */
    @Override
    public String getTextoTiempo() {
        if (estaEnMuerteSubita()) return "MUERTE SÚBITA";
        return formato(getTiempoRestante());
    }

    /**
     * @return ModoJuego.POR_TIEMPO
     */
    @Override
    public ModoJuego getModo() { return ModoJuego.POR_TIEMPO; }

    /**
     * @return minutos que dura el partido
     */
    @Override
    public int getValor() { return (int) (duracion / 60); }
}
