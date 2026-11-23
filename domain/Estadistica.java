package domain;

/**
 * Estadísticas que un jugador puede mejorar con sus puntos.
 * Reemplaza los textos "speed", "kick", "jump" y "survival".
 */
public enum Estadistica {
    /** Velocidad al moverse. */
    SPEED("Velocidad"),
    /** Fuerza de la patada (daño al rival). */
    KICK("Patada"),
    /** Altura del salto. */
    JUMP("Salto"),
    /** Vida máxima. */
    SURVIVAL("Resistencia");

    private final String nombre;

    /**
     * Crea una estadística con su nombre para mostrar.
     * @param nombre nombre en español
     */
    Estadistica(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return nombre de la estadística en español
     */
    public String getNombre() { return nombre; }

    /**
     * @return el nombre en español
     */
    @Override
    public String toString() { return nombre; }
}
