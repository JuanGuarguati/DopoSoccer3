package dto;

import java.awt.Color;

/**
 * Objeto de transferencia de datos (DTO) que lleva desde el dominio hasta la presentación
 * lo necesario para dibujar un elemento del partido.
 * No tiene lógica del juego ni depende de domain o presentation: solo guarda datos.
 * Dice cómo pintar cada objeto (forma, posición, tamaño y colores), así la cancha
 * se pinta sin tocar los objetos del dominio y sin preguntar por el país.
 */
public class ComandoDibujo {

    /**
     * Forma que debe pintar la presentación.
     */
    public enum Forma {
        /** Cielo y pasto de la cancha. */
        CANCHA,
        /** Arco (red, travesaño y poste). */
        ARCO,
        /** Balón. */
        BALON,
        /** Jugador (cabeza, cuerpo, pie y sangre). */
        JUGADOR
    }

    /** Forma que se pinta. */
    public final Forma forma;
    /** Posición horizontal: esquina en CANCHA, ARCO y BALON; centro de la cabeza en JUGADOR. */
    public final int x;
    /** Posición vertical: nivel del piso en CANCHA; esquina en ARCO y BALON; centro de la cabeza en JUGADOR. */
    public final int y;
    /** Ancho del elemento (en JUGADOR es el radio de la cabeza). */
    public final int ancho;
    /** Alto del elemento. */
    public final int alto;
    /** Número del jugador (1 o 2); 0 si no es un jugador. */
    public final int numero;
    /** Color de la cabeza del jugador; null si no es un jugador. */
    public final Color colorCabeza;
    /** Color de la banda de la frente del jugador; null si no es un jugador. */
    public final Color colorBanda;
    /** Color del cuerpo del jugador; null si no es un jugador. */
    public final Color colorCuerpo;
    /** true si el cuerpo del jugador lleva rayas blancas. */
    public final boolean rayas;
    /** Texto que se escribe encima del jugador (por ejemplo "J1 · Camerún"); null si no hay. */
    public final String etiqueta;
    /** true si mira a la derecha (JUGADOR) o si es el arco de la izquierda (ARCO). */
    public final boolean haciaDerecha;
    /** Avance de la patada entre 0 y 1, o negativo si el jugador no está pateando. */
    public final double avancePatada;
    /** true si el jugador está aturdido. */
    public final boolean aturdido;
    /** Avance del golpe entre 0 y 1; menor que 0 si el jugador no está golpeado. */
    public final double avanceGolpe;

    /**
     * Crea el comando de un elemento que no es jugador (cancha, arco o balón).
     * @param forma forma que se pinta
     * @param x posición horizontal
     * @param y posición vertical
     * @param ancho ancho del elemento
     * @param alto alto del elemento
     * @param haciaDerecha true si es el arco de la izquierda (abre hacia la derecha)
     */
    public ComandoDibujo(Forma forma, int x, int y, int ancho, int alto, boolean haciaDerecha) {
        this(forma, x, y, ancho, alto, 0, null, false, null, haciaDerecha, -1, false, -1);
    }

    /**
     * Crea un comando con todos los datos (se usa para los jugadores).
     * @param forma forma que se pinta
     * @param x posición horizontal
     * @param y posición vertical
     * @param ancho ancho (radio en el jugador)
     * @param alto alto del elemento
     * @param numero número del jugador
     * @param colores colores {cabeza, banda, cuerpo} del jugador (puede ser null)
     * @param rayas true si el cuerpo lleva rayas
     * @param etiqueta texto que va encima del jugador
     * @param haciaDerecha true si mira a la derecha
     * @param avancePatada avance de la patada entre 0 y 1, o negativo si no está pateando
     * @param aturdido true si está aturdido
     * @param avanceGolpe avance del golpe entre 0 y 1, o negativo si no está golpeado
     */
    public ComandoDibujo(Forma forma, int x, int y, int ancho, int alto, int numero, Color[] colores,
                         boolean rayas, String etiqueta, boolean haciaDerecha, double avancePatada,
                         boolean aturdido, double avanceGolpe) {
        this.forma = forma;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.numero = numero;
        this.colorCabeza = colores == null ? null : colores[0];
        this.colorBanda = colores == null ? null : colores[1];
        this.colorCuerpo = colores == null ? null : colores[2];
        this.rayas = rayas;
        this.etiqueta = etiqueta;
        this.haciaDerecha = haciaDerecha;
        this.avancePatada = avancePatada;
        this.aturdido = aturdido;
        this.avanceGolpe = avanceGolpe;
    }
}
