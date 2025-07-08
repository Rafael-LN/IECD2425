package common;

import java.util.List;

public record UserProfileData(
        String username,
        int age,
        String nationality,
        int wins,
        int losses,
        long timePlayed,
        String photoBase64,
        List<GameHistory> gamesHistory
) {}