#!/usr/bin/env sh
export PD_SIM_GPIO=${PD_SIM_GPIO:-true}
export PD_SERVER_URL=${PD_SERVER_URL:-http://localhost:8080/api}
export PD_SERIAL_PORT=${PD_SERIAL_PORT:-auto}
export PD_BAUD=${PD_BAUD:-115200}
export PD_DATA_DIR=${PD_DATA_DIR:-data}
exec java -jar target/gerenciamento-acesso-1.0.0-shaded.jar


