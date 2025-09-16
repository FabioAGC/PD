package br.com.pd.acesso.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    public static class User {
        public String name;
        public String passwordHash;
        public boolean admin;

        public User() {}
        public User(String name, String password, boolean admin) {
            this.name = name;
            this.passwordHash = hash(password);
            this.admin = admin;
        }

        @JsonIgnore
        public boolean isPasswordValid(String password) {
            return passwordHash.equals(hash(password));
        }
    }

    private final FileStorage storage;
    private List<User> users;

    public UserRepository(FileStorage storage) {
        this.storage = storage;
        this.users = storage.read("users.json", new TypeReference<List<User>>() {}, new ArrayList<>());
        if (users.stream().noneMatch(u -> u.admin)) {
            users.add(new User("admin", "admin", true));
            persist();
        }
    }

    public synchronized void addUser(String name, String password, boolean admin) {
        users.add(new User(name, password, admin));
        persist();
    }

    public synchronized List<User> list() { return new ArrayList<>(users); }

    public Optional<User> findByName(String name) {
        return users.stream().filter(u -> u.name.equalsIgnoreCase(name)).findFirst();
    }

    private void persist() {
        storage.write("users.json", users);
    }

    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}



