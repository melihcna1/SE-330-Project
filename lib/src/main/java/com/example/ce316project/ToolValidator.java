package com.example.ce316project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Central service to validate external tools.
 */
public class ToolValidator {
    private static final long TIMEOUT_SECONDS = 5;

    /**
     * Try to run the tool with versionArgs first; if that fails and it's a known fallback case,
     * return fallback=internal flag in errorMessage.
     */
    public ValidationResult validate(ToolSpec spec) {
        long start = System.currentTimeMillis();
        // build command: [executable] + versionArgs
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(spec.getExecutable());
        cmd.addAll(spec.getVersionArgs());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();
            boolean finished = proc.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                proc.destroyForcibly();
                return new ValidationResult(false, null,
                        "Timeout after " + TIMEOUT_SECONDS + "s", elapsed(start));
            }
            int exit = proc.exitValue();
            String output = readStream(proc);
            if (exit == 0) {
                return new ValidationResult(true, output, null, elapsed(start));
            } else {
                return handleFallback(spec, output, elapsed(start));
            }
        } catch (IOException | InterruptedException e) {
            return handleFallback(spec, e.getMessage(), elapsed(start));
        }
    }

    private ValidationResult handleFallback(ToolSpec spec, String message, long duration) {
        // fallback for UNZIP -> built-in java.util.zip
        if (spec.getType() == ToolType.UNZIP) {
            return new ValidationResult(true,
                    "Using built-in ZIP library",
                    "External unzip not found; using internal ZIP",
                    duration);
        }
        // fallback for DIFF -> internal line-by-line
        if (spec.getType() == ToolType.DIFF) {
            return new ValidationResult(true,
                    "Using built-in diff",
                    "External diff not found; using internal comparator",
                    duration);
        }
        // otherwise report unavailable
        return new ValidationResult(false, null,
                "Tool “" + spec.getExecutable() + "” not available: " + message,
                duration);
    }

    private String readStream(Process proc) throws IOException {
        try (BufferedReader rd = new BufferedReader(
                new InputStreamReader(proc.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString().trim();
        }
    }

    private long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }
}
