# BlockVault Backend

A secure, decentralized file storage platform backend built with **Java Spring Boot** and **IPFS** integration.

## 🚀 Features

- ✅ **IPFS Integration** - Decentralized file storage using InterPlanetary File System
- 🔐 **JWT Authentication** - Secure user authentication with JSON Web Tokens
- 🔒 **AES-256 Encryption** - End-to-end file encryption
- 📊 **RESTful APIs** - Complete REST API for file management
- 💾 **File Versioning** - Track file version history
- 📈 **Dashboard Analytics** - Storage statistics and usage analytics
- 🔄 **File Pinning** - Automatic IPFS pinning for data persistence
- 👥 **Multi-user Support** - User isolation and storage quotas

## 📋 Prerequisites

Before running the backend, ensure you have the following installed:

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **IPFS** ([Installation Guide](#ipfs-installation))

## 🔧 IPFS Installation

### Windows

1. Download IPFS from [https://dist.ipfs.tech/#go-ipfs](https://dist.ipfs.tech/#go-ipfs)
2. Extract the archive to a folder (e.g., `C:\ipfs`)
3. Add IPFS to your PATH environment variable
4. Initialize IPFS:

   ```bash
   ipfs init
   ```

5. Start IPFS daemon:

   ```bash
   ipfs daemon
   ```

### Linux / macOS

```bash
# Download and install
wget https://dist.ipfs.tech/go-ipfs/v0.17.0/go-ipfs_v0.17.0_linux-amd64.tar.gz
tar -xvzf go-ipfs_v0.17.0_linux-amd64.tar.gz
cd go-ipfs
sudo bash install.sh

# Initialize and start
ipfs init
ipfs daemon
```

**Important:** The IPFS daemon must be running before starting the BlockVault backend!

## ⚙️ Configuration

The main configuration file is located at `src/main/resources/application.yml`.

### Key Configuration Settings

```yaml
# Server Configuration
server:
  port: 8080

# IPFS Configuration
ipfs:
  host: localhost
  port: 5001
  protocol: http

# JWT Configuration
jwt:
  secret: YOUR-SECRET-KEY-HERE  # Change this in production!
  expiration: 86400000  # 24 hours

# Database (H2 for development)
spring:
  datasource:
    url: jdbc:h2:file:./data/blockvault
    username: sa
    password:

# Storage Quota
blockvault:
  storage:
    default-quota: 5368709120  # 5GB default per user
```

> **⚠️ Security Warning:** Change the JWT secret key before deploying to production!

## 🏃 Running the Application

### 1. Clone the repository (if not already cloned)

```bash
cd "d:\My Projects\BlockVault - Decentralized Storage\backend"
```

### 2. Ensure IPFS daemon is running

```bash
ipfs daemon
```

You should see output like:

```
API server listening on /ip4/127.0.0.1/tcp/5001
```

### 3. Build and run the application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Alternatively, using the JAR file:

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/blockvault-backend-1.0.0.jar
```

### 4. Verify the application is running

The backend should start on **<http://localhost:8080>**

You should see the BlockVault startup banner:

```
╔══════════════════════════════════════════════════════════╗
║                                                          ║
║         BlockVault Backend - Successfully Started        ║
║                                                          ║
║  🚀 Server running on: http://localhost:8080            ║
║  📊 H2 Console: http://localhost:8080/h2-console        ║
║  🔒 Authentication: JWT-based                            ║
║  🌐 IPFS Integration: Active                             ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

## 📚 API Documentation

### Authentication Endpoints

#### Register User

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "storageQuota": 5368709120,
    "usedStorage": 0
  }
}
```

### File Management Endpoints

> **Note:** All file endpoints require the `Authorization: Bearer <token>` header.

#### Upload File

```http
POST /api/files/upload
Authorization: Bearer <your-jwt-token>
Content-Type: multipart/form-data

file: <binary-file-data>
encrypt: true
encryptionKey: <optional-base64-key>
```

**Response:**

```json
{
  "success": true,
  "message": "File uploaded successfully",
  "file": {
    "id": 1,
    "name": "document.pdf",
    "cid": "QmXk9VBjjK7FqMxS8...",
    "size": "2.34 MB",
    "type": "PDF",
    "encrypted": true,
    "date": "Jan 18, 2026"
  },
  "encryptionKey": "dGhpc2lzYXNlY3JldGtleQ==",
  "warning": "Save this encryption key securely! It cannot be recovered."
}
```

#### Get All Files

```http
GET /api/files
Authorization: Bearer <your-jwt-token>
```

#### Download File

```http
GET /api/files/download/{cid}?decryptionKey=<key>
Authorization: Bearer <your-jwt-token>
```

#### Delete File

```http
DELETE /api/files/{id}
Authorization: Bearer <your-jwt-token>
```

#### Generate Share Link

```http
POST /api/files/{id}/share
Authorization: Bearer <your-jwt-token>
```

#### Get File Versions

```http
GET /api/files/{id}/versions
Authorization: Bearer <your-jwt-token>
```

### Dashboard Endpoints

#### Get Storage Statistics

```http
GET /api/dashboard/stats
Authorization: Bearer <your-jwt-token>
```

#### Get Recent Files

```http
GET /api/dashboard/recent
Authorization: Bearer <your-jwt-token>
```

#### Get Activity Feed

```http
GET /api/dashboard/activity
Authorization: Bearer <your-jwt-token>
```

### Analytics Endpoints

#### Get Analytics

```http
GET /api/analytics
Authorization: Bearer <your-jwt-token>
```

## 🗄️ Database Access

The application uses **H2 Database** for development. Access the H2 console:

- **URL:** <http://localhost:8080/h2-console>
- **JDBC URL:** `jdbc:h2:file:./data/blockvault`
- **Username:** `sa`
- **Password:** *(leave empty)*

## 🧪 Testing the Backend

### Using cURL

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Upload a file (replace <TOKEN> with your JWT)
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@test-file.pdf" \
  -F "encrypt=true"

# Get all files
curl -X GET http://localhost:8080/api/files \
  -H "Authorization: Bearer <TOKEN>"

# Get dashboard stats
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer <TOKEN>"
```

### Using Postman

1. Import the API endpoints into Postman
2. Set the Authorization header to `Bearer <your-jwt-token>`
3. Test all endpoints

## 🔌 Frontend Integration

To connect the frontend with the backend:

1. **Update CORS settings** in `application.yml` if your frontend runs on a different port
2. **Replace mock data** in the frontend JavaScript with actual API calls
3. **Store JWT token** in localStorage or sessionStorage
4. **Add Authorization header** to all authenticated requests

Example JavaScript integration:

```javascript
// Login
async function login(username, password) {
    const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    const data = await response.json();
    if (data.success) {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
    }
    return data;
}

// Upload file
async function uploadFile(file, encrypt = false) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('encrypt', encrypt);
    
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/files/upload', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData
    });
    return await response.json();
}

// Get all files
async function getFiles() {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/files', {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return await response.json();
}
```

## 🐛 Troubleshooting

### IPFS Connection Error

**Error:** `Failed to connect to IPFS node`

**Solution:**

1. Ensure IPFS daemon is running: `ipfs daemon`
2. Check IPFS is listening on port 5001: `netstat -an | grep 5001`
3. Verify IPFS API is accessible: `curl http://localhost:5001/api/v0/version`

### Port Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**

1. Change the port in `application.yml`:

   ```yaml
   server:
     port: 8081
   ```

2. Or kill the process using port 8080

### JWT Token Errors

**Error:** `JWT signature does not match`

**Solution:**

- Ensure you're using the same JWT secret across restarts
- Check that the token hasn't expired (default: 24 hours)

## 📦 Project Structure

```
backend/
├── src/main/java/com/blockvault/
│   ├── BlockVaultApplication.java          # Main application class
│   ├── config/
│   │   ├── SecurityConfig.java            # Spring Security configuration
│   │   └── CorsConfig.java                # CORS configuration
│   ├── controller/
│   │   ├── AuthController.java            # Authentication endpoints
│   │   ├── FileController.java            # File management endpoints
│   │   ├── DashboardController.java       # Dashboard endpoints
│   │   └── AnalyticsController.java       # Analytics endpoints
│   ├── service/
│   │   ├── IPFSService.java               # IPFS integration
│   │   ├── EncryptionService.java         # AES-256 encryption
│   │   ├── FileService.java               # File management logic
│   │   ├── AuthService.java               # Authentication logic
│   │   └── DashboardService.java          # Dashboard analytics
│   ├── repository/
│   │   ├── UserRepository.java            # User data access
│   │   ├── FileMetadataRepository.java    # File metadata access
│   │   └── FileVersionRepository.java     # Version history access
│   ├── model/
│   │   ├── User.java                      # User entity
│   │   ├── FileMetadata.java              # File metadata entity
│   │   └── FileVersion.java               # File version entity
│   └── security/
│       ├── JwtUtil.java                   # JWT utilities
│       ├── JwtAuthenticationFilter.java   # JWT filter
│       └── CustomUserDetailsService.java  # User details service
├── src/main/resources/
│   └── application.yml                    # Application configuration
├── pom.xml                                 # Maven dependencies
└── README.md                               # This file
```

## 🚀 Production Deployment

For production deployment:

1. **Change JWT Secret** in `application.yml`
2. **Switch to PostgreSQL** instead of H2
3. **Enable HTTPS/SSL**
4. **Set up proper CORS** origins
5. **Use environment variables** for sensitive data
6. **Configure IPFS** cluster for redundancy
7. **Set up logging** and monitoring
8. **Implement rate limiting**

## 📝 License

This project is part of BlockVault - Decentralized File Storage Platform.

## 👨‍💻 Support

For issues or questions:

- Check the troubleshooting section
- Review API documentation
- Verify IPFS daemon is running
- Check application logs in the console

---

**Built with ❤️ using Java Spring Boot and IPFS**
