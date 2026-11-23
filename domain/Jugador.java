package domain;

import dto.ComandoDibujo;
import dto.EstadoJugador;

/**
 * Jugador del partido. Se mueve, salta y patea. Tiene goles, vida, estadísticas, puntos
 * y la barra de poder (se llena con el tiempo; los poderes se usan en la Versión cuatro).
 * (x, y) es el centro de la cabeza del personaje.
 * Es Movible (se mueve), Dibujable (arma su ComandoDibujo), Interactuable (cabecea el balón)
 * Pateable (el rival le puede pegar una patada) y Golpeable (tiene vida y recibe daño).
 * El jugador humano lo controla el teclado; la Maquina (subclase) decide sola.
 */
public class Jugador implements Movible, Dibujable, Interactuable, Pateable, Golpeable {

    /** Radio del cuerpo del jugador. */
    public static final int RADIO = 25;
    /** Distancia extra a la que llega el pie al patear. */
    public static final double ALCANCE_PIE = 20;
    /** Daño base de una patada al rival. */
    public static final double DANIO_PATADA = 10;
    /** Segundos que el jugador queda aturdido cuando su vida llega a cero. */
    public static final double TIEMPO_ATURDIDO = 3;
    /** Fuerza horizontal que recibe el balón con una patada. */
    public static final double FUERZA_PATADA_X = 550;
    /** Fuerza vertical que recibe el balón con una patada (negativa es hacia arriba). */
    public static final double FUERZA_PATADA_Y = -380;
    /** Segundos que dura la animación de la patada. */
    public static final double DURACION_PATADA = 0.25;
    /** Segundos que se muestra el golpe después de recibir daño. */
    public static final double DURACION_GOLPE = 0.4;
    /** Velocidad mínima con la que sale el balón después de un cabezazo. */
    public static final double VELOCIDAD_MINIMA_CABEZAZO = 300;
    /** Segundos que tarda en llenarse la barra de poder. */
    public static final double SEGUNDOS_BARRA = 10;
    /** Píxeles que la cabeza se monta sobre el cuerpo (el cuerpo empieza un poco antes del borde de la cabeza). */
    public static final int CUELLO = 5;
    /** Largo del pie, medido desde el borde del cuerpo hacia donde mira el jugador. */
    public static final int LARGO_PIE = 14;
    /** Alto del pie. */
    public static final int ALTO_PIE = 10;
    /** Fracción de velocidad que conserva el balón al rebotar en el cuerpo o el pie. */
    public static final double REBOTE_CUERPO = 0.6;

    private int numero;
    private Personaje personaje;
    private Estadisticas estadisticas;
    private double x;
    private double y;
    private double velocidadY;
    private boolean enSuelo;
    private boolean mirandoDerecha;
    private int goles;
    private double vida;
    private boolean aturdido;
    private double tiempoAturdido;
    private int puntos;
    private double tiempoPatada;
    private double tiempoGolpe;
    /** Carga de la barra de poder, entre 0 (vacía) y 1 (llena). */
    private double barraPoder;

    /**
     * Crea un jugador con 0 goles, vida máxima y estadísticas en 1.
     * El jugador 1 mira a la derecha y el jugador 2 a la izquierda.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje (país) elegido
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public Jugador(int numero, Personaje personaje, double x, double y) {
        this.numero = numero;
        this.personaje = personaje;
        this.estadisticas = new Estadisticas();
        this.x = x;
        this.y = y;
        this.enSuelo = true;
        this.mirandoDerecha = (numero == 1);
        this.vida = estadisticas.getVidaMax();
    }

    /**
     * Mueve el jugador a la izquierda un paso, si no está aturdido.
     */
    public void moverIzquierda() {
        if (aturdido) return;
        x -= estadisticas.getVelocidad();
        mirandoDerecha = false;
    }

    /**
     * Mueve el jugador a la derecha un paso, si no está aturdido.
     */
    public void moverDerecha() {
        if (aturdido) return;
        x += estadisticas.getVelocidad();
        mirandoDerecha = true;
    }

    /**
     * Hace saltar al jugador si está en el suelo y no está aturdido.
     */
    public void saltar() {
        if (aturdido || !enSuelo) return;
        velocidadY = -estadisticas.getFuerzaSalto();
        enSuelo = false;
    }

