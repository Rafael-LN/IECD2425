package server.database;

import java.io.Serializable;
import java.util.List;
import common.GameHistory;

public record PlayerRecord(
        String username,
        String password,
        int age,
        String nationality,
        String photoBase64,
        int wins,
        int losses,
        long timePlayed,
        List<GameHistory> gamesHistory
) implements Serializable {}