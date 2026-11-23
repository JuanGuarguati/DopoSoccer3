package presentation;

import domain.PerfilMaquina;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JPanel: pantalla de instrucciones ("Cómo jugar"). Explica el objetivo, los controles,
 * los modos de juego, los perfiles de la máquina y cómo guardar y abrir partidas.
 * Hereda el fondo de PanelDegradado y vuelve al inicio con la VistaPrincipal.
 */
public class PanelInstrucciones extends PanelDegradado {

    /** Botón para volver a la pantalla de inicio. */
    private BotonRedondeado botonVolver;
    /** Ventana principal (navegación). */
    private VistaPrincipal vista;

    /**
     * Crea la pantalla de instrucciones.
     * @param vista ventana principal (navegación)
     */
    public PanelInstrucciones(VistaPrincipal vista) {
        super(new BorderLayout(10, 12));
        this.vista = vista;
        prepareView();
        prepareController();
    }

    /**
     * Organiza el título, dos tarjetas con el texto y el botón Volver.
     */
    private void prepareView() {
        setBorder(new EmptyBorder(16, 40, 16, 40));
        add(Estilo.crearTitulo("CÓMO JUGAR"), BorderLayout.NORTH);

        PanelTarjeta juego = new PanelTarjeta("El partido", Estilo.PRIMARIO, new GridLayout(0, 1, 0, 4));
        agregarLineas(juego, new String[] {
            "Mete el balón en el arco del rival.",
            "Jugador 1 defiende el arco izquierdo; jugador 2, el derecho.",
            "Patear al rival le quita vida; con vida 0 queda aturdido.",
            "Por tiempo: gana quien tenga más goles (empate = muerte súbita).",
            "Por goles: gana el primero que llegue al número de goles.",
            "Ganar da " + domain.ConstantesJuego.PUNTOS_GANADOR + " puntos y perder "
                + domain.ConstantesJuego.PUNTOS_PERDEDOR + "; con ellos se mejoran las estadísticas."
        });

        PanelTarjeta controles = new PanelTarjeta("Controles", Estilo.SECUNDARIO, new GridLayout(0, 1, 0, 4));
        agregarLineas(controles, new String[] {
            "Jugador 1: " + PanelJugadores.CONTROLES_J1,
            "Jugador 2: " + PanelJugadores.CONTROLES_J2,
            "P o el botón II: pausa.   Esc: terminar.",
            "Contra la máquina, el jugador 2 se mueve solo.",
            "Perfiles de la máquina: " + nombresPerfiles() + ".",
            "Archivo > Guardar / Abrir partida (.txt)."
        });

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        centro.setOpaque(false);
        centro.add(juego);
        centro.add(controles);
        add(centro, BorderLayout.CENTER);

        botonVolver = Estilo.crearBotonSecundario("<  Volver");
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        abajo.setOpaque(false);
        abajo.add(botonVolver);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Agrega cada línea de texto a una tarjeta como un JLabel.
     * @param tarjeta tarjeta donde van las líneas
     * @param lineas textos
     */
    private void agregarLineas(PanelTarjeta tarjeta, String[] lineas) {
        for (String linea : lineas) {
            JLabel etiqueta = Estilo.crearEtiqueta("<html>" + linea + "</html>");
            etiqueta.setFont(Estilo.TEXTO_PEQUENO);
            tarjeta.add(etiqueta);
        }
    }

    /**
     * @return nombres de los perfiles de la máquina separados por comas
     */
    private String nombresPerfiles() {
        StringBuilder texto = new StringBuilder();
        for (PerfilMaquina perfil : PerfilMaquina.values()) {
            if (texto.length() > 0) texto.append(", ");
            texto.append(perfil.getNombre());
        }
        return texto.toString();
    }

    /**
     * Oyente del botón Volver: regresa a la pantalla de inicio.
     */
    private void prepareController() {
        botonVolver.addActionListener(new ActionListener() {
            /** Atiende el botón Volver. */
            public void actionPerformed(ActionEvent e) {
                vista.mostrarInicio();
            }
        });
    }
}
