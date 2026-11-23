package presentation;

import domain.Modalidad;
import domain.Partido;
import domain.PerfilMaquina;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: pantalla de configuración del partido (modalidad, perfil de la máquina,
 * resolución, modo de juego y minutos o goles). Los países se eligen después, en PanelJugadores.
 * Componentes Swing que usa:
 * JLabel (título y etiquetas), JComboBox (listas de opciones con enums),
 * JSpinner (minutos o goles, solo números válidos), BotonRedondeado (siguiente) y PanelTarjeta (grupos).
 * La modalidad es el enum Modalidad del dominio. Hereda el fondo de PanelDegradado.
 * Los colores y fuentes salen de la clase Estilo.
 */
public class PanelConfiguracion extends PanelDegradado {

    /** Valor mínimo de minutos o goles. */
    public static final int VALOR_MINIMO = 1;
    /** Valor máximo de minutos o goles. */
    public static final int VALOR_MAXIMO = 99;
    /** Valor inicial de minutos o goles. */
    public static final int VALOR_INICIAL = 2;

    /**
     * Resoluciones de la cancha que se pueden elegir.
     */
    public enum Resolucion {
        /** 1280 x 720. */
        HD(1280, 720),
        /** 1600 x 900. */
        HD_PLUS(1600, 900),
        /** 1920 x 1080. */
        FULL_HD(1920, 1080);

        private final int ancho;
        private final int alto;

        /**
         * Crea una resolución.
         * @param ancho ancho en píxeles
         * @param alto alto en píxeles
         */
        Resolucion(int ancho, int alto) {
            this.ancho = ancho;
            this.alto = alto;
        }

        /**
         * @return tamaño en píxeles
         */
        public Dimension getDimension() { return new Dimension(ancho, alto); }

        /**
         * @return texto "ancho x alto"
         */
        @Override
        public String toString() { return ancho + " x " + alto; }
    }

    /** JComboBox: modalidad de juego. */
    private JComboBox<Modalidad> comboModalidad;
    /** JComboBox: perfil de la máquina (solo activo en jugador vs máquina). */
    private JComboBox<PerfilMaquina> comboPerfil;
    /** JComboBox: resolución de la cancha. */
    private JComboBox<Resolucion> comboResolucion;
    /** JComboBox: modo de juego (por tiempo o por goles). */
    private JComboBox<Partido.ModoJuego> comboModo;
    /** JSpinner: minutos o goles (solo deja escoger números entre VALOR_MINIMO y VALOR_MAXIMO). */
    private JSpinner selectorValor;
    /** Botón redondeado para pasar a la pantalla de jugadores. */
    private BotonRedondeado botonSiguiente;
    /** Ventana principal, a la que se le pide pasar a la pantalla de jugadores. */
    private VistaPrincipal vista;

    /**
     * Crea la pantalla de configuración.
     * @param vista ventana principal (navegación)
     */
    public PanelConfiguracion(VistaPrincipal vista) {
        super(new BorderLayout(10, 15));
        this.vista = vista;
        prepareView();
        prepareController();
    }

