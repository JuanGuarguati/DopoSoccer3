package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Centraliza los colores y las fuentes de toda la interfaz, para no repetirlos en cada pantalla.
 * También tiene métodos de ayuda para crear componentes con el estilo del juego
 * (títulos, etiquetas, botones redondeados, listas desplegables y cajas de texto).
 * No se crean objetos de esta clase: todo es estático.
 */
public final class Estilo {

    // ---------- Colores generales ----------
    /** Color de arriba del fondo degradado de las pantallas de menú. */
    public static final Color FONDO_ARRIBA = new Color(18, 32, 92);
    /** Color de abajo del fondo degradado de las pantallas de menú. */
    public static final Color FONDO_ABAJO = new Color(122, 34, 160);
    /** Color principal de los botones (naranja vivo). */
    public static final Color PRIMARIO = new Color(255, 120, 20);
    /** Color de los botones principales cuando el mouse pasa por encima. */
    public static final Color PRIMARIO_HOVER = new Color(255, 170, 40);
    /** Color secundario de los botones (verde vivo). */
    public static final Color SECUNDARIO = new Color(0, 185, 120);
    /** Color de los botones secundarios cuando el mouse pasa por encima. */
    public static final Color SECUNDARIO_HOVER = new Color(40, 225, 160);
    /** Color de los botones de peligro (terminar, salir). */
    public static final Color PELIGRO = new Color(230, 50, 80);
    /** Color de los botones de peligro cuando el mouse pasa por encima. */
    public static final Color PELIGRO_HOVER = new Color(255, 95, 120);
    /** Color de los botones desactivados. */
    public static final Color DESACTIVADO = new Color(130, 130, 145);
    /** Color de acento: amarillo para títulos y bordes resaltados. */
    public static final Color ACENTO = new Color(255, 215, 0);
    /** Color del texto claro (sobre fondos oscuros). */
    public static final Color TEXTO_CLARO = Color.WHITE;
    /** Color del texto oscuro (sobre fondos claros). */
    public static final Color TEXTO_OSCURO = new Color(25, 25, 50);
    /** Fondo semitransparente de las tarjetas (grupos de opciones). */
    public static final Color TARJETA = new Color(255, 255, 255, 40);
    /** Fondo de las listas desplegables y cajas de texto. */
    public static final Color CAMPO = new Color(250, 250, 255);
    /** Sombra de los títulos (azul oscuro). */
    public static final Color SOMBRA_TITULO = new Color(10, 15, 50);
    /** Fondo oscuro semitransparente del menú de pausa. */
    public static final Color FONDO_PAUSA = new Color(20, 20, 50, 245);

    // ---------- Colores de la cancha ----------
    /** Color del cielo. */
    public static final Color CIELO = new Color(110, 195, 255);
    /** Color del cielo cerca del horizonte (degradado). */
    public static final Color CIELO_ABAJO = new Color(200, 235, 255);
    /** Color del pasto. */
    public static final Color PASTO = new Color(60, 170, 70);
    /** Color de las franjas oscuras del pasto. */
    public static final Color PASTO_OSCURO = new Color(45, 145, 55);
    /** Color del jugador 1. */
    public static final Color COLOR_J1 = new Color(30, 110, 255);
    /** Color del jugador 2. */
    public static final Color COLOR_J2 = new Color(240, 50, 60);
    /** Colores {cabeza, banda, cuerpo} de un jugador aturdido. */
    public static final Color[] COLORES_ATURDIDO = {Color.LIGHT_GRAY, Color.GRAY, Color.GRAY};
    /** Color de la barra de vida. */
    public static final Color VIDA = new Color(40, 210, 70);
    /** Color de la sangre cuando un jugador recibe un golpe. */
    public static final Color SANGRE = new Color(200, 0, 0);
    /** Sombra de los botones (negro transparente). */
    public static final Color SOMBRA = new Color(0, 0, 0, 70);
    /** Brillo de la parte de arriba de los botones (blanco transparente). */
    public static final Color BRILLO = new Color(255, 255, 255, 60);
    /** Fondo del marcador de la cancha (negro transparente). */
    public static final Color FONDO_MARCADOR = new Color(0, 0, 0, 150);
    /** Capa que oscurece la cancha en pausa (negro transparente). */
    public static final Color OSCURECER_PAUSA = new Color(0, 0, 0, 120);
    /** Trazo de los bordes de tarjetas y del menú de pausa. */
    public static final BasicStroke TRAZO_BORDE = new BasicStroke(3);
    /** Trazo del borde de un botón cuando el mouse está encima. */
    public static final BasicStroke TRAZO_HOVER = new BasicStroke(2);

