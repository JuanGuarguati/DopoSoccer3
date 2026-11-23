package presentation;

/**
 * Contrato de navegación y control que usan los paneles y el ciclo del juego para hablar
 * con la ventana principal sin depender de su clase concreta.
 * La ventana principal (DOPOSoccerGUI) la implementa y hace de mediador.
 */
public interface VistaPrincipal {

    /** Muestra la pantalla de inicio. */
    void mostrarInicio();

    /** Muestra la pantalla de instrucciones (cómo jugar). */
    void mostrarInstrucciones();

    /** Muestra la pantalla de configuración para preparar un partido nuevo. */
    void mostrarConfiguracion();

    /** Revisa la configuración y, si es válida, muestra la pantalla de jugadores. */
    void confirmarConfiguracion();

    /** Inicia el partido con la configuración y los países elegidos. */
    void iniciarPartido();

    /**
     * Ejecuta un paso del juego: teclas presionadas, dominio y fin del partido.
     * @param segundos duración del paso
     */
    void pasoDeJuego(double segundos);

    /** Vuelve a pintar la cancha. */
    void refrescar();

    /** Detiene el ciclo del juego. */
    void detenerCiclo();
}
