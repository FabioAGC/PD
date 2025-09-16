$ErrorActionPreference = "Stop"
if (-not $env:PD_SIM_GPIO) { $env:PD_SIM_GPIO = "true" }
if (-not $env:PD_SERVER_URL) { $env:PD_SERVER_URL = "http://localhost:8080/api" }
if (-not $env:PD_SERIAL_PORT) { $env:PD_SERIAL_PORT = "auto" }
if (-not $env:PD_BAUD) { $env:PD_BAUD = "115200" }
if (-not $env:PD_DATA_DIR) { $env:PD_DATA_DIR = "data" }
if (-not $env:PD_WEB_PORT) { $env:PD_WEB_PORT = "8081" }

Write-Host "Starting on http://localhost:$env:PD_WEB_PORT"

& java -jar "target\gerenciamento-acesso-1.0.0-shaded.jar"


