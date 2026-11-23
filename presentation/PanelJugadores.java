package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: pantalla de jugadores. Cada jugador elige su país y gasta sus puntos
 * mejorando sus estadísticas (velocidad, patada, salto, resistencia) antes del partido.
 * Componentes Swing que usa:
 * JComboBox (país), JLabel (puntos, precio y valor de cada estadística),
 * BotonRedondeado (mejorar, volver e iniciar) y PanelTarjeta (un grupo por jugador).
 * Hereda el fondo de PanelDegradado y navega con la VistaPrincipal.
 * Los colores y fuentes salen de la clase Estilo.
 */
public class PanelJugadores extends PanelDegradado {

    /** Texto de los controles del jugador 1. */
    public static final String CONTROLES_J1 = "A D: mover, W saltar, S patear";
    /** Texto de los controles del jugador 2. */
    public static final String CONTROLES_J2 = "← →: mover, ↑ saltar, ↓ patear";

    /** Fachada del dominio de donde se leen puntos y estadísticas. */
    private DOPOSoccer dominio;
    /** JComboBox: país del jugador 1. */
    private JComboBox<Pais> comboPais1;
    /** JComboBox: país del jugador 2. */
    private JComboBox<Pais> comboPais2;
    /** JLabel: puntos de cada jugador. */
    private JLabel[] etiquetasPuntos;
    /** JLabel: precio de la siguiente mejora de cada jugador. */
    private JLabel[] etiquetasPrecio;
    /** JLabel: valor de cada estadística, [jugador][estadística]. */
    private JLabel[][] etiquetasValor;
    /** Botón "+" de cada estadística, [jugador][estadística]. */
    private BotonRedondeado[][] botonesMejorar;
    /** Tarjeta (grupo) de cada jugador. */
    private PanelTarjeta[] tarjetas;
    /** JLabel: controles del jugador 2 (cambia si es la máquina). */
    private JLabel etiquetaControles2;
    /** Botón que vuelve a la configuración. */
    private BotonRedondeado botonVolver;
    /** Botón que inicia el partido. */
    private BotonRedondeado botonIniciar;
    /** Ventana principal (navegación: volver e iniciar el partido). */
    private VistaPrincipal vista;

    /**
     * Crea la pantalla de jugadores.
     * @param dominio fachada del juego
     * @param vista ventana principal (navegación)
     */
    public PanelJugadores(DOPOSoccer dominio, VistaPrincipal vista) {
        super(new BorderLayout(10, 12));
        this.dominio = dominio;
        this.vista = vista;
        prepareView();
        prepareController();
        actualizar();
    }

