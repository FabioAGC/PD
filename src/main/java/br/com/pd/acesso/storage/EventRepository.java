package br.com.pd.acesso.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

public class EventRepository {
    private final FileStorage storage;
    private List<Map<String, Object>> events;

    public EventRepository(FileStorage storage) {
        this.storage = storage;
        this.events = storage.read("events.json", 
            new TypeReference<List<Map<String, Object>>>() {}, new ArrayList<>());
    }

    public synchronized void add(String user, int door) {
        Map<String, Object> e = new HashMap<>();
        e.put("user", user);
        e.put("door", door);
        e.put("timestamp", System.currentTimeMillis());
        events.add(e);
        storage.write("events.json", events);
    }

    public synchronized List<Map<String, Object>> list() {
        return new ArrayList<>(events);
    }
}
