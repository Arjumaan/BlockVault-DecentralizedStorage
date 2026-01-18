# BlockVault Backend - Project Structure

## 📁 Complete File Structure

```
backend/
├── src/
│   └── main/
│       ├── java/com/blockvault/
│       │   ├── BlockVaultApplication.java          ✅ Main application class
│       │   │
│       │   ├── config/                            ⚙️ Configuration
│       │   │   ├── CorsConfig.java                ✅ CORS settings
│       │   │   └── SecurityConfig.java            ✅ Spring Security + JWT
│       │   │
│       │   ├── controller/                        🌐 REST API Endpoints
│       │   │   ├── AnalyticsController.java       ✅ Analytics APIs
│       │   │   ├── AuthController.java            ✅ Login/Register
│       │   │   ├── DashboardController.java       ✅ Dashboard stats
│       │   │   └── FileController.java            ✅ File management
│       │   │
│       │   ├── model/                             📊 Database Entities
│       │   │   ├── FileMetadata.java              ✅ File metadata entity
│       │   │   ├── FileVersion.java               ✅ Version tracking
│       │   │   └── User.java                      ✅ User entity
│       │   │
│       │   ├── repository/                        💾 Data Access Layer
│       │   │   ├── FileMetadataRepository.java    ✅ File queries
│       │   │   ├── FileVersionRepository.java     ✅ Version queries
│       │   │   └── UserRepository.java            ✅ User queries
│       │   │
│       │   ├── security/                          🔒 Authentication & Security
│       │   │   ├── CustomUserDetailsService.java  ✅ User details
│       │   │   ├── JwtAuthenticationFilter.java   ✅ JWT filter
│       │   │   └── JwtUtil.java                   ✅ JWT utilities
│       │   │
│       │   └── service/                           ⚡ Business Logic
│       │       ├── AuthService.java               ✅ Authentication logic
│       │       ├── DashboardService.java          ✅ Dashboard analytics
│       │       ├── EncryptionService.java         ✅ AES-256 encryption
│       │       ├── FileService.java               ✅ File management
│       │       └── IPFSService.java               ✅ IPFS integration
│       │
│       └── resources/
│           └── application.yml                    ✅ Application config
│
├── .env.template                                  ✅ Environment template
├── .gitignore                                     ✅ Git ignore rules
├── pom.xml                                        ✅ Maven dependencies
├── QUICKSTART.md                                  ✅ Quick start guide
├── README.md                                      ✅ Full documentation
├── start.bat                                      ✅ Windows startup script
└── start.sh                                       ✅ Linux/Mac startup script
```

## 📊 Statistics

- **Total Java Files:** 21
- **Controllers:** 4
- **Services:** 5
- **Repositories:** 3
- **Models:** 3
- **Security Classes:** 3
- **Configuration Classes:** 2
- **Main Application:** 1

## 🎯 Key Components

### 1. Application Layer (`BlockVaultApplication.java`)

- Main entry point
- Spring Boot application configuration
- Startup banner display

### 2. Configuration Layer

- **SecurityConfig.java** - JWT authentication, password encryption, endpoint protection
- **CorsConfig.java** - Cross-Origin Resource Sharing for frontend integration

### 3. Controller Layer (REST APIs)

- **AuthController** - `/api/auth/*` - Registration and login
- **FileController** - `/api/files/*` - File upload, download, delete, share
- **DashboardController** - `/api/dashboard/*` - Statistics and activity
- **AnalyticsController** - `/api/analytics` - Usage analytics

### 4. Service Layer (Business Logic)

- **IPFSService** - IPFS node connection, file upload/download, pinning
- **EncryptionService** - AES-256-GCM encryption/decryption, key generation
- **FileService** - File management orchestration
- **AuthService** - User registration and authentication
- **DashboardService** - Statistics calculation and analytics

### 5. Repository Layer (Data Access)

- **UserRepository** - User CRUD operations
- **FileMetadataRepository** - File metadata queries
- **FileVersionRepository** - Version history queries

