package br.com.pd.acesso.hardware;

public class GpioService {
    private final boolean simulated;
    private boolean door1Open = false;
    private boolean door2Open = false;

    public GpioService(boolean simulated) {
        this.simulated = simulated;
    }

    public synchronized void setDoor1(boolean open) {
        this.door1Open = open;
        if (!simulated) {
            // Integrate with actual GPIO here (e.g., sysfs or libgpiod via JNI)
        }
        System.out.println("[GPIO] Porta 1 = " + (open ? "ABERTA" : "FECHADA"));
    }

    public synchronized void setDoor2(boolean open) {
        this.door2Open = open;
        if (!simulated) {
            // Integrate with actual GPIO here
        }
        System.out.println("[GPIO] Porta 2 = " + (open ? "ABERTA" : "FECHADA"));
    }

    public boolean isDoor1Open() { return door1Open; }
    public boolean isDoor2Open() { return door2Open; }
}



