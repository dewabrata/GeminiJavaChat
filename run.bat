@echo off
echo ========================================
echo   GEMINI CHATBOT CLI
echo ========================================
echo.

REM Check if GEMINI_API_KEY is set
if "%GEMINI_API_KEY%"=="" (
    echo [ERROR] GEMINI_API_KEY environment variable is not set!
    echo.
    echo Please set your Gemini API key first:
    echo   set GEMINI_API_KEY=your_api_key_here
    echo.
    echo Then run this script again.
    echo.
    pause
    exit /b 1
)

echo [INFO] Starting Gemini Chatbot CLI...
echo [INFO] API Key: %GEMINI_API_KEY:~0,8%...
echo.

mvn exec:java

pause
