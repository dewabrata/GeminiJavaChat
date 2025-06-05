# PowerShell script untuk build dan manage Docker images di Windows
# Gemini Chatbot CLI - Docker Build Script

param(
    [Parameter(Position=0)]
    [ValidateSet("production", "development", "test")]
    [string]$Environment = "production",
    
    [Parameter()]
    [string]$Tag = "latest",
    
    [Parameter()]
    [switch]$Push,
    
    [Parameter()]
    [string]$Registry = "",
    
    [Parameter()]
    [switch]$NoCache,
    
    [Parameter()]
    [switch]$Clean,
    
    [Parameter()]
    [switch]$Help
)

# Colors untuk output
$Global:Colors = @{
    Red = [System.ConsoleColor]::Red
    Green = [System.ConsoleColor]::Green
    Yellow = [System.ConsoleColor]::Yellow
    Blue = [System.ConsoleColor]::Blue
    White = [System.ConsoleColor]::White
}

# Functions
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $timestamp = Get-Date -Format "HH:mm:ss"
    
    switch ($Level) {
        "INFO" { 
            Write-Host "[$timestamp] INFO: " -ForegroundColor $Colors.Green -NoNewline
            Write-Host $Message
        }
        "ERROR" { 
            Write-Host "[$timestamp] ERROR: " -ForegroundColor $Colors.Red -NoNewline
            Write-Host $Message
        }
        "WARNING" { 
            Write-Host "[$timestamp] WARNING: " -ForegroundColor $Colors.Yellow -NoNewline
            Write-Host $Message
        }
        "DEBUG" { 
            Write-Host "[$timestamp] DEBUG: " -ForegroundColor $Colors.Blue -NoNewline
            Write-Host $Message
        }
    }
}

function Show-Help {
    Write-Host "Gemini Chatbot CLI - Docker Build Script (Windows PowerShell)" -ForegroundColor $Colors.Blue
    Write-Host ""
    Write-Host "Usage: .\docker-build.ps1 [OPTIONS]" -ForegroundColor $Colors.White
    Write-Host ""
    Write-Host "Parameters:" -ForegroundColor $Colors.White
    Write-Host "  -Environment ENV     Environment (production|development|test) [default: production]" -ForegroundColor $Colors.White
    Write-Host "  -Tag TAG            Docker image tag [default: latest]" -ForegroundColor $Colors.White
    Write-Host "  -Push               Push image to registry after build" -ForegroundColor $Colors.White
    Write-Host "  -Registry REG       Registry URL for push (e.g. docker.io/username)" -ForegroundColor $Colors.White
    Write-Host "  -NoCache            Build without using cache" -ForegroundColor $Colors.White
    Write-Host "  -Clean              Clean up intermediate images" -ForegroundColor $Colors.White
    Write-Host "  -Help               Show this help" -ForegroundColor $Colors.White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor $Colors.Yellow
    Write-Host "  .\docker-build.ps1                                    # Build production image" -ForegroundColor $Colors.White
    Write-Host "  .\docker-build.ps1 -Environment development          # Build development image" -ForegroundColor $Colors.White
    Write-Host "  .\docker-build.ps1 -Environment production -Tag v1.0.0  # Build with custom tag" -ForegroundColor $Colors.White
    Write-Host "  .\docker-build.ps1 -Push -Registry docker.io/myuser     # Build and push to registry" -ForegroundColor $Colors.White
    Write-Host "  .\docker-build.ps1 -Clean                            # Build and clean up" -ForegroundColor $Colors.White
}

# Show help if requested
if ($Help) {
    Show-Help
    exit 0
}

# Main execution
try {
    Write-Log "Starting Docker build process..." "INFO"
    Write-Log "Environment: $Environment" "INFO"
    
    # Set Dockerfile based on environment
    switch ($Environment) {
        "production" {
            $dockerfile = "Dockerfile"
            $imageName = "gemini-chatbot"
        }
        "development" {
            $dockerfile = "Dockerfile.dev"
            $imageName = "gemini-chatbot-dev"
        }
        "test" {
            $dockerfile = "Dockerfile.dev"
            $imageName = "gemini-chatbot-test"
        }
    }
    
    # Build full image name
    $fullImageName = "${imageName}:${Tag}"
    if ($Registry) {
        $fullImageName = "${Registry}/${fullImageName}"
    }
    
    Write-Log "Dockerfile: $dockerfile" "INFO"
    Write-Log "Image name: $fullImageName" "INFO"
    
    # Check if .env file exists
    if (!(Test-Path ".env")) {
        Write-Log ".env file not found. Make sure to create it before running the container." "WARNING"
        if (Test-Path ".env.example") {
            Write-Log "You can copy .env.example to .env as a starting point:" "INFO"
            Write-Log "  Copy-Item .env.example .env" "INFO"
        }
    }
    
    # Prepare build arguments
    $buildArgs = @("-f", $dockerfile, "-t", $fullImageName)
    
    if ($NoCache) {
        $buildArgs += "--no-cache"
    }
    
    $buildArgs += "."
    
    # Build the image
    Write-Log "Building Docker image..." "INFO"
    Write-Log "Command: docker build $($buildArgs -join ' ')" "DEBUG"
    
    $buildResult = & docker build @buildArgs
    
    if ($LASTEXITCODE -eq 0) {
        Write-Log "Build completed successfully!" "INFO"
        Write-Log "Image: $fullImageName" "INFO"
    } else {
        Write-Log "Build failed!" "ERROR"
        exit 1
    }
    
    # Push to registry if requested
    if ($Push) {
        if (-not $Registry) {
            Write-Log "Registry URL is required for push operation" "ERROR"
            exit 1
        }
        
        Write-Log "Pushing image to registry..." "INFO"
        & docker push $fullImageName
        
        if ($LASTEXITCODE -eq 0) {
            Write-Log "Push completed successfully!" "INFO"
        } else {
            Write-Log "Push failed!" "ERROR"
            exit 1
        }
    }
    
    # Clean up if requested
    if ($Clean) {
        Write-Log "Cleaning up intermediate images..." "INFO"
        & docker image prune -f
        Write-Log "Cleanup completed!" "INFO"
    }
    
    # Show image info
    Write-Log "Image information:" "INFO"
    & docker images $imageName --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
    
    Write-Log "Build process completed!" "INFO"
    Write-Host ""
    Write-Log "To run the container:" "INFO"
    
    switch ($Environment) {
        "production" {
            Write-Host "  docker run -it --env-file .env $fullImageName" -ForegroundColor $Colors.Yellow
        }
        "development" {
            Write-Host "  docker-compose --profile development up" -ForegroundColor $Colors.Yellow
        }
        "test" {
            Write-Host "  docker-compose --profile test up" -ForegroundColor $Colors.Yellow
        }
    }
    
} catch {
    Write-Log "An error occurred: $($_.Exception.Message)" "ERROR"
    exit 1
}