    // ---------- Fuentes ----------
    /** Familia llamativa para títulos (la primera que exista en el computador). */
    public static final String FAMILIA_TITULO = elegirFamilia("Impact", "Arial Black", "Segoe UI Black", Font.SANS_SERIF);
    /** Familia para textos y botones (la primera que exista en el computador). */
    public static final String FAMILIA_TEXTO = elegirFamilia("Segoe UI", "Verdana", "Tahoma", Font.SANS_SERIF);
    /** Grosor de los títulos: Impact ya es grueso; si no está instalada, se usa negrita para que se note. */
    public static final int GROSOR_TITULO = FAMILIA_TITULO.equals(Font.SANS_SERIF) ? Font.BOLD : Font.PLAIN;
    /** Fuente del título grande de la pantalla de inicio. */
    public static final Font TITULO_GRANDE = new Font(FAMILIA_TITULO, GROSOR_TITULO, 72);
    /** Fuente de los títulos de cada pantalla. */
    public static final Font TITULO = new Font(FAMILIA_TITULO, GROSOR_TITULO, 46);
    /** Fuente de los subtítulos (nombre de cada grupo). */
    public static final Font SUBTITULO = new Font(FAMILIA_TITULO, GROSOR_TITULO, 22);
    /** Fuente de los botones. */
    public static final Font BOTON = new Font(FAMILIA_TEXTO, Font.BOLD, 18);
    /** Fuente de las etiquetas y opciones. */
    public static final Font TEXTO = new Font(FAMILIA_TEXTO, Font.BOLD, 15);
    /** Fuente pequeña (ayudas e información de la cancha). */
    public static final Font TEXTO_PEQUENO = new Font(FAMILIA_TEXTO, Font.BOLD, 12);
    /** Radio de las esquinas redondeadas. */
    public static final int REDONDEO = 22;

    /**
     * No se crean objetos de esta clase.
     */
    private Estilo() {
    }

    /**
     * Escoge la primera familia de fuente que esté instalada.
     * @param familias familias en orden de preferencia (la última siempre existe)
     * @return familia elegida
     */
    private static String elegirFamilia(String... familias) {
        String[] instaladas = new String[0];
        try {
            instaladas = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        } catch (Exception e) {
            // Sin entorno gráfico: se usa la fuente lógica.
        }
        for (String familia : familias) {
            for (String instalada : instaladas) {
                if (instalada.equalsIgnoreCase(familia)) return instalada;
            }
        }
        return familias[familias.length - 1];
    }


