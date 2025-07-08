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
                case "move" -> handleMove(payload);
                case "logoutRequest" -> handleLogout(payload);
                case "getProfileRequest" -> handleGetProfile(payload);
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
            sendUserProfileResponse("Login efetuado com sucesso.", "login", player);
        } else {
            client.send(XmlMessageBuilder.buildResponse(
                "error",
                "Credenciais inválidas ou utilizador já tem sessão iniciada noutro cliente.",
                "login"
            ));
        }
    }

    private void handleLogout(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        boolean success = userDb.logout(username);
        String response = XmlMessageBuilder.buildResponse(
                success ? "success" : "error",
                success ? "Logout efetuado com sucesso." : "Erro ao efetuar logout.",
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
            client.send(XmlMessageBuilder.buildResponse("error", "Dados de registo em falta.", "register"));
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            client.send(XmlMessageBuilder.buildResponse("error", "Idade inválida.", "register"));
            return;
        }

        boolean success = userDb.register(username, password, age, nationality, photo);
        if (success) {
            client.setUsername(username);
            PlayerRecord player = userDb.getPlayer(username);
            sendUserProfileResponse("Registo efetuado com sucesso.","register", player);
        } else {
            client.send(XmlMessageBuilder.buildResponse(
                "error",
                "Utilizador já existe.",
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
                success ? "Foto atualizada com sucesso." : "Erro ao atualizar foto.",
                "updateProfile"
        );
        client.send(response);
    }

    private void handleFindMatch(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        client.setUsername(username);

        String response = XmlMessageBuilder.buildFindMatchRequest(username);
        client.send(response);

        MatchmakingQueue.addToQueue(client);
    }

    private void handleMove(Element payload) {
        String rowStr = XmlMessageReader.getTextValue(payload, "row");
        String colStr = XmlMessageReader.getTextValue(payload, "col");
        if (rowStr == null || colStr == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Coordenadas em falta.", "move"));
            return;
        }
        int row, col;
        try {
            row = Integer.parseInt(rowStr);
            col = Integer.parseInt(colStr);
        } catch (NumberFormatException e) {
            client.send(XmlMessageBuilder.buildResponse("error", "Coordenadas inválidas.", "move"));
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


        long tempoTotal = 0;
        for (GameHistory jogo : novoHistorico) {
            tempoTotal += jogo.duration();
        }


        int novasVitorias = result.equals("win") ? player.wins() + 1 : player.wins();
        int novasDerrotas = result.equals("defeat") ? player.losses() + 1 : player.losses();


        PlayerRecord atualizado = new PlayerRecord(
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

        userDb.updatePlayer(atualizado);
    }

    // Handler para pedido de perfil do utilizador
    private void handleGetProfile(Element payload) {
        String username = XmlMessageReader.getTextValue(payload, "username");
        if (username == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Username em falta no pedido de perfil.", "getProfile"));
            return;
        }
        PlayerRecord player = userDb.getPlayer(username);
        if (player == null) {
            client.send(XmlMessageBuilder.buildResponse("error", "Utilizador não encontrado.", "getProfile"));
            return;
        }
        sendUserProfileResponse("Perfil obtido com sucesso.", "getProfile", player);
    }
}
