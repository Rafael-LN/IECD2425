package client.handler;

import client.ClientSession;
import common.XmlMessageReader;
import org.w3c.dom.Document;

public class ClientMessageHandler {

    private final ClientSession session;

    public ClientMessageHandler(ClientSession session) {
        this.session = session;
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
