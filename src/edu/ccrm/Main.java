package edu.ccrm;

import edu.ccrm.cli.CLIMenu;
import edu.ccrm.config.AppConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Campus Course & Records Manager (CCRM) ===");
        
        // Singleton pattern demonstration
        AppConfig config = AppConfig.getInstance();
        config.loadConfiguration();
        
        CLIMenu menu = new CLIMenu();
        menu.start();
    }
}