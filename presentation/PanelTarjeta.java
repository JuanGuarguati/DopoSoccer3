package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JPanel transparente con esquinas redondeadas que agrupa opciones (reemplaza el TitledBorder).
 * Dibuja un título con la fuente llamativa y una línea del color que se le indique.
 */
public class PanelTarjeta extends JPanel {

    /** Título que se dibuja arriba de la tarjeta. */
    private String titulo;
    /** Color de la línea del borde y del título. */
    private Color color;

    /**
     * Crea una tarjeta.
     * @param titulo título de la tarjeta
     * @param color color del borde y del título
     * @param layout organización (layout) de los componentes de adentro
     */
    public PanelTarjeta(String titulo, Color color, LayoutManager layout) {
        super(layout);
        this.titulo = titulo;
        this.color = color;
        setOpaque(false);
        setBorder(new EmptyBorder(44, 18, 16, 18));
    }

    /**
     * Cambia el título de la tarjeta.
     * @param titulo nuevo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }

    /**
     * @return título de la tarjeta
     */
    public String getTitulo() { return titulo; }

    /**
     * Dibuja el fondo semitransparente redondeado, el borde y el título.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Estilo.TARJETA);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Estilo.REDONDEO, Estilo.REDONDEO);
        g2.setColor(color);
        g2.setStroke(Estilo.TRAZO_BORDE);
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, Estilo.REDONDEO, Estilo.REDONDEO);
        g2.setFont(Estilo.SUBTITULO);
        g2.drawString(titulo, 18, 32);
        g2.dispose();
        super.paintComponent(g);
    }
}
