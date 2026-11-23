package presentation;

import domain.Jugador;
import dto.ComandoDibujo;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Pinta los elementos del partido a partir de sus ComandoDibujo (DTO).
 * No toca los objetos del dominio: solo lee los datos del comando (y las medidas del pie y el cuello
 * de Jugador, para que el dibujo coincida con los choques).
 * Cada forma (cancha, arco, balón, jugador) tiene su propio método.
 * Los trazos y colores se crean una sola vez como constantes (no en cada cuadro).
 */
public final class RenderizadorElementos {

    /** Gotas que se dibujan cuando el jugador recibe un golpe. */
    public static final int GOTAS = 5;
    /** Franjas del pasto. */
    public static final int FRANJAS = 10;
    /** Separación entre las rayas del uniforme. */
    public static final int SEPARACION_RAYAS = 8;
    /** Tamaño del ojo del jugador. */
    public static final int OJO = 12;
    /** Alto del campo de juego que se ve detrás de los jugadores (en perspectiva). */
    public static final int ALTO_CAMPO = 200;
    /** Puntos con que se dibuja el círculo central. */
    public static final int PUNTOS_CIRCULO = 56;
    /** Grados que gira el pie en lo más alto de la patada. */
    public static final int ANGULO_PATADA = 55;

    /** Trazo normal (1 px). */
    private static final Stroke TRAZO_NORMAL = new BasicStroke(1);
    /** Trazo de los postes y travesaños. */
    private static final Stroke TRAZO_ARCO = new BasicStroke(5);
    /** Trazo del borde de la cabeza. */
    private static final Stroke TRAZO_CABEZA = new BasicStroke(4);
    /** Color de la red del arco (blanco transparente). */
    private static final Color RED_ARCO = new Color(255, 255, 255, 120);
    /** Trazo de las líneas del campo. */
    private static final Stroke TRAZO_LINEAS = new BasicStroke(3);
    /** Color de las líneas del campo (blanco casi opaco). */
    private static final Color LINEAS = new Color(255, 255, 255, 210);
    /** Color del borde de la cancha, debajo del piso. */
    private static final Color BORDE_CANCHA = new Color(35, 110, 45);
    /** Color del zapato. */
    private static final Color ZAPATO = new Color(25, 25, 30);
    /** Color de la suela. */
    private static final Color SUELA = new Color(235, 235, 235);

    /**
     * No se crean objetos de esta clase.
     */
    private RenderizadorElementos() { }

    /**
     * Pinta un elemento según su forma.
     * @param g contexto gráfico
     * @param c comando de dibujo del elemento
     */
    public static void dibujar(Graphics2D g, ComandoDibujo c) {
        switch (c.forma) {
            case CANCHA: dibujarCancha(g, c); break;
            case ARCO: dibujarArco(g, c); break;
            case BALON: dibujarBalon(g, c); break;
            default: dibujarJugador(g, c);
        }
    }

    /**
     * Pinta el cielo degradado, el campo en perspectiva con sus líneas y el borde debajo del piso.
     * @param g contexto gráfico
     * @param c comando de la cancha (y es el nivel del piso)
     */
    private static void dibujarCancha(Graphics2D g, ComandoDibujo c) {
        int piso = c.y;
        int fondo = piso - ALTO_CAMPO;
        g.setPaint(new GradientPaint(0, 0, Estilo.CIELO, 0, fondo, Estilo.CIELO_ABAJO));
        g.fillRect(c.x, 0, c.ancho, fondo);
        dibujarCampo(g, c.ancho, fondo, piso);
        g.setColor(BORDE_CANCHA);
        g.fillRect(c.x, piso, c.ancho, c.alto - piso);
        g.setColor(LINEAS);
        g.fillRect(c.x, piso, c.ancho, 2);
    }

