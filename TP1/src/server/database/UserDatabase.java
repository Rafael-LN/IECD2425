package server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase implements Serializable {

    private static final String FILE_PATH = "TP1/data/users.ser";

    private ConcurrentHashMap<String, PlayerRecord> users;

    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();

    public UserDatabase() {
        users = loadFromFile();
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
    }

    public synchronized boolean register(String username, String password, int age, String nationality, String photoBase64) {
        if (users.containsKey(username)) return false;
        users.put(username, new PlayerRecord(
            username, password, age, nationality, photoBase64, 0, 0, 0, new ArrayList<>()
        ));
        saveToFile();
        return true;
    }

    // Garante que o ‘login’ é atómico e impede ‘logins’ concorrentes com o mesmo utilizador
    public synchronized boolean login(String username, String password) {
        PlayerRecord player = users.get(username);
        if (player != null && player.password().equals(password)) {
            if (activeSessions.contains(username)) return false; // Já autenticado
            activeSessions.add(username);
            return true;
        }
        return false;
    }


    public synchronized boolean logout(String username) {
        return activeSessions.remove(username);
    }

    private void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(users);
            oos.close();
            System.out.println("💾 Base de dados de utilizadores guardada.");
        } catch (IOException e) {
            System.err.println("❌ Falha ao guardar utilizadores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, PlayerRecord> loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return null;

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            ConcurrentHashMap<String, PlayerRecord> loadedUsers = (ConcurrentHashMap<String, PlayerRecord>) ois.readObject();
            ois.close();
            System.out.println("✅ Base de dados de utilizadores carregada.");
            return loadedUsers;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Não foi possível carregar utilizadores: " + e.getMessage());
            return null;
        }
    }

    public synchronized boolean updatePhoto(String username, String photoBase64) {
        PlayerRecord player = users.get(username);
        if (player == null) return false;

        users.put(username, new PlayerRecord(
                player.username(),
                player.password(),
                player.age(),
                player.nationality(),
                photoBase64,
                player.wins(),
                player.losses(),
                player.timePlayed(),
                player.gamesHistory()
        ));
        saveToFile();
        return true;
    }

    public synchronized PlayerRecord getPlayer(String username) {
        return users.get(username);
    }

    /**
     * Atualiza o registo de um jogador na base de dados e persiste.
     * @param player Novo PlayerRecord a guardar
     */
    public synchronized void updatePlayer(PlayerRecord player) {
        users.put(player.username(), player);
        saveToFile();
    }

    /**
     * Devolve uma lista de todos os jogadores registados.
     */
    public synchronized Collection<PlayerRecord> getAllPlayers() {
        return users.values();
    }
}
