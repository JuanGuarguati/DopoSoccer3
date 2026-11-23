package domain;

import java.util.function.Supplier;

/**
 * Países (personajes) que se pueden elegir en DOPOSoccer.
 * Cada país sabe su nombre para mostrar y cómo crear su Personaje, así que agregar un país
 * solo pide una subclase de Personaje y una constante aquí (sin switch en otras clases).
 */
public enum Pais {
    /** Corea del Sur. */
    COREA_DEL_SUR("Corea del Sur", CoreaDelSur::new),
    /** Camerún. */
    CAMERUN("Camerún", Camerun::new),
    /** Nigeria. */
    NIGERIA("Nigeria", Nigeria::new),
    /** Estados Unidos. */
    ESTADOS_UNIDOS("Estados Unidos", EstadosUnidos::new);

    private final String nombre;
    private final Supplier<Personaje> fabrica;

    /**
     * Crea un país con su nombre para mostrar y la fábrica de su personaje.
     * @param nombre nombre del país
     * @param fabrica crea el personaje del país
     */
    Pais(String nombre, Supplier<Personaje> fabrica) {
        this.nombre = nombre;
        this.fabrica = fabrica;
    }

    /**
     * Crea un personaje nuevo de este país.
     * @return el personaje del país
     */
    public Personaje crearPersonaje() {
        return fabrica.get();
    }

    /**
     * @return nombre del país para mostrar al usuario
     */
    public String getNombre() { return nombre; }

    /**
     * Busca un país por el nombre de su constante (por ejemplo "CAMERUN").
     * @param texto nombre de la constante
     * @return el país
     * @throws DOPOSoccerException si el texto no corresponde a ningún país
     */
    public static Pais desdeTexto(String texto) throws DOPOSoccerException {
        try {
            return Pais.valueOf(texto.trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DOPOSoccerException(DOPOSoccerException.PAIS_INVALIDO);
        }
    }

    /**
     * @return el nombre para mostrar (así se ve en los JComboBox)
     */
    @Override
    public String toString() { return nombre; }
}
