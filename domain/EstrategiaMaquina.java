package domain;

/**
 * Estrategia de juego de una máquina (patrón Strategy).
 * La máquina le pregunta a su estrategia qué hacer en cada turno; la estrategia
 * se puede cambiar mientras el partido está en curso.
 */
public interface EstrategiaMaquina {

    /**
     * Decide qué hace la máquina en este turno.
     * @param maquina máquina que va a actuar
     * @param partido partido en curso (de aquí se leen el balón, la cancha y el rival)
     * @return acción que debe ejecutar la máquina
     */
    Accion decidirAccion(Maquina maquina, Partido partido);

    /**
     * @return perfil al que corresponde esta estrategia
     */
    PerfilMaquina getPerfil();
}
