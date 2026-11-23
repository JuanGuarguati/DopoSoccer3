package presentation;

import domain.*;
import dto.EstadoJugador;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * JFrame: ventana principal de DOPOSoccer (es el gran componente que contiene todo).
 * MVC: modelo = DOPOSoccer (dominio), vista = los paneles, controlador = los oyentes.
 *
 * Organización de componentes:
 *   JFrame (esta clase)
 *     JMenuBar  con JMenu "Archivo" y JMenu "Juego" (cada opción es un JMenuItem)
 *     JFileChooser para guardar y abrir partidas (.txt) con el PartidoDAO del dominio
 *     JPanel principal con CardLayout, que muestra una tarjeta a la vez:
 *       PanelInicio (JPanel) pantalla de bienvenida
 *       PanelInstrucciones (JPanel) cómo jugar
 *       PanelConfiguracion (JPanel) para preparar el partido
 *       PanelJugadores (JPanel) para elegir país y mejorar estadísticas
 *       PanelCancha (JPanel) donde se juega; adentro tiene el PanelPausa (JPanel)
 *
 * Implementa VistaPrincipal: los paneles y el CicloJuego le piden la navegación y cada paso
 * del juego sin conocer esta clase (mediador).
 *
 * Controles: jugador 1 A/D mover, W saltar, S patear; jugador 2 flechas, arriba saltar,
 * abajo patear; P pausa, Esc terminar. En jugador vs máquina, el jugador 2 lo mueve la máquina.
 */
public class DOPOSoccerGUI extends JFrame implements VistaPrincipal {

    /**
     * Pantallas (tarjetas del CardLayout). Reemplaza los textos "inicio", "configuracion", ...
     */
    public enum Pantalla {
        /** Pantalla de bienvenida. */
        INICIO,
        /** Pantalla de instrucciones. */
        INSTRUCCIONES,
        /** Pantalla de configuración del partido. */
        CONFIGURACION,
        /** Pantalla para elegir país y mejorar estadísticas. */
        JUGADORES,
        /** Pantalla donde se juega. */
        CANCHA
    }

    /** Tamaño de la ventana en las pantallas de menú. */
    public static final Dimension TAMANO_MENU = new Dimension(980, 600);

    /** Modelo: fachada del dominio. */
    private DOPOSoccer dominio;
    /** Estilo (layout) del panel principal: muestra una tarjeta a la vez. */
    private CardLayout tarjetas;
    /** JPanel contenedor principal: guarda las tarjetas (inicio, configuración, jugadores y cancha). */
    private JPanel principal;
    /** JPanel de la pantalla de inicio. */
    private PanelInicio panelInicio;
    /** JPanel de la pantalla de instrucciones. */
    private PanelInstrucciones panelInstrucciones;
    /** JPanel de la pantalla de jugadores. */
    private PanelJugadores panelJugadores;
    /** JPanel de la pantalla de configuración. */
    private PanelConfiguracion panelConfiguracion;
    /** JPanel de la pantalla de juego. */
    private PanelCancha panelCancha;
    /** Ciclo del juego con paso fijo (usa un Timer de Swing). */
    private CicloJuego ciclo;
    /** Teclas de movimiento que están presionadas en este momento. */
    private Set<Integer> teclasPresionadas;
    /** JMenuItem del menú Archivo: vuelve a la configuración para un partido nuevo. */
    private JMenuItem itemNuevo;
    /** JMenuItem del menú Archivo: abre una partida guardada (.txt). */
    private JMenuItem itemAbrir;
    /** JMenuItem del menú Archivo: guarda la partida en un archivo (.txt). */
    private JMenuItem itemGuardar;
    /** JMenuItem del menú Juego: cambia la estrategia de la máquina. */
    private JMenuItem itemEstrategia;
    /** JFileChooser: ventana para escoger el archivo de la partida. */
    private JFileChooser selectorArchivo;
    /** JMenuItem del menú Juego: pausa o reanuda el partido. */
    private JMenuItem itemPausa;
    /** JMenuItem del menú Juego: termina el partido. */
    private JMenuItem itemTerminar;
    /** JMenuItem del menú Archivo: cierra la aplicación. */
    private JMenuItem itemSalir;

    /**
     * Crea la ventana: modelo, vista y controlador.
     */
    private DOPOSoccerGUI() {
        dominio = DOPOSoccer.getInstance();
        teclasPresionadas = new HashSet<>();
        prepareView();
        prepareController();
    }

