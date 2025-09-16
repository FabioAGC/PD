package br.com.pd.acesso.web;

import br.com.pd.acesso.hardware.GpioService;
import br.com.pd.acesso.http.HttpReporter;
import br.com.pd.acesso.storage.EventRepository;
import br.com.pd.acesso.storage.UserRepository;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

public class WebServer {
    private final Javalin app;

    public WebServer(int port, UserRepository users, EventRepository events, GpioService gpio, HttpReporter http) {
        this.app = Javalin.create(cfg -> {
            cfg.staticFiles.add(s -> {
                s.hostedPath = "/";
                s.directory = "/public";
                s.location = Location.CLASSPATH;
                s.precompress = false;
            });
        });

        app.get("/api/health", ctx -> ctx.result("ok"));

        app.get("/api/users", ctx -> {
            List<Map<String, Object>> out = users.list().stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", u.name);
                    m.put("admin", u.admin);
                    return m;
                })
                .collect(Collectors.toList());
            ctx.json(out);
        });

        app.post("/api/users", ctx -> {
            var body = ctx.bodyAsClass(UserCreate.class);
            users.addUser(body.name, body.password, body.admin);
            Map<String, Object> m = new HashMap<>();
            m.put("ok", true);
            ctx.status(201).json(m);
        });

        app.get("/api/events", ctx -> {
            String adminName = ctx.queryParam("adminName");
            String adminPassword = ctx.queryParam("adminPassword");
            if (!isAdmin(users, adminName, adminPassword)) {
                Map<String, Object> m = new HashMap<>();
                m.put("error", "forbidden");
                ctx.status(403).json(m);
                return;
            }
            ctx.json(events.list());
        });

        app.post("/api/doors/{door}/open", ctx -> {
            int door = Integer.parseInt(ctx.pathParam("door"));
            var auth = ctx.bodyAsClass(Auth.class);
            if (!isUser(users, auth.name, auth.password)) {
                Map<String, Object> m = new HashMap<>();
                m.put("error", "unauthorized");
                ctx.status(401).json(m);
                return;
            }
            if (door == 1) {
                gpio.setDoor1(true);
            } else if (door == 2) {
                gpio.setDoor2(true);
            } else {
                Map<String, Object> m = new HashMap<>();
                m.put("error", "invalid door");
                ctx.status(400).json(m);
                return;
            }
            int register = (door == 1) ? 0x34 : 0x35;
            int value = 0x00FF;
            byte[] frame = http.buildModbusFrame(register, value);
            events.add(auth.name, door);
            Map<String, Object> m = new HashMap<>();
            m.put("ok", true);
            m.put("frameHex", toHex(frame));
            ctx.json(m);
        });

        app.start(port);
    }

    private static boolean isAdmin(UserRepository repo, String name, String password) {
        if (name == null || password == null) return false;
        return repo.findByName(name).filter(u -> u.admin && u.isPasswordValid(password)).isPresent();
    }

    private static boolean isUser(UserRepository repo, String name, String password) {
        if (name == null || password == null) return false;
        return repo.findByName(name).filter(u -> u.isPasswordValid(password)).isPresent();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    public record UserCreate(String name, String password, boolean admin) {}
    public record Auth(String name, String password) {}
}
