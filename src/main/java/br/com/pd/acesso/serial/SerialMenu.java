package br.com.pd.acesso.serial;

import br.com.pd.acesso.config.AppConfig;
import br.com.pd.acesso.hardware.GpioService;
import br.com.pd.acesso.http.HttpReporter;
import br.com.pd.acesso.storage.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class SerialMenu {
    private final AppConfig config;
    private final UserRepository users;
    private final GpioService gpio;
    private final HttpReporter http;

    public SerialMenu(AppConfig config, UserRepository users, GpioService gpio, HttpReporter http) {
        this.config = config;
        this.users = users;
        this.gpio = gpio;
        this.http = http;
    }

    public void start() {
        println("PD Soluções - Gerenciamento de Acesso");
        println("UART:" + config.getSerialPort() + " @" + config.getBaudRate());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                showMenu();
                String opt = reader.readLine();
                if (opt == null) return;
                switch (opt.trim()) {
                    case "1" -> cadastro(reader);
                    case "2" -> listarUsuarios();
                    case "3" -> listarEventos(reader);
                    case "4" -> liberarPorta(reader, 1);
                    case "5" -> liberarPorta(reader, 2);
                    default -> println("Opção inválida");
                }
            } catch (IOException e) {
                println("Erro: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        println("");
        println("1) Cadastrar usuário");
        println("2) Listar usuários");
        println("3) Listar eventos (admin)");
        println("4) Liberar porta 1");
        println("5) Liberar porta 2");
        print("Escolha: ");
    }

    private void cadastro(BufferedReader reader) throws IOException {
        print("Nome: ");
        String nome = reader.readLine();
        print("Senha: ");
        String senha = reader.readLine();
        print("Administrador? (s/N): ");
        boolean admin = "s".equalsIgnoreCase(reader.readLine());
        users.addUser(nome, senha, admin);
        http.sendUser(Map.of("name", nome, "admin", admin));
        println("Usuário cadastrado.");
    }

    private void listarUsuarios() {
        List<UserRepository.User> list = users.list();
        println("Usuários cadastrados:");
        for (UserRepository.User u : list) {
            println("- " + u.name + (u.admin ? " (admin)" : ""));
        }
    }

    private void listarEventos(BufferedReader reader) throws IOException {
        if (!authenticateAdmin(reader)) {
            println("Acesso negado.");
            return;
        }
        println("Eventos são enviados ao servidor. Consulte o backend.");
    }

    private boolean authenticateAdmin(BufferedReader reader) throws IOException {
        print("Usuário admin: ");
        String nome = reader.readLine();
        print("Senha: ");
        String senha = reader.readLine();
        return users.findByName(nome).filter(u -> u.admin && u.isPasswordValid(senha)).isPresent();
    }

    private boolean authenticateUser(BufferedReader reader) throws IOException {
        print("Usuário: ");
        String nome = reader.readLine();
        print("Senha: ");
        String senha = reader.readLine();
        boolean ok = users.findByName(nome).filter(u -> u.isPasswordValid(senha)).isPresent();
        if (!ok) println("Credenciais inválidas.");
        return ok;
    }

    private void liberarPorta(BufferedReader reader, int porta) throws IOException {
        if (!authenticateUser(reader)) return;
        if (porta == 1) gpio.setDoor1(true); else gpio.setDoor2(true);
        int register = (porta == 1) ? 0x34 : 0x35;
        int value = 0x00FF; // aberto
        byte[] frame = http.buildModbusFrame(register, value);
        println("Frame MODBUS enviado (hex): " + bytesToHex(frame));
        http.sendEvent(http.eventPayload("usuario", porta));
    }

    private static void println(String s) { System.out.println(s); }
    private static void print(String s) { System.out.print(s); }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }
}