    /**
     * Arma la vista: menú y las cuatro pantallas (inicio, configuración, jugadores y cancha).
     */
    private void prepareView() {
        setTitle("DOPOSoccer");                 // título del JFrame
        prepareViewMenu();
        tarjetas = new CardLayout();
        principal = new JPanel(tarjetas);       // JPanel contenedor con CardLayout
        panelInicio = new PanelInicio(this);
        panelInstrucciones = new PanelInstrucciones(this);
        panelConfiguracion = new PanelConfiguracion(this);
        panelJugadores = new PanelJugadores(dominio, this);
        panelCancha = new PanelCancha(dominio);
        principal.add(panelInicio, Pantalla.INICIO.name());                 // tarjeta 1 (se muestra primero)
        principal.add(panelInstrucciones, Pantalla.INSTRUCCIONES.name());   // tarjeta 2
        principal.add(panelConfiguracion, Pantalla.CONFIGURACION.name());   // tarjeta 3
        principal.add(panelJugadores, Pantalla.JUGADORES.name());           // tarjeta 4
        principal.add(panelCancha, Pantalla.CANCHA.name());                 // tarjeta 5
        add(principal);                                     // el JPanel va dentro del JFrame
        selectorArchivo = new JFileChooser();               // JFileChooser para guardar y abrir
        selectorArchivo.setFileFilter(new FileNameExtensionFilter("Partidas de DOPOSoccer (*.txt)", "txt"));
        setResizable(false);                                // tamaño fijo: menú o resolución elegida
        setSize(TAMANO_MENU);
        setLocationRelativeTo(null);
    }

    /**
     * Arma la barra de menú: JMenuBar con dos JMenu (Archivo y Juego) y sus JMenuItem.
     */
    private void prepareViewMenu() {
        JMenuBar barra = new JMenuBar();            // JMenuBar: barra de menú del JFrame
        barra.setBackground(Estilo.FONDO_ARRIBA);
        JMenu archivo = new JMenu("Archivo");       // JMenu: contenedor de opciones
        itemNuevo = new JMenuItem("Nuevo partido");
        itemAbrir = new JMenuItem("Abrir partida");
        itemGuardar = new JMenuItem("Guardar partida");
        itemSalir = new JMenuItem("Salir");
        archivo.add(itemNuevo);
        archivo.add(itemAbrir);
        archivo.add(itemGuardar);
        archivo.addSeparator();
        archivo.add(itemSalir);
        JMenu juego = new JMenu("Juego");           // JMenu: contenedor de opciones
        itemPausa = new JMenuItem("Pausar / continuar (P)");
        itemTerminar = new JMenuItem("Terminar partido (Esc)");
        itemEstrategia = new JMenuItem("Cambiar estrategia de la máquina");
        juego.add(itemPausa);
        juego.add(itemTerminar);
        juego.addSeparator();
        juego.add(itemEstrategia);
        for (JMenu menu : new JMenu[] {archivo, juego}) {
            menu.setFont(Estilo.TEXTO);
            menu.setForeground(Estilo.ACENTO);
            for (Component item : menu.getMenuComponents()) item.setFont(Estilo.TEXTO);
        }
        barra.add(archivo);
        barra.add(juego);
        setJMenuBar(barra);                         // se pone la barra en el JFrame
    }

