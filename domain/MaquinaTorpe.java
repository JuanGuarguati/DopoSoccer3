package domain;

import java.util.Random;

/**
 * Estrategia torpe: escoge al azar qué hacer en cada turno.
 */
public class MaquinaTorpe implements EstrategiaMaquina {

    private Random azar;

    /**
     * Crea la estrategia torpe con un azar distinto cada vez.
     */
    public MaquinaTorpe() {
        azar = new Random();
    }

    /**
     * Crea la estrategia torpe con una semilla fija (sirve para las pruebas).
     * @param semilla semilla del generador de números al azar
     */
    public MaquinaTorpe(long semilla) {
        azar = new Random(semilla);
    }

    /**
     * Decide una acción cualquiera al azar.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso
     * @return una de las acciones de Accion, al azar
     */
    @Override
    public Accion decidirAccion(Maquina maquina, Partido partido) {
        Accion[] acciones = Accion.values();
        return acciones[azar.nextInt(acciones.length)];
    }

    /**
     * @return PerfilMaquina.TORPE
     */
    @Override
    public PerfilMaquina getPerfil() { return PerfilMaquina.TORPE; }
}
