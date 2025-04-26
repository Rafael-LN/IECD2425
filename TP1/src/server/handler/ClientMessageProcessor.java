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
            System.out.println("📩 A processar pedido: " + type + " de " + (client.getUsername() != null ? client.getUsername() : "cliente desconhecido"));

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

                    System.out.println(success ?
                            "✅ Login bem-sucedido: " + username :
                            "❌ Falha no login: " + username);
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

                    System.out.println(success ?
                            "✅ Registo bem-sucedido: " + username :
                            "❌ Falha no registo: " + username);
                }

                case "findMatch" -> {
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    client.setUsername(username);

                    System.out.println("🔎 Jogador a procurar partida: " + username);
                    MatchmakingQueue.addToQueue(client);
                }

                default -> {
                    System.out.println("⚠️ Pedido desconhecido: " + type);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
