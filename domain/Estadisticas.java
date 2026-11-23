package domain;

/**
 * Estadísticas de un jugador: speed, kick, jump y survival.
 * Todas empiezan en 1 y pueden subir hasta 10. Aplican a todos los personajes del jugador.
 * Implementa Mejorable: cada mejora comprada sube en 1 una estadística.
 */
public class Estadisticas implements Mejorable {

    /** Valor mínimo de una estadística. */
    public static final int MINIMO = 1;
    /** Valor máximo de una estadística. */
    public static final int MAXIMO = 10;
    /** Desplazamiento base del jugador, en píxeles por paso. */
    public static final double VELOCIDAD_BASE = 4;
    /** Velocidad vertical base del salto, en píxeles por segundo. */
    public static final double SALTO_BASE = 480;

    private int speed;
    private int kick;
    private int jump;
    private int survival;

    /**
     * Crea las estadísticas con todos los valores en 1.
     */
    public Estadisticas() {
        speed = MINIMO;
        kick = MINIMO;
        jump = MINIMO;
        survival = MINIMO;
    }

    /**
     * Crea las estadísticas con valores dados (se usa al abrir una partida guardada).
     * @param speed valor de speed
     * @param kick valor de kick
     * @param jump valor de jump
     * @param survival valor de survival
     * @throws DOPOSoccerException si algún valor no está entre 1 y 10
     */
    public Estadisticas(int speed, int kick, int jump, int survival) throws DOPOSoccerException {
        for (int valor : new int[] {speed, kick, jump, survival}) {
            if (valor < MINIMO || valor > MAXIMO) {
                throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_INVALIDA);
            }
        }
        this.speed = speed;
        this.kick = kick;
        this.jump = jump;
        this.survival = survival;
    }

    /**
     * Crea una copia con los mismos valores (la fachada entrega copias para que nadie
     * cambie las estadísticas sin pagar la mejora).
     * @return copia de estas estadísticas
     */
    public Estadisticas copiar() {
        Estadisticas copia = new Estadisticas();
        copia.speed = speed;
        copia.kick = kick;
        copia.jump = jump;
        copia.survival = survival;
        return copia;
    }

    /**
     * Calcula el multiplicador de daño: 1 + 0.10 * (kick - 1).
     * @return multiplicador del daño que hace el jugador
     */
    public double getMultiplicadorKick() {
        return 1 + 0.10 * (kick - 1);
    }

    /**
     * Calcula la vida máxima: 100 * (1 + 0.10 * (survival - 1)).
     * @return vida máxima del jugador
     */
    public double getVidaMax() {
        return 100 * (1 + 0.10 * (survival - 1));
    }

    /**
     * Calcula cuántos píxeles avanza el jugador en cada paso.
     * Cada punto de speed por encima de 1 suma un 10 %.
     * @return desplazamiento por paso
     */
    public double getVelocidad() {
        return VELOCIDAD_BASE * (1 + 0.10 * (speed - 1));
    }

    /**
     * Calcula la velocidad inicial del salto.
     * Cada punto de jump por encima de 1 suma un 10 %.
     * @return velocidad vertical del salto
     */
    public double getFuerzaSalto() {
        return SALTO_BASE * (1 + 0.10 * (jump - 1));
    }

    /**
     * Sube en 1 una estadística.
     * @param estadistica estadística que se quiere mejorar (enum Estadistica)
     * @throws DOPOSoccerException si la estadística es null o ya está en 10
     */
    @Override
    public void mejorar(Estadistica estadistica) throws DOPOSoccerException {
        if (estadistica == null) throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_MAXIMA);
        switch (estadistica) {
            case SPEED: speed = subir(speed); break;
            case KICK: kick = subir(kick); break;
            case JUMP: jump = subir(jump); break;
            default: survival = subir(survival); break;
        }
    }

    /**
     * Retorna el valor (de 1 a 10) de una estadística.
     * @param estadistica estadística que se quiere consultar
     * @return valor actual
     */
    @Override
    public int getValor(Estadistica estadistica) {
        switch (estadistica) {
            case SPEED: return speed;
            case KICK: return kick;
            case JUMP: return jump;
            default: return survival;
        }
    }

    /**
     * Cuenta cuántas mejoras se han comprado (cada punto por encima de 1 es una mejora).
     * @return número de mejoras compradas
     */
    @Override
    public int getMejorasCompradas() {
        return speed + kick + jump + survival - 4 * MINIMO;
    }

    /**
     * Suma 1 a un valor si no ha llegado al máximo.
     * @param valor valor actual
     * @return valor + 1
     * @throws DOPOSoccerException si el valor ya está en el máximo
     */
    private int subir(int valor) throws DOPOSoccerException {
        if (valor >= MAXIMO) throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_MAXIMA);
        return valor + 1;
    }

    /**
     * @return valor de speed
     */
    public int getSpeed() { return speed; }

    /**
     * @return valor de kick
     */
    public int getKick() { return kick; }

    /**
     * @return valor de jump
     */
    public int getJump() { return jump; }

    /**
     * @return valor de survival
     */
    public int getSurvival() { return survival; }
}