    /**
     * Pinta el fondo degradado de las pantallas de menú, con círculos de colores de adorno.
     * @param g contexto gráfico
     * @param ancho ancho del panel
     * @param alto alto del panel
     */
    public static void pintarFondo(Graphics g, int ancho, int alto) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, FONDO_ARRIBA, ancho, alto, FONDO_ABAJO));
        g2.fillRect(0, 0, ancho, alto);
        g2.setColor(new Color(255, 120, 20, 45));
        g2.fillOval(-alto / 4, alto / 2, alto, alto);
        g2.setColor(new Color(0, 185, 120, 45));
        g2.fillOval(ancho - alto * 2 / 3, -alto / 3, alto, alto);
        g2.dispose();
    }

    /**
     * Crea un título con la fuente llamativa, en amarillo, centrado y con sombra oscura.
     * @param texto texto del título
     * @return JLabel del título
     */
    public static JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(SOMBRA_TITULO);
                g2.drawString(getText(), x + 3, y + 3);
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        titulo.setFont(TITULO);
        titulo.setForeground(ACENTO);
        return titulo;
    }

    /**
     * Crea una etiqueta de texto claro.
     * @param texto texto de la etiqueta
     * @return JLabel con el estilo del juego
     */
    public static JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TEXTO);
        etiqueta.setForeground(TEXTO_CLARO);
        return etiqueta;
    }

    /**
     * Crea un texto de ayuda en amarillo (por ejemplo, la tecla de una acción).
     * @param texto texto de la ayuda
     * @return JLabel con el estilo de ayuda
     */
    public static JLabel crearAyuda(String texto) {
        JLabel ayuda = crearEtiqueta(texto);
        ayuda.setForeground(ACENTO);
        return ayuda;
    }

    /**
     * Crea un botón principal (naranja) redondeado y con efecto al pasar el mouse.
     * @param texto texto del botón
     * @return el botón
     */
    public static BotonRedondeado crearBoton(String texto) {
        return new BotonRedondeado(texto, PRIMARIO, PRIMARIO_HOVER);
    }

    /**
     * Crea un botón secundario (verde) redondeado y con efecto al pasar el mouse.
     * @param texto texto del botón
     * @return el botón
     */
    public static BotonRedondeado crearBotonSecundario(String texto) {
        return new BotonRedondeado(texto, SECUNDARIO, SECUNDARIO_HOVER);
    }

    /**
     * Crea un botón de peligro (rojo) redondeado y con efecto al pasar el mouse.
     * @param texto texto del botón
     * @return el botón
     */
    public static BotonRedondeado crearBotonPeligro(String texto) {
        return new BotonRedondeado(texto, PELIGRO, PELIGRO_HOVER);
    }

    /**
     * Le da el estilo del juego a una lista desplegable: fuente, colores, borde redondeado
     * y borde amarillo al pasar el mouse.
     * @param combo JComboBox que se arregla
     */
    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(TEXTO);
        combo.setBackground(CAMPO);
        combo.setForeground(TEXTO_OSCURO);
        combo.setFocusable(false);
        combo.setBorder(bordeCampo(SECUNDARIO));
        combo.setRenderer(new DefaultListCellRenderer() {
            /** Pinta cada opción: la seleccionada en naranja con texto blanco. */
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                JLabel celda = (JLabel) super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                celda.setBorder(new EmptyBorder(4, 8, 4, 8));
                celda.setFont(TEXTO);
                celda.setBackground(seleccionado ? PRIMARIO : CAMPO);
                celda.setForeground(seleccionado ? TEXTO_CLARO : TEXTO_OSCURO);
                return celda;
            }
        });
        agregarHover(combo, combo);
        for (Component hijo : combo.getComponents()) agregarHover(hijo, combo);
    }

    /**
     * Le da el estilo del juego a una caja de texto.
     * @param campo JTextField que se arregla
     */
    public static void estilizarCampo(JTextField campo) {
        campo.setFont(TEXTO);
        campo.setBackground(CAMPO);
        campo.setForeground(TEXTO_OSCURO);
        campo.setHorizontalAlignment(SwingConstants.CENTER);
        campo.setBorder(bordeCampo(SECUNDARIO));
        agregarHover(campo, campo);
    }

    /**
     * Crea el borde redondeado de una lista o caja de texto.
     * @param color color de la línea
     * @return borde redondeado con margen interno
     */
    public static Border bordeCampo(Color color) {
        return new CompoundBorder(new LineBorder(color, 3, true), new EmptyBorder(3, 6, 3, 6));
    }

    /**
     * Cambia el borde de un componente a amarillo mientras el mouse está encima.
     * @param origen componente que recibe el mouse
     * @param destino componente al que se le cambia el borde
     */
    private static void agregarHover(Component origen, final JComponent destino) {
        origen.addMouseListener(new MouseAdapter() {
            /** El mouse entra: borde amarillo. */
            public void mouseEntered(MouseEvent e) {
                destino.setBorder(bordeCampo(ACENTO));
            }
            /** El mouse sale: borde normal. */
            public void mouseExited(MouseEvent e) {
                destino.setBorder(bordeCampo(SECUNDARIO));
            }
        });
    }
}
