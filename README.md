PD Soluções – Gerenciamento de Acesso (Java)


### Interface Web Adicionada
- 🌐 **Servidor web embarcado** (Javalin) na porta configurável
- 🔐 **4 botões principais**:
     - Register User (cadastro)
     - List Users (listagem)
     - Open Door 1/2 (liberação de portas)
     - Events (Admin) (eventos)
- 📱 Totalmente responsiva (mobile, tablet, desktop)
- 🔄 Feedback visual com status coloridos
- ⚡ Validação em tempo real e tratamento de erros

## Como Usar

### Build
```bash
mvn -q -DskipTests package
```

### Execução
```bash
# Linux
sh scripts/start.sh

# Windows PowerShell
powershell -ExecutionPolicy Bypass -File scripts/start.ps1
```

###

Acesso às Interfaces
- **Interface Web**: `http://localhost:8081` (porta configurável)
- **Menu Serial**: Console interativo (mantido como teste)

### Variáveis de Ambiente
- `PD_SIM_GPIO` (true/false) – usa GPIO simulado por padrão
- `PD_SERVER_URL` – base do servidor HTTP, ex: http://localhost:8080/api
- `PD_SERIAL_PORT` – porta serial (não usada no modo console; informar quando integrar ao UART)
- `PD_BAUD` – baudrate
- `PD_DATA_DIR` – diretório para `users.json`
- `PD_WEB_PORT` – porta do servidor web (padrão: 8081)

## Especificações Técnicas

### Mapeamento Modbus RTU
- **Porta 1** → registrador `0x0034`
- **Porta 2** → registrador `0x0035`
- **Valores**: `0x0000` (fechado), `0x00FF` (aberto)
- **Address**: `0x01`
- **Função**: `0x06` (Write Single Register)

### Endpoints da API Web
- `GET /api/health` - Status do sistema
- `GET /api/users` - Lista usuários cadastrados
- `POST /api/users` - Cadastra novo usuário
- `POST /api/doors/{1|2}/open` - Abre porta específica
- `GET /api/events` - Lista eventos (requer autenticação admin)

### Estrutura do Projeto
```
src/main/java/br/com/pd/acesso/
├── Main.java                    # Ponto de entrada
├── config/AppConfig.java        # Configurações
├── hardware/GpioService.java    # Controle GPIO
├── http/HttpReporter.java       # Cliente HTTP
├── modbus/ModbusRtu.java        # Protocolo Modbus
├── serial/SerialMenu.java       # Menu console
├── storage/                     # Persistência
│   ├── FileStorage.java
│   ├── UserRepository.java
│   └── EventRepository.java
└── web/WebServer.java           # Servidor web

src/main/resources/public/
└── index.html                   # Interface web
```

### Integração UART Real
- O menu usa `System.in/out`. Para UART real, basta redirecionar o console para `/dev/ttyS*` ou integre `jSerialComm` abrindo a porta indicada em `PD_SERIAL_PORT`.

### Dependências Principais
- **Javalin 5.6.2** - Servidor web embarcado
- **OkHttp 4.12.0** - Cliente HTTP
- **Jackson 2.17.1** - Serialização JSON
- **jSerialComm 2.10.4** - Comunicação serial


