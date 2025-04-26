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

    public static String buildResponse(String status, String message, String operation) {
        return "<response>" +
                "<status>" + status + "</status>" +
                "<message>" + message + "</message>" +
                "<operation>" + operation + "</operation>" +
                "</response>";
    }

    public static String buildGameStart(String you, String opponent, boolean firstToPlay) {
        return "<gameStart>" +
                "<player>" + you + "</player>" +
                "<opponent>" + opponent + "</opponent>" +
                "<firstToPlay>" + firstToPlay + "</firstToPlay>" +
                "</gameStart>";
    }
}