    /**
     * Organiza los componentes de la pantalla.
     */
    private void prepareView() {
        setBorder(new EmptyBorder(20, 40, 20, 40));
        add(Estilo.crearTitulo("CONFIGURACIÓN"), BorderLayout.NORTH);

        // JComboBox y JTextField: opciones que elige el usuario (cada lista usa un enum)
        comboModalidad = new JComboBox<>(Modalidad.values());
        comboPerfil = new JComboBox<>(PerfilMaquina.values());
        comboPerfil.setEnabled(false);
        comboResolucion = new JComboBox<>(Resolucion.values());
        comboModo = new JComboBox<>(Partido.ModoJuego.values());
        selectorValor = new JSpinner(new SpinnerNumberModel(VALOR_INICIAL, VALOR_MINIMO, VALOR_MAXIMO, 1));
        for (JComboBox<?> combo : new JComboBox<?>[] {comboModalidad, comboPerfil, comboResolucion, comboModo}) {
            Estilo.estilizarCombo(combo);
        }
        Estilo.estilizarCampo(((JSpinner.DefaultEditor) selectorValor.getEditor()).getTextField());

        // JPanel centro: contiene los dos grupos lado a lado (el de Juego más ancho)
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        // PanelTarjeta: grupo "Juego"
        PanelTarjeta juego = new PanelTarjeta("Juego", Estilo.PRIMARIO, new GridLayout(4, 2, 10, 10));
        juego.add(Estilo.crearEtiqueta("Modalidad:"));
        juego.add(comboModalidad);
        juego.add(Estilo.crearEtiqueta("Perfil de la máquina:"));
        juego.add(comboPerfil);
        juego.add(Estilo.crearEtiqueta("Resolución:"));
        juego.add(comboResolucion);
        juego.add(Estilo.crearEtiqueta("Modo de juego:"));
        juego.add(comboModo);

        // PanelTarjeta: grupo "Partido"
        PanelTarjeta partido = new PanelTarjeta("Partido", Estilo.SECUNDARIO, new GridLayout(4, 2, 10, 10));
        partido.add(Estilo.crearEtiqueta("Minutos o goles:"));
        partido.add(selectorValor);
        partido.add(Estilo.crearEtiqueta("Pausa:"));
        partido.add(Estilo.crearAyuda("tecla P"));
        partido.add(Estilo.crearEtiqueta("Terminar:"));
        partido.add(Estilo.crearAyuda("tecla Esc"));
        partido.add(Estilo.crearEtiqueta("Guardar / abrir:"));
        partido.add(Estilo.crearAyuda("menú Archivo"));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.6;
        c.insets = new Insets(0, 0, 0, 10);
        centro.add(juego, c);
        c.weightx = 0.4;
        c.insets = new Insets(0, 10, 0, 0);
        centro.add(partido, c);
        add(centro, BorderLayout.CENTER);

        // Botón redondeado: le pide a la ventana principal (VistaPrincipal) pasar a jugadores
        botonSiguiente = Estilo.crearBoton("Siguiente: elegir jugadores  >");
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        abajo.setOpaque(false);
        abajo.add(botonSiguiente);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Oyentes: el JComboBox de modalidad habilita el perfil de la máquina solo en
     * jugador vs máquina, y el botón Siguiente le pide a la vista confirmar la configuración.
     */
    private void prepareController() {
        botonSiguiente.addActionListener(new ActionListener() {
            /** Atiende el botón Siguiente. */
            public void actionPerformed(ActionEvent e) {
                vista.confirmarConfiguracion();
            }
        });
        comboModalidad.addActionListener(new ActionListener() {
            /** Atiende el cambio de modalidad. */
            public void actionPerformed(ActionEvent e) {
                comboModalidadAction();
            }
        });
    }

    /**
     * Activa o desactiva el JComboBox de perfil según la modalidad.
     */
    private void comboModalidadAction() {
        comboPerfil.setEnabled(esContraMaquina());
    }

    /**
     * @return modalidad elegida (enum Modalidad del dominio)
     */
    public Modalidad getModalidad() { return (Modalidad) comboModalidad.getSelectedItem(); }

    /**
     * @return true si se eligió jugador vs máquina
     */
    public boolean esContraMaquina() { return getModalidad().tieneMaquina(); }

    /**
     * @return perfil de la máquina elegido, o null si es jugador vs jugador
     */
    public PerfilMaquina getPerfil() {
        return esContraMaquina() ? (PerfilMaquina) comboPerfil.getSelectedItem() : null;
    }

    /**
     * @return modo de juego elegido
     */
    public Partido.ModoJuego getModo() { return (Partido.ModoJuego) comboModo.getSelectedItem(); }

    /**
     * @return minutos o goles elegidos en el JSpinner
     */
    public int getValor() { return (Integer) selectorValor.getValue(); }

    /**
     * Retorna el tamaño de cancha elegido.
     * @return dimensión en píxeles de la resolución seleccionada
     */
    public Dimension getResolucion() {
        return ((Resolucion) comboResolucion.getSelectedItem()).getDimension();
    }
}
