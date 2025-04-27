package common;

public record UserProfileData(
        String username,
        int age,
        String nationality,
        int wins,
        int losses,
        long timePlayed,
        String photoBase64
) {}