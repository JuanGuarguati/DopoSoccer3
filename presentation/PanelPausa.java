package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: menú que aparece sobre la cancha cuando el partido está en pausa.
 * Componentes Swing que contiene:
 * JLabel con el título "PAUSA" y seis BotonRedondeado (continuar, terminar partido,
 * guardar partida, abrir partida, cambiar máquina y volver al menú).
 * La ventana principal (JFrame) le asigna la acción a cada botón.
 * Los colores y fuentes salen de la clase Estilo.
 */
public class PanelPausa extends JPanel {

    /** Botón que reanuda el partido. */
    private BotonRedondeado botonContinuar;
    /** Botón que termina el partido. */
    private BotonRedondeado botonTerminar;
    /** Botón que guarda la partida en un archivo. */
    private BotonRedondeado botonGuardar;
    /** Botón que abre una partida guardada. */
    private BotonRedondeado botonAbrir;
    /** Botón que cambia la estrategia de la máquina. */
    private BotonRedondeado botonEstrategia;
    /** Botón que vuelve a la configuración. */
    private BotonRedondeado botonMenu;

    /**
     * Crea el menú de pausa.
     */
    public PanelPausa() {
        prepareView();
    }

    /**
     * Organiza el JLabel del título (arriba) y los seis botones en dos columnas (centro).
     */
    private void prepareView() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(20, 30, 25, 30));
        JLabel titulo = Estilo.crearTitulo("PAUSA");
        add(titulo, BorderLayout.NORTH);
        botonContinuar = prepararBoton(Estilo.crearBotonSecundario("Continuar"));
        botonTerminar = prepararBoton(Estilo.crearBotonPeligro("Terminar partido"));
        botonGuardar = prepararBoton(Estilo.crearBoton("Guardar partida"));
        botonAbrir = prepararBoton(Estilo.crearBoton("Abrir partida"));
        botonEstrategia = prepararBoton(Estilo.crearBoton("Cambiar máquina"));
        botonMenu = prepararBoton(Estilo.crearBotonPeligro("Volver al menú"));
        // JPanel transparente con GridLayout 3x2 para los botones
        JPanel botones = new JPanel(new GridLayout(3, 2, 15, 12));
        botones.setOpaque(false);
        botones.add(botonContinuar);
        botones.add(botonTerminar);
        botones.add(botonGuardar);
        botones.add(botonAbrir);
        botones.add(botonEstrategia);
        botones.add(botonMenu);
        add(botones, BorderLayout.CENTER);
    }

    /**
     * Ajusta un botón del menú: tamaño fijo y sin tomar el foco, para que el teclado siga en la cancha.
     * @param boton botón que se ajusta
     * @return el mismo botón
     */
    private BotonRedondeado prepararBoton(BotonRedondeado boton) {
        boton.setFocusable(false);
        boton.setPreferredSize(new Dimension(220, 48));
        return boton;
    }

    /**
     * Activa el botón de cambiar máquina solo si el partido es contra la máquina.
     * @param contraMaquina true si el jugador 2 es la máquina
     */
    public void setContraMaquina(boolean contraMaquina) {
        botonEstrategia.setEnabled(contraMaquina);
    }

    /**
     * Dibuja el fondo redondeado y semitransparente del menú, con borde amarillo.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Estilo.FONDO_PAUSA);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.setColor(Estilo.ACENTO);
        g2.setStroke(Estilo.TRAZO_BORDE);
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * @return botón Continuar
     */
    public JButton getBotonContinuar() { return botonContinuar; }

    /**
     * @return botón Terminar partido
     */
    public JButton getBotonTerminar() { return botonTerminar; }

    /**
     * @return botón Guardar partida
     */
    public JButton getBotonGuardar() { return botonGuardar; }

    /**
     * @return botón Abrir partida
     */
    public JButton getBotonAbrir() { return botonAbrir; }

    /**
     * @return botón Cambiar máquina
     */
    public JButton getBotonEstrategia() { return botonEstrategia; }

    /**
     * @return botón Volver al menú
     */
    public JButton getBotonMenu() { return botonMenu; }
}
