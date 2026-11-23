package domain;

import dto.ComandoDibujo;

/**
 * Elemento del partido que se puede dibujar. Cada elemento arma su propio ComandoDibujo
 * para que la presentación lo pinte sin acceder al dominio directamente.
 * La implementan Cancha, Arco, Balon y Jugador.
 */
public interface Dibujable {

    /**
     * @return datos para dibujar el elemento en su estado actual
     */
    ComandoDibujo aComandoDibujo();
}
