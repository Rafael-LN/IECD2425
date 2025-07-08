package common;

import java.util.List;

public class XmlMessageBuilder {

    
    private static String wrapWithMessage(String contentXml) {
        return "<message>" + contentXml + "</message>";
    }

    public static String buildLoginRequest(String username, String password) {
        String content = "<loginRequest>" +
                "<username>" + username + "</username>" +
                "<password>" + password + "</password>" +
                "</loginRequest>";
        return wrapWithMessage(content);
    }

    public static String buildRegisterRequest(String username, String password, int age, String nationality, String photoPath) {
        String content = "<registerRequest>" +
                "<username>" + username + "</username>" +
                "<password>" + password + "</password>" +
                "<age>" + age + "</age>" +
                "<nationality>" + nationality + "</nationality>" +
                "<photo>" + photoPath + "</photo>" +
                "</registerRequest>";
        return wrapWithMessage(content);
    }

    public static String buildUpdateProfileRequest(String username, String photoBase64) {
        String content = "<updateProfileRequest>" +
                "<username>" + username + "</username>" +
                "<photo>" + photoBase64 + "</photo>" +
                "</updateProfileRequest>";
        return wrapWithMessage(content);
    }

    public static String buildRequestProfile(String username) {
        String content = "<getProfileRequest>" +
                "<username>" + username + "</username>" +
                "</getProfileRequest>";
        return wrapWithMessage(content);
    }


    public static String buildResponse(String status, String message, String operation) {
        String content = "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>" + operation + "</operation>"+
                "</response>";
        return wrapWithMessage(content);
    }

    // Serializa o histórico de jogos para XML (String)
    private static String buildGamesHistoryXml(List<GameHistory> gamesHistory) {
        StringBuilder xml = new StringBuilder("<gamesHistory>");
        if (gamesHistory != null) {
            for (GameHistory entry : gamesHistory) {
                xml.append("<game>" + "<dateTime>").append(entry.dateTime()).append("</dateTime>").append("<duration>").append(entry.duration()).append("</duration>").append("<opponent>").append(entry.opponent()).append("</opponent>").append("<result>").append(entry.result()).append("</result>").append("</game>");
            }
        }
        xml.append("</gamesHistory>");
        return xml.toString();
    }

    public static String buildFindMatchRequest(String username) {
        String content = "<findMatch>" +
                "<username>" + username + "</username>" +
                "</findMatch>";
        return wrapWithMessage(content);
    }

    public static String buildCancelMatchRequest(String username) {
        String content = "<cancelMatch>" +
                "<username>" + username + "</username>" +
                "</cancelMatch>";
        return wrapWithMessage(content);
    }

    public static String buildGameStart(String player, String opponent, boolean firstToPlay) {
        String content = "<gameStart>" +
                "<player>" + player + "</player>" +
                "<opponent>" + opponent + "</opponent>" +
                "<firstToPlay>" + firstToPlay + "</firstToPlay>" +
                "</gameStart>";
        return wrapWithMessage(content);
    }

    public static String buildMoveRequest(String username, int row, int col) {
        String content = "<move>" +
                "<player>" + username + "</player>" +
                "<row>" + row + "</row>" +
                "<col>" + col + "</col>" +
                "</move>";
        return wrapWithMessage(content);
    }

    public static String buildGameEnd(String winner, String reason, String message) {
        String content = "<gameEnd>";
        if (winner != null && !winner.isEmpty()) {
            content += "<winner>" + winner + "</winner>";
        }
        content += "<reason>" + reason + "</reason>";
        content += "<message>" + message + "</message>";
        content += "</gameEnd>";
        return wrapWithMessage(content);
    }

    public static String buildLogoutRequest(String username) {
        String content = "<logoutRequest>" +
                "<username>" + username + "</username>" +
                "</logoutRequest>";
        return wrapWithMessage(content);
    }


   public static String buildResponse(String status, String message, String operation, String username, String photoBase64, int age, String nationality, int wins, int losses, long timePlayed, List<GameHistory> gamesHistory) {
        String content = "<response>"
            + "<status>" + status + "</status>"
            + "<message>" + message + "</message>"
            + "<operation>" + operation + "</operation>"
            + "<username>" + username + "</username>"
            + "<photo>" + (photoBase64 != null ? photoBase64 : "") + "</photo>"
            + "<age>" + age + "</age>"
            + "<nationality>" + nationality + "</nationality>"
            + "<wins>" + wins + "</wins>"
            + "<losses>" + losses + "</losses>"
            + "<timePlayed>" + timePlayed + "</timePlayed>"
            + buildGamesHistoryXml(gamesHistory)
            + "</response>";
        return wrapWithMessage(content);
    }

    public static String buildQuitMatchRequest(String username) {
        String content = "<quitMatch>" +
                "<username>" + username + "</username>" +
                "</quitMatch>";
        return wrapWithMessage(content);
    }
}
