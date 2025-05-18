package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectIO {
    private static final String CURRENT_SCHEMA_VERSION = "1.0";
    private static final Gson gson = new Gson();

    public static void save(Project project, Path path) throws IOException {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        
        byte[] bytes = gson.toJson(project).getBytes();
        FileUtil.atomicWrite(path, bytes);
    }

    public static Project load(Path path) throws IOException, InvalidFormatException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!Files.exists(path)) {
            throw new IOException("Project file does not exist: " + path);
        }

        String json = Files.readString(path);
        try {
            Project project = gson.fromJson(json, Project.class);
            if (project == null) {
                throw new InvalidFormatException("Project file is empty or invalid");
            }
            if (!CURRENT_SCHEMA_VERSION.equals(project.getSchemaVersion())) {
                throw new InvalidFormatException("Project schema version mismatch. Expected " + 
                    CURRENT_SCHEMA_VERSION + " but got " + project.getSchemaVersion());
            }
            return project;
        } catch (JsonSyntaxException e) {
            throw new InvalidFormatException("Invalid project file format: " + e.getMessage());
        }
    }

    public static class InvalidFormatException extends Exception {
        public InvalidFormatException(String message) {
            super(message);
        }

        public InvalidFormatException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
