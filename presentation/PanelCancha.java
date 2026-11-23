package presentation;

import domain.ConstantesJuego;
import domain.DOPOSoccer;
import domain.DOPOSoccerException;
import domain.Log;
import dto.ComandoDibujo;
import dto.EstadoJugador;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: dibuja la cancha, los arcos, el balón, los jugadores y el estado del juego
 * (tiempo, marcador y vida de cada jugador).
 * Todo se lee a través de la fachada DOPOSoccer como DTO: los elementos llegan como
 * ComandoDibujo (los pinta RenderizadorElementos) y los jugadores como EstadoJugador.
 * Las medidas del dominio se escalan a la resolución elegida y la cancha queda centrada.
 * Componentes Swing que contiene:
 * JButton de pausa (arriba a la derecha) y PanelPausa (JPanel con el menú de pausa).
 * Recibe el teclado (KeyListener) que le asigna la ventana principal.
 * Los colores y fuentes salen de la clase Estilo.
 */
public class PanelCancha extends JPanel {

    /** Ancho del recuadro del marcador. */
    public static final int ANCHO_MARCADOR = 180;
    /** Alto del recuadro del marcador. */
    public static final int ALTO_MARCADOR = 60;
    /** Alto de la franja de arriba (marcador e información) que no se oscurece en pausa. */
    public static final int ALTO_INFORMACION = 100;
    /** Ancho de la barra de vida. */
    public static final int ANCHO_BARRA = 220;
    /** Alto de la barra de vida. */
    public static final int ALTO_BARRA = 14;
    /** Ancho de la barra de poder (flama). */
    public static final int ANCHO_PODER = 250;
    /** Alto de la barra de poder (flama). */
    public static final int ALTO_PODER = 26;
    /** Radio de la cabeza que se dibuja junto a las barras. */
    public static final int RADIO_CABEZA = 28;
    /** Espacio que ocupa la cabeza antes de las barras. */
    public static final int ESPACIO_CABEZA = 66;
    /** Altura de la barra de poder. */
    public static final int Y_PODER = 28;
    /** Altura de la barra de vida. */
    public static final int Y_VIDA = 60;
    /** Margen de la información de cada jugador hasta el borde. */
    public static final int MARGEN = 20;
    /** Fuente del marcador. */
    private static final Font FUENTE_MARCADOR = Estilo.SUBTITULO.deriveFont(30f);
    /** Fuente del texto de la vida. */
    private static final Font FUENTE_VIDA = Estilo.TEXTO_PEQUENO.deriveFont(11f);
    /** Color de la barra de poder vacía. */
    private static final Color PODER_CLARO = new Color(255, 245, 170);
    /** Color de la barra de poder a la mitad. */
    private static final Color PODER_MEDIO = new Color(255, 140, 0);
    /** Color de la barra de poder llena. */
    private static final Color PODER_ROJO = new Color(210, 0, 0);
    /** Fondo de la barra de poder. */
    private static final Color FONDO_PODER = new Color(60, 25, 10, 200);

    /** Fachada del dominio de donde se lee el estado del partido. */
    private DOPOSoccer dominio;
    /** Botón redondeado "II" para pausar el partido. */
    private BotonRedondeado botonPausa;
    /** JPanel: menú de pausa que aparece encima de la cancha. */
    private PanelPausa panelPausa;

    /**
     * Crea el panel de la cancha.
     * @param dominio fachada del juego de donde se lee el estado
     */
    public PanelCancha(DOPOSoccer dominio) {
        this.dominio = dominio;
        setFocusable(true);
        setBackground(Estilo.CIELO);
        prepareView();
    }

    /**
     * Ubica el botón de pausa arriba a la derecha (debajo de la información del jugador 2)
     * y el menú de pausa en el centro, oculto hasta que se pause el partido.
     */
    private void prepareView() {
        setLayout(new BorderLayout());
        // JButton de pausa: no toma el foco para que el teclado siga en la cancha
        botonPausa = Estilo.crearBoton("II");
        botonPausa.setFocusable(false);
        botonPausa.setToolTipText("Pausar (P)");
        // JPanel transparente arriba (NORTH) que ubica el botón a la derecha
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        arriba.setOpaque(false);
        arriba.setBorder(new EmptyBorder(ALTO_INFORMACION + 4, 0, 0, 15));
        arriba.add(botonPausa);
        add(arriba, BorderLayout.NORTH);
        // PanelPausa (JPanel): oculto hasta que se pause
        panelPausa = new PanelPausa();
        panelPausa.setVisible(false);
        // JPanel transparente en el centro (GridBagLayout) para centrar el menú
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(panelPausa);
        add(centro, BorderLayout.CENTER);
    }

    /**
     * Muestra u oculta el menú de pausa (y oculta el JButton de pausa mientras se ve el menú).
     * @param visible true para mostrarlo
     */
    public void mostrarPausa(boolean visible) {
        panelPausa.setContraMaquina(dominio.esContraMaquina());
        panelPausa.setVisible(visible);
        botonPausa.setVisible(!visible);
        revalidate();
        repaint();
    }

