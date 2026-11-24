DOPOSoccer - Versión tres (interfaces, DTO, Strategy, ciclo de juego fijo, correcciones y pruebas de aceptación)

ESTRUCTURA (lista para BlueJ)
DOPOSoccer/
  package.bluej        proyecto de BlueJ (abrir esta carpeta con Proyecto > Abrir proyecto)
  domain/              lógica del juego (no sabe nada de Swing)
  dto/                 datos que el dominio le entrega a la interfaz: ComandoDibujo y EstadoJugador
  presentation/        interfaz gráfica (Swing)
  tests/               pruebas de unidad y de aceptación (JUnit 4): 100 pruebas
  lib/                 junit-4.13.2.jar y hamcrest-core-1.3.jar (VS Code y consola)
  +libs/               las mismas librerías; BlueJ las carga solo desde esta carpeta
  documentacion/       retrospectiva (v1 en PDF y actual), planificación de las versiones,
                       bocetos de la interfaz (interfaces/), capturas del juego (capturas/)
                       y diagramas de clases y de secuencia en PNG (diagramas/)
  DOPOSoccer_Diagramas_.asta  diagramas de Astah (clases y secuencia)

CÓMO ABRIRLO EN BLUEJ
1. Proyecto > Abrir proyecto... y elegir la carpeta DOPOSoccer (la que tiene package.bluej).
2. Clic en "Compilar".
3. Para jugar: entrar al paquete presentation, clic derecho en DOPOSoccerGUI > void main(String[] args).
4. Para las pruebas: clic en "Ejecutar pruebas" (o clic derecho en cada clase de tests).

CÓMO ABRIRLO EN VISUAL STUDIO CODE
1. Instalar el "Extension Pack for Java".
2. Archivo > Abrir carpeta... y elegir la carpeta DOPOSoccer (la configuración ya está en .vscode).
3. Para jugar: Ejecutar y depurar > "Jugar DOPOSoccer" (o Run sobre presentation/DOPOSoccerGUI.java).
4. Para las pruebas: pestaña Testing (el matraz) > ejecutar todas las pruebas de la carpeta tests.

CONTROLES
Jugador 1: A / D mover, W saltar, S patear
Jugador 2: flechas (si es la máquina, se mueve sola)
P o el botón II: pausa     Esc: terminar
En la pantalla de inicio, "Cómo jugar" muestra las reglas y los controles.

DISEÑO
Interfaces (11):
  domain: Movible (Jugador, Balon), Dibujable (Cancha, Arco, Balon, Jugador),
          Interactuable (Jugador, Arco), Pateable (Balon, Jugador), PoderEspecial (Personaje),
          EstrategiaMaquina (las 5 máquinas), RepositorioPartida (PartidoDAO),
          ControlPartido (DOPOSoccer, Partido), Golpeable (Jugador), Mejorable (Estadisticas)
  presentation: VistaPrincipal (DOPOSoccerGUI; los paneles solo conocen esta interfaz)
Patrones: Strategy (la Maquina recibe su EstrategiaMaquina y se puede cambiar en pleno partido),
  Singleton (DOPOSoccer y PartidoDAO), DTO (dto.ComandoDibujo dice cómo pintar cada objeto),
  fábricas en los enums (Pais crea el Personaje, ModoJuego crea el Partido, PerfilMaquina crea la estrategia).
Enums: Pais, Direccion, Estadistica, Accion, PerfilMaquina, Modalidad, Partido.ModoJuego,
  PanelConfiguracion.Resolucion, DOPOSoccerGUI.Pantalla, ComandoDibujo.Forma.
Excepciones: DOPOSoccerException y sus hijas PersistenciaException y FormatoArchivoException.
Ciclo de juego: CicloJuego avanza el dominio en pasos fijos de 1/60 s (no depende de los cuadros
  por segundo del computador) y después repinta una sola vez.

PARA LA VERSIÓN CUATRO (ver documentacion/Planificacion de las versiones DOPOSoccer.docx)
- Tiros de poder del Anexo 1 (los métodos de PoderEspecial existen, pero todavía no hacen nada).
- Modificación de la resolución de la cancha.
- Resto de la estética (tribuna, anuncios, arcos con malla, aura y efectos).
- Error pendiente: el precio de las mejoras debe ser 500 y duplicarse.

YA INCLUIDO EN ESTA VERSIÓN (adelantado)
- El balón choca con la cabeza, el cuerpo y el pie del cabezón (ya no los atraviesa) y avanza
  en sub-pasos, así que tampoco atraviesa el travesaño aunque vaya muy rápido.
- El pie siempre se ve y gira al patear.
- Las líneas de la cancha (borde, mitad, círculo central y áreas) en perspectiva.
- La barra de poder en forma de flama, con la cabeza del país; se llena en 10 s
  (usar el poder es de la Versión cuatro).