### 6. Model Layer (Database Entities)

- **User** - User account with storage quota
- **FileMetadata** - File information with IPFS CID
- **FileVersion** - File version tracking

### 7. Security Layer

- **JwtUtil** - JWT token generation and validation
- **JwtAuthenticationFilter** - Request authentication
- **CustomUserDetailsService** - User details loading

## 🔧 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.2.1 |
| Language | Java | 17+ |
| Security | Spring Security + JWT | Latest |
| Database | H2 (Dev) / PostgreSQL (Prod) | Latest |
| IPFS | Java IPFS HTTP Client | 1.3.3 |
| Encryption | AES-256-GCM | Built-in |
| Build Tool | Maven | 3.6+ |

## 📡 API Endpoints Summary

### Authentication (Public)

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### File Management (Protected)

- `POST /api/files/upload` - Upload file to IPFS
- `GET /api/files` - Get all user files
- `GET /api/files/{id}` - Get file by ID
- `GET /api/files/download/{cid}` - Download file from IPFS
- `DELETE /api/files/{id}` - Delete file
- `POST /api/files/{id}/share` - Generate share link
- `GET /api/files/{id}/versions` - Get version history

### Dashboard (Protected)

- `GET /api/dashboard/stats` - Storage statistics
- `GET /api/dashboard/recent` - Recent files
- `GET /api/dashboard/activity` - Activity feed

### Analytics (Protected)

- `GET /api/analytics` - Usage analytics data

## 🔐 Security Features

1. **JWT Authentication** - Stateless token-based auth
2. **Password Encryption** - BCrypt hashing
3. **AES-256-GCM Encryption** - File encryption
4. **CORS Protection** - Configurable origins
5. **Request Validation** - Input sanitization
6. **User Isolation** - File access control

## 💾 Database Schema

### Users Table

- id, username, email, password
- created_at, storage_quota, used_storage

### File Metadata Table

- id, filename, cid, file_size, file_type
- encrypted, encryption_key_hash, uploaded_at
- user_id, pinned, replication_count

### File Versions Table

- id, file_metadata_id, version_number
- cid, created_at, description, file_size

## 🌐 IPFS Integration

- Automatic connection to IPFS node on startup
- File upload with CID generation
- File download via CID
- Automatic pinning for persistence
- Pin removal on file deletion

## 🚀 Build & Run

```bash
# Build
mvn clean install

# Run (development)
mvn spring-boot:run

# Build JAR (production)
mvn clean package
java -jar target/blockvault-backend-1.0.0.jar
```

## 📝 Configuration

Key settings in `application.yml`:

- Server port (default: 8080)
- IPFS connection (localhost:5001)
- JWT secret and expiration
- Database settings
- CORS allowed origins
- Storage quotas

## ✨ Features Implemented

- ✅ User registration and authentication
- ✅ JWT-based security
- ✅ File upload to IPFS with encryption
- ✅ File download from IPFS with decryption
- ✅ File metadata management
- ✅ File versioning
- ✅ Storage quota tracking
- ✅ Dashboard statistics
- ✅ Activity feed
- ✅ Analytics data
- ✅ Share link generation
- ✅ IPFS pinning management
- ✅ Multi-user support
- ✅ CORS configuration
- ✅ Error handling
- ✅ Logging

## 🔮 Future Enhancements (Optional)

- [ ] Blockchain anchoring (Web3j integration)
- [ ] File sharing with permissions
- [ ] Public/private file visibility
- [ ] File search functionality
- [ ] Batch file operations
- [ ] File compression before upload
- [ ] Thumbnail generation for images
- [ ] File preview support
- [ ] Rate limiting
- [ ] API documentation with Swagger
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] Kubernetes deployment configs

## 📚 Documentation Files

- **README.md** - Complete documentation with API reference
- **QUICKSTART.md** - 5-minute quick start guide
- **PROJECT_STRUCTURE.md** - This file (project overview)

---

**All essential features are implemented and ready to use! 🎉**
