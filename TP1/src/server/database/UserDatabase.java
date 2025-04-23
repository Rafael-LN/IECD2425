package server.database;

import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase {

    private final ConcurrentHashMap<String, PlayerRecord> users = new ConcurrentHashMap<>();

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new PlayerRecord(username, password));
        return true;
    }

    public boolean login(String username, String password) {
        PlayerRecord p = users.get(username);
        return p != null && p.password().equals(password);
    }

    public record PlayerRecord(String username, String password) {}
}
