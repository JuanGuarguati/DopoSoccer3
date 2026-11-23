package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JButton con esquinas redondeadas, color de fondo propio y efecto al pasar el mouse
 * (se aclara y crece un poco su sombra). Los colores vienen de la clase Estilo.
 */
public class BotonRedondeado extends JButton {

    /** Color normal del botón. */
    private Color colorBase;
    /** Color del botón cuando el mouse está encima. */
    private Color colorHover;
    /** true mientras el mouse está encima del botón. */
    private boolean encima;

    /**
     * Crea un botón redondeado.
     * @param texto texto del botón
     * @param colorBase color normal
     * @param colorHover color al pasar el mouse
     */
    public BotonRedondeado(String texto, Color colorBase, Color colorHover) {
        super(texto);
        this.colorBase = colorBase;
        this.colorHover = colorHover;
        setFont(Estilo.BOTON);
        setForeground(Estilo.TEXTO_CLARO);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 22, 10, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        prepareController();
    }

    /**
     * Oyente del mouse: marca si el mouse está encima para pintar el efecto.
     */
    private void prepareController() {
        addMouseListener(new MouseAdapter() {
            /** El mouse entra al botón. */
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }
            /** El mouse sale del botón. */
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    /**
     * Dibuja la sombra, el fondo redondeado (normal, encima, presionado o desactivado),
     * un brillo arriba y luego el texto.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = getWidth();
        int alto = getHeight();
        int sombra = encima && isEnabled() ? 5 : 3;
        g2.setColor(Estilo.SOMBRA);
        g2.fillRoundRect(2, sombra, ancho - 4, alto - sombra, Estilo.REDONDEO, Estilo.REDONDEO);
        Color fondo = !isEnabled() ? Estilo.DESACTIVADO
            : getModel().isPressed() ? colorBase.darker()
            : encima ? colorHover : colorBase;
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, ancho - 2, alto - sombra, Estilo.REDONDEO, Estilo.REDONDEO);
        g2.setColor(Estilo.BRILLO);
        g2.fillRoundRect(4, 3, ancho - 10, (alto - sombra) / 2 - 2, Estilo.REDONDEO, Estilo.REDONDEO);
        if (encima && isEnabled()) {
            g2.setColor(Estilo.ACENTO);
            g2.setStroke(Estilo.TRAZO_HOVER);
            g2.drawRoundRect(1, 1, ancho - 4, alto - sombra - 2, Estilo.REDONDEO, Estilo.REDONDEO);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * @return true si el mouse está encima del botón
     */
    public boolean estaEncima() { return encima; }
}
