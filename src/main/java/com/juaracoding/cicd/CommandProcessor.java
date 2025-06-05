package com.juaracoding.cicd;

public class CommandProcessor {
    private PersonaManager personaManager;
    private CLIInterface cli;
    
    public CommandProcessor(PersonaManager personaManager, CLIInterface cli) {
        this.personaManager = personaManager;
        this.cli = cli;
    }
    
    public boolean processCommand(String input) {
        if (!input.startsWith("/")) {
            return false; // Not a command
        }
        
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";
        
        switch (command) {
            case "/help":
                cli.showHelp();
                return true;
                
            case "/clear":
                cli.clearScreen();
                cli.showWelcome();
                return true;
                
            case "/persona":
                return handlePersonaCommand(argument);
                
            case "/list-personas":
                cli.showPersonas();
                return true;
                
            case "/exit":
            case "/quit":
                return false; // Signal to exit
                
            default:
                cli.showError("Perintah tidak dikenal: " + command + 
                            ". Ketik '/help' untuk melihat daftar perintah.");
                return true;
        }
    }
    
    private boolean handlePersonaCommand(String personaName) {
        if (personaName.isEmpty()) {
            cli.showError("Silakan specify nama persona. Contoh: /persona creative");
            cli.showPersonas();
            return true;
        }
        
        if (personaManager.setPersona(personaName)) {
            cli.showPersonaChanged(personaManager.getCurrentPersonaCapitalized());
            return true;
        } else {
            cli.showError("Persona '" + personaName + "' tidak ditemukan.");
            cli.showPersonas();
            return true;
        }
    }
    
    public boolean isExitCommand(String input) {
        return input.equalsIgnoreCase("/exit") || input.equalsIgnoreCase("/quit");
    }
}
