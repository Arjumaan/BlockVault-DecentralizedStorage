# BlockVault - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Install IPFS

**Windows:**

```powershell
# Download IPFS Desktop (easiest option)
# Visit: https://docs.ipfs.tech/install/ipfs-desktop/

# OR use command line IPFS
# Download from: https://dist.ipfs.tech/#go-ipfs and extract
# Add to PATH, then:
ipfs init
ipfs daemon
```

**Linux/Mac:**

```bash
wget https://dist.ipfs.tech/kubo/v0.24.0/kubo_v0.24.0_linux-amd64.tar.gz
tar -xvzf kubo_v0.24.0_linux-amd64.tar.gz
cd kubo
sudo bash install.sh
ipfs init
ipfs daemon
```

### Step 2: Start the Backend

**Option A: Using the startup script (Recommended)**

Windows:

```powershell
cd backend
start.bat
```

Linux/Mac:

```bash
cd backend
chmod +x start.sh
./start.sh
```

**Option B: Manual start**

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Step 3: Test the Backend

Open a new terminal and test the API:

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo\",\"email\":\"demo@blockvault.com\",\"password\":\"demo123\"}"

# Login and get token
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"demo\",\"password\":\"demo123\"}"
```

**You should receive a JWT token. Copy it for the next steps!**

### Step 4: Upload Your First File

Replace `<YOUR_TOKEN>` with the token from Step 3:

```bash
# Create a test file
echo "Hello BlockVault!" > test.txt

# Upload it
curl -X POST http://localhost:8080/api/files/upload ^
  -H "Authorization: Bearer <YOUR_TOKEN>" ^
  -F "file=@test.txt" ^
  -F "encrypt=true"
```

**You'll receive a CID (Content Identifier) and encryption key!**

### Step 5: View Your Files

```bash
# Get all files
curl -X GET http://localhost:8080/api/files ^
  -H "Authorization: Bearer <YOUR_TOKEN>"

# Get dashboard stats
curl -X GET http://localhost:8080/api/dashboard/stats ^
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

## 🎯 What's Next?

### Access the H2 Database Console

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:file:./data/blockvault`
- Username: `sa`
- Password: *(leave empty)*

### Integrate with Frontend

Update the frontend JavaScript to call these real APIs instead of using mock data. See the README for integration examples.

### Verify IPFS Integration

```bash
# Check pinned files in IPFS
ipfs pin ls

# View your files on IPFS
# Use the CID from upload response:
ipfs cat <YOUR_CID>
```

## 🐛 Common Issues

**Problem: "IPFS connection not available"**

- Solution: Make sure `ipfs daemon` is running in another terminal

**Problem: "Port 8080 already in use"**

- Solution: Change port in `application.yml` or kill the process using port 8080

**Problem: "JWT signature does not match"**

- Solution: Make sure you're using the correct token from the login response

## 📚 Full Documentation

For complete API documentation, troubleshooting, and advanced features, see:

- [Backend README](README.md)
- [Frontend Integration Guide](README.md#frontend-integration)

## ✅ Success Checklist

- [ ] IPFS daemon is running
- [ ] Backend starts successfully on port 8080
- [ ] User registration works
- [ ] User login returns a JWT token
- [ ] File upload returns a CID
- [ ] Files are visible in IPFS (`ipfs pin ls`)
- [ ] Dashboard stats show correct data

**Congratulations! Your BlockVault backend is running! 🎉**
