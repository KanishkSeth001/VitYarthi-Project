package edu.ccrm.io.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecursiveFileLister {
    
    // Recursive method to list files by depth
    public static List<Path> listFilesByDepth(Path directory, int maxDepth) throws IOException {
        List<Path> result = new ArrayList<>();
        listFilesRecursive(directory, result, 0, maxDepth);
        return result;
    }
    
    private static void listFilesRecursive(Path current, List<Path> result, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            return;
        }
        
        try {
            if (Files.isDirectory(current)) {
                Files.list(current).forEach(path -> {
                    if (Files.isRegularFile(path)) {
                        result.add(path);
                    } else if (Files.isDirectory(path)) {
                        listFilesRecursive(path, result, currentDepth + 1, maxDepth);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + current);
        }
    }
}