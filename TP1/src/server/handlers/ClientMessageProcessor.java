package server.handlers;

import common.XmlMessageBuilder;
import common.XmlMessageReader;
import org.w3c.dom.Document;
import server.database.UserDatabase;

import java.io.ObjectOutputStream;

public class ClientMessageProcessor {

    private static final UserDatabase userDb = new UserDatabase();

    public static void process(Document doc, ObjectOutputStream out) {
        try {
            String type = XmlMessageReader.getRootElement(doc);

            switch (type) {
                case "loginRequest" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    String password = XmlMessageReader.getTextValue(doc, "password");

                    boolean success = userDb.login(username, password);
                    String response = XmlMessageBuilder.buildResponse(
                            success ? "success" : "error",
                            success ? "Login efetuado com sucesso." : "Credenciais inválidas."
                    );
                    out.writeObject(response);
                }

                case "registerRequest" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    String password = XmlMessageReader.getTextValue(doc, "password");

                    boolean success = userDb.register(username, password);
                    String response = XmlMessageBuilder.buildResponse(
                            success ? "success" : "error",
                            success ? "Registo efetuado com sucesso." : "Utilizador já existe."
                    );
                    out.writeObject(response);
                }

                case "move" -> {
                    String player = XmlMessageReader.getTextValue(doc, "player");
                    int x = XmlMessageReader.getIntValue(doc, "x");
                    int y = XmlMessageReader.getIntValue(doc, "y");

                    System.out.println("[JOGADA] " + player + ": (" + x + "," + y + ")");
                    // aqui não se envia resposta — depende do protocolo que definires
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
