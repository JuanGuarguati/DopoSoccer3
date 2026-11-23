package presentation;

import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 * JPanel base de las pantallas de menú: pinta el fondo degradado de colores de Estilo.
 * Lo heredan PanelInstrucciones, PanelConfiguracion y PanelJugadores.
 */
public class PanelDegradado extends JPanel {

    /**
     * Crea el panel con el layout dado.
     * @param layout administrador de diseño del panel
     */
    public PanelDegradado(LayoutManager layout) {
        super(layout);
    }

    /**
     * Pinta el fondo degradado de colores.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Estilo.pintarFondo(g, getWidth(), getHeight());
    }
}
