package edu.ccrm.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class AppConfig {
    private static AppConfig instance;
    private Path dataDirectory;
    private Path backupDirectory;
    private DateTimeFormatter dateFormatter;
    
    private AppConfig() {
        // Private constructor for singleton
        initializeDefaults();
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void initializeDefaults() {
        this.dataDirectory = Paths.get("data");
        this.backupDirectory = Paths.get("backups");
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    public void loadConfiguration() {
        try {
            // Create directories if they don't exist
            java.nio.file.Files.createDirectories(dataDirectory);
            java.nio.file.Files.createDirectories(backupDirectory);
            System.out.println("Configuration loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }
    
    // Getters
    public Path getDataDirectory() { return dataDirectory; }
    public Path getBackupDirectory() { return backupDirectory; }
    public DateTimeFormatter getDateFormatter() { return dateFormatter; }
}