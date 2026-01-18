# BlockVault API Test Collection

This file contains ready-to-use API test commands.

## Prerequisites

- Backend server running on `http://localhost:8080`
- IPFS daemon running

---

## 1. Authentication Tests

### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"email\":\"test@blockvault.com\",\"password\":\"test123\"}"
```

**Expected Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@blockvault.com"
  }
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"password\":\"test123\"}"
```

**Expected Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@blockvault.com",
    "storageQuota": 5368709120,
    "usedStorage": 0
  }
}
```

**💡 Save the token from the response! Replace `<TOKEN>` in all following commands with your actual token.**

---

## 2. File Management Tests

### Upload a File (Without Encryption)

```bash
# First create a test file
echo "Hello from BlockVault!" > test.txt

# Upload it
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@test.txt" \
  -F "encrypt=false"
```

### Upload a File (With Encryption)

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@test.txt" \
  -F "encrypt=true"
```

**⚠️ Important: Save the `encryptionKey` from the response! You'll need it to decrypt the file.**

### Get All Files

```bash
curl -X GET http://localhost:8080/api/files \
  -H "Authorization: Bearer <TOKEN>"
```

### Get File by ID

```bash
curl -X GET http://localhost:8080/api/files/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### Download File (No Decryption)

```bash
# Replace <CID> with the actual CID from upload response
curl -X GET "http://localhost:8080/api/files/download/<CID>" \
  -H "Authorization: Bearer <TOKEN>" \
  --output downloaded-file.txt
```

### Download File (With Decryption)

```bash
# Replace <CID> and <ENCRYPTION_KEY> with actual values
curl -X GET "http://localhost:8080/api/files/download/<CID>?decryptionKey=<ENCRYPTION_KEY>" \
  -H "Authorization: Bearer <TOKEN>" \
  --output decrypted-file.txt
```

### Generate Share Link

```bash
curl -X POST http://localhost:8080/api/files/1/share \
  -H "Authorization: Bearer <TOKEN>"
```

### Get File Versions

```bash
curl -X GET http://localhost:8080/api/files/1/versions \
  -H "Authorization: Bearer <TOKEN>"
```

### Delete File

```bash
curl -X DELETE http://localhost:8080/api/files/1 \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 3. Dashboard Tests

### Get Storage Statistics

```bash
curl -X GET http://localhost:8080/api/dashboard/stats \
  -H "Authorization: Bearer <TOKEN>"
```

**Expected Response:**

```json
{
  "success": true,
  "stats": {
    "totalStorage": 5368709120,
    "usedStorage": 12345,
    "availableStorage": 5368696775,
    "usagePercentage": 0.0002,
    "totalStorageFormatted": "5.00 GB",
    "usedStorageFormatted": "12.05 KB",
    "availableStorageFormatted": "5.00 GB",
    "fileCount": 2,
    "activeNodes": 3,
    "replicationFactor": 3,
    "pinnedFiles": 2
  }
}
```

### Get Recent Files

```bash
curl -X GET http://localhost:8080/api/dashboard/recent \
  -H "Authorization: Bearer <TOKEN>"
```

### Get Activity Feed

```bash
curl -X GET http://localhost:8080/api/dashboard/activity \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 4. Analytics Tests

### Get Analytics Data

```bash
curl -X GET http://localhost:8080/api/analytics \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 5. IPFS Verification

After uploading a file, verify it's in IPFS:

```bash
# List all pinned files
ipfs pin ls

# View file content directly from IPFS (replace <CID>)
ipfs cat <CID>

# Get file stats
ipfs files stat /ipfs/<CID>
```

---

## Windows PowerShell Commands

If you're using PowerShell on Windows, use these instead:

### Register

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"testuser","email":"test@blockvault.com","password":"test123"}'
```

### Login

```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"testuser","password":"test123"}'

$token = $response.token
Write-Host "Token: $token"
```

### Upload File

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$form = @{
    file = Get-Item -Path "test.txt"
    encrypt = "false"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/files/upload" `
  -Method POST `
  -Headers $headers `
  -Form $form
```

### Get Files

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/files" `
  -Method GET `
  -Headers @{"Authorization"="Bearer $token"}
```

---

## Testing Workflow

1. **Start IPFS daemon:**

   ```bash
   ipfs daemon
   ```

2. **Start backend:**

   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Register and login:**
   - Use the register endpoint
   - Use the login endpoint and save the token

4. **Upload files:**
   - Test both encrypted and non-encrypted uploads
   - Save the CID and encryption key

5. **Verify in IPFS:**
   - Check `ipfs pin ls`
   - Try `ipfs cat <CID>`

6. **Test download:**
   - Download without decryption
   - Download with decryption (for encrypted files)

7. **Check dashboard:**
   - Verify storage stats
   - Check recent files
   - View activity feed

8. **Test deletion:**
   - Delete a file
   - Verify it's removed from the database
   - Check IPFS pins are removed

---

## Common Response Codes

- `200 OK` - Request successful
- `201 Created` - Resource created (registration)
- `400 Bad Request` - Invalid input or error
- `401 Unauthorized` - Missing or invalid JWT token
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Tips for Testing

1. **Use Postman or Insomnia** for easier API testing
2. **Save the JWT token** in environment variables
3. **Keep track of CIDs** from upload responses
4. **Store encryption keys** securely when testing encrypted uploads
5. **Check backend logs** for detailed error messages
6. **Verify IPFS daemon** is running before testing

---

**Happy Testing! 🚀**
