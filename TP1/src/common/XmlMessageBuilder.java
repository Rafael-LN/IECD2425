package common;

public class XmlMessageBuilder {

    public static String buildLoginRequest(String username, String password) {
        return "<loginRequest xmlns=\"http://tp1/auth\">" +
                "<username>" + username + "</username>" +
                "<password>" + password + "</password>" +
                "</loginRequest>";
    }

    public static String buildRegisterRequest(String username, String password, int age, String nationality, String photoPath) {
        return "<registerRequest xmlns=\"http://tp1/auth\">" +
                "<username>" + username + "</username>" +
                "<password>" + password + "</password>" +
                "<age>" + age + "</age>" +
                "<nationality>" + nationality + "</nationality>" +
                "<photo>" + photoPath + "</photo>" +
                "</registerRequest>";
    }

    public static String buildUpdateProfileRequest(String username, String photoBase64) {
        return "<updateProfileRequest>" +
                "<username>" + username + "</username>" +
                "<photo>" + photoBase64 + "</photo>" +
                "</updateProfileRequest>";
    }


    public static String buildResponse(String status, String message, String operation) {
        return "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>" + operation + "</operation>" +
                "</response>";
    }

    public static String buildLoginResponse(String status, String message, String username, String photoBase64, int age, String nationality, int wins, int losses, long timePlayed) {
        return "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>login</operation>" +
                "<username>" + username + "</username>" +
                "<photo>" + (photoBase64 != null ? photoBase64 : "") + "</photo>" +
                "<age>" + age + "</age>" +
                "<nationality>" + nationality + "</nationality>" +
                "<wins>" + wins + "</wins>" +
                "<losses>" + losses + "</losses>" +
                "<timePlayed>" + timePlayed + "</timePlayed>" +
                "</response>";
    }

    public static String buildFindMatchRequest(String username) {
        return "<findMatch>" +
                "<username>" + username + "</username>" +
                "</findMatch>";
    }


    public static String buildGameStart(String you, String opponent, boolean firstToPlay) {
        return "<gameStart>" +
                "<player>" + you + "</player>" +
                "<opponent>" + opponent + "</opponent>" +
                "<firstToPlay>" + firstToPlay + "</firstToPlay>" +
                "</gameStart>";
    }
}
