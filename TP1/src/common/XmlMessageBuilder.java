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

    public static String buildMove(String player, int x, int y) {
        return "<move xmlns=\"http://tp1/game\">" +
                "<player>" + player + "</player>" +
                "<x>" + x + "</x>" +
                "<y>" + y + "</y>" +
                "</move>";
    }

    public static String buildGameStart(String opponent, boolean firstPlayer) {
        return "<gameStart xmlns=\"http://tp1/game\">" +
                "<opponent>" + opponent + "</opponent>" +
                "<firstPlayer>" + firstPlayer + "</firstPlayer>" +
                "</gameStart>";
    }
}
