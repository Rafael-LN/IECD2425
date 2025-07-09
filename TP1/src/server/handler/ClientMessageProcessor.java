package server.handler;

import common.XmlMessageReader;
import common.XmlMessageBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import server.ClientConnection;
import server.database.UserDatabase;
import server.database.PlayerRecord;
import common.GameHistory;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientMessageProcessor {

    private final ClientConnection client;
    private final BufferedReader in;
    private final UserDatabase userDb;

    public ClientMessageProcessor(ClientConnection client, BufferedReader in, UserDatabase userDb) {
        this.client = client;
        this.in = in;
        this.userDb = userDb;
    }

    public void start() {
        try {
            while (true) {
                String xml = in.readLine(); // Lê uma linha de XML
                if (xml == null) break; // Fim de ligação
                String xsdPath = "TP1/src/common/xsd/gameProtocol.xsd"; // Ajusta o caminho se necessário
                if (!XmlMessageReader.validateXml(xml, xsdPath)) {
                    client.send(XmlMessageBuilder.buildResponse(
                        "error",
                        "Mensagem XML inválida (não cumpre o XSD).",
                        "xmlValidation"
                    ));
                    continue;
                }
                Document doc = XmlMessageReader.parseXml(xml);
                process(doc);
            }
        } catch (Exception e) {
            System.err.println("Ligação terminada: " + e.getMessage());
        } finally {
            client.closeConnection();
        }
    }

    public void process(Document doc) {
        try {
            Element payload = XmlMessageReader.getPayloadElement(doc);
            String type = XmlMessageReader.getPayloadType(doc);

            System.out.println("🔽 Recebido pedido para " + type);

            switch (type) {
                case "loginRequest" -> handleLogin(payload);
                case "registerRequest" -> handleRegister(payload);
                case "updateProfileRequest" -> handleUpdateProfile(payload);
                case "findMatch" -> handleFindMatch(payload);
                case "cancelMatch" -> handleCancelMatch(payload);
                case "move" -> handleMove(payload);
                case "logoutRequest" -> handleLogout(payload);
                case "getProfileRequest" -> handleGetProfile(payload);
                case "quitMatch" -> handleQuitMatch(payload);
                case "getRankingRequest" -> handleGetRanking();
                case null -> System.err.println("Erro no tipo de mensagem recebida");
                default -> System.err.println("Tipo de mensagem desconhecido: " + type);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }

    private void sendUserProfileResponse(String message, String operation, PlayerRecord player) {
        String response = common.XmlMessageBuilder.buildResponse(
            "success",
            message,
            operation,
            player.username(),
            player.photoBase64(),
            player.age(),
            player.nationality(),
            player.wins(),
            player.losses(),
            player.timePlayed(),
            player.gamesHistory()
        );
        client.send(response);
    }

    private void handleLogin(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        String password = XmlMessageReader.getTextValue(payload, "password");

        boolean success = userDb.login(username, password);
        if (success) {
            client.setUsername(username);
            PlayerRecord player = userDb.getPlayer(username);
            sendUserProfileResponse("Login successful.", "login", player);
        } else {
            client.send(XmlMessageBuilder.buildResponse(
                "error",
                "Invalid credentials or user already logged in on another client.",
                "login"
            ));
        }
    }

    private void handleLogout(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        boolean success = userDb.logout(username);
        String response = XmlMessageBuilder.buildResponse(
                success ? "success" : "error",
                success ? "Logout successful." : "Logout failed.",
                "logout"
        );
        client.send(response);
    }

    private void handleRegister(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        String password = XmlMessageReader.getTextValue(payload, "password");
        String ageStr = XmlMessageReader.getTextValue(payload, "age");
        String nationality = XmlMessageReader.getTextValue(payload, "nationality");
        String photo = XmlMessageReader.getTextValue(payload, "photo");

        if (username == null || password == null || ageStr == null || nationality == null || photo == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Missing registration data.", "register"));
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            client.send(XmlMessageBuilder.buildResponse("error", "Invalid age.", "register"));
            return;
        }

        boolean success = userDb.register(username, password, age, nationality, photo);
        if (success) {
            client.setUsername(username);
            PlayerRecord player = userDb.getPlayer(username);
            sendUserProfileResponse("Registration successful.","register", player);
        } else {
            client.send(XmlMessageBuilder.buildResponse(
                "error",
                "User already exists.",
                "register"
            ));
        }
    }

    private void handleUpdateProfile(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        String photo = XmlMessageReader.getTextValue(payload, "photo");

        boolean success = userDb.updatePhoto(username, photo);
        String response = XmlMessageBuilder.buildResponse(
                success ? "success" : "error",
                success ? "Profile photo updated successfully." : "Failed to update profile photo.",
                "updateProfile"
        );
        client.send(response);
    }

    private void handleFindMatch(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        client.setUsername(username);

        System.out.println("🔍 Matchmaking request from " + username);

        String response = XmlMessageBuilder.buildResponse(
            "success",
            "Matchmaking request received. Waiting for an opponent...",
            "findMatch"
        );
        client.send(response);

        MatchmakingQueue.addToQueue(client);
    }

    private void handleCancelMatch(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        client.setUsername(username);

        String response = XmlMessageBuilder.buildResponse(
            "success",
            "❌ Matchmaking cancelled successfully.",
            "cancelMatch"
        );
        client.send(response);

        MatchmakingQueue.removeFromQueue(client);
    }

    private void handleMove(Element payload) {
        String rowStr = XmlMessageReader.getTextValue(payload, "row");
        String colStr = XmlMessageReader.getTextValue(payload, "col");
        if (rowStr == null || colStr == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Missing coordinates.", "move"));
            return;
        }
        int row, col;
        try {
            row = Integer.parseInt(rowStr);
            col = Integer.parseInt(colStr);
        } catch (NumberFormatException e) {
            client.send(XmlMessageBuilder.buildResponse("error", "Invalid coordinates.", "move"));
            return;
        }
        ActiveGamesManager.processMove(client, row, col);
    }

    // Regista o listener para o fim do jogo
    public void registerGameEndListener() {
        ActiveGamesManager.setGameEndListener(new GameEndListener() {
            @Override
            public void onGameEnd(ClientConnection winner, ClientConnection loser, String result, long duration) {
                updateGameHistory(winner.getUsername(), loser.getUsername(), "win", duration);
                updateGameHistory(loser.getUsername(), winner.getUsername(), "defeat", duration);
            }
            @Override
            public void onGameDraw(ClientConnection p1, ClientConnection p2, long duration) {
                updateGameHistory(p1.getUsername(), p2.getUsername(), "draw", duration);
                updateGameHistory(p2.getUsername(), p1.getUsername(), "draw", duration);
            }
        });
    }

    // Atualiza o histórico de jogos de um jogador
    private void updateGameHistory(String username, String opponent, String result, long duration) {
        PlayerRecord player = userDb.getPlayer(username);
        if (player == null) return;

        GameHistory novoJogo = new GameHistory(
            LocalDateTime.now(),
            duration,
            opponent,
            result
        );


        ArrayList<GameHistory> novoHistorico = new ArrayList<>(player.gamesHistory());
        novoHistorico.add(novoJogo);


        PlayerRecord atualizado = getPlayerRecord(result, novoHistorico, player);

        userDb.updatePlayer(atualizado);
    }

    private static PlayerRecord getPlayerRecord(String result, ArrayList<GameHistory> novoHistorico, PlayerRecord player) {
        long tempoTotal = 0;
        for (GameHistory jogo : novoHistorico) {
            tempoTotal += jogo.duration();
        }

        int novasVitorias = result.equals("win") ? player.wins() + 1 : player.wins();
        int novasDerrotas = result.equals("defeat") ? player.losses() + 1 : player.losses();

        return new PlayerRecord(
            player.username(),
            player.password(),
            player.age(),
            player.nationality(),
            player.photoBase64(),
            novasVitorias,
            novasDerrotas,
            tempoTotal,
                novoHistorico
        );
    }

    private void handleGetProfile(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        if (username == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Username missing in profile request.", "getProfile"));
            return;
        }
        PlayerRecord player = userDb.getPlayer(username);
        if (player == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "User not found.", "getProfile"));
            return;
        }
        sendUserProfileResponse("Profile retrieved successfully.", "getProfile", player);
    }

    private void handleQuitMatch(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        client.setUsername(username);

        String response = XmlMessageBuilder.buildResponse("success", "Quit request received.", "quitMatch");
        client.send(response);

        MatchmakingQueue.removeFromQueue(client);

        ActiveGamesManager.endGameForClient(client);
    }

    private void handleGetRanking() {
        List<PlayerRecord> players = new ArrayList<>(userDb.getAllPlayers());

        String response = XmlMessageBuilder.buildRankingResponseArray(players);
        client.send(response);
    }
}