    /**
     * @return JButton de pausa de la pantalla
     */
    public JButton getBotonPausa() { return botonPausa; }

    /**
     * @return menú de pausa (JPanel)
     */
    public PanelPausa getPanelPausa() { return panelPausa; }

    /**
     * Dibuja todo el estado del partido. Swing lo llama cada vez que se hace repaint().
     * La cancha se escala a la resolución y se centra en el panel.
     * @param g contexto gráfico (Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!dominio.hayPartido()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            int ancho = ConstantesJuego.ANCHO_CANCHA;
            int alto = ConstantesJuego.ALTO_CANCHA;
            double escala = Math.min(getWidth() / (double) ancho, getHeight() / (double) alto);
            g2.translate((getWidth() - ancho * escala) / 2, (getHeight() - alto * escala) / 2);
            g2.scale(escala, escala);
            for (ComandoDibujo comando : dominio.getComandosDibujo()) {
                RenderizadorElementos.dibujar(g2, comando);
            }
            dibujarEstado(g2, ancho, alto);
        } catch (DOPOSoccerException e) {
            Log.record(e);
        } finally {
            g2.dispose();
        }
    }

    /**
     * Dibuja el marcador, el tiempo y la información de cada jugador.
     * Si el partido está en pausa, oscurece la cancha (no el marcador).
     * @param g contexto gráfico
     * @param ancho ancho lógico de la cancha
     * @param alto alto lógico de la cancha
     * @throws DOPOSoccerException si no se puede leer el partido
     */
    private void dibujarEstado(Graphics2D g, int ancho, int alto) throws DOPOSoccerException {
        EstadoJugador j1 = dominio.getEstadoJugador(1);
        EstadoJugador j2 = dominio.getEstadoJugador(2);
        int centro = ancho / 2;
        g.setColor(Estilo.FONDO_MARCADOR);
        g.fillRoundRect(centro - ANCHO_MARCADOR / 2, 8, ANCHO_MARCADOR, ALTO_MARCADOR, 15, 15);
        g.setColor(Color.WHITE);
        g.setFont(FUENTE_MARCADOR);
        String marcador = j1.goles + " - " + j2.goles;
        g.drawString(marcador, centro - g.getFontMetrics().stringWidth(marcador) / 2, 40);
        g.setFont(Estilo.TEXTO_PEQUENO);
        String tiempo = dominio.getTextoTiempo();
        g.drawString(tiempo, centro - g.getFontMetrics().stringWidth(tiempo) / 2, 60);
        dibujarInfoJugador(g, j1, MARGEN, true, Estilo.COLOR_J1);
        dibujarInfoJugador(g, j2, ancho - MARGEN, false, Estilo.COLOR_J2);
        if (dominio.estaPausado()) {
            g.setColor(Estilo.OSCURECER_PAUSA);
            g.fillRect(0, ALTO_INFORMACION, ancho, alto - ALTO_INFORMACION);
        }
    }

    /**
     * Dibuja la información de un jugador: en la esquina la cabeza de su país, al lado el nombre,
     * la barra de poder en forma de flama (arriba) y la barra de vida (debajo), y abajo su perfil
     * si es la máquina. El jugador 2 se dibuja como en un espejo.
     * @param g contexto gráfico
     * @param j estado del jugador (DTO)
     * @param borde borde de la pantalla donde va la cabeza (izquierdo o derecho)
     * @param izquierda true si la información va a la izquierda (jugador 1)
     * @param color color del jugador
     */
    private void dibujarInfoJugador(Graphics2D g, EstadoJugador j, int borde, boolean izquierda, Color color) {
        int lado = izquierda ? 1 : -1;
        int cabezaX = borde + lado * RADIO_CABEZA;
        int inicio = borde + lado * ESPACIO_CABEZA;
        int xPoder = izquierda ? inicio : inicio - ANCHO_PODER;
        int xVida = izquierda ? inicio : inicio - ANCHO_BARRA;
        RenderizadorElementos.dibujarCabeza(g, cabezaX, Y_VIDA - 8, RADIO_CABEZA,
            j.aturdido ? Estilo.COLORES_ATURDIDO : j.colores, izquierda, j.aturdido ? Color.GRAY : color);
        g.setColor(color);
        g.setFont(Estilo.TEXTO);
        String nombre = (j.maquina ? "CPU" : "J" + j.numero) + " - " + j.pais;
        g.drawString(nombre, izquierda ? inicio : inicio - g.getFontMetrics().stringWidth(nombre), 22);
        dibujarFlama(g, xPoder, Y_PODER, j.barraPoder, izquierda);
        dibujarBarra(g, xVida, Y_VIDA, j.vida / j.vidaMax, j.aturdido ? Color.GRAY : Estilo.VIDA);
        g.setFont(FUENTE_VIDA);
        g.setColor(Color.WHITE);
        g.drawString("Vida " + (int) j.vida + "/" + (int) j.vidaMax, xVida + 6, Y_VIDA + 11);
        String poder = j.barraPoder >= 1 ? "¡Poder listo!" : "Poder " + (int) (j.barraPoder * 100) + " %";
        int xTexto = izquierda ? xPoder + 12 : xPoder + ANCHO_PODER - 12 - g.getFontMetrics().stringWidth(poder);
        g.drawString(poder, xTexto, Y_PODER + ALTO_PODER - 8);
        if (j.maquina) {
            g.setFont(Estilo.TEXTO_PEQUENO);
            g.setColor(color);
            String perfil = "Máquina: " + j.perfil;
            g.drawString(perfil, izquierda ? inicio : inicio - g.getFontMetrics().stringWidth(perfil), 92);
        }
    }

