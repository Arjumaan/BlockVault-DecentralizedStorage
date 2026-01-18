# 🚀 BlockVault - Decentralized File Storage Platform

[![GitHub](https://img.shields.io/badge/GitHub-BlockVault-blue?logo=github)](https://github.com/Arjumaan/BlockVault-DecentralizedStorage)
[![IPFS](https://img.shields.io/badge/Storage-IPFS-65c2cb?logo=ipfs)](https://ipfs.tech)
[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A **secure, fault-tolerant, censorship-resistant** file storage system built with **Java Spring Boot** and **IPFS** (InterPlanetary File System).

![BlockVault Banner](https://via.placeholder.com/1200x300/050505/00E676?text=BlockVault+•+Decentralized+Storage)

## 🎯 Problem Statement

Traditional cloud storage providers (Google Drive, Dropbox, OneDrive) suffer from:

- ❌ Single point of failure
- ❌ Data breaches and privacy concerns
- ❌ Subscription dependency
- ❌ Limited transparency
- ❌ Risk of censorship
- ❌ Vendor lock-in

## 💡 Solution

**BlockVault** uses **IPFS** to store files in a distributed P2P network with:

- ✅ Content-based addressing (CID) instead of location-based storage
- ✅ End-to-end AES-256 encryption
- ✅ Java backend for authentication and metadata management
- ✅ Client dashboard for upload/download/search
- ✅ Optional blockchain anchoring for immutability
- ✅ Automatic file versioning

---

## 🌟 Features

### Core Features

- 📤 **Upload to IPFS** - Files stored with unique Content Identifier (CID)
- 📥 **Download from IPFS** - Retrieve files using CID
- 🔒 **AES-256 Encryption** - Automatic encryption/decryption
- 👤 **User Authentication** - JWT-based secure login
- 📊 **Storage Management** - Track usage with 5GB default quota
- 📁 **File Versioning** - Complete version history
- 🔗 **Share Links** - Generate public IPFS gateway links

### Security Features

- 🛡️ **JWT Authentication** - Stateless token-based authentication
- 🔐 **Password Encryption** - BCrypt hashing
- 🔒 **File Encryption** - AES-256-GCM before upload
- 👥 **User Isolation** - Secure multi-user support
- 🌐 **CORS Protection** - Configurable allowed origins

### Dashboard Features

- 📊 **Storage Statistics** - Real-time usage tracking
- 📁 **File Management** - Upload, download, delete, share
- 📈 **Analytics** - Charts and usage insights
- 🕒 **Activity Feed** - Recent file operations
- 🔍 **CID Lookup** - Find files by Content ID

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Backend** | Spring Boot | 3.2.1 |
| **Language** | Java | 17+ |
| **Security** | JWT + BCrypt | Latest |
| **Encryption** | AES-256-GCM | Built-in |
| **Storage** | IPFS | Latest |
| **Database** | H2 / PostgreSQL | Latest |
| **Build Tool** | Maven | 3.6+ |
| **Frontend** | HTML/CSS/JS | Vanilla |

---

## 📂 Project Structure

```
BlockVault/
├── index.html                    # Frontend UI (Complete)
├── backend/                      # Spring Boot Backend
│   ├── src/main/java/           # Java source code (21 files)
│   │   ├── config/              # Security & CORS configuration
│   │   ├── controller/          # REST API endpoints (4)
│   │   ├── service/             # Business logic (5)
│   │   ├── repository/          # Data access (3)
│   │   ├── model/               # Database entities (3)
│   │   └── security/            # JWT authentication (3)
│   ├── src/main/resources/      # Configuration files
│   │   └── application.yml      # App configuration
│   ├── pom.xml                  # Maven dependencies
│   ├── README.md                # Backend documentation
│   ├── QUICKSTART.md            # 5-minute quick start
│   ├── API_TESTS.md             # API test commands
│   └── start.bat / start.sh     # Startup scripts
├── PROJECT_OVERVIEW.md          # Complete project overview
└── .gitignore                   # Git ignore rules
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **IPFS** - [Installation Guide](#ipfs-installation)

### IPFS Installation

#### Windows

```powershell
# Download from: https://dist.ipfs.tech/#go-ipfs
# Extract and add to PATH, then:
ipfs init
ipfs daemon
```

#### Linux / macOS

```bash
wget https://dist.ipfs.tech/kubo/v0.24.0/kubo_v0.24.0_linux-amd64.tar.gz
tar -xvzf kubo_v0.24.0_linux-amd64.tar.gz
cd kubo
sudo bash install.sh
ipfs init
ipfs daemon
```

### Running BlockVault

**1. Start IPFS daemon:**

```bash
ipfs daemon
```

**2. Start the backend:**

```bash
cd backend
./start.sh       # Linux/Mac
# OR
start.bat        # Windows
```

**3. Open the frontend:**
Open `index.html` in your browser or serve it:

```bash
# Python
python -m http.server 5500

# Node.js
npx http-server -p 5500
```

**4. Test the API:**

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@test.com","password":"demo123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```

---

## 📡 API Documentation

### Authentication (Public)

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### File Management (Protected - Requires JWT)

- `POST /api/files/upload` - Upload file to IPFS
- `GET /api/files` - Get all user files
- `GET /api/files/{id}` - Get file metadata
- `GET /api/files/download/{cid}` - Download file from IPFS
- `DELETE /api/files/{id}` - Delete file
- `POST /api/files/{id}/share` - Generate share link
- `GET /api/files/{id}/versions` - Get version history

### Dashboard (Protected)

- `GET /api/dashboard/stats` - Storage statistics
- `GET /api/dashboard/recent` - Recent files
- `GET /api/dashboard/activity` - Activity feed

### Analytics (Protected)

- `GET /api/analytics` - Usage analytics

**Full API documentation:** See [backend/README.md](backend/README.md)

---

## 📸 Screenshots

### Dashboard

![Dashboard](https://via.placeholder.com/800x500/050505/00E676?text=Dashboard+•+Storage+Stats)

### File Upload

![Upload](https://via.placeholder.com/800x500/050505/00E676?text=File+Upload+•+Encryption+Toggle)

### File Vault

![Vault](https://via.placeholder.com/800x500/050505/00E676?text=File+Vault+•+Search+%26+Filter)

---

## 🎯 Use Cases

1. **Personal Cloud Storage** - Your own decentralized Dropbox
2. **Document Management** - Secure file versioning
3. **Data Backup** - Distributed, fault-tolerant backup
4. **File Sharing** - Censorship-resistant sharing
5. **Research Data** - Immutable data storage
6. **Media Hosting** - Decentralized video/image hosting

---

## 🔒 Security

- **AES-256-GCM Encryption** - Military-grade file encryption
- **JWT Authentication** - Secure, stateless authentication
- **BCrypt Password Hashing** - Industry-standard password security
- **User Isolation** - Complete data separation between users
- **CORS Protection** - Configurable cross-origin policies
- **Input Validation** - Request sanitization and validation

---

## 📊 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                Frontend (HTML/CSS/JS)                   │
│              Modern UI with Dashboard                   │
└─────────────────┬───────────────────────────────────────┘
                  │ REST API (JWT Authentication)
┌─────────────────▼───────────────────────────────────────┐
│                Spring Boot Backend                       │
│  ┌────────────────────────────────────────────────────┐ │
│  │  Controllers → Services → Repositories → DB        │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────┬───────────────────────────────────────┘
                  │ IPFS HTTP API
┌─────────────────▼───────────────────────────────────────┐
│                  IPFS Network                           │
│        Decentralized File Storage (P2P)                 │
└─────────────────────────────────────────────────────────┘
```

---

## 📚 Documentation

- **[PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)** - Complete project overview
- **[backend/README.md](backend/README.md)** - Backend setup & API guide
- **[backend/QUICKSTART.md](backend/QUICKSTART.md)** - 5-minute quick start
- **[backend/API_TESTS.md](backend/API_TESTS.md)** - API testing commands
- **[backend/PROJECT_STRUCTURE.md](backend/PROJECT_STRUCTURE.md)** - Architecture details

---

## 🧪 Testing

Comprehensive test commands available in [backend/API_TESTS.md](backend/API_TESTS.md)

**Quick test:**

```bash
# 1. Register and login
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"test123"}'

# 2. Upload a file
echo "Hello BlockVault!" > test.txt
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -F "file=@test.txt" \
  -F "encrypt=true"

# 3. Verify in IPFS
ipfs pin ls
```

---

## 🚀 Deployment

### Development

- **Frontend:** Serve `index.html` locally
- **Backend:** `mvn spring-boot:run`
- **IPFS:** Local daemon

### Production

- **Frontend:** Deploy to Netlify, Vercel, or GitHub Pages
- **Backend:** Deploy to AWS, Azure, or Heroku
- **Database:** Switch to PostgreSQL
- **IPFS:** Use IPFS cluster or Pinata/Infura gateways

---

## 🤝 Contributing

Contributions are welcome! Here's how:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **IPFS** - InterPlanetary File System
- **Spring Boot** - Java framework
- **Java Community** - For continuous improvements

---

## 📞 Contact

**Developer:** Arjumaan  
**GitHub:** [@Arjumaan](https://github.com/Arjumaan)  
**Project Link:** [BlockVault-DecentralizedStorage](https://github.com/Arjumaan/BlockVault-DecentralizedStorage)

---

## 📈 Project Stats

- **Total Files:** 30+
- **Lines of Code:** ~5,700+
- **API Endpoints:** 13
- **Database Tables:** 3
- **Services:** 5
- **Documentation Pages:** 6

---

## ⭐ Star this repo if you find it useful

---

**Built with ❤️ using Java Spring Boot, IPFS, and Modern Web Technologies**

*Welcome to the future of decentralized file storage!* 🚀
