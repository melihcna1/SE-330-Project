package com.example.ce316project;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectIO {
    private static final Gson gson = new Gson();

    public static void save(Project project, Path path) throws IOException {
        byte[] bytes = gson.toJson(project).getBytes();
        FileUtil.atomicWrite(path, bytes);
    }

    public static Project load(Path path) throws IOException {
        String json = Files.readString(path);
        try {
            Project p = gson.fromJson(json, Project.class);
            if (!"1.0".equals(p.getSchemaVersion())) {
            }
            return p;
        } catch (com.google.gson.JsonSyntaxException e) {
        }
        return null;
    }
}
