package com.example.ce316project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Processes ZIP files for student submissions.
 * Supports batch processing of multiple ZIP files in a directory.
 */
public class ZipProcessor {
    private final String submissionsDir;
    private final String extractDir;
    private final List<String> processedFiles = new ArrayList<>();
    private final List<String> failedFiles = new ArrayList<>();

    /**
     * Creates a new ZipProcessor.
     * 
     * @param submissionsDir Directory containing student ZIP submissions
     * @param extractDir Directory where ZIP files will be extracted
     */
    public ZipProcessor(String submissionsDir, String extractDir) {
        this.submissionsDir = submissionsDir;
        this.extractDir = extractDir;
        
        // Create extract directory if it doesn't exist
        File extractDirFile = new File(extractDir);
        if (!extractDirFile.exists()) {
            extractDirFile.mkdirs();
        }
    }

    /**
     * Processes all ZIP files in the submissions directory.
     * 
     * @return List of StudentResult objects for processed submissions
     */
    public List<StudentResult> processAllZipFiles() {
        List<StudentResult> results = new ArrayList<>();
        File dir = new File(submissionsDir);
        File[] zipFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".zip"));
        
        if (zipFiles == null || zipFiles.length == 0) {
            return results;
        }
        
        for (File zipFile : zipFiles) {
            String studentId = getStudentIdFromFilename(zipFile.getName());
            try {
                String extractPath = extractZipFile(zipFile, studentId);
                processedFiles.add(zipFile.getName());
                
                // Create a StudentResult with initial "Processing" status
                StudentResult result = new StudentResult(
                    studentId, 
                    "Processing", 
                    "", 
                    "Extracted to " + extractPath,
                    "",
                    System.currentTimeMillis()
                );
                results.add(result);
            } catch (IOException e) {
                failedFiles.add(zipFile.getName());
                StudentResult result = new StudentResult(
                    studentId,
                    "Failed",
                    "Error extracting ZIP: " + e.getMessage(),
                    "",
                    "",
                    System.currentTimeMillis()
                );
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Extracts a ZIP file to the extract directory.
     * 
     * @param zipFile The ZIP file to extract
     * @param studentId The student ID for creating a specific folder
     * @return Path to the extracted directory
     * @throws IOException If extraction fails
     */
    private String extractZipFile(File zipFile, String studentId) throws IOException {
        String destinationDir = Paths.get(extractDir, studentId).toString();
        File destDirFile = new File(destinationDir);
        if (destDirFile.exists()) {
            // Clean up existing directory
            Files.walk(destDirFile.toPath())
                .sorted((a, b) -> -a.compareTo(b)) // Sort in reverse to delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Log but continue
                        System.err.println("Failed to delete: " + path);
                    }
                });
        }
        destDirFile.mkdirs();
        
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDirFile, zipEntry);
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Create parent directories if they don't exist
                    new File(newFile.getParent()).mkdirs();
                    
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        
        return destinationDir;
    }
    
    /**
     * Creates a new file in the extract directory, resolving security issues with file paths.
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        
        return destFile;
    }
    
    /**
     * Extracts student ID from a filename. 
     * Assumes filename format is typically studentID_assignment.zip or similar.
     */
    private String getStudentIdFromFilename(String filename) {
        // Remove .zip extension
        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
        
        // If contains underscore, take everything before the first underscore as student ID
        if (nameWithoutExt.contains("_")) {
            return nameWithoutExt.substring(0, nameWithoutExt.indexOf('_'));
        }
        
        // Otherwise return the whole name without extension
        return nameWithoutExt;
    }
    
    /**
     * Returns a list of successfully processed files.
     */
    public List<String> getProcessedFiles() {
        return processedFiles;
    }
    
    /**
     * Returns a list of files that failed to process.
     */
    public List<String> getFailedFiles() {
        return failedFiles;
    }
} 