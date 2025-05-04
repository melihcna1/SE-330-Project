package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Save/load Configuration to JSON with schemaVersion.
 */
public class ConfigurationIO {
    private static final String SCHEMA_VERSION = "1.0";
    private static final Gson gson = new Gson();

    public static void save(Configuration cfg, Path path) throws IOException {
        // wrap with version
        JsonEnvelope<Configuration> env = new JsonEnvelope<>(SCHEMA_VERSION, cfg);
        byte[] bytes = gson.toJson(env).getBytes();
        FileUtil.atomicWrite(path, bytes);
    }

    public static Configuration load(Path path) throws IOException, InvalidFormatException {
        String json = Files.readString(path);
        JsonEnvelope<?> env;
        try {
            env = gson.fromJson(json, JsonEnvelope.class);
        } catch (JsonSyntaxException e) {
            throw new InvalidFormatException("Invalid JSON", e);
        }
        if (!SCHEMA_VERSION.equals(env.schemaVersion)) {
            throw new InvalidFormatException("Expected schema " + SCHEMA_VERSION +
                    " but got " + env.schemaVersion);
        }
        //noinspection unchecked
        return (Configuration) env.payload;
    }

    private static class JsonEnvelope<T> {
        String schemaVersion;
        T payload;

        JsonEnvelope(String v, T p) {
            schemaVersion = v;
            payload = p;
        }
    }

    public static class InvalidFormatException extends Exception {
        public InvalidFormatException(String msg) {
            super(msg);
        }

        public InvalidFormatException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}