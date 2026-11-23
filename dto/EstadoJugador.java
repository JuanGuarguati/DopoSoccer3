package dto;

import java.awt.Color;

/**
 * Objeto de transferencia de datos (DTO) con el estado de un jugador para mostrarlo
 * en la interfaz (marcador, vida, barra de poder y resultado). Es de solo lectura: así la presentación
 * no puede cambiar al jugador del dominio y saltarse las reglas del juego.
 */
public class EstadoJugador {

    /** Número del jugador (1 o 2). */
    public final int numero;
    /** Nombre del país para mostrar (por ejemplo "Camerún"). */
    public final String pais;
    /** Colores {cabeza, banda, cuerpo} del país (para dibujar la cabeza junto a las barras). */
    public final Color[] colores;
    /** Goles anotados en el partido. */
    public final int goles;
    /** Vida actual. */
    public final double vida;
    /** Vida máxima. */
    public final double vidaMax;
    /** true si está aturdido. */
    public final boolean aturdido;
    /** true si lo controla la máquina. */
    public final boolean maquina;
    /** Perfil de la máquina para mostrar; null si es humano. */
    public final String perfil;
    /** Carga de la barra de poder entre 0 y 1. */
    public final double barraPoder;

    /**
     * Crea el estado de un jugador.
     * @param numero número del jugador
     * @param pais nombre del país para mostrar
     * @param colores colores {cabeza, banda, cuerpo} del país
     * @param goles goles anotados
     * @param vida vida actual
     * @param vidaMax vida máxima
     * @param aturdido true si está aturdido
     * @param maquina true si lo controla la máquina
     * @param perfil perfil de la máquina para mostrar (null si es humano)
     * @param barraPoder carga de la barra de poder entre 0 y 1
     */
    public EstadoJugador(int numero, String pais, Color[] colores, int goles, double vida, double vidaMax,
                         boolean aturdido, boolean maquina, String perfil, double barraPoder) {
        this.numero = numero;
        this.pais = pais;
        this.colores = colores.clone();
        this.goles = goles;
        this.vida = vida;
        this.vidaMax = vidaMax;
        this.aturdido = aturdido;
        this.maquina = maquina;
        this.perfil = perfil;
        this.barraPoder = barraPoder;
    }
}
