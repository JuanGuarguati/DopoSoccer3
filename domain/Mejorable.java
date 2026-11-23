package domain;

/**
 * Elemento que se puede mejorar comprando mejoras con puntos.
 * La implementa Estadisticas (speed, kick, jump y survival). DOPOSoccer cobra la mejora
 * y le pide al Mejorable que suba la estadística.
 */
public interface Mejorable {

    /**
     * Sube en 1 una estadística.
     * @param estadistica estadística que se mejora
     * @throws DOPOSoccerException si no se puede mejorar (null o ya está en el máximo)
     */
    void mejorar(Estadistica estadistica) throws DOPOSoccerException;

    /**
     * @param estadistica estadística que se consulta
     * @return valor actual de la estadística
     */
    int getValor(Estadistica estadistica);

    /**
     * @return número de mejoras compradas (sirve para calcular el precio de la siguiente)
     */
    int getMejorasCompradas();
}
