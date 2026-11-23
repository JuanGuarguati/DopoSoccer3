package domain;

import java.io.File;

/**
 * Contrato de persistencia: guardar y abrir partidas.
 * La fachada DOPOSoccer depende de esta interfaz y no de cómo se guarda el archivo.
 * La implementa PartidoDAO (archivos de texto).
 */
public interface RepositorioPartida {

    /**
     * Guarda la partida del juego en un archivo.
     * @param juego juego que se guarda
     * @param archivo archivo destino
     * @throws DOPOSoccerException si no se puede guardar
     */
    void guardar(DOPOSoccer juego, File archivo) throws DOPOSoccerException;

    /**
     * Abre una partida guardada y la carga en el juego.
     * @param juego juego donde se carga la partida
     * @param archivo archivo que se abre
     * @throws DOPOSoccerException si no se puede abrir o no es una partida válida
     */
    void abrir(DOPOSoccer juego, File archivo) throws DOPOSoccerException;
}
