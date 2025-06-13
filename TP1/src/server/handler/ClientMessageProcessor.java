package server.handler;

import common.XmlMessageBuilder;
import common.XmlMessageReader;
import org.w3c.dom.Document;
import server.ClientConnection;
import server.database.PlayerRecord;
import server.database.UserDatabase;

public class ClientMessageProcessor {

    private static final UserDatabase userDb = new UserDatabase();

    public static void process(Document doc, ClientConnection client) {
        try {
            String type = doc.getDocumentElement().getLocalName();
            System.out.println("📩 A processar pedido: " + type + " de " + (client.getUsername() != null ? client.getUsername() : "cliente desconhecido"));

            switch (type) {
                case "loginRequest" -> handleLoginRequest(doc, client);
                case "registerRequest" -> handleRegisterRequest(doc, client);
                case "updateProfileRequest" -> handleUpdateProfileRequest(doc, client);
                case "findMatch" -> handleFindMatch(doc, client);
                case "cancelMatch" -> handleCancelMatch(doc, client);

                default -> System.out.println("⚠️ Pedido desconhecido: " + type);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleLoginRequest(Document doc, ClientConnection client) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        String password = XmlMessageReader.getTextValue(doc, "password");
        boolean success = userDb.login(username, password);
        if (success) client.setUsername(username);
        if (success) {
            var player = userDb.getPlayer(username);
            String response = buildLoginResponse("success", "Login efetuado com sucesso.", username, player);
            client.send(response);
        } else {
            String response = XmlMessageBuilder.buildResponse("error", "Credenciais inválidas.", "login");
            client.send(response);
        }
    }

    private static void handleRegisterRequest(Document doc, ClientConnection client) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        String password = XmlMessageReader.getTextValue(doc, "password");
        int age = Integer.parseInt(XmlMessageReader.getTextValue(doc, "age"));
        String nationality = XmlMessageReader.getTextValue(doc, "nationality");
        String photo = XmlMessageReader.getTextValue(doc, "photo");

        boolean success = userDb.register(username, password, age, nationality, photo);
        if (success) {
            var player = userDb.getPlayer(username);
            String response = buildLoginResponse("success", "Registo efetuado com sucesso.", username, player);
            client.send(response);
        } else {
            String response = XmlMessageBuilder.buildResponse("error", "Utilizador já existe.", "register");
            client.send(response);
        }
        System.out.println(success ? "✅ Registo bem-sucedido: " + username : "❌ Falha no registo: " + username);
    }

    private static void handleFindMatch(Document doc, ClientConnection client) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        client.setUsername(username);
        System.out.println("🔎 Jogador a procurar partida: " + username);
        MatchmakingQueue.addToQueue(client);

        String response = XmlMessageBuilder.buildResponse("success", "🔎 Matchmaking started...", "findMatch");
        client.send(response);
    }

    private static void handleCancelMatch(Document doc, ClientConnection client) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        client.setUsername(username);

        System.out.println("⛔ Cancelamento de matchmaking: " + username);

        MatchmakingQueue.removeFromQueue(client);

        String response = XmlMessageBuilder.buildResponse("success", "Partida cancelada.", "cancelMatch");
        client.send(response);
    }


    private static void handleUpdateProfileRequest(Document doc, ClientConnection client) {
        String username = XmlMessageReader.getTextValue(doc, "username");
        String photoBase64 = XmlMessageReader.getTextValue(doc, "photo");
        boolean success = userDb.updatePhoto(username, photoBase64);
        String response = XmlMessageBuilder.buildResponse(
                success ? "success" : "error",
                success ? "Perfil atualizado com sucesso." : "Erro ao atualizar perfil.",
                "updateProfile"
        );
        client.send(response);
        System.out.println(success ? "✅ Foto atualizada para: " + username : "❌ Erro ao atualizar foto para: " + username);
    }

    private static String buildLoginResponse(String status, String message, String username, PlayerRecord player) {
        return XmlMessageBuilder.buildLoginResponse(
                status,
                message,
                username,
                player.photoBase64(),
                player.age(),
                player.nationality(),
                player.wins(),
                player.losses(),
                player.timePlayed()
        );
    }
}