    /**
     * Organiza los componentes: título, un grupo por jugador y los botones de abajo.
     */
    private void prepareView() {
        setBorder(new EmptyBorder(16, 40, 16, 40));
        add(Estilo.crearTitulo("JUGADORES"), BorderLayout.NORTH);

        comboPais1 = new JComboBox<>(Pais.values());
        comboPais2 = new JComboBox<>(Pais.values());
        comboPais2.setSelectedIndex(1);
        Estilo.estilizarCombo(comboPais1);
        Estilo.estilizarCombo(comboPais2);
        int n = Estadistica.values().length;
        etiquetasPuntos = new JLabel[2];
        etiquetasPrecio = new JLabel[2];
        etiquetasValor = new JLabel[2][n];
        botonesMejorar = new BotonRedondeado[2][n];
        tarjetas = new PanelTarjeta[2];
        etiquetaControles2 = Estilo.crearEtiqueta(CONTROLES_J2);

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        centro.setOpaque(false);
        centro.add(crearGrupo(1, comboPais1, Estilo.COLOR_J1, Estilo.crearEtiqueta(CONTROLES_J1)));
        centro.add(crearGrupo(2, comboPais2, Estilo.COLOR_J2, etiquetaControles2));
        add(centro, BorderLayout.CENTER);

        botonVolver = Estilo.crearBotonSecundario("<  Volver");
        botonIniciar = Estilo.crearBoton("¡Iniciar partido!");
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        abajo.setOpaque(false);
        abajo.add(botonVolver);
        abajo.add(botonIniciar);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Arma el grupo de un jugador: país, puntos, precio, cada estadística con su botón "+" y los controles.
     * @param numero número del jugador (1 o 2)
     * @param comboPais JComboBox del país de ese jugador
     * @param color color del jugador
     * @param controles JLabel con las teclas del jugador
     * @return la tarjeta del grupo
     */
    private PanelTarjeta crearGrupo(int numero, JComboBox<Pais> comboPais, Color color, JLabel controles) {
        int i = numero - 1;
        Estadistica[] estadisticas = Estadistica.values();
        PanelTarjeta grupo = new PanelTarjeta("Jugador " + numero, color,
            new GridLayout(estadisticas.length + 4, 2, 8, 5));
        tarjetas[i] = grupo;
        grupo.add(Estilo.crearEtiqueta("País:"));
        grupo.add(comboPais);
        etiquetasPuntos[i] = Estilo.crearEtiqueta("");
        etiquetasPuntos[i].setForeground(Estilo.ACENTO);
        etiquetasPrecio[i] = Estilo.crearEtiqueta("");
        grupo.add(Estilo.crearEtiqueta("Puntos:"));
        grupo.add(etiquetasPuntos[i]);
        grupo.add(Estilo.crearEtiqueta("Precio de la mejora:"));
        grupo.add(etiquetasPrecio[i]);
        for (int k = 0; k < estadisticas.length; k++) {
            etiquetasValor[i][k] = Estilo.crearEtiqueta("");
            botonesMejorar[i][k] = Estilo.crearBotonSecundario("+");
            botonesMejorar[i][k].setBorder(new EmptyBorder(2, 12, 2, 12));
            botonesMejorar[i][k].setToolTipText("Mejorar " + estadisticas[k].getNombre());
            JPanel fila = new JPanel(new BorderLayout(6, 0));
            fila.setOpaque(false);
            fila.add(etiquetasValor[i][k], BorderLayout.CENTER);
            fila.add(botonesMejorar[i][k], BorderLayout.EAST);
            grupo.add(Estilo.crearEtiqueta(estadisticas[k].getNombre() + ":"));
            grupo.add(fila);
        }
        grupo.add(Estilo.crearEtiqueta("Controles:"));
        controles.setFont(Estilo.TEXTO_PEQUENO);
        grupo.add(controles);
        return grupo;
    }

    /**
     * Oyentes (ActionListener): cada botón "+" mejora su estadística; Volver regresa a la
     * configuración e Iniciar le pide a la vista empezar el partido.
     */
    private void prepareController() {
        botonVolver.addActionListener(new ActionListener() {
            /** Atiende el botón Volver. */
            public void actionPerformed(ActionEvent e) {
                vista.mostrarConfiguracion();
            }
        });
        botonIniciar.addActionListener(new ActionListener() {
            /** Atiende el botón Iniciar partido. */
            public void actionPerformed(ActionEvent e) {
                vista.iniciarPartido();
            }
        });
        Estadistica[] estadisticas = Estadistica.values();
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < estadisticas.length; k++) {
                final int numero = i + 1;
                final Estadistica estadistica = estadisticas[k];
                botonesMejorar[i][k].addActionListener(new ActionListener() {
                    /** Atiende el botón "+" de una estadística. */
                    public void actionPerformed(ActionEvent e) {
                        mejorarAction(numero, estadistica);
                    }
                });
            }
        }
    }

    /**
     * Compra una mejora para un jugador; si no se puede, muestra el motivo.
     * @param numero número del jugador (1 o 2)
     * @param estadistica estadística a mejorar
     */
    private void mejorarAction(int numero, Estadistica estadistica) {
        try {
            dominio.mejorarEstadistica(numero, estadistica);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo mejorar", JOptionPane.WARNING_MESSAGE);
        }
        actualizar();
    }

    /**
     * Vuelve a leer del dominio los puntos, el precio y las estadísticas de los dos jugadores.
     */
    public void actualizar() {
        try {
            Estadistica[] estadisticas = Estadistica.values();
            for (int i = 0; i < 2; i++) {
                int numero = i + 1;
                Estadisticas e = dominio.getEstadisticas(numero);
                etiquetasPuntos[i].setText(String.valueOf(dominio.getPuntos(numero)));
                etiquetasPrecio[i].setText(dominio.getPrecioMejora(numero) + " puntos");
                for (int k = 0; k < estadisticas.length; k++) {
                    int valor = e.getValor(estadisticas[k]);
                    etiquetasValor[i][k].setText(valor + " / " + Estadisticas.MAXIMO);
                    botonesMejorar[i][k].setEnabled(valor < Estadisticas.MAXIMO);
                }
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Cambia el grupo del jugador 2 según la modalidad: "Máquina" o "Jugador 2".
     * @param contraMaquina true si el jugador 2 es la máquina
     * @param perfil perfil de la máquina (se muestra en el título), o null
     */
    public void setContraMaquina(boolean contraMaquina, PerfilMaquina perfil) {
        tarjetas[1].setTitulo(contraMaquina ? "Máquina (" + perfil + ")" : "Jugador 2");
        etiquetaControles2.setText(contraMaquina ? "La controla la computadora" : CONTROLES_J2);
    }

    /**
     * @return país del jugador 1
     */
    public Pais getPais1() { return (Pais) comboPais1.getSelectedItem(); }

    /**
     * @return país del jugador 2
     */
    public Pais getPais2() { return (Pais) comboPais2.getSelectedItem(); }
}
