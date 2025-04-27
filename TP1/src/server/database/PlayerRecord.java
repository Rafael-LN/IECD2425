package server.database;

import java.io.Serializable;

public record PlayerRecord(
        String username,
        String password,
        int age,
        String nationality,
        String photoBase64,
        int wins,
        int losses,
        long timePlayed
) implements Serializable {}