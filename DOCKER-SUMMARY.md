# 🐳 Docker Quick Reference - Gemini Chatbot CLI

## 📁 Files yang Dibuat

✅ **Dockerfile** - Production build (multi-stage, optimized)
✅ **Dockerfile.dev** - Development build (dengan tools)  
✅ **docker-compose.yml** - Orchestration (production/dev/test)
✅ **docker-entrypoint.sh** - Entry point script (Linux/macOS)
✅ **docker-build.sh** - Build script (Linux/macOS)
✅ **docker-build.ps1** - Build script (Windows PowerShell)
✅ **.dockerignore** - Exclude unnecessary files
✅ **docker-quickstart.bat** - Quick start (Windows)
✅ **README-Docker.md** - Dokumentasi lengkap

## 🚀 Quick Start

### Windows (Termudah)
```cmd
# Double-click atau run di Command Prompt
docker-quickstart.bat
```

### Linux/macOS
```bash
# Make executable (sekali saja)
chmod +x docker-build.sh docker-entrypoint.sh

# Build dan run production
docker-compose --profile production up --build
```

### Manual Docker
```bash
# Build
docker build -f Dockerfile -t gemini-chatbot:latest .

# Run
docker run -it --env-file .env gemini-chatbot:latest
```

## 📋 Prerequisites

1. **Docker Desktop** installed dan running
2. **File .env** dengan GEMINI_API_KEY
3. **Internet connection** untuk dependencies

## 🛠️ Environment Setup

```bash
# Copy environment template
cp .env.example .env

# Edit dan tambahkan API key
# GEMINI_API_KEY=your_api_key_here
```

## 🔧 Commands Cheat Sheet

| Command | Description |
|---------|-------------|
| `docker-quickstart.bat` | Windows quick start menu |
| `./docker-build.sh` | Linux/macOS production build |
| `./docker-build.sh -e development` | Development build |
| `docker-compose --profile production up` | Production dengan compose |
| `docker-compose --profile development up` | Development dengan hot reload |
| `docker-compose --profile test up` | Run tests |

## 🎯 Image Sizes

- **Production**: ~150-200MB (optimized)
- **Development**: ~400-500MB (with build tools)

## 🔍 Troubleshooting

| Issue | Solution |
|-------|----------|
| Permission denied | `chmod +x *.sh` (Linux/macOS) |
| .env not found | Copy from `.env.example` |
| Port 5005 in use | Change port in `docker-compose.yml` |
| Build fails | Run `docker system prune -f` |

## 📚 More Info

👉 **Complete documentation**: `README-Docker.md`
🛠️ **Build options**: `./docker-build.sh -h`
🔧 **PowerShell help**: `.\docker-build.ps1 -Help`

---
**Happy Containerizing! 🐳🚀**
