#!/bin/bash

# Docker Entry Point Script untuk Gemini Chatbot CLI
# Supports Linux and Windows containers

set -e

# Colors untuk output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function untuk logging
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] INFO:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR:${NC} $1" >&2
}

warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING:${NC} $1"
}

# Function untuk health check
health_check() {
    log "Performing health check..."
    
    # Check if Java is available
    if ! command -v java &> /dev/null; then
        error "Java not found"
        exit 1
    fi
    
    # Check Java version
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log "Java version: $JAVA_VERSION"
    
    # Check if classes directory exists
    if [ ! -d "/app/classes" ]; then
        error "Application classes not found"
        exit 1
    fi
    
    # Check if lib directory exists
    if [ ! -d "/app/lib" ]; then
        error "Application libraries not found"
        exit 1
    fi
    
    log "Health check passed"
    exit 0
}

# Function untuk setup environment
setup_environment() {
    log "Setting up environment..."
    
    # Check for .env file locations
    ENV_FILE=""
    if [ -f "/app/.env" ]; then
        ENV_FILE="/app/.env"
    elif [ -f "/app/config/.env" ]; then
        ENV_FILE="/app/config/.env"
    elif [ -f "/.env" ]; then
        ENV_FILE="/.env"
    fi
    
    if [ ! -z "$ENV_FILE" ]; then
        log "Loading environment from: $ENV_FILE"
        # Export variables dari .env file
        export $(grep -v '^#' "$ENV_FILE" | xargs)
    else
        warning "No .env file found. Make sure GEMINI_API_KEY is set as environment variable."
    fi
    
    # Validate required environment variables
    if [ -z "$GEMINI_API_KEY" ]; then
        error "GEMINI_API_KEY environment variable is required!"
        error "Please provide it via:"
        error "  1. .env file mounted to /app/.env"
        error "  2. Environment variable: -e GEMINI_API_KEY=your_key"
        error "  3. Docker secrets or config"
        exit 1
    fi
    
    # Set default JAVA_OPTS jika belum diset
    if [ -z "$JAVA_OPTS" ]; then
        export JAVA_OPTS="-Xmx512m -Xms256m"
    fi
    
    log "Environment setup completed"
}

# Function untuk setup locale dan encoding
setup_locale() {
    log "Setting up locale and encoding..."
    
    # Set UTF-8 encoding
    export LANG=C.UTF-8
    export LC_ALL=C.UTF-8
    
    # Java encoding options
    export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
}

# Function untuk cleanup on exit
cleanup() {
    log "Cleaning up..."
    # Add any cleanup logic here
    exit 0
}

# Setup signal handlers
trap cleanup SIGTERM SIGINT

# Main execution
main() {
    log "Starting Gemini Chatbot CLI Container..."
    log "Environment: ${APP_ENV:-development}"
    
    # Handle special commands
    case "${1:-}" in
        --health-check)
            health_check
            ;;
        --version)
            log "Gemini Chatbot CLI v1.0"
            java -version
            exit 0
            ;;
        --help)
            echo "Gemini Chatbot CLI - Docker Container"
            echo ""
            echo "Usage:"
            echo "  docker run -it --env-file .env gemini-chatbot"
            echo ""
            echo "Environment Variables:"
            echo "  GEMINI_API_KEY   - Required: Your Gemini API key"
            echo "  JAVA_OPTS        - Optional: JVM options (default: -Xmx512m -Xms256m)"
            echo "  APP_ENV          - Optional: Environment (development/production)"
            echo ""
            echo "Volume Mounts:"
            echo "  -v ./env:/app/.env               - Mount .env file"
            echo "  -v ./config:/app/config          - Mount config directory"
            echo ""
            echo "Special Commands:"
            echo "  --health-check   - Perform health check"
            echo "  --version        - Show version information"
            echo "  --help           - Show this help"
            exit 0
            ;;
    esac
    
    # Setup environment
    setup_locale
    setup_environment
    
    # Log system information
    log "Container started successfully"
    log "Working directory: $(pwd)"
    log "Java options: $JAVA_OPTS"
    log "Classpath: /app/classes:/app/lib/*"
    
    # Execute the command
    log "Starting application..."
    exec "$@"
}

# Run main function with all arguments
main "$@"
