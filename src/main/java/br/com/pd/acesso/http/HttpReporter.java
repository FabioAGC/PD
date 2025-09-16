package br.com.pd.acesso.http;

import br.com.pd.acesso.modbus.ModbusRtu;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpReporter {
    private final String baseUrl;
    private final OkHttpClient client = new OkHttpClient();
    private final ModbusRtu modbus;

    public HttpReporter(String baseUrl, ModbusRtu modbus) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.modbus = modbus;
    }

    public void sendUser(Map<String, Object> user) {
        postJson("/users", user);
    }

    public void sendEvent(Map<String, Object> event) {
        postJson("/events", event);
    }

    public byte[] buildModbusFrame(int register, int value) {
        return modbus.buildWriteSingleRegister(register, value);
    }

    private void postJson(String path, Map<String, Object> payload) {
        try {
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            Request req = new Request.Builder()
                    .url(baseUrl + path)
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();
            try (Response resp = client.newCall(req).execute()) {
                // best-effort; ignoring response body
            }
        } catch (IOException e) {
            System.err.println("Falha ao enviar para servidor: " + e.getMessage());
        }
    }

    public Map<String, Object> eventPayload(String userName, int door) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userName);
        map.put("door", door);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }
}



