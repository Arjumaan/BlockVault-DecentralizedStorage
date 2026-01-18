@echo off
echo ========================================
echo BlockVault Backend - Startup Script
echo ========================================
echo.

REM Check if IPFS is running
echo [1/3] Checking IPFS daemon...
curl -s http://localhost:5001/api/v0/version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] IPFS daemon is not running!
    echo Please start IPFS daemon first: ipfs daemon
    pause
    exit /b 1
)
echo [OK] IPFS daemon is running

echo.
echo [2/3] Building the application...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo [OK] Build successful

echo.
echo [3/3] Starting BlockVault Backend...
echo.
call mvn spring-boot:run

pause
