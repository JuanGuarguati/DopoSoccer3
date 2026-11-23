package domain;

/**
 * Perfiles de la máquina. Cada perfil sabe crear su estrategia (patrón Strategy).
 * Reemplaza la lista de textos "Torpe", "Ofensiva", ... de la interfaz.
 */
public enum PerfilMaquina {
    /** Escoge sus acciones al azar. */
    TORPE("Torpe"),
    /** Siempre va a buscar el balón para anotar. */
    OFENSIVA("Ofensiva"),
    /** Se queda cuidando su arco. */
    DEFENSIVA("Defensiva"),
    /** Ataca, defiende, cabecea y patea al rival según la situación. */
    EXPERTA("Experta"),
    /** Defiende cuando va ganando y ataca cuando va empatando o perdiendo. */
    EQUILIBRADA("Equilibrada");

    private final String nombre;

    /**
     * Crea un perfil con su nombre para mostrar.
     * @param nombre nombre del perfil
     */
    PerfilMaquina(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Crea la estrategia que corresponde a este perfil.
     * @return estrategia nueva
     */
    public EstrategiaMaquina crearEstrategia() {
        switch (this) {
            case TORPE: return new MaquinaTorpe();
            case OFENSIVA: return new MaquinaOfensiva();
            case DEFENSIVA: return new MaquinaDefensiva();
            case EXPERTA: return new MaquinaExperta();
            default: return new MaquinaEquilibrada();
        }
    }

    /**
     * Busca un perfil por el nombre de su constante (por ejemplo "OFENSIVA").
     * @param texto nombre de la constante
     * @return el perfil
     * @throws DOPOSoccerException si el texto no corresponde a ningún perfil
     */
    public static PerfilMaquina desdeTexto(String texto) throws DOPOSoccerException {
        try {
            return PerfilMaquina.valueOf(texto.trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DOPOSoccerException(DOPOSoccerException.PERFIL_INVALIDO);
        }
    }

    /**
     * @return nombre del perfil para mostrar
     */
    public String getNombre() { return nombre; }

    /**
     * @return el nombre para mostrar (así se ve en los JComboBox)
     */
    @Override
    public String toString() { return nombre; }
}