    /**
     * Patea. Revisa los elementos Pateable en orden (primero el balón y después el rival)
     * y patea solo al primero que alcance: el balón sale impulsado y el rival recibe
     * 10 x multiplicadorKick de daño.
     * @param balon balón del partido
     * @param rival jugador contrario
     */
    public void patear(Balon balon, Jugador rival) {
        if (aturdido) return;
        tiempoPatada = DURACION_PATADA;
        for (Pateable pateable : new Pateable[] {balon, rival}) {
            if (pateable.estaAlAlcanceDe(this)) {
                pateable.serPateado(this);
                return;
            }
        }
    }

    /**
     * Indica si el pie del pateador alcanza a este jugador (interfaz Pateable).
     * @param pateador jugador que patea
     * @return true si lo alcanza
     */
    @Override
    public boolean estaAlAlcanceDe(Jugador pateador) {
        return pateador != this && pateador.alcanzaRival(this);
    }

    /**
     * Recibe la patada del rival (interfaz Pateable): pierde 10 x multiplicadorKick del pateador.
     * @param pateador jugador que patea
     */
    @Override
    public void serPateado(Jugador pateador) {
        recibirDanio(DANIO_PATADA * pateador.getEstadisticas().getMultiplicadorKick());
    }

    /**
     * Choque con el balón (interfaz Interactuable): primero revisa la cabeza (cabezazo)
     * y, si no la tocó, el cuerpo y el pie. El balón nunca los atraviesa.
     * @param balon balón del partido
     * @param partido partido en curso
     */
    @Override
    public void interactuar(Balon balon, Partido partido) {
        if (chocarConCabeza(balon)) return;
        chocarConCuerpo(balon);
    }

    /**
     * Choque del balón con la cabeza (cabezazo): el balón se saca fuera de la cabeza y sale rebotado.
     * @param balon balón del partido
     * @return true si el balón tocó la cabeza
     */
    private boolean chocarConCabeza(Balon balon) {
        double dx = balon.getX() - x;
        double dy = balon.getY() - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        double minimo = RADIO + balon.getRadio();
        if (distancia >= minimo || distancia == 0) return false;
        double nx = dx / distancia;
        double ny = dy / distancia;
        double velocidad = Math.max(VELOCIDAD_MINIMA_CABEZAZO, balon.getVelocidad());
        balon.rebotarEn(x + nx * minimo, y + ny * minimo, nx, ny, 1);
        balon.recibirPatada(nx * velocidad, ny * velocidad);
        return true;
    }

    /**
     * Choque del balón con el cuerpo o con el pie: el balón no los puede atravesar,
     * así que se saca fuera y rebota.
     * @param balon balón del partido
     * @return true si el balón tocó el cuerpo o el pie
     */
    public boolean chocarConCuerpo(Balon balon) {
        double base = y + RADIO * 2;
        double frente = mirandoDerecha ? 1 : -1;
        double bordeFrente = x + frente * RADIO / 2.0;
        double puntaPie = bordeFrente + frente * LARGO_PIE;
        boolean cuerpo = chocarConRectangulo(balon, x - RADIO / 2.0, y + RADIO - CUELLO, x + RADIO / 2.0, base);
        boolean pie = chocarConRectangulo(balon, Math.min(x, puntaPie), base - ALTO_PIE, Math.max(x, puntaPie), base);
        return cuerpo || pie;
    }

