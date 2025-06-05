# 🐳 Docker Setup untuk Gemini Chatbot CLI

Dokumentasi lengkap untuk menjalankan aplikasi Gemini Chatbot CLI menggunakan Docker di Linux dan Windows.

## 📋 Prasyarat

- **Docker Engine** 20.10+ 
- **Docker Compose** v2.0+
- **Git** (untuk clone repository)
- **GEMINI_API_KEY** (dari Google AI Studio)

### Instalasi Docker

#### Linux (Ubuntu/Debian)
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### Windows
Download dan install **Docker Desktop** dari [docker.com](https://www.docker.com/products/docker-desktop/)

#### macOS
```bash
# Menggunakan Homebrew
brew install docker docker-compose
```

## 🏗️ Struktur File Docker

```
├── Dockerfile              # Production build (multi-stage)
├── Dockerfile.dev          # Development build
├── docker-compose.yml      # Orchestration untuk semua environment
├── docker-entrypoint.sh    # Entry point script (Linux/macOS)
├── docker-build.sh         # Build script untuk Linux/macOS
├── docker-build.ps1        # Build script untuk Windows PowerShell
├── .dockerignore           # Files yang dikecualikan dari build
└── README-Docker.md        # Dokumentasi ini
```

## ⚙️ Setup Environment

1. **Copy environment file:**
   ```bash
   # Linux/macOS
   cp .env.example .env
   
   # Windows PowerShell
   Copy-Item .env.example .env
   ```

2. **Edit file .env dan tambahkan API key:**
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

## 🚀 Cara Menjalankan

### Option 1: Docker Compose (Recommended)

#### Production
```bash
# Build dan run production
docker-compose --profile production up --build

# Run background
docker-compose --profile production up -d

# Stop
docker-compose --profile production down
```

#### Development
```bash
# Build dan run development dengan hot reload
docker-compose --profile development up --build

# Attach ke container untuk debugging
docker-compose --profile development exec gemini-chatbot-dev bash
```

#### Testing
```bash
# Run tests
docker-compose --profile test up --build
```

### Option 2: Docker Build Scripts

#### Linux/macOS
```bash
# Make script executable
chmod +x docker-build.sh

# Build production
./docker-build.sh

# Build development
./docker-build.sh -e development

# Build dengan custom tag
./docker-build.sh -e production -t v1.0.0

# Build dan push ke registry
./docker-build.sh -p -r docker.io/username

# Show help
./docker-build.sh -h
```

#### Windows PowerShell
```powershell
# Build production
.\docker-build.ps1

# Build development
.\docker-build.ps1 -Environment development

# Build dengan custom tag
.\docker-build.ps1 -Environment production -Tag v1.0.0

# Build dan push ke registry
.\docker-build.ps1 -Push -Registry docker.io/username

# Show help
.\docker-build.ps1 -Help
```

### Option 3: Manual Docker Commands

#### Production Build
```bash
# Build image
docker build -f Dockerfile -t gemini-chatbot:latest .

# Run container
docker run -it --env-file .env gemini-chatbot:latest

# Run dengan volume mount untuk .env
docker run -it -v $(pwd)/.env:/app/.env gemini-chatbot:latest

# Windows PowerShell
docker run -it -v ${PWD}/.env:/app/.env gemini-chatbot:latest
```

#### Development Build
```bash
# Build development image
docker build -f Dockerfile.dev -t gemini-chatbot:dev .

# Run dengan source code mounting
docker run -it \
  -v $(pwd)/src:/app/src \
  -v $(pwd)/.env:/app/.env \
  -v $(pwd)/pom.xml:/app/pom.xml \
  gemini-chatbot:dev

# Windows PowerShell
docker run -it `
  -v ${PWD}/src:/app/src `
  -v ${PWD}/.env:/app/.env `
  -v ${PWD}/pom.xml:/app/pom.xml `
  gemini-chatbot:dev
```

## 🛠️ Konfigurasi Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `GEMINI_API_KEY` | API key untuk Gemini | - | ✅ |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` | ❌ |
| `APP_ENV` | Environment mode | `production` | ❌ |

### Contoh Environment Variables

#### Production
```env
GEMINI_API_KEY=your_api_key_here
JAVA_OPTS=-Xmx512m -Xms256m
APP_ENV=production
```

#### Development
```env
GEMINI_API_KEY=your_api_key_here
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
APP_ENV=development
```

## 📊 Monitoring dan Debugging

### Health Check
```bash
# Check container health
docker ps

# Manual health check
docker exec gemini-chatbot-prod /usr/local/bin/docker-entrypoint.sh --health-check

# View logs
docker logs gemini-chatbot-prod

# Follow logs
docker logs -f gemini-chatbot-prod
```

### Development Debugging
```bash
# Attach to development container
docker-compose --profile development exec gemini-chatbot-dev bash

# View Maven build output
docker-compose --profile development logs -f

# Remote debugging (port 5005 exposed)
# Connect your IDE to localhost:5005
```

### Container Information
```bash
# Show container info
docker inspect gemini-chatbot-prod

# Show image info
docker images gemini-chatbot

# Resource usage
docker stats gemini-chatbot-prod
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Permission Denied (Linux)
```bash
# Fix Docker permissions
sudo usermod -aG docker $USER
newgrp docker

# Fix script permissions
chmod +x docker-build.sh
chmod +x docker-entrypoint.sh
```

#### 2. Port Already in Use
```bash
# Kill process using port 5005
sudo lsof -i :5005
sudo kill -9 <PID>

# Or change port in docker-compose.yml
ports:
  - "5006:5005"  # Change external port
```

#### 3. .env File Not Found
```bash
# Create .env file
cp .env.example .env

# Edit dengan text editor
nano .env  # Linux
notepad .env  # Windows
```

#### 4. Memory Issues
```bash
# Increase memory limits
export JAVA_OPTS="-Xmx1g -Xms512m"

# Or edit docker-compose.yml
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
```

#### 5. Build Cache Issues
```bash
# Clear Docker cache
docker system prune -f

# Build without cache
docker build --no-cache -f Dockerfile -t gemini-chatbot:latest .

# Or using build script
./docker-build.sh --no-cache
```

### Performance Optimization

#### 1. Multi-stage Build Benefits
- **Smaller final image** (~150-200MB vs ~500MB+)
- **Security** (no build tools in production)
- **Layer caching** untuk build yang lebih cepat

#### 2. Memory Tuning
```bash
# For low memory systems
JAVA_OPTS="-Xmx256m -Xms128m"

# For high performance
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

#### 3. Development Performance
```bash
# Mount Maven cache untuk faster builds
volumes:
  - maven-cache:/root/.m2
```

## 🚢 Deployment ke Production

### 1. Build Production Image
```bash
# Build optimized production image
./docker-build.sh -e production -t v1.0.0 --clean

# Tag for registry
docker tag gemini-chatbot:v1.0.0 your-registry.com/gemini-chatbot:v1.0.0
```

### 2. Push ke Container Registry
```bash
# Docker Hub
docker push your-username/gemini-chatbot:v1.0.0

# Private registry
./docker-build.sh -p -r your-registry.com/your-username
```

### 3. Deploy di Server
```bash
# Pull image di server
docker pull your-registry.com/gemini-chatbot:v1.0.0

# Run dengan restart policy
docker run -d \
  --name gemini-chatbot \
  --restart unless-stopped \
  --env-file .env \
  your-registry.com/gemini-chatbot:v1.0.0
```

## 📝 Development Workflow

### 1. Setup Development Environment
```bash
# Clone repository
git clone <repository-url>
cd <repository-name>

# Setup environment
cp .env.example .env
# Edit .env dengan API key

# Start development
docker-compose --profile development up
```

### 2. Code Changes
```bash
# Code changes akan auto-reload (volume mounting)
# Edit files di src/

# Rebuild jika ada perubahan dependency
docker-compose --profile development down
docker-compose --profile development up --build
```

### 3. Testing
```bash
# Run tests
docker-compose --profile test up

# Run specific test
docker-compose --profile development exec gemini-chatbot-dev mvn test -Dtest=SpecificTest
```

### 4. Build untuk Production
```bash
# Test production build
./docker-build.sh -e production

# Push ke registry
./docker-build.sh -e production -t v1.0.0 -p -r your-registry.com/username
```

## 🔒 Security Best Practices

1. **Non-root user** dalam container
2. **Multi-stage build** untuk mengurangi attack surface
3. **Environment variables** untuk secrets (jangan hardcode)
4. **Read-only volumes** untuk configuration
5. **Health checks** untuk monitoring
6. **Resource limits** untuk mencegah resource exhaustion

## 📚 Referensi

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Java Docker Best Practices](https://docs.docker.com/language/java/)
- [Alpine Linux Package Manager](https://wiki.alpinelinux.org/wiki/Alpine_Package_Keeper)

---

## 🆘 Support

Jika mengalami masalah:
1. Check logs: `docker logs <container-name>`
2. Verify environment: `docker exec <container> env`
3. Test health check: `docker exec <container> --health-check`
4. Check resources: `docker stats`
5. Review this documentation

**Happy Containerizing! 🐳🚀**
