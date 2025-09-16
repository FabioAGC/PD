package br.com.pd.acesso;

import br.com.pd.acesso.config.AppConfig;
import br.com.pd.acesso.hardware.GpioService;
import br.com.pd.acesso.http.HttpReporter;
import br.com.pd.acesso.modbus.ModbusRtu;
import br.com.pd.acesso.serial.SerialMenu;
import br.com.pd.acesso.storage.FileStorage;
import br.com.pd.acesso.storage.EventRepository;
import br.com.pd.acesso.storage.UserRepository;
import br.com.pd.acesso.web.WebServer;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.fromEnv();

        FileStorage storage = new FileStorage(config.getDataDir());
        UserRepository userRepository = new UserRepository(storage);
        EventRepository eventRepository = new EventRepository(storage);

        ModbusRtu modbus = new ModbusRtu((byte) 0x01);
        GpioService gpio = new GpioService(config.isSimulatedGpio());
        HttpReporter reporter = new HttpReporter(config.getServerUrl(), modbus);

        int port = config.getWebPort();
        new WebServer(port, userRepository, eventRepository, gpio, reporter);
        System.out.println("Servidor web iniciado em http://localhost:" + port);

        SerialMenu menu = new SerialMenu(config, userRepository, gpio, reporter);
        menu.start();
    }
}