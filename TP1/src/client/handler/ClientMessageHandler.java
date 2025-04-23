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

                if ("success".equals(status)) {
                    System.out.println("✅ " + msg);

                    // Detetar login com base na mensagem
                    if (msg.toLowerCase().contains("login")) {
                        session.login(session.getUsername()); // valor temporário — idealmente definido antes
                        gui.onLoginSuccess(session.getUsername());
                    }

                    // Detetar registo com base na mensagem
                    if (msg.toLowerCase().contains("registration")) {
                        gui.onRegisterSuccess(session.getUsername());
                    }

                } else {
                    System.out.println("❌ " + msg);
                }
            }

            case "gameStart" -> {
                System.out.println("🎮 Jogo a começar...");
            }
        }
    }
}
