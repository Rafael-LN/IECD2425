package client.handler;

import client.ClientSession;
import common.GameClientListener;
import common.UserProfileData;
import common.XmlMessageReader;
import org.w3c.dom.Document;

public class ClientMessageHandler {

    private final ClientSession session;
    private final GameClientListener gui;

    public ClientMessageHandler(ClientSession session, GameClientListener gui) {
        this.session = session;
        this.gui = gui;
    }

    public void handle(Document doc) {
        String type = XmlMessageReader.getRootElement(doc);

        switch (type) {
            case "response" -> {
                String status = XmlMessageReader.getTextValue(doc, "status");
                String msg = XmlMessageReader.getTextValue(doc, "message");
                String operation = XmlMessageReader.getTextValue(doc, "operation");

                if (operation == null) {
                    operation = determineOperationType(msg);
                }

                if ("success".equals(status)) {
                    System.out.println("✅ SUCESSO: " + msg);

                    switch (operation) {
                        case "login" -> {
                            populateUserProfile(doc);
                            session.login(session.getProfile());
                            gui.onLoginSuccess(session.getProfile().username());
                        }

                        case "register" -> {
                            populateUserProfile(doc);
                            session.login(session.getProfile());
                            gui.onRegisterSuccess(session.getProfile().username());
                        }

                        case "updateProfile" -> {
                            System.out.println("✅ Foto de perfil atualizada com sucesso.");
                            String updatedPhoto = XmlMessageReader.getTextValue(doc, "photo");
                            if (updatedPhoto != null && !updatedPhoto.isEmpty()) {
                                session.updatePhoto(updatedPhoto);
                            }
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
                String player = XmlMessageReader.getTextValue(doc, "player");
                String opponent = XmlMessageReader.getTextValue(doc, "opponent");
                boolean isPlayerFirst = Boolean.parseBoolean(XmlMessageReader.getTextValue(doc, "firstToPlay"));

                System.out.println("🎮 Match iniciado com: " + opponent + " | Primeiro a jogar: " + (isPlayerFirst ? player : opponent));
                gui.onGameStart(player, opponent, isPlayerFirst);
            }
        }
    }

    private void populateUserProfile(Document doc) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        String photoBase64 = XmlMessageReader.getTextValue(doc, "photo");
        int age = Integer.parseInt(XmlMessageReader.getTextValue(doc, "age"));
        String nationality = XmlMessageReader.getTextValue(doc, "nationality");
        int wins = Integer.parseInt(XmlMessageReader.getTextValue(doc, "wins"));
        int losses = Integer.parseInt(XmlMessageReader.getTextValue(doc, "losses"));
        long timePlayed = Long.parseLong(XmlMessageReader.getTextValue(doc, "timePlayed"));

        session.login(new UserProfileData(username, age, nationality, wins, losses, timePlayed, photoBase64));
    }

    private String determineOperationType(String message) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("login")) {
            return "login";
        } else if (lowerMessage.contains("registo")) {
            return "register";
        } else if (lowerMessage.contains("partida") || lowerMessage.contains("jogo")) {
            return "findMatch";
        } else if (lowerMessage.contains("perfil") || lowerMessage.contains("foto")) {
            return "updateProfile";
        }
        return "unknown";
    }
}