#!/bin/bash

echo "========================================"
echo "   GEMINI CHATBOT CLI"
echo "========================================"
echo ""

# Check if GEMINI_API_KEY is set
if [ -z "$GEMINI_API_KEY" ]; then
    echo "[ERROR] GEMINI_API_KEY environment variable is not set!"
    echo ""
    echo "Please set your Gemini API key first:"
    echo "  export GEMINI_API_KEY=your_api_key_here"
    echo ""
    echo "Then run this script again."
    echo ""
    exit 1
fi

echo "[INFO] Starting Gemini Chatbot CLI..."
echo "[INFO] API Key: ${GEMINI_API_KEY:0:8}..."
echo ""

mvn exec:java