    /**
     * Saca el balón de un rectángulo del jugador (cuerpo o pie) y lo hace rebotar.
     * Si el centro del balón quedó adentro, lo saca por el lado más cercano (nunca hacia el piso).
     * @param balon balón del partido
     * @param izquierda borde izquierdo
     * @param arriba borde de arriba
     * @param derecha borde derecho
     * @param abajo borde de abajo
     * @return true si el balón tocaba el rectángulo
     */
    private boolean chocarConRectangulo(Balon balon, double izquierda, double arriba, double derecha, double abajo) {
        double bx = balon.getX();
        double by = balon.getY();
        double r = balon.getRadio();
        double cercanoX = Math.max(izquierda, Math.min(derecha, bx));
        double cercanoY = Math.max(arriba, Math.min(abajo, by));
        double dx = bx - cercanoX;
        double dy = by - cercanoY;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia >= r) return false;
        if (distancia > 0) {
            balon.rebotarEn(cercanoX + dx / distancia * r, cercanoY + dy / distancia * r,
                dx / distancia, dy / distancia, REBOTE_CUERPO);
            return true;
        }
        double haciaIzquierda = bx - izquierda;
        double haciaDerecha = derecha - bx;
        double haciaArriba = by - arriba;
        if (haciaArriba <= haciaIzquierda && haciaArriba <= haciaDerecha) {
            balon.rebotarEn(bx, arriba - r, 0, -1, REBOTE_CUERPO);
        } else if (haciaIzquierda < haciaDerecha) {
            balon.rebotarEn(izquierda - r, by, -1, 0, REBOTE_CUERPO);
        } else {
            balon.rebotarEn(derecha + r, by, 1, 0, REBOTE_CUERPO);
        }
        return true;
    }

    /**
     * Arma los datos para dibujar al jugador (interfaz Dibujable): posición, colores de su país, hacia dónde mira,
     * cuánto va de la patada, si está aturdido o golpeado, y la etiqueta "J1 · país" ("CPU" si es la máquina).
     * @return comando de dibujo del jugador
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        String etiqueta = (esMaquina() ? "CPU" : "J" + numero) + " · " + getPais();
        double avanceGolpe = estaGolpeado() ? 1 - tiempoGolpe / DURACION_GOLPE : -1;
        double avancePatada = estaPateando() ? 1 - tiempoPatada / DURACION_PATADA : -1;
        return new ComandoDibujo(ComandoDibujo.Forma.JUGADOR, (int) x, (int) y, RADIO, RADIO * 2,
            numero, personaje.getColores(), personaje.tieneRayas(), etiqueta, mirandoDerecha,
            avancePatada, aturdido, avanceGolpe);
    }

    /**
     * Indica si el balón está al alcance del jugador (cuerpo + pie).
     * @param balon balón del partido
     * @return true si lo alcanza
     */
    public boolean tocaBalon(Balon balon) {
        double dx = balon.getX() - x;
        double dy = balon.getY() - (y + RADIO);
        double distancia = Math.sqrt(dx * dx + dy * dy);
        return distancia <= RADIO * 2 + balon.getRadio() + ALCANCE_PIE;
    }

    /**
     * Indica si el rival está al alcance del pie.
     * @param rival jugador contrario
     * @return true si lo alcanza
     */
    public boolean alcanzaRival(Jugador rival) {
        double dx = Math.abs(rival.getX() - x);
        double dy = Math.abs(rival.getY() - y);
        return dx <= RADIO * 2 + ALCANCE_PIE && dy <= RADIO * 2;
    }

    /**
     * Resta vida al jugador y muestra el golpe. La vida nunca queda menor que cero.
     * Solo si llega a cero queda aturdido 3 segundos.
     * @param danio cantidad de vida que se resta
     */
    @Override
    public void recibirDanio(double danio) {
        vida = Math.max(0, vida - danio);
        tiempoGolpe = DURACION_GOLPE;
        if (vida == 0 && !aturdido) {
            aturdido = true;
            tiempoAturdido = TIEMPO_ATURDIDO;
        }
    }

    /**
     * Suma un gol al jugador.
     */
    public void anotarGol() {
        goles++;
    }

    /**
     * Suma puntos al jugador (100 al ganar, 25 al perder).
     * @param cantidad puntos que se suman
     */
    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
    }

    /**
     * Turno del jugador para decidir qué hacer. El jugador humano no hace nada aquí
     * porque lo controla el teclado; la Maquina lo sobrescribe.
     * @param partido partido en curso
     */
    public void jugar(Partido partido) {
        // El jugador humano se controla con el teclado.
    }

    /**
     * Arma el estado del jugador para mostrarlo en la interfaz (DTO de solo lectura).
     * @return estado del jugador
     */
    public EstadoJugador aEstado() {
        return new EstadoJugador(numero, getPais().getNombre(), personaje.getColores(), goles, vida, getVidaMax(),
            aturdido, esMaquina(), getNombrePerfil(), barraPoder);
    }

    /**
     * @return nombre del perfil de la máquina; null porque este jugador es humano (la Maquina lo sobrescribe)
     */
    public String getNombrePerfil() { return null; }

    /**
     * @return false, porque este jugador lo controla una persona (la Maquina retorna true)
     */
    public boolean esMaquina() { return false; }

    /**
     * Mueve el jugador (interfaz Movible): carga la barra de poder, gravedad, límites de la cancha
     * y tiempo de aturdimiento.
     * Al terminar el aturdimiento, la vida vuelve a su máximo.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha del partido
     */
    @Override
    public void mover(double segundos, Cancha cancha) {
        tiempoPatada = Math.max(0, tiempoPatada - segundos);
        tiempoGolpe = Math.max(0, tiempoGolpe - segundos);
        aumentarBarra(segundos / SEGUNDOS_BARRA);
        if (aturdido) {
            tiempoAturdido -= segundos;
            if (tiempoAturdido <= 0) {
                aturdido = false;
                tiempoAturdido = 0;
                vida = estadisticas.getVidaMax();
            }
        }
        velocidadY += cancha.getGravedad() * segundos;
        y += velocidadY * segundos;
        double sueloCabeza = cancha.getNivelPiso() - RADIO * 2;
        if (y >= sueloCabeza) {
            y = sueloCabeza;
            velocidadY = 0;
            enSuelo = true;
        }
        x = Math.max(RADIO, Math.min(cancha.getAncho() - RADIO, x));
    }

    /**
     * Desplaza al jugador horizontalmente sin salirse de la cancha.
     * Se usa para separar a los jugadores cuando se pisan.
     * @param dx distancia horizontal (negativa hacia la izquierda)
     * @param cancha cancha del partido
     */
    public void desplazar(double dx, Cancha cancha) {
        x = Math.max(RADIO, Math.min(cancha.getAncho() - RADIO, x + dx));
    }

    /**
     * Cambia las estadísticas del jugador (las que compró en partidos anteriores)
     * y deja la vida en su nuevo máximo.
     * @param estadisticas estadísticas que usa el jugador
     */
    public void setEstadisticas(Estadisticas estadisticas) {
        this.estadisticas = estadisticas;
        this.vida = estadisticas.getVidaMax();
    }

    /**
     * Suma carga a la barra de poder, sin pasarse de llena.
     * @param fraccion fracción de la barra que se suma (1 = la barra completa)
     */
    public void aumentarBarra(double fraccion) {
        barraPoder = Math.max(0, Math.min(1, barraPoder + fraccion));
    }

    /**
     * @return carga de la barra de poder entre 0 y 1
     */
    public double getBarraPoder() { return barraPoder; }

    /**
     * Cambia los goles del jugador (se usa al abrir una partida guardada).
     * @param goles goles anotados
     */
    public void setGoles(int goles) {
        this.goles = Math.max(0, goles);
    }

    /**
     * Pone el jugador en una posición, quieto y en el suelo (interfaz Movible).
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    @Override
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        velocidadY = 0;
        enSuelo = true;
        mirandoDerecha = (numero == 1);
    }

    /**
     * @return número del jugador
     */
    public int getNumero() { return numero; }

    /**
     * @return goles anotados
     */
    public int getGoles() { return goles; }

    /**
     * @return vida actual
     */
    @Override
    public double getVida() { return vida; }

    /**
     * @return personaje del jugador
     */
    public Personaje getPersonaje() { return personaje; }

    /**
     * @return estadísticas del jugador
     */
    public Estadisticas getEstadisticas() { return estadisticas; }

    /**
     * @return puntos acumulados
     */
    public int getPuntos() { return puntos; }

    /**
     * @return true si está aturdido
     */
    @Override
    public boolean estaAturdido() { return aturdido; }

    /**
     * @return posición horizontal
     */
    @Override
    public double getX() { return x; }

    /**
     * @return posición vertical
     */
    @Override
    public double getY() { return y; }

    /**
     * @return radio de la cabeza
     */
    @Override
    public int getRadio() { return RADIO; }

    /**
     * @return píxeles que avanza en cada paso según su speed (interfaz Movible)
     */
    @Override
    public double getVelocidad() { return estadisticas.getVelocidad(); }

    /**
     * @return true si está en el suelo (no está saltando)
     */
    public boolean estaEnSuelo() { return enSuelo; }

    /**
     * @return true si mira a la derecha
     */
    public boolean estaMirandoDerecha() { return mirandoDerecha; }

    /**
     * @return país del personaje del jugador
     */
    public Pais getPais() { return personaje.getPais(); }

    /**
     * @return vida máxima según las estadísticas
     */
    @Override
    public double getVidaMax() { return estadisticas.getVidaMax(); }

    /**
     * @return true mientras se ve la animación de la patada
     */
    public boolean estaPateando() { return tiempoPatada > 0; }

    /**
     * @return segundos que le quedan a la animación de la patada
     */
    public double getTiempoPatada() { return tiempoPatada; }

    /**
     * @return true mientras se muestra el golpe recibido
     */
    public boolean estaGolpeado() { return tiempoGolpe > 0; }

    /**
     * @return segundos que le quedan al golpe
     */
    public double getTiempoGolpe() { return tiempoGolpe; }
}
