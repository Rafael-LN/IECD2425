package common;

public class XmlMessageBuilder {

    
    private static String wrapWithMessage(String contentXml) {
        return "<message xmlns=\"http://tp1/protocol\">" + contentXml + "</message>";
    }

    public static String buildLoginRequest(String username, String password) {
        String content = "<auth:loginRequest xmlns:auth=\"http://tp1/auth\">" +
                "<auth:username>" + username + "</auth:username>" +
                "<auth:password>" + password + "</auth:password>" +
                "</auth:loginRequest>";
        return wrapWithMessage(content);
    }

    public static String buildRegisterRequest(String username, String password, int age, String nationality, String photoPath) {
        String content = "<auth:registerRequest xmlns:auth=\"http://tp1/auth\">" +
                "<auth:username>" + username + "</auth:username>" +
                "<auth:password>" + password + "</auth:password>" +
                "<auth:age>" + age + "</auth:age>" +
                "<auth:nationality>" + nationality + "</auth:nationality>" +
                "<auth:photo>" + photoPath + "</auth:photo>" +
                "</auth:registerRequest>";
        return wrapWithMessage(content);
    }

    public static String buildUpdateProfileRequest(String username, String photoBase64) {
        String content = "<auth:updateProfileRequest xmlns:auth=\"http://tp1/auth\">" +
                "<auth:username>" + username + "</auth:username>" +
                "<auth:photo>" + photoBase64 + "</auth:photo>" +
                "</auth:updateProfileRequest>";
        return wrapWithMessage(content);
    }

    public static String buildResponse(String status, String message, String operation) {
        String content = "<auth:response xmlns:auth=\"http://tp1/auth\">" +
                "<auth:status>" + status + "</auth:status>" +
                "<auth:message>" + message + "</auth:message>" +
                "<auth:operation>" + operation + "</auth:operation>" +
                "</auth:response>";
        return wrapWithMessage(content);
    }

    public static String buildLoginResponse(String status, String message, String username, String photoBase64, int age, String nationality, int wins, int losses, long timePlayed) {
        String content = "<auth:response xmlns:auth=\"http://tp1/auth\">" +
                "<auth:status>" + status + "</auth:status>" +
                "<auth:message>" + message + "</auth:message>" +
                "<auth:operation>login</auth:operation>" +
                "<auth:username>" + username + "</auth:username>" +
                "<auth:photo>" + (photoBase64 != null ? photoBase64 : "") + "</auth:photo>" +
                "<auth:age>" + age + "</auth:age>" +
                "<auth:nationality>" + nationality + "</auth:nationality>" +
                "<auth:wins>" + wins + "</auth:wins>" +
                "<auth:losses>" + losses + "</auth:losses>" +
                "<auth:timePlayed>" + timePlayed + "</auth:timePlayed>" +
                "</auth:response>";
        return wrapWithMessage(content);
    }

    public static String buildFindMatchRequest(String username) {
        String content = "<game:findMatch xmlns:game=\"http://tp1/game\">" +
                "<game:username>" + username + "</game:username>" +
                "</game:findMatch>";
        return wrapWithMessage(content);
    }

    public static String buildCancelMatchRequest(String username) {
        String content = "<game:cancelMatch xmlns:game=\"http://tp1/game\">" +
                "<game:username>" + username + "</game:username>" +
                "</game:cancelMatch>";
        return wrapWithMessage(content);
    }

    public static String buildGameStart(String player, String opponent, boolean firstToPlay) {
        String content = "<game:gameStart xmlns:game=\"http://tp1/game\">" +
                "<game:player>" + player + "</game:player>" +
                "<game:opponent>" + opponent + "</game:opponent>" +
                "<game:firstToPlay>" + firstToPlay + "</game:firstToPlay>" +
                "</game:gameStart>";
        return wrapWithMessage(content);
    }

    public static String buildMoveRequest(String username, int row, int col) {
        String content = "<game:move xmlns:game=\"http://tp1/game\">" +
                "<game:username>" + username + "</game:username>" +
                "<game:row>" + row + "</game:row>" +
                "<game:col>" + col + "</game:col>" +
                "</game:move>";
        return wrapWithMessage(content);
    }
}
