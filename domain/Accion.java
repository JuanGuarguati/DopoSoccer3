package domain;

/**
 * Acciones que puede escoger una máquina en cada turno (ver EstrategiaMaquina).
 */
public enum Accion {
    /** No hace nada en este turno. */
    QUEDARSE,
    /** Se mueve un paso a la izquierda. */
    MOVER_IZQUIERDA,
    /** Se mueve un paso a la derecha. */
    MOVER_DERECHA,
    /** Salta. */
    SALTAR,
    /** Patea (el balón o al rival). */
    PATEAR
}
