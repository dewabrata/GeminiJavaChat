# Gemini Chatbot CLI

Aplikasi chatbot CLI menggunakan Google Gemini Flash dengan tampilan yang menarik dan sistem persona.

## 🚀 Fitur

- 🤖 **Integrasi Gemini Flash**: Menggunakan Google Gemini Flash API
- 🎨 **CLI yang Menarik**: Interface colorful dengan ANSI colors
- 🎭 **Sistem Persona**: 5 persona berbeda (Assistant, Creative, Developer, Teacher, Friend)
- 💬 **Commands**: `/help`, `/clear`, `/persona`, `/list-personas`, `/exit`
- ⚡ **Loading Animation**: Animasi saat menunggu response
- 🔒 **Environment Variables**: API key disimpan dalam environment variable

## 📦 Prerequisites

- Java 17 atau lebih tinggi
- Maven
- Google Gemini API Key

## 🛠️ Setup

### 1. Clone/Download Project
```bash
cd d:/CICD\ day5/javaProgram/testcicd
```

### 2. Get Gemini API Key
1. Kunjungi [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Buat API key baru
3. Salin API key tersebut

### 3. Set Environment Variable
**Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=your_api_key_here
```

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your_api_key_here"
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY=your_api_key_here
```

### 4. Build Project
```bash
mvn clean compile
```

### 5. Run Application

**Option 1: Using Maven Exec Plugin (Recommended)**
```bash
mvn exec:java
```

**Option 2: Using Run Scripts**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

**Option 3: Direct Java Command**
```bash
# Windows
run-java.bat

# Manual Java command
java -cp "target/classes;target/dependency/*" com.juaracoding.cicd.Main
```

## 🎭 Persona yang Tersedia

| Persona | Emoji | Deskripsi |
|---------|-------|-----------|
| assistant | 🤖 | AI assistant yang helpful dan informatif (default) |
| creative | 🎨 | AI kreatif untuk brainstorming dan ide-ide unik |
| developer | 💻 | Senior developer yang fokus pada programming |
| teacher | 📚 | Guru yang sabar dan detail dalam menjelaskan |
| friend | 😊 | Teman yang ramah dan casual |

## 💬 Commands

| Command | Deskripsi |
|---------|-----------|
| `/help` | Tampilkan daftar commands |
| `/clear` | Hapus layar dan reset percakapan |
| `/persona [name]` | Ganti persona (contoh: `/persona creative`) |
| `/list-personas` | Lihat semua persona tersedia |
| `/exit` atau `/quit` | Keluar dari aplikasi |

## 📱 Contoh Penggunaan

```
╔════════════════════════════════════════════════════════════╗
║                    🤖 GEMINI CHATBOT CLI                   ║
║                     Persona: Assistant                     ║
╚════════════════════════════════════════════════════════════╝

✨ Selamat datang di Gemini Chatbot CLI!
💡 Ketik '/help' untuk melihat daftar perintah
🚪 Ketik '/exit' atau '/quit' untuk keluar

💬 You: Halo, bagaimana kabarmu?

🤖 Assistant: Halo! Saya baik-baik saja, terima kasih sudah bertanya. 
              Saya siap membantu Anda hari ini. Ada yang bisa saya 
              bantu?

💬 You: /persona creative

✨ Persona changed to: Creative

💬 You: Berikan ide kreatif untuk aplikasi mobile

🎨 Creative: Wah, seru! Mari brainstorming ide-ide aplikasi mobile 
            yang unik dan kreatif! ...
```

## 🔧 Troubleshooting

### Error: GEMINI_API_KEY tidak ditemukan
Pastikan environment variable `GEMINI_API_KEY` sudah di-set dengan benar.

### Error: Tidak dapat terhubung ke Gemini API
1. Periksa koneksi internet
2. Pastikan API key valid
3. Periksa quota API key di Google AI Studio

### Build Error
Pastikan Java 17 dan Maven sudah ter-install dengan benar:
```bash
java -version
mvn -version
```

## 📝 Dependencies

- **OkHttp 4.12.0**: HTTP client untuk API calls
- **Gson 2.10.1**: JSON serialization/deserialization  
- **Jansi 2.4.1**: ANSI colors untuk CLI styling

## 🏗️ Struktur Project

```
src/main/java/com/juaracoding/cicd/
├── Main.java                 # Entry point
├── ChatBot.java             # Main application logic
├── CLIInterface.java        # Beautiful CLI interface
├── GeminiClient.java        # Gemini API client
├── PersonaManager.java      # Persona management
├── CommandProcessor.java    # Command handling
├── config/
│   └── Config.java         # Environment configuration
└── models/
    ├── ChatMessage.java    # Chat message model
    ├── GeminiRequest.java  # API request model
    └── GeminiResponse.java # API response model
```

## 📄 License

This project is for educational purposes.

## 🤝 Contributing

Feel free to contribute to this project by submitting issues or pull requests.
