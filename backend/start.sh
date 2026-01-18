#!/bin/bash
echo "========================================"
echo "BlockVault Backend - Startup Script"
echo "========================================"
echo ""

# Check if IPFS is running
echo "[1/3] Checking IPFS daemon..."
if ! curl -s http://localhost:5001/api/v0/version > /dev/null 2>&1; then
    echo "[ERROR] IPFS daemon is not running!"
    echo "Please start IPFS daemon first: ipfs daemon"
    exit 1
fi
echo "[OK] IPFS daemon is running"

echo ""
echo "[2/3] Building the application..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed!"
    exit 1
fi
echo "[OK] Build successful"

echo ""
echo "[3/3] Starting BlockVault Backend..."
echo ""
mvn spring-boot:run
