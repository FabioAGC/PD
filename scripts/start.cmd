@echo off
if "%PD_SIM_GPIO%"=="" set PD_SIM_GPIO=true
if "%PD_SERVER_URL%"=="" set PD_SERVER_URL=http://localhost:8080/api
if "%PD_SERIAL_PORT%"=="" set PD_SERIAL_PORT=auto
if "%PD_BAUD%"=="" set PD_BAUD=115200
if "%PD_DATA_DIR%"=="" set PD_DATA_DIR=data
if "%PD_WEB_PORT%"=="" set PD_WEB_PORT=8081

echo Starting on http://localhost:%PD_WEB_PORT%

java -jar target\gerenciamento-acesso-1.0.0-shaded.jar


