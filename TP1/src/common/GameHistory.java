package common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa um histórico de jogo para um jogador.
 */
public record GameHistory(
    LocalDateTime dateTime,   // Data e hora do jogo
    long duration,            // Duração em milissegundos
    String opponent,          // Nome do adversário
    String result             // "vitória", "derrota" ou "empate"
) implements Serializable {}