    /**
     * Controlador: asigna los oyentes (listeners) a la ventana, al menú, a los botones,
     * al teclado y crea el ciclo del juego.
     */
    private void prepareController() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            /** Pide confirmación antes de cerrar la ventana. */
            public void windowClosing(WindowEvent e) {
                salirAction();
            }
        });
        itemSalir.addActionListener(new ActionListener() {
            /** Atiende la opción Salir. */
            public void actionPerformed(ActionEvent e) {
                salirAction();
            }
        });
        itemNuevo.addActionListener(new ActionListener() {
            /** Atiende la opción Nuevo. */
            public void actionPerformed(ActionEvent e) {
                nuevoAction();
            }
        });
        ActionListener abrir = new ActionListener() {
            /** Atiende la opción Abrir partida. */
            public void actionPerformed(ActionEvent e) {
                abrirAction();
            }
        };
        ActionListener guardar = new ActionListener() {
            /** Atiende la opción Guardar partida. */
            public void actionPerformed(ActionEvent e) {
                guardarAction();
            }
        };
        ActionListener estrategia = new ActionListener() {
            /** Atiende la opción Cambiar estrategia de la máquina. */
            public void actionPerformed(ActionEvent e) {
                cambiarEstrategiaAction();
            }
        };
        itemAbrir.addActionListener(abrir);
        itemGuardar.addActionListener(guardar);
        itemEstrategia.addActionListener(estrategia);
        panelCancha.getPanelPausa().getBotonAbrir().addActionListener(abrir);
        panelCancha.getPanelPausa().getBotonGuardar().addActionListener(guardar);
        panelCancha.getPanelPausa().getBotonEstrategia().addActionListener(estrategia);
        itemPausa.addActionListener(new ActionListener() {
            /** Atiende la opción Pausa. */
            public void actionPerformed(ActionEvent e) {
                pausaAction();
            }
        });
        itemTerminar.addActionListener(new ActionListener() {
            /** Atiende la opción Terminar. */
            public void actionPerformed(ActionEvent e) {
                terminarAction();
            }
        });
        prepareControllerPausa();
        panelCancha.addKeyListener(new KeyAdapter() {
            /** Registra la tecla presionada. */
            public void keyPressed(KeyEvent e) {
                teclaPresionadaAction(e.getKeyCode());
            }
            /** Quita la tecla que se soltó. */
            public void keyReleased(KeyEvent e) {
                teclasPresionadas.remove(e.getKeyCode());
            }
        });
        panelCancha.addFocusListener(new FocusAdapter() {
            /** Si la cancha pierde el foco (por ejemplo, por un diálogo), suelta todas las teclas. */
            public void focusLost(FocusEvent e) {
                teclasPresionadas.clear();
            }
        });
        // Ciclo del juego: paso fijo, le avisa a esta ventana (VistaPrincipal) en cada paso
        ciclo = new CicloJuego(this);
    }

    /**
     * Asigna los oyentes del JButton de pausa y de los JButton del menú de pausa.
     */
    private void prepareControllerPausa() {
        PanelPausa pausa = panelCancha.getPanelPausa();
        ActionListener pausar = new ActionListener() {
            /** Atiende el botón de pausa y el botón Continuar. */
            public void actionPerformed(ActionEvent e) {
                pausaAction();
            }
        };
        panelCancha.getBotonPausa().addActionListener(pausar);
        pausa.getBotonContinuar().addActionListener(pausar);
        pausa.getBotonTerminar().addActionListener(new ActionListener() {
            /** Atiende el botón Terminar partido. */
            public void actionPerformed(ActionEvent e) {
                terminarAction();
            }
        });
        pausa.getBotonMenu().addActionListener(new ActionListener() {
            /** Atiende el botón Volver al menú. */
            public void actionPerformed(ActionEvent e) {
                volverMenuAction();
            }
        });
    }

    /**
     * Muestra la pantalla de inicio (VistaPrincipal).
     */
    @Override
    public void mostrarInicio() {
        mostrarMenu(Pantalla.INICIO);
    }

    /**
     * Muestra la pantalla de instrucciones (VistaPrincipal).
     */
    @Override
    public void mostrarInstrucciones() {
        mostrarMenu(Pantalla.INSTRUCCIONES);
    }

    /**
     * Vuelve a la pantalla de configuración para un partido nuevo (VistaPrincipal).
     */
    @Override
    public void mostrarConfiguracion() {
        mostrarMenu(Pantalla.CONFIGURACION);
    }

    /**
     * Pasa de la configuración a la pantalla de jugadores (VistaPrincipal).
     * El valor ya viene validado por el JSpinner de la configuración.
     */
    @Override
    public void confirmarConfiguracion() {
        mostrarJugadores();
    }

    /**
     * Lee la configuración y los países e inicia el partido (VistaPrincipal).
     * Muestra un mensaje si algo no es válido.
     */
    @Override
    public void iniciarPartido() {
        try {
            dominio.iniciarPartido(panelJugadores.getPais1(), panelJugadores.getPais2(),
                panelConfiguracion.getModo(), panelConfiguracion.getValor(),
                panelConfiguracion.getModalidad(), panelConfiguracion.getPerfil());
            mostrarCancha(false);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Un paso del juego (VistaPrincipal, lo llama el CicloJuego): mueve según las teclas presionadas,
     * actualiza el dominio (allí juega la máquina) y revisa si el partido terminó.
     * @param segundos duración del paso
     */
    @Override
    public void pasoDeJuego(double segundos) {
        try {
            if (!dominio.hayPartido()) return;
            if (dominio.estaTerminado()) {
                finAction();
                return;
            }
            if (!dominio.estaPausado()) {
                if (teclasPresionadas.contains(KeyEvent.VK_A)) dominio.moverJugador(1, Direccion.IZQUIERDA);
                if (teclasPresionadas.contains(KeyEvent.VK_D)) dominio.moverJugador(1, Direccion.DERECHA);
                if (!dominio.esContraMaquina()) {
                    if (teclasPresionadas.contains(KeyEvent.VK_LEFT)) dominio.moverJugador(2, Direccion.IZQUIERDA);
                    if (teclasPresionadas.contains(KeyEvent.VK_RIGHT)) dominio.moverJugador(2, Direccion.DERECHA);
                }
            }
            dominio.actualizar(segundos);
            if (dominio.estaTerminado()) {
                finAction();
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Vuelve a pintar la cancha (VistaPrincipal, lo llama el CicloJuego en cada cuadro).
     */
    @Override
    public void refrescar() {
        panelCancha.repaint();
    }

    /**
     * Detiene el ciclo del juego (VistaPrincipal).
     */
    @Override
    public void detenerCiclo() {
        ciclo.detener();
    }

    /**
     * Muestra una pantalla de menú con el tamaño del menú. Detiene el juego y cierra la pausa.
     * @param pantalla pantalla que se muestra
     */
    private void mostrarMenu(Pantalla pantalla) {
        detenerCiclo();
        teclasPresionadas.clear();
        panelCancha.mostrarPausa(false);
        tarjetas.show(principal, pantalla.name());
        setSize(TAMANO_MENU);
        setLocationRelativeTo(null);
    }

    /**
     * Muestra la pantalla de jugadores con los puntos y estadísticas al día.
     */
    private void mostrarJugadores() {
        panelJugadores.actualizar();
        panelJugadores.setContraMaquina(panelConfiguracion.esContraMaquina(), panelConfiguracion.getPerfil());
        mostrarMenu(Pantalla.JUGADORES);
    }

    /**
     * Muestra la cancha con la resolución elegida y arranca el ciclo del juego.
     * @param enPausa true si debe aparecer con el menú de pausa abierto (al abrir una partida)
     */
    private void mostrarCancha(boolean enPausa) {
        panelCancha.setPreferredSize(panelConfiguracion.getResolucion());
        panelCancha.mostrarPausa(enPausa);
        tarjetas.show(principal, Pantalla.CANCHA.name());
        pack();
        setLocationRelativeTo(null);
        teclasPresionadas.clear();
        panelCancha.requestFocusInWindow();
        ciclo.iniciar();
    }

    /**
     * Atiende una tecla: saltar, patear, pausar y terminar se hacen una vez;
     * las de movimiento se guardan para moverse mientras estén presionadas.
     * Contra la máquina, las teclas del jugador 2 no hacen nada. Sin partido en curso, se ignoran.
     * @param codigo código de la tecla
     */
    private void teclaPresionadaAction(int codigo) {
        try {
            if (!dominio.hayPartido() || dominio.estaTerminado()) return;
            boolean humano2 = !dominio.esContraMaquina();
            switch (codigo) {
                case KeyEvent.VK_W: dominio.saltarJugador(1); break;
                case KeyEvent.VK_S: dominio.patearJugador(1); break;
                case KeyEvent.VK_UP: if (humano2) dominio.saltarJugador(2); break;
                case KeyEvent.VK_DOWN: if (humano2) dominio.patearJugador(2); break;
                case KeyEvent.VK_P: pausaAction(); break;
                case KeyEvent.VK_ESCAPE: terminarAction(); break;
                default: teclasPresionadas.add(codigo);
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Muestra el ganador, el marcador, los puntos obtenidos en este partido y los puntos
     * acumulados de cada jugador. Luego pasa a la pantalla de jugadores para gastar los puntos.
     */
    private void finAction() {
        detenerCiclo();
        teclasPresionadas.clear();
        panelCancha.mostrarPausa(false);
        try {
            EstadoJugador j1 = dominio.getEstadoJugador(1);
            EstadoJugador j2 = dominio.getEstadoJugador(2);
            int ganador = dominio.getNumeroGanador();
            String marcador = "Marcador: " + j1.goles + " - " + j2.goles + "\n";
            String mensaje;
            if (ganador == 0) {
                mensaje = "EMPATE\n" + marcador + "No se otorgan puntos.\n";
            } else {
                EstadoJugador g = ganador == 1 ? j1 : j2;
                EstadoJugador perdedor = ganador == 1 ? j2 : j1;
                mensaje = "¡Ganó el jugador " + g.numero + " (" + g.pais + ")!\n"
                    + marcador
                    + "Puntos obtenidos: jugador " + g.numero + " +" + ConstantesJuego.PUNTOS_GANADOR
                    + ", jugador " + perdedor.numero + " +" + ConstantesJuego.PUNTOS_PERDEDOR + "\n";
            }
            mensaje += "Puntos acumulados: jugador 1 = " + dominio.getPuntos(1)
                + ", jugador 2 = " + dominio.getPuntos(2);
            JOptionPane.showMessageDialog(this, mensaje, "Fin del partido", JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
        mostrarJugadores();
    }

    /**
     * Pausa o reanuda el partido.
     */
    private void pausaAction() {
        try {
            dominio.pausar();
            teclasPresionadas.clear();
            panelCancha.mostrarPausa(dominio.estaPausado());
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        panelCancha.requestFocusInWindow();
    }

    /**
     * Termina el partido en cualquier momento, después de confirmar. Mientras se pregunta,
     * el partido queda en pausa; si el usuario dice que no, vuelve como estaba.
     */
    private void terminarAction() {
        try {
            if (!dominio.hayPartido() || dominio.estaTerminado()) return;
            boolean yaPausado = dominio.estaPausado();
            if (!yaPausado) dominio.pausar();
            teclasPresionadas.clear();
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea terminar el partido?", "Terminar",
                JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                if (dominio.estaPausado()) dominio.pausar();
                dominio.terminar();
            } else if (!yaPausado) {
                dominio.pausar();
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
        panelCancha.requestFocusInWindow();
    }

    /**
     * Vuelve a la pantalla de configuración para un partido nuevo.
     */
    private void nuevoAction() {
        mostrarConfiguracion();
    }

    /**
     * Termina el partido actual con el marcador que lleva y vuelve a la configuración, después de confirmar.
     */
    private void volverMenuAction() {
        teclasPresionadas.clear();
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Desea volver al menú? El partido se terminará con el marcador actual.", "Volver al menú",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dominio.terminar();
            } catch (DOPOSoccerException e) {
                Log.record(e);
            }
            nuevoAction();
        }
    }

    /**
     * Guarda la partida en un archivo .txt que elige el usuario (usa el PartidoDAO a través de la fachada).
     * Si el partido está corriendo, primero lo pausa.
     */
    private void guardarAction() {
        try {
            if (dominio.hayPartido() && !dominio.estaTerminado() && !dominio.estaPausado()) {
                dominio.pausar();
                panelCancha.mostrarPausa(true);
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
        if (selectorArchivo.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            dominio.guardar(selectorArchivo.getSelectedFile());
            JOptionPane.showMessageDialog(this, "La partida se guardó correctamente.", "Guardar partida",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre una partida guardada (.txt). Si tenía un partido en curso, se muestra la cancha en pausa;
     * si no, se muestra la pantalla de jugadores con los puntos y estadísticas cargados.
     */
    private void abrirAction() {
        if (selectorArchivo.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File archivo = selectorArchivo.getSelectedFile();
        try {
            dominio.abrir(archivo);
            if (dominio.hayPartido()) {
                mostrarCancha(true);
            } else {
                mostrarJugadores();
            }
            JOptionPane.showMessageDialog(this, "Se abrió la partida " + archivo.getName() + ".",
                "Abrir partida", JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deja escoger otro perfil para la máquina en pleno partido (patrón Strategy).
     */
    private void cambiarEstrategiaAction() {
        if (!dominio.esContraMaquina()) {
            JOptionPane.showMessageDialog(this, DOPOSoccerException.NO_HAY_MAQUINA, "DOPOSoccer",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            PerfilMaquina actual = dominio.getPerfilMaquina();
            PerfilMaquina nuevo = (PerfilMaquina) JOptionPane.showInputDialog(this,
                "Escoja la nueva estrategia de la máquina:", "Cambiar máquina", JOptionPane.QUESTION_MESSAGE,
                null, PerfilMaquina.values(), actual);
            if (nuevo != null) dominio.cambiarEstrategia(nuevo);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        panelCancha.repaint();
        panelCancha.requestFocusInWindow();
    }

    /**
     * Pregunta si desea salir y cierra la aplicación.
     */
    private void salirAction() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir de DOPOSoccer?", "Salir",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            detenerCiclo();
            dispose();
            System.exit(0);
        }
    }

    /**
     * Inicia la aplicación.
     * @param args no se usan
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            /** Crea y muestra la ventana en el hilo de eventos de Swing. */
            public void run() {
                DOPOSoccerGUI gui = new DOPOSoccerGUI();
                gui.setVisible(true);
            }
        });
    }
}
