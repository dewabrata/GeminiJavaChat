#!/bin/bash

# Script untuk build dan manage Docker images
# Gemini Chatbot CLI - Docker Build Script

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Functions
log() {
    echo -e "${GREEN}[$(date +'%H:%M:%S')] INFO:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%H:%M:%S')] ERROR:${NC} $1" >&2
}

warning() {
    echo -e "${YELLOW}[$(date +'%H:%M:%S')] WARNING:${NC} $1"
}

# Default values
ENVIRONMENT="production"
BUILD_ARGS=""
PUSH_TO_REGISTRY=false
REGISTRY=""
TAG="latest"

# Help function
show_help() {
    echo "Gemini Chatbot CLI - Docker Build Script"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENV        Environment (production|development|test) [default: production]"
    echo "  -t, --tag TAG        Docker image tag [default: latest]"
    echo "  -p, --push           Push image to registry after build"
    echo "  -r, --registry REG   Registry URL for push (e.g. docker.io/username)"
    echo "  --no-cache           Build without using cache"
    echo "  --clean              Clean up intermediate images"
    echo "  -h, --help           Show this help"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Build production image"
    echo "  $0 -e development                    # Build development image"
    echo "  $0 -e production -t v1.0.0          # Build with custom tag"
    echo "  $0 -p -r docker.io/myuser           # Build and push to registry"
    echo "  $0 --clean                          # Build and clean up"
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -t|--tag)
            TAG="$2"
            shift 2
            ;;
        -p|--push)
            PUSH_TO_REGISTRY=true
            shift
            ;;
        -r|--registry)
            REGISTRY="$2"
            shift 2
            ;;
        --no-cache)
            BUILD_ARGS="$BUILD_ARGS --no-cache"
            shift
            ;;
        --clean)
            CLEAN_UP=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Validate environment
case $ENVIRONMENT in
    production|development|test)
        ;;
    *)
        error "Invalid environment: $ENVIRONMENT"
        error "Valid environments: production, development, test"
        exit 1
        ;;
esac

# Set Dockerfile based on environment
case $ENVIRONMENT in
    production)
        DOCKERFILE="Dockerfile"
        IMAGE_NAME="gemini-chatbot"
        ;;
    development)
        DOCKERFILE="Dockerfile.dev"
        IMAGE_NAME="gemini-chatbot-dev"
        ;;
    test)
        DOCKERFILE="Dockerfile.dev"
        IMAGE_NAME="gemini-chatbot-test"
        ;;
esac

# Build image name with tag
FULL_IMAGE_NAME="$IMAGE_NAME:$TAG"
if [ ! -z "$REGISTRY" ]; then
    FULL_IMAGE_NAME="$REGISTRY/$FULL_IMAGE_NAME"
fi

log "Starting Docker build process..."
log "Environment: $ENVIRONMENT"
log "Dockerfile: $DOCKERFILE"
log "Image name: $FULL_IMAGE_NAME"

# Check if .env file exists
if [ ! -f ".env" ]; then
    warning ".env file not found. Make sure to create it before running the container."
    if [ -f ".env.example" ]; then
        log "You can copy .env.example to .env as a starting point:"
        log "  cp .env.example .env"
    fi
fi

# Build the image
log "Building Docker image..."
docker build \
    -f "$DOCKERFILE" \
    -t "$FULL_IMAGE_NAME" \
    $BUILD_ARGS \
    .

if [ $? -eq 0 ]; then
    log "Build completed successfully!"
    log "Image: $FULL_IMAGE_NAME"
else
    error "Build failed!"
    exit 1
fi

# Push to registry if requested
if [ "$PUSH_TO_REGISTRY" = true ]; then
    if [ -z "$REGISTRY" ]; then
        error "Registry URL is required for push operation"
        exit 1
    fi
    
    log "Pushing image to registry..."
    docker push "$FULL_IMAGE_NAME"
    
    if [ $? -eq 0 ]; then
        log "Push completed successfully!"
    else
        error "Push failed!"
        exit 1
    fi
fi

# Clean up if requested
if [ "$CLEAN_UP" = true ]; then
    log "Cleaning up intermediate images..."
    docker image prune -f
    log "Cleanup completed!"
fi

# Show image info
log "Image information:"
docker images "$IMAGE_NAME" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"

log "Build process completed!"
log ""
log "To run the container:"
case $ENVIRONMENT in
    production)
        log "  docker run -it --env-file .env $FULL_IMAGE_NAME"
        ;;
    development)
        log "  docker-compose --profile development up"
        ;;
    test)
        log "  docker-compose --profile test up"
        ;;
esac
