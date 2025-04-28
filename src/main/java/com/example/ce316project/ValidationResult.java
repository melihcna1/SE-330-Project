package com.example.ce316project;

/**
 * Result of trying to invoke an external tool.
 */
public class ValidationResult {
    private final boolean available;
    private final String versionOutput;
    private final String errorMessage;
    private final long durationMs;

    public ValidationResult(boolean available, String versionOutput,
                            String errorMessage, long durationMs) {
        this.available = available;
        this.versionOutput = versionOutput;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
    }

    public boolean isAvailable() { return available; }
    public String getVersionOutput() { return versionOutput; }
    public String getErrorMessage() { return errorMessage; }
    public long getDurationMs() { return durationMs; }
}
