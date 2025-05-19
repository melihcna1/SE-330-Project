package com.example.ce316project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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

        if (!dir.exists() || !dir.isDirectory()) {
            String error = "Submissions directory does not exist: " + submissionsDir;
            results.add(new StudentResult("SYSTEM", "Failed", error, error, "", System.currentTimeMillis()));
            return results;
        }

        File[] zipFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".zip"));

        if (zipFiles == null || zipFiles.length == 0) {
            String error = "No ZIP files found in directory: " + submissionsDir;
            results.add(new StudentResult("SYSTEM", "Failed", error, error, "", System.currentTimeMillis()));
            return results;
        }

        for (File zipFile : zipFiles) {
            String studentId = getStudentIdFromFilename(zipFile.getName());
            StringBuilder log = new StringBuilder();
            try {
                // Check file size
                if (zipFile.length() > 50 * 1024 * 1024) { // 50MB limit
                    throw new IOException("ZIP file too large (max 50MB): " + zipFile.getName());
                }

                // Validate ZIP file first
                if (!isValidZipFile(zipFile)) {
                    throw new IOException("Invalid or corrupted ZIP file: " + zipFile.getName());
                }

                // Check for zip slip vulnerability
                try (ZipFile zip = new ZipFile(zipFile)) {
                    if (zip.stream().anyMatch(entry -> entry.getName().contains(".."))) {
                        throw new IOException("Security violation: ZIP entry contains path traversal");
                    }
                }

                log.append("Starting extraction...\n");
                String extractPath = extractZipFile(zipFile, studentId);
                log.append("Extracted to: ").append(extractPath).append("\n");
                processedFiles.add(zipFile.getName());

                // Create successful result after extraction
                results.add(new StudentResult(
                    studentId,
                    "Ready",
                    "",
                    log.toString(),
                    "",
                    System.currentTimeMillis()
                ));

            } catch (IOException e) {
                log.append("Error: ").append(e.getMessage()).append("\n");
                failedFiles.add(zipFile.getName());
                results.add(new StudentResult(
                    studentId,
                    "Failed",
                    "Error processing submission: " + e.getMessage(),
                    log.toString(),
                    "",
                    System.currentTimeMillis()
                ));
            }
        }

        return results;
    }

    /**
     * Checks if a ZIP file is valid.
     *
     * @param file The ZIP file to check
     * @return True if the ZIP file is valid, false otherwise
     */
    private boolean isValidZipFile(File file) {
        try (ZipFile zipFile = new ZipFile(file, StandardCharsets.UTF_8)) {
            // Check for at least one entry and verify CRC
            return zipFile.stream().findFirst().isPresent();
        } catch (IOException e) {
            return false;
        }
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

        // Clean up existing directory
        if (destDirFile.exists()) {
            Files.walk(destDirFile.toPath())
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete: " + path);
                    }
                });
        }
        destDirFile.mkdirs();

        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            zip.stream().forEach(entry -> {
                try {
                    File newFile = newFile(destDirFile, entry);
                    if (entry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        // Create parent directories
                        new File(newFile.getParent()).mkdirs();

                        // Extract file with a size limit check
                        if (entry.getSize() > 10 * 1024 * 1024) { // 10MB per file limit
                            throw new IOException("ZIP entry too large (max 10MB): " + entry.getName());
                        }

                        // Extract file
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            zip.getInputStream(entry).transferTo(fos);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error extracting " + entry.getName(), e);
                }
            });
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

