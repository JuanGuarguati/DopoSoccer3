package presentation;

import domain.ConstantesJuego;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Ciclo del juego con paso fijo (patrón acumulador): el dominio avanza siempre
 * ConstantesJuego.PASOS_POR_SEGUNDO veces por segundo, sin importar qué tan rápido
 * pinte el computador. Así el juego va a la misma velocidad en cualquier equipo.
 * Usa un Timer de Swing y le avisa a la VistaPrincipal en cada paso.
 */
public class CicloJuego {

    /** Segundos que dura cada paso del dominio. */
    public static final double SEGUNDOS_POR_PASO = 1.0 / ConstantesJuego.PASOS_POR_SEGUNDO;
    /** Máximo de pasos por cuadro, para no quedarse atrás si el equipo se demora. */
    public static final int MAX_PASOS_POR_CUADRO = 5;
    /** Milisegundos entre cada cuadro que se pinta. */
    public static final int MILISEGUNDOS_POR_CUADRO = 1000 / ConstantesJuego.PASOS_POR_SEGUNDO;

    /** Timer de Swing que dispara cada cuadro. */
    private final Timer timer;
    /** Vista a la que se le avisa en cada paso. */
    private final VistaPrincipal vista;
    /** Momento (en nanosegundos) del cuadro anterior. */
    private long ultimoTiempo;
    /** Tiempo acumulado que falta por simular. */
    private double acumulado;
    /** true mientras el ciclo está corriendo. */
    private boolean corriendo;

    /**
     * Crea el ciclo del juego (detenido).
     * @param vista vista principal que recibe cada paso
     */
    public CicloJuego(VistaPrincipal vista) {
        this.vista = vista;
        this.timer = new Timer(MILISEGUNDOS_POR_CUADRO, new ActionListener() {
            /** Ejecuta un cuadro en cada tic del timer. */
            public void actionPerformed(ActionEvent e) {
                cuadro();
            }
        });
    }

    /**
     * Un cuadro: ejecuta los pasos del dominio que hagan falta según el tiempo real
     * (máximo MAX_PASOS_POR_CUADRO) y luego vuelve a pintar.
     */
    private void cuadro() {
        long ahora = System.nanoTime();
        acumulado += (ahora - ultimoTiempo) / 1_000_000_000.0;
        ultimoTiempo = ahora;
        int pasos = 0;
        while (corriendo && acumulado >= SEGUNDOS_POR_PASO && pasos < MAX_PASOS_POR_CUADRO) {
            vista.pasoDeJuego(SEGUNDOS_POR_PASO);
            acumulado -= SEGUNDOS_POR_PASO;
            pasos++;
        }
        if (pasos == MAX_PASOS_POR_CUADRO) acumulado = 0;
        vista.refrescar();
    }

    /**
     * Arranca el ciclo.
     */
    public void iniciar() {
        ultimoTiempo = System.nanoTime();
        acumulado = 0;
        corriendo = true;
        timer.start();
    }

    /**
     * Detiene el ciclo.
     */
    public void detener() {
        corriendo = false;
        timer.stop();
    }

    /**
     * @return true si el ciclo está corriendo
     */
    public boolean estaCorriendo() { return corriendo; }
}
