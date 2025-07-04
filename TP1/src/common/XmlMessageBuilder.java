package common;

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

    public static String buildResponse(String status, String message, String operation) {
        String content = "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>" + operation + "</operation>";
        return wrapWithMessage(content);
    }

    public static String buildAuthResponse(String status, String message, String operation, String username, String photoBase64, int age, String nationality, int wins, int losses, long timePlayed) {
        String content = "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>"+ operation +"</operation>" +
                "<username>" + username + "</username>" +
                "<photo>" + (photoBase64 != null ? photoBase64 : "") + "</photo>" +
                "<age>" + age + "</age>" +
                "<nationality>" + nationality + "</nationality>" +
                "<wins>" + wins + "</wins>" +
                "<losses>" + losses + "</losses>" +
                "<timePlayed>" + timePlayed + "</timePlayed>" +
                "</response>";
        return wrapWithMessage(content);
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
                "<username>" + username + "</username>" +
                "<row>" + row + "</row>" +
                "<col>" + col + "</col>" +
                "</move>";
        return wrapWithMessage(content);
    }
}
