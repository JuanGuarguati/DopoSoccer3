package domain;

/**
 * Modalidad del partido: quién controla al jugador 2.
 * Antes solo existía en la interfaz (por la posición en el JComboBox).
 */
public enum Modalidad {
    /** Los dos jugadores son personas. */
    JUGADOR_VS_JUGADOR("Jugador vs Jugador", false),
    /** El jugador 2 es la máquina. */
    JUGADOR_VS_MAQUINA("Jugador vs Máquina", true);

    private final String nombre;
    private final boolean maquina;

    /**
     * Crea una modalidad.
     * @param nombre nombre para mostrar
     * @param maquina true si el jugador 2 es la máquina
     */
    Modalidad(String nombre, boolean maquina) {
        this.nombre = nombre;
        this.maquina = maquina;
    }

    /**
     * @return true si el jugador 2 es la máquina
     */
    public boolean tieneMaquina() { return maquina; }

    /**
     * @return nombre para mostrar
     */
    public String getNombre() { return nombre; }

    /**
     * @return el nombre para mostrar (así se ve en los JComboBox)
     */
    @Override
    public String toString() { return nombre; }
}
