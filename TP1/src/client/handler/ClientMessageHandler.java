package client.handler;

import client.ClientSession;
import common.GameClientListener;
import common.UserProfileData;
import common.XmlMessageReader;
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
                    }
                } else {
                    // Handle failures
                    switch (operation) {
                        case "login" -> gui.onLoginError(msg);
                        case "register" -> gui.onRegisterError(msg);
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
        }
    }

    private void populateUserProfile(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        String photoBase64 = XmlMessageReader.getTextValue(payload, "photo");
        int age = XmlMessageReader.getIntValue(payload, "age");
        String nationality = XmlMessageReader.getTextValue(payload, "nationality");
        int wins = XmlMessageReader.getIntValue(payload, "wins");
        int losses = XmlMessageReader.getIntValue(payload, "losses");
        long timePlayed = Long.parseLong(XmlMessageReader.getTextValue(payload, "timePlayed"));

        session.login(new UserProfileData(username, age, nationality, wins, losses, timePlayed, photoBase64));
    }

    private String determineOperationType(String message) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("login")) {
            return "login";
        } else if (lowerMessage.contains("registo")) {
            return "register";
        }
        return "unknown";
    }
}