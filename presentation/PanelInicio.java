package presentation;

import domain.Pais;
import domain.Personaje;
import dto.ComandoDibujo;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JPanel: pantalla de inicio de DOPOSoccer. Dibuja el título, la cancha y dos jugadores
 * (Corea del Sur y Camerún) con sus banderas, y tiene los botones para empezar y para ver
 * las instrucciones. Los jugadores se pintan con RenderizadorElementos (igual que en la cancha).
 * Los colores y fuentes salen de la clase Estilo.
 */
public class PanelInicio extends JPanel {

    /** Radio de la cabeza de los jugadores de la portada. */
    public static final int RADIO_PORTADA = 40;

    /** Botón redondeado para empezar. */
    private BotonRedondeado botonIniciar;
    /** Botón redondeado para ver las instrucciones. */
    private BotonRedondeado botonInstrucciones;
    /** Ventana principal (navegación). */
    private VistaPrincipal vista;

    /**
     * Crea la pantalla de inicio.
     * @param vista ventana principal (navegación)
     */
    public PanelInicio(VistaPrincipal vista) {
        this.vista = vista;
        prepareView();
        prepareController();
    }

    /**
     * Ubica el botón de inicio abajo, en el centro.
     */
    private void prepareView() {
        setLayout(new BorderLayout());
        setBackground(Estilo.CIELO);
        botonIniciar = Estilo.crearBoton("¡JUGAR!");
        botonIniciar.setFont(Estilo.SUBTITULO);
        botonIniciar.setPreferredSize(new Dimension(220, 60));
        botonInstrucciones = Estilo.crearBotonSecundario("Cómo jugar");
        botonInstrucciones.setPreferredSize(new Dimension(180, 60));
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        abajo.setOpaque(false);
        abajo.setBorder(new EmptyBorder(0, 0, 30, 0));
        abajo.add(botonInstrucciones);
        abajo.add(botonIniciar);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Oyentes: ¡JUGAR! lleva a la configuración y Cómo jugar a las instrucciones.
     */
    private void prepareController() {
        botonIniciar.addActionListener(new ActionListener() {
            /** Atiende el botón ¡JUGAR!. */
            public void actionPerformed(ActionEvent e) {
                vista.mostrarConfiguracion();
            }
        });
        botonInstrucciones.addActionListener(new ActionListener() {
            /** Atiende el botón Cómo jugar. */
            public void actionPerformed(ActionEvent e) {
                vista.mostrarInstrucciones();
            }
        });
    }

    /**
     * Dibuja el cielo degradado, el pasto a franjas, el título con sombra, los dos jugadores y sus banderas.
     * @param g contexto gráfico (Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = getWidth();
        int alto = getHeight();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int piso = alto * 3 / 4;
        g2.setPaint(new GradientPaint(0, 0, Estilo.CIELO, 0, piso, Estilo.CIELO_ABAJO));
        g2.fillRect(0, 0, ancho, piso);
        int franja = Math.max(1, ancho / 10);
        for (int i = 0; i * franja < ancho; i++) {
            g2.setColor(i % 2 == 0 ? Estilo.PASTO : Estilo.PASTO_OSCURO);
            g2.fillRect(i * franja, piso, franja + 1, alto - piso);
        }
        g2.setFont(Estilo.TITULO_GRANDE);
        String titulo = "DOPOSoccer";
        int xTitulo = (ancho - g2.getFontMetrics().stringWidth(titulo)) / 2;
        g2.setColor(Estilo.FONDO_ARRIBA);
        g2.drawString(titulo, xTitulo + 4, alto / 5 + 4);
        g2.setColor(Estilo.ACENTO);
        g2.drawString(titulo, xTitulo, alto / 5);
        int r = RADIO_PORTADA;
        int y = piso - 2 * r - r / 2;
        RenderizadorElementos.dibujar(g2, comandoPortada(Pais.COREA_DEL_SUR, 1, ancho / 4, y, true));
        RenderizadorElementos.dibujar(g2, comandoPortada(Pais.CAMERUN, 2, ancho * 3 / 4, y, false));
        dibujarBanderaCorea(g2, ancho / 4 - 45, alto / 5 + 30, 90, 60);
        dibujarBanderaCamerun(g2, ancho * 3 / 4 - 45, alto / 5 + 30, 90, 60);
    }

    /**
     * Arma el ComandoDibujo de un jugador de la portada con los colores de su país.
     * @param pais país del jugador
     * @param numero número del jugador (define el color del borde)
     * @param x centro horizontal de la cabeza
     * @param y centro vertical de la cabeza
     * @param derecha true si mira a la derecha
     * @return comando de dibujo del jugador
     */
    private ComandoDibujo comandoPortada(Pais pais, int numero, int x, int y, boolean derecha) {
        Personaje personaje = pais.crearPersonaje();
        return new ComandoDibujo(ComandoDibujo.Forma.JUGADOR, x, y, RADIO_PORTADA, RADIO_PORTADA * 2, numero,
            personaje.getColores(), personaje.tieneRayas(), pais.getNombre(), derecha, -1, false, -1);
    }

    /**
     * Dibuja una bandera simplificada de Corea del Sur.
     * @param g contexto gráfico
     * @param x esquina izquierda
     * @param y esquina superior
     * @param w ancho
     * @param h alto
     */
    private void dibujarBanderaCorea(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        int d = h / 2;
        int cx = x + w / 2 - d / 2;
        int cy = y + h / 2 - d / 2;
        Color[] colores = Pais.COREA_DEL_SUR.crearPersonaje().getColores();
        g.setColor(colores[1]);
        g.fillArc(cx, cy, d, d, 0, 180);
        g.setColor(colores[2]);
        g.fillArc(cx, cy, d, d, 180, 180);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    /**
     * Dibuja una bandera simplificada de Camerún.
     * @param g contexto gráfico
     * @param x esquina izquierda
     * @param y esquina superior
     * @param w ancho
     * @param h alto
     */
    private void dibujarBanderaCamerun(Graphics2D g, int x, int y, int w, int h) {
        int tercio = w / 3;
        Color[] colores = Pais.CAMERUN.crearPersonaje().getColores();
        g.setColor(colores[0]);
        g.fillRect(x, y, tercio, h);
        g.setColor(colores[2]);
        g.fillRect(x + tercio, y, tercio, h);
        g.setColor(colores[1]);
        g.fillRect(x + 2 * tercio, y, w - 2 * tercio, h);
        g.fillOval(x + w / 2 - 6, y + h / 2 - 6, 12, 12);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }
}
