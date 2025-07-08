package client.handler;

import client.ClientSession;
import common.GameClientListener;
import common.UserProfileData;
import common.XmlMessageReader;
import common.GameHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ClientMessageHandler {

    private final ClientSession session;
    private final GameClientListener gui;

    public ClientMessageHandler(ClientSession session, GameClientListener gui) {
        this.session = session;
        this.gui = gui;
    }

    public void handle(Document doc) {
        Element payload = XmlMessageReader.getPayloadElement(doc);
        String type = XmlMessageReader.getPayloadType(doc);

        switch (type) {
            case "response" -> {
                String status = XmlMessageReader.getTextValue(payload, "status");
                String msg = XmlMessageReader.getTextValue(payload, "message");
                String operation = XmlMessageReader.getTextValue(payload, "operation");

                if (operation == null) {
                    operation = determineOperationType(msg);
                }

                if ("success".equals(status)) {
                    System.out.println("✅ SUCESSO: " + msg);

                    switch (operation) {
                        case "login" -> {
                            populateUserProfile(payload);
                            session.login(session.getProfile());
                            gui.onLoginSuccess(session.getProfile().username());
                        }

                        case "register" -> {
                            populateUserProfile(payload);
                            session.login(session.getProfile());
                            gui.onRegisterSuccess(session.getProfile().username());
                        }

                        case "logout" -> {
                            System.out.println("✅ Logout realizado com sucesso.");
                            session.logout();
                            gui.onLogoutSuccess();
                        }

                        case "updateProfile" -> {
                            System.out.println("✅ Foto de perfil atualizada com sucesso.");
                            String updatedPhoto = XmlMessageReader.getTextValue(payload, "photo");
                            if (updatedPhoto != null && !updatedPhoto.isEmpty()) {
                                session.updatePhoto(updatedPhoto);
                            }
                        }

                        case "cancelMatch" -> {
                            System.out.println("❌ Matchmaking cancelado com sucesso.");
                        }

                        case "findMatch" -> {
                            System.out.println("🔎 Pedido de matchmaking enviado. Aguardando emparelhamento...");
                        }
                        case "getProfile" -> {
                            populateUserProfile(payload);
                            gui.onProfileView(session.getProfile());
                        }
                    }
                } else {
                    // Handle failures
                    switch (operation) {
                        case "login" -> gui.onLoginError(msg);
                        case "register" -> gui.onRegisterError(msg);
                        case "logout" -> gui.onLogoutError(msg);
                        default -> System.out.println("❌ " + msg);
                    }
                }
            }

            case "gameStart" -> {
                String player = XmlMessageReader.getTextValue(payload, "player");
                String opponent = XmlMessageReader.getTextValue(payload, "opponent");
                boolean isPlayerFirst = XmlMessageReader.getBooleanValue(payload, "firstToPlay");

                System.out.println("🎮 Match iniciado com: " + opponent + " | Primeiro a jogar: " + (isPlayerFirst ? player : opponent));
                gui.onGameStart(player, opponent, isPlayerFirst);
            }

            case "move" -> {
                String who = XmlMessageReader.getTextValue(payload, "player");
                int row = XmlMessageReader.getIntValue(payload, "row");
                int col = XmlMessageReader.getIntValue(payload, "col");

                System.out.println("📥 Jogada recebida: " + who + " @ [" + row + "," + col + "]");
                gui.onMove(row, col, who);
            }

            case "gameEnd" -> {
                String winner = XmlMessageReader.getTextValue(payload, "winner");
                String reason = XmlMessageReader.getTextValue(payload, "reason");
                String msg = XmlMessageReader.getTextValue(payload, "message");
                System.out.println("🏁 Fim de jogo: " + reason + ". " + msg + (winner != null && !winner.isEmpty() ? " Vencedor: " + winner : ""));
                gui.onGameEnd(winner, reason, msg);
            }
        }
    }

    private List<GameHistory> parseGamesHistory(Element payload) {
        List<GameHistory> gamesHistory = new ArrayList<>();
        Element gamesHistoryElem = null;
        for (int i = 0; i < payload.getChildNodes().getLength(); i++) {
            if (payload.getChildNodes().item(i) instanceof Element el && "gamesHistory".equals(el.getNodeName())) {
                gamesHistoryElem = el;
                break;
            }
        }
        if (gamesHistoryElem != null) {
            for (int i = 0; i < gamesHistoryElem.getChildNodes().getLength(); i++) {
                if (gamesHistoryElem.getChildNodes().item(i) instanceof Element gameElem && "game".equals(gameElem.getNodeName())) {
                    String dateTime = XmlMessageReader.getTextValue(gameElem, "dateTime");
                    String duration = XmlMessageReader.getTextValue(gameElem, "duration");
                    String opponent = XmlMessageReader.getTextValue(gameElem, "opponent");
                    String result = XmlMessageReader.getTextValue(gameElem, "result");
                    try {
                        gamesHistory.add(new GameHistory(
                            java.time.LocalDateTime.parse(dateTime),
                            Long.parseLong(duration),
                            opponent,
                            result
                        ));
                    } catch (Exception ignored) {}
                }
            }
        }
        return gamesHistory;
    }

    private void populateUserProfile(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        String photoBase64 = XmlMessageReader.getTextValue(payload, "photo");
        int age = XmlMessageReader.getIntValue(payload, "age");
        String nationality = XmlMessageReader.getTextValue(payload, "nationality");
        int wins = XmlMessageReader.getIntValue(payload, "wins");
        int losses = XmlMessageReader.getIntValue(payload, "losses");
        long timePlayed = Long.parseLong(XmlMessageReader.getTextValue(payload, "timePlayed"));

        List<GameHistory> gamesHistory = parseGamesHistory(payload);
        session.login(new UserProfileData(username, age, nationality, wins, losses, timePlayed, photoBase64, gamesHistory));
    }

    private String determineOperationType(String message) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("login")) {
            return "login";
        } else if (lowerMessage.contains("registo")) {
            return "register";
        } else if (lowerMessage.contains("logout")) {
            return "logout";
        }
        return "unknown";
    }
}