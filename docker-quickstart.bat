@echo off
REM Quick start script untuk Gemini Chatbot CLI di Windows
REM Batch script untuk memudahkan penggunaan Docker

echo.
echo ================================
echo  Gemini Chatbot CLI - Docker
echo ================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker tidak terinstall atau tidak bisa diakses!
    echo Silakan install Docker Desktop dari: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

REM Check if .env file exists
if not exist ".env" (
    echo [WARNING] File .env tidak ditemukan!
    if exist ".env.example" (
        echo [INFO] Copying .env.example ke .env...
        copy ".env.example" ".env"
        echo [INFO] Silakan edit file .env dan tambahkan GEMINI_API_KEY Anda
        echo [INFO] Kemudian jalankan script ini lagi
        pause
        exit /b 1
    ) else (
        echo [ERROR] File .env.example tidak ditemukan!
        echo [INFO] Silakan buat file .env dengan content:
        echo GEMINI_API_KEY=your_api_key_here
        pause
        exit /b 1
    )
)

echo [INFO] Docker terdeteksi
echo [INFO] File .env ditemukan
echo.

REM Menu selection
echo Pilih mode deployment:
echo 1. Production (Optimized build)
echo 2. Development (Hot reload)
echo 3. Test (Run tests)
echo 4. Build only (Tidak run)
echo 5. Clean up (Hapus containers dan images)
echo.
set /p choice="Masukkan pilihan (1-5): "

if "%choice%"=="1" goto production
if "%choice%"=="2" goto development
if "%choice%"=="3" goto test
if "%choice%"=="4" goto buildonly
if "%choice%"=="5" goto cleanup
echo [ERROR] Pilihan tidak valid!
pause
exit /b 1

:production
echo.
echo [INFO] Starting Production mode...
echo [INFO] Building dan running production container...
docker-compose --profile production up --build
goto end

:development
echo.
echo [INFO] Starting Development mode...
echo [INFO] Building dan running development container dengan hot reload...
echo [INFO] Source code akan di-mount untuk development
docker-compose --profile development up --build
goto end

:test
echo.
echo [INFO] Running Tests...
docker-compose --profile test up --build
goto end

:buildonly
echo.
echo [INFO] Building images only...
echo [INFO] Building production image...
docker build -f Dockerfile -t gemini-chatbot:latest .
echo [INFO] Building development image...
docker build -f Dockerfile.dev -t gemini-chatbot:dev .
echo [INFO] Build completed!
echo.
echo Images yang tersedia:
docker images gemini-chatbot
echo.
echo Untuk menjalankan:
echo   Production: docker run -it --env-file .env gemini-chatbot:latest
echo   Development: docker run -it -v %CD%\src:/app/src -v %CD%\.env:/app/.env gemini-chatbot:dev
goto end

:cleanup
echo.
echo [WARNING] Ini akan menghapus semua containers dan images yang terkait!
set /p confirm="Apakah Anda yakin? (y/N): "
if /i not "%confirm%"=="y" goto end

echo [INFO] Stopping containers...
docker-compose --profile production down 2>nul
docker-compose --profile development down 2>nul
docker-compose --profile test down 2>nul

echo [INFO] Removing containers...
docker rm -f gemini-chatbot-prod gemini-chatbot-dev gemini-chatbot-test 2>nul

echo [INFO] Removing images...
docker rmi gemini-chatbot:latest gemini-chatbot:dev gemini-chatbot:test 2>nul

echo [INFO] Cleaning up unused images...
docker image prune -f

echo [INFO] Cleanup completed!
goto end

:end
echo.
echo [INFO] Script selesai
pause
