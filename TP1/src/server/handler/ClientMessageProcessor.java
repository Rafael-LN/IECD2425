package server.handler;

import common.XmlMessageReader;
import org.w3c.dom.Document;
import server.ClientConnection;
import server.database.UserDatabase;

public class ClientMessageProcessor {

    private static final UserDatabase userDb = new UserDatabase();

    public static void process(Document doc, ClientConnection client) {
        try {
            String type = doc.getDocumentElement().getLocalName();

            switch (type) {
                case "loginRequest" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    String password = XmlMessageReader.getTextValue(doc, "password");

                    boolean success = userDb.login(username, password);
                    if (success) client.setUsername(username);

                    String response = common.XmlMessageBuilder.buildResponse(
                            success ? "success" : "error",
                            success ? "Login efetuado com sucesso." : "Credenciais inválidas.",
                            "login"
                    );
                    client.send(response);
                }

                case "registerRequest" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    String password = XmlMessageReader.getTextValue(doc, "password");

                    boolean success = userDb.register(username, password);
                    if (success) client.setUsername(username);

                    String response = common.XmlMessageBuilder.buildResponse(
                            success ? "success" : "error",
                            success ? "Registo efetuado com sucesso." : "Utilizador já existe.",
                            "register"
                    );
                    client.send(response);
                }

                case "findMatch" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    client.setUsername(username);
                    MatchmakingQueue.addToQueue(client);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