    /**
     * Pinta el campo en perspectiva: pasto a franjas y las líneas de la cancha
     * (borde, línea del medio, círculo central, áreas grandes y chicas y puntos de penal).
     * @param g contexto gráfico
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás del campo
     * @param piso altura del borde de adelante (donde pisan los jugadores)
     */
    private static void dibujarCampo(Graphics2D g, int ancho, int fondo, int piso) {
        g.setColor(Estilo.PASTO_OSCURO);
        g.fillRect(0, fondo, ancho, piso - fondo);
        for (int i = 0; i < FRANJAS; i++) {
            g.setColor(i % 2 == 0 ? Estilo.PASTO : Estilo.PASTO_OSCURO);
            g.fill(cuadro(ancho, fondo, piso, (double) i / FRANJAS, 0, (i + 1.0) / FRANJAS, 1));
        }
        g.setColor(LINEAS);
        g.setStroke(TRAZO_LINEAS);
        g.draw(cuadro(ancho, fondo, piso, 0.02, 0.08, 0.98, 0.97));
        g.draw(linea(ancho, fondo, piso, 0.5, 0.08, 0.5, 0.97));
        g.draw(ovalo(ancho, fondo, piso, 0.5, 0.52, 0.09, 0.3));
        for (double lado : new double[] {0.02, 0.98}) {
            double dentro = lado < 0.5 ? 1 : -1;
            g.draw(cuadro(ancho, fondo, piso, lado, 0.25, lado + dentro * 0.13, 0.82));
            g.draw(cuadro(ancho, fondo, piso, lado, 0.38, lado + dentro * 0.05, 0.68));
            Point2D punto = proyectar(ancho, fondo, piso, lado + dentro * 0.09, 0.53);
            g.fillOval((int) punto.getX() - 3, (int) punto.getY() - 2, 6, 4);
        }
        Point2D centro = proyectar(ancho, fondo, piso, 0.5, 0.52);
        g.fillOval((int) centro.getX() - 4, (int) centro.getY() - 3, 8, 6);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Pasa un punto del campo (u de 0 a 1 a lo ancho, v de 0 atrás a 1 adelante) a la pantalla,
     * con perspectiva: al fondo el campo se ve más angosto.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u posición a lo ancho (0 a 1)
     * @param v profundidad (0 atrás, 1 adelante)
     * @return punto en la pantalla
     */
    private static Point2D proyectar(int ancho, int fondo, int piso, double u, double v) {
        double anchoFila = ancho * (0.82 + 0.3 * v);
        return new Point2D.Double(ancho / 2.0 + (u - 0.5) * anchoFila, fondo + (piso - fondo) * v);
    }

    /**
     * Arma un cuadrilátero del campo en perspectiva.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u1 lado izquierdo (0 a 1)
     * @param v1 lado de atrás (0 a 1)
     * @param u2 lado derecho (0 a 1)
     * @param v2 lado de adelante (0 a 1)
     * @return figura proyectada
     */
    private static Shape cuadro(int ancho, int fondo, int piso, double u1, double v1, double u2, double v2) {
        Path2D figura = new Path2D.Double();
        double[][] esquinas = {{u1, v1}, {u2, v1}, {u2, v2}, {u1, v2}};
        for (int i = 0; i < esquinas.length; i++) {
            Point2D p = proyectar(ancho, fondo, piso, esquinas[i][0], esquinas[i][1]);
            if (i == 0) figura.moveTo(p.getX(), p.getY()); else figura.lineTo(p.getX(), p.getY());
        }
        figura.closePath();
        return figura;
    }

    /**
     * Arma una línea del campo en perspectiva.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u1 inicio a lo ancho
     * @param v1 inicio en profundidad
     * @param u2 fin a lo ancho
     * @param v2 fin en profundidad
     * @return línea proyectada
     */
    private static Shape linea(int ancho, int fondo, int piso, double u1, double v1, double u2, double v2) {
        Path2D figura = new Path2D.Double();
        Point2D a = proyectar(ancho, fondo, piso, u1, v1);
        Point2D b = proyectar(ancho, fondo, piso, u2, v2);
        figura.moveTo(a.getX(), a.getY());
        figura.lineTo(b.getX(), b.getY());
        return figura;
    }

    /**
     * Arma un círculo del campo (se ve como óvalo por la perspectiva).
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u centro a lo ancho
     * @param v centro en profundidad
     * @param radioU radio a lo ancho
     * @param radioV radio en profundidad
     * @return óvalo proyectado
     */
    private static Shape ovalo(int ancho, int fondo, int piso, double u, double v, double radioU, double radioV) {
        Path2D figura = new Path2D.Double();
        for (int i = 0; i <= PUNTOS_CIRCULO; i++) {
            double a = 2 * Math.PI * i / PUNTOS_CIRCULO;
            Point2D p = proyectar(ancho, fondo, piso, u + Math.cos(a) * radioU, v + Math.sin(a) * radioV);
            if (i == 0) figura.moveTo(p.getX(), p.getY()); else figura.lineTo(p.getX(), p.getY());
        }
        return figura;
    }

    /**
     * Pinta un arco: la red, el travesaño y el poste del lado de la cancha.
     * @param g contexto gráfico
     * @param c comando del arco (haciaDerecha es true en el arco izquierdo)
     */
    private static void dibujarArco(Graphics2D g, ComandoDibujo c) {
        g.setColor(RED_ARCO);
        g.fillRect(c.x, c.y, c.ancho, c.alto);
        g.setColor(Color.WHITE);
        g.setStroke(TRAZO_ARCO);
        g.drawLine(c.x, c.y, c.x + c.ancho, c.y);
        int poste = c.haciaDerecha ? c.x + c.ancho : c.x;
        g.drawLine(poste, c.y, poste, c.y + c.alto);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Pinta el balón: círculo blanco con borde y un punto en el centro.
     * @param g contexto gráfico
     * @param c comando del balón (cuadro que lo encierra)
     */
    private static void dibujarBalon(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho / 2;
        g.setColor(Color.WHITE);
        g.fillOval(c.x, c.y, c.ancho, c.alto);
        g.setColor(Color.BLACK);
        g.drawOval(c.x, c.y, c.ancho, c.alto);
        g.fillOval(c.x + r - 4, c.y + r - 4, 8, 8);
    }

    /**
     * Pinta un jugador: cuerpo con los colores de su país (con rayas si las tiene), el pie
     * (siempre visible; gira al patear), cabeza, sangre si recibió un golpe y la etiqueta encima.
     * Si está aturdido se pinta gris con estrellas.
     * @param g contexto gráfico
     * @param c comando del jugador (x, y es el centro de la cabeza y ancho el radio)
     */
    private static void dibujarJugador(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho;
        Color color = c.numero == 1 ? Estilo.COLOR_J1 : Estilo.COLOR_J2;
        Color[] colores = c.aturdido ? Estilo.COLORES_ATURDIDO
                                     : new Color[] {c.colorCabeza, c.colorBanda, c.colorCuerpo};
        int cuello = Jugador.CUELLO * r / Jugador.RADIO;
        g.setColor(colores[2]);
        g.fillRect(c.x - r / 2, c.y + r - cuello, r, r + cuello);
        if (!c.aturdido && c.rayas) {
            g.setColor(Color.WHITE);
            for (int franja = c.y + r; franja < c.y + 2 * r; franja += SEPARACION_RAYAS) {
                g.fillRect(c.x - r / 2, franja, r, SEPARACION_RAYAS / 2);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(c.x - r / 2, c.y + r - cuello, r, r + cuello);
        dibujarPie(g, c);
        dibujarCabeza(g, c.x, c.y, r, colores, c.haciaDerecha, c.aturdido ? Color.GRAY : color);
        if (c.avanceGolpe >= 0) dibujarSangre(g, c);
        g.setFont(Estilo.TEXTO_PEQUENO);
        int anchoEtiqueta = g.getFontMetrics().stringWidth(c.etiqueta);
        g.setColor(Estilo.FONDO_MARCADOR);
        g.fillRoundRect(c.x - anchoEtiqueta / 2 - 6, c.y - r - 22, anchoEtiqueta + 12, 18, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString(c.etiqueta, c.x - anchoEtiqueta / 2, c.y - r - 8);
        g.setColor(color);
        g.fillRect(c.x - anchoEtiqueta / 2 - 6, c.y - r - 6, anchoEtiqueta + 12, 2);
        if (c.aturdido) {
            g.setColor(Color.YELLOW);
            g.drawString("* * *", c.x - 15, c.y - r - 28);
        }
    }

    /**
     * Pinta una cabeza: círculo con la banda en la frente, borde del color del jugador
     * y un ojo hacia donde mira. También se usa junto a las barras del marcador.
     * @param g contexto gráfico
     * @param x centro horizontal
     * @param y centro vertical
     * @param r radio
     * @param colores colores {cabeza, banda, cuerpo}
     * @param derecha true si mira a la derecha
     * @param borde color del borde
     */
    public static void dibujarCabeza(Graphics2D g, int x, int y, int r, Color[] colores, boolean derecha, Color borde) {
        int ojo = OJO * r / Jugador.RADIO;
        g.setColor(colores[0]);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        Shape antes = g.getClip();
        g.clip(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        g.setColor(colores[1]);
        g.fillRect(x - r, y - r / 2, 2 * r, r / 3);
        g.setClip(antes);
        g.setStroke(TRAZO_CABEZA);
        g.setColor(borde);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(TRAZO_NORMAL);
        int xOjo = derecha ? x + r / 3 : x - r / 3 - ojo + 2;
        g.setColor(Color.WHITE);
        g.fillOval(xOjo, y - 4, ojo, ojo);
        g.setColor(Color.BLACK);
        g.drawOval(xOjo, y - 4, ojo, ojo);
        g.fillOval(xOjo + (derecha ? ojo / 2 : 1), y, ojo * 2 / 5, ojo * 2 / 5);
    }

    /**
     * Pinta el pie (zapato). Siempre se ve apoyado adelante del cuerpo; al patear gira hacia
     * arriba y adelante según el avance de la patada, y vuelve a su lugar.
     * Sus medidas son las mismas que usa el dominio para los choques con el balón.
     * @param g contexto gráfico
     * @param c comando del jugador
     */
    private static void dibujarPie(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho;
        int largo = Jugador.LARGO_PIE + r / 2;
        int alto = Jugador.ALTO_PIE;
        double direccion = c.haciaDerecha ? 1 : -1;
        AffineTransform antes = g.getTransform();
        g.translate(c.x, c.y + 2 * r - alto / 2.0);
        if (c.avancePatada >= 0) {
            double giro = Math.sin(Math.PI * c.avancePatada);
            g.rotate(-direccion * Math.toRadians(ANGULO_PATADA) * giro);
            g.translate(direccion * 4 * giro, 0);
        }
        int inicio = direccion > 0 ? -alto / 2 : -largo;
        int ancho = largo + alto / 2;
        g.setColor(ZAPATO);
        g.fillRoundRect(inicio, -alto / 2, ancho, alto, alto, alto);
        g.setColor(SUELA);
        g.fillRect(inicio + alto / 2, alto / 2 - 2, ancho - alto, 2);
        g.setTransform(antes);
    }

    /**
     * Pinta gotas de sangre que salen de la cabeza según el avance del golpe.
     * @param g contexto gráfico
     * @param c comando del jugador
     */
    private static void dibujarSangre(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho;
        g.setColor(Estilo.SANGRE);
        for (int i = 0; i < GOTAS; i++) {
            double angulo = Math.PI * (0.15 + 0.7 * i / (GOTAS - 1));
            int dx = (int) (Math.cos(angulo) * (r + 6 + 14 * c.avanceGolpe));
            int dy = (int) (Math.sin(angulo) * (r + 6 + 14 * c.avanceGolpe));
            g.fillOval(c.x - dx - 3, c.y - dy - 3, 6, 6);
        }
    }
}
