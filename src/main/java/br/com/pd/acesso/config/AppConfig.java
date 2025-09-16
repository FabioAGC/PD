package br.com.pd.acesso.config;

import java.nio.file.Path;

public class AppConfig {
    private final boolean simulatedGpio;
    private final String serverUrl;
    private final String serialPort;
    private final int baudRate;
    private final Path dataDir;
    private final int webPort;

    public AppConfig(boolean simulatedGpio, String serverUrl, String serialPort, int baudRate, Path dataDir, int webPort) {
        this.simulatedGpio = simulatedGpio;
        this.serverUrl = serverUrl;
        this.serialPort = serialPort;
        this.baudRate = baudRate;
        this.dataDir = dataDir;
        this.webPort = webPort;
    }

    public static AppConfig fromEnv() {
        boolean sim = Boolean.parseBoolean(System.getenv().getOrDefault("PD_SIM_GPIO", "true"));
        String url = System.getenv().getOrDefault("PD_SERVER_URL", "http://localhost:8080/api");
        String port = System.getenv().getOrDefault("PD_SERIAL_PORT", "auto");
        int baud = Integer.parseInt(System.getenv().getOrDefault("PD_BAUD", "115200"));
        Path dir = Path.of(System.getenv().getOrDefault("PD_DATA_DIR", "data"));
        int web = Integer.parseInt(System.getenv().getOrDefault("PD_WEB_PORT", "8081"));
        return new AppConfig(sim, url, port, baud, dir, web);
    }

    public boolean isSimulatedGpio() { return simulatedGpio; }
    public String getServerUrl() { return serverUrl; }
    public String getSerialPort() { return serialPort; }
    public int getBaudRate() { return baudRate; }
    public Path getDataDir() { return dataDir; }
    public int getWebPort() { return webPort; }
}



