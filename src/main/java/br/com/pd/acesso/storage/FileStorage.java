package br.com.pd.acesso.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileStorage {
    private final Path baseDir;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileStorage(Path baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized <T> void write(String name, T data) {
        try {
            Files.createDirectories(baseDir);
            mapper.writerWithDefaultPrettyPrinter().writeValue(baseDir.resolve(name).toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized <T> T read(String name, TypeReference<T> type, T defaultValue) {
        try {
            Path path = baseDir.resolve(name);
            if (!Files.exists(path)) return defaultValue;
            return mapper.readValue(path.toFile(), type);
        } catch (IOException e) {
            return defaultValue;
        }
    }

    public <T> List<T> readList(String name, Class<T> clazz) {
        return read(name, new TypeReference<List<T>>() {}, Collections.emptyList());
    }
}



