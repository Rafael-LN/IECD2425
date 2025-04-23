package client.handler;

import client.ClientSession;
import common.GameClientListener;
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
        String type = doc.getDocumentElement().getLocalName();

        switch (type) {
            case "response" -> {
                String status = XmlMessageReader.getTextValue(doc, "status");
                String msg = XmlMessageReader.getTextValue(doc, "message");
                String operation = XmlMessageReader.getTextValue(doc, "operation");

                if (operation == null) {
                    // Fallback to string matching if operation not explicitly provided
                    operation = determineOperationType(msg);
                }

                if ("success".equals(status)) {
                    System.out.println("✅ " + msg);

                    switch (operation) {
                        case "login" -> {
                            session.login(session.getUsername());
                            gui.onLoginSuccess(session.getUsername());
                        }
                        case "register" -> {
                            session.login(session.getUsername());
                            gui.onRegisterSuccess(session.getUsername());
                        }
                        case "findMatch" -> {
                            // Handle matchmaking success if needed
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
                System.out.println("🎮 Jogo a começar...");

                String you = XmlMessageReader.getTextValue(doc, "player");
                String opponent = XmlMessageReader.getTextValue(doc, "opponent");
                boolean youStart = Boolean.parseBoolean(XmlMessageReader.getTextValue(doc, "firstToPlay"));

                gui.onGameStart(you, opponent, youStart);
            }
        }
    }

    private String determineOperationType(String message) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("login")) {
            return "login";
        } else if (lowerMessage.contains("registo")) {
            return "register";
        } else if (lowerMessage.contains("partida") || lowerMessage.contains("jogo")) {
            return "findMatch";
        }
        return "unknown";
    }
}
