package com.juaracoding.cicd;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Scanner;

public class CLIInterface {
    private Scanner scanner;
    
    public CLIInterface() {
        AnsiConsole.systemInstall();
        this.scanner = new Scanner(System.in);
    }
    
    public void showWelcome() {
        clearScreen();
        printColoredLine("╔════════════════════════════════════════════════════════════╗", Ansi.Color.CYAN);
        printColoredLine("║                    🤖 GEMINI CHATBOT CLI                   ║", Ansi.Color.CYAN);
        printColoredLine("║                     Persona: Assistant                     ║", Ansi.Color.YELLOW);
        printColoredLine("╚════════════════════════════════════════════════════════════╝", Ansi.Color.CYAN);
        System.out.println();
        
        printColoredLine("✨ Selamat datang di Gemini Chatbot CLI!", Ansi.Color.GREEN);
        printColoredLine("💡 Ketik '/help' untuk melihat daftar perintah", Ansi.Color.YELLOW);
        printColoredLine("🚪 Ketik '/exit' atau '/quit' untuk keluar", Ansi.Color.YELLOW);
        System.out.println();
    }
    
    public void showHelp() {
        System.out.println();
        printColoredLine("📋 DAFTAR PERINTAH:", Ansi.Color.CYAN);
        printColoredLine("────────────────────", Ansi.Color.CYAN);
        System.out.println("💬 /help              - Tampilkan bantuan");
        System.out.println("🧹 /clear             - Hapus layar dan reset percakapan");
        System.out.println("🎭 /persona [name]    - Ganti persona sistem");
        System.out.println("📝 /list-personas     - Lihat semua persona tersedia");
        System.out.println("🚪 /exit atau /quit   - Keluar aplikasi");
        System.out.println();
    }
    
    public void showPersonas() {
        System.out.println();
        printColoredLine("🎭 PERSONA TERSEDIA:", Ansi.Color.CYAN);
        printColoredLine("──────────────────────", Ansi.Color.CYAN);
        System.out.println("🤖 assistant   - AI assistant yang membantu (default)");
        System.out.println("🎨 creative    - AI yang kreatif untuk brainstorming");
        System.out.println("💻 developer   - AI yang fokus pada programming");
        System.out.println("📚 teacher     - AI yang menjelaskan dengan detail");
        System.out.println("😊 friend      - AI yang casual dan ramah");
        System.out.println();
    }
    
    public String getUserInput() {
        System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).bold().a("💬 You: ").reset());
        return scanner.nextLine().trim();
    }
    
    public void showBotResponse(String response, String persona) {
        System.out.println();
        String emoji = getPersonaEmoji(persona);
        printWrappedResponse(emoji + " " + persona + ": " + response, Ansi.Color.BLUE);
        System.out.println();
    }
    
    public void showLoading() {
        System.out.print(Ansi.ansi().fg(Ansi.Color.YELLOW).a("⏳ Menunggu response dari Gemini...").reset());
        // Simple loading animation
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println();
    }
    
    public void showError(String error) {
        System.out.println();
        printColoredLine("❌ ERROR: " + error, Ansi.Color.RED);
        System.out.println();
    }
    
    public void showSuccess(String message) {
        printColoredLine("✅ " + message, Ansi.Color.GREEN);
    }
    
    public void showPersonaChanged(String persona) {
        System.out.println();
        printColoredLine("✨ Persona changed to: " + persona, Ansi.Color.MAGENTA);
    }
    
    public void clearScreen() {
        System.out.print(Ansi.ansi().eraseScreen().cursor(1, 1));
    }
    
    public void showGoodbye() {
        System.out.println();
        printColoredLine("👋 Terima kasih telah menggunakan Gemini Chatbot CLI!", Ansi.Color.CYAN);
        printColoredLine("🌟 Sampai jumpa lagi!", Ansi.Color.YELLOW);
        System.out.println();
    }
    
    private void printColoredLine(String text, Ansi.Color color) {
        System.out.println(Ansi.ansi().fg(color).bold().a(text).reset());
    }
    
    private void printWrappedResponse(String text, Ansi.Color color) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineLength = 0;
        int maxWidth = 60;
        
        for (String word : words) {
            if (lineLength + word.length() + 1 > maxWidth && lineLength > 0) {
                System.out.println(Ansi.ansi().fg(color).a(line.toString()).reset());
                line = new StringBuilder("              " + word); // Indentation for continuation
                lineLength = 14 + word.length();
            } else {
                if (line.length() > 0) {
                    line.append(" ");
                    lineLength++;
                }
                line.append(word);
                lineLength += word.length();
            }
        }
        
        if (line.length() > 0) {
            System.out.println(Ansi.ansi().fg(color).a(line.toString()).reset());
        }
    }
    
    private String getPersonaEmoji(String persona) {
        switch (persona.toLowerCase()) {
            case "assistant": return "🤖";
            case "creative": return "🎨";
            case "developer": return "💻";
            case "teacher": return "📚";
            case "friend": return "😊";
            default: return "🤖";
        }
    }
    
    public void close() {
        scanner.close();
        AnsiConsole.systemUninstall();
    }
}