    /**
     * Arma la figura de la barra de poder: una flama acostada, con la cola redonda en un extremo,
     * lenguas de fuego arriba (más altas hacia la punta) y la punta hacia el centro de la pantalla.
     * @param x borde izquierdo
     * @param y borde de arriba
     * @param puntaDerecha true si la punta va a la derecha
     * @return figura de la barra
     */
    private Shape formaFlama(int x, int y, boolean puntaDerecha) {
        double w = ANCHO_PODER;
        double h = ALTO_PODER;
        double arriba = h * 0.35;
        Path2D figura = new Path2D.Double();
        figura.moveTo(h * 0.35, h);
        figura.lineTo(w * 0.8, h);
        figura.quadTo(w * 0.93, h, w, h * 0.1);
        figura.quadTo(w * 0.9, h * 0.35, w * 0.84, arriba);
        int lenguas = 6;
        for (int k = lenguas; k > 0; k--) {
            double fin = w * 0.84 * (k - 1) / lenguas + h * 0.2;
            double alto = arriba - (arriba - 1) * k / lenguas;
            double medio = (w * 0.84 * k / lenguas + fin) / 2;
            figura.quadTo(medio + w * 0.03, alto + h * 0.05, medio + w * 0.02, alto - h * 0.02);
            figura.quadTo(medio - w * 0.02, arriba * 0.9, fin, arriba);
        }
        figura.quadTo(0, arriba, 0, h * 0.68);
        figura.quadTo(0, h, h * 0.35, h);
        figura.closePath();
        AffineTransform ubicar = new AffineTransform();
        ubicar.translate(x, y);
        if (!puntaDerecha) {
            ubicar.translate(w, 0);
            ubicar.scale(-1, 1);
        }
        return ubicar.createTransformedShape(figura);
    }

    /**
     * Dibuja la barra de poder con forma de flama. Se llena desde la cola hacia la punta y su color
     * cambia gradualmente con el porcentaje: de claro a naranja y hasta rojo cuando está llena.
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param y borde de arriba
     * @param fraccion parte llena, entre 0 y 1
     * @param puntaDerecha true si la punta va a la derecha (jugador 1)
     */
    private void dibujarFlama(Graphics2D g, int x, int y, double fraccion, boolean puntaDerecha) {
        Shape flama = formaFlama(x, y, puntaDerecha);
        double f = Math.max(0, Math.min(1, fraccion));
        g.setColor(FONDO_PODER);
        g.fill(flama);
        Shape antes = g.getClip();
        g.clip(flama);
        int lleno = (int) (ANCHO_PODER * f);
        int xLleno = puntaDerecha ? x : x + ANCHO_PODER - lleno;
        Color color = colorPoder(f);
        g.setPaint(new GradientPaint(0, y, color.brighter(), 0, y + ALTO_PODER, color.darker()));
        g.fillRect(xLleno, y, lleno, ALTO_PODER);
        g.setClip(antes);
        g.setColor(Color.BLACK);
        g.draw(flama);
    }

    /**
     * Color de la barra de poder según el porcentaje: pasa de claro (vacía) a naranja (mitad)
     * y a rojo (llena), mezclando los colores poco a poco.
     * @param fraccion carga entre 0 y 1
     * @return color de la barra
     */
    public static Color colorPoder(double fraccion) {
        double f = Math.max(0, Math.min(1, fraccion));
        return f < 0.5 ? mezclar(PODER_CLARO, PODER_MEDIO, f * 2) : mezclar(PODER_MEDIO, PODER_ROJO, (f - 0.5) * 2);
    }

    /**
     * Mezcla dos colores.
     * @param a color inicial
     * @param b color final
     * @param t cuánto del color final (0 a 1)
     * @return color mezclado
     */
    private static Color mezclar(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                         (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                         (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    /**
     * Dibuja una barra horizontal llena en una fracción.
     * @param g contexto gráfico
     * @param x posición horizontal
     * @param y posición vertical
     * @param fraccion parte llena, entre 0 y 1
     * @param color color de la parte llena
     */
    private void dibujarBarra(Graphics2D g, int x, int y, double fraccion, Color color) {
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, ANCHO_BARRA, ALTO_BARRA, ALTO_BARRA, ALTO_BARRA);
        g.setColor(color);
        int lleno = (int) (ANCHO_BARRA * Math.max(0, Math.min(1, fraccion)));
        g.fillRoundRect(x, y, lleno, ALTO_BARRA, ALTO_BARRA, ALTO_BARRA);
    }
}
