package domain;

import java.awt.Color;

/**
 * Personaje que representa a un país. Implementa PoderEspecial: cada país define
 * su tiro de poder y el efecto sobre el rival.
 */
public abstract class Personaje implements PoderEspecial {

    private Pais pais;
    private int danioTiro;
    private double duracionEfecto;

    /**
     * Crea un personaje.
     * @param pais país que representa
     * @param danioTiro daño base del tiro de poder (ya multiplicado por 5, Anexo 1)
     * @param duracionEfecto duración del efecto del tiro de poder en segundos
     */
    public Personaje(Pais pais, int danioTiro, double duracionEfecto) {
        this.pais = pais;
        this.danioTiro = danioTiro;
        this.duracionEfecto = duracionEfecto;
    }

    /**
     * Crea el personaje que corresponde a un país.
     * @param pais país elegido (enum Pais)
     * @return el personaje del país
     * @throws DOPOSoccerException si el país es null
     */
    public static Personaje crear(Pais pais) throws DOPOSoccerException {
        if (pais == null) throw new DOPOSoccerException(DOPOSoccerException.PAIS_INVALIDO);
        return pais.crearPersonaje();
    }

    /**
     * Colores del uniforme, tomados de la bandera del país. Cada país define los suyos.
     * @return arreglo {cabeza, banda, cuerpo}
     */
    public abstract Color[] getColores();

    /**
     * Indica si el cuerpo del uniforme lleva rayas blancas. Por defecto no.
     * @return true si el uniforme tiene rayas
     */
    public boolean tieneRayas() { return false; }

    /**
     * Ejecuta el tiro de poder del personaje sobre el balón.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public abstract void tiroDePoder(Balon balon, boolean haciaDerecha);

    /**
     * Aplica al rival el efecto del tiro de poder.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public abstract void aplicarEfecto(Jugador rival);

    /**
     * @return país del personaje (enum Pais)
     */
    public Pais getPais() { return pais; }

    /**
     * @return daño base del tiro de poder
     */
    @Override
    public int getDanioTiro() { return danioTiro; }

    /**
     * @return duración del efecto en segundos
     */
    @Override
    public double getDuracionEfecto() { return duracionEfecto; }
}
