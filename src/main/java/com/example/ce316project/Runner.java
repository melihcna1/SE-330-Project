package com.example.ce316project;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.*;

public class Runner {
    private final Configuration config;
    private final File[] submissionDirs;
    private final File inputFile;
    private final File expectedOutputFile;
    private static final int TIMEOUT_SECONDS = 30;

    public Runner(Configuration config, File[] submissionDirs, File inputFile, File expectedOutputFile) {
        this.config = config;
        this.submissionDirs = submissionDirs;
        this.inputFile = inputFile;
        this.expectedOutputFile = expectedOutputFile;
    }

    public StudentResult[] run() {
        StudentResult[] results = new StudentResult[submissionDirs.length];

        for (int i = 0; i < submissionDirs.length; i++) {
            File dir = submissionDirs[i];
            String studentId = dir.getName();

            try {
                ProcessBuilder pb = createProcessBuilder(dir);
                results[i] = runSubmission(studentId, pb);
            } catch (Exception e) {
                results[i] = new StudentResult(
                        studentId,
                        "Failed",
                        e.getMessage(),
                        "Error running submission",
                        "",
                        System.currentTimeMillis()
                );
            }
        }

        return results;
    }

    private ProcessBuilder createProcessBuilder(File workDir) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(workDir);

        if (config.getTools().getType() == ToolType.COMPILER) {
            pb.command(Arrays.asList(
                    config.getTools().getLocation(),
                    config.getCompilerArguments(),
                    "*.java" // Or other source files based on language
            ));
        } else {
            pb.command(Arrays.asList(
                    config.getTools().getLocation(),
                    config.getRunCall()
            ));
        }

        pb.redirectErrorStream(true);
        return pb;
    }

    // In Runner.java
    private StudentResult runSubmission(String studentId, ProcessBuilder pb) {
        try {
            Process process = pb.start();

            // Handle input file
            if (inputFile != null && inputFile.exists()) {
                try (OutputStream out = process.getOutputStream()) {
                    Files.copy(inputFile.toPath(), out);
                }
            }

            // Collect output with timeout
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                return output.toString();
            });

            String output;
            try {
                output = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                process.destroyForcibly();
                return new StudentResult(
                        studentId,
                        "Failed",
                        "Execution timed out after " + TIMEOUT_SECONDS + " seconds",
                        "",
                        "",
                        System.currentTimeMillis()
                );
            } finally {
                executor.shutdownNow();
            }

            int exitCode = process.waitFor();

            // Compare with expected output if execution succeeded
            String diffOutput = "";
            if (exitCode == 0 && expectedOutputFile != null && expectedOutputFile.exists()) {
                String expectedOutput = Files.readString(expectedOutputFile.toPath());
                diffOutput = compareOutputs(output, expectedOutput);

                // Update status based on output comparison
                String status = diffOutput.startsWith("Outputs match") ? "Passed" : "Failed";
                return new StudentResult(
                        studentId,
                        status,
                        "",
                        output,
                        diffOutput,
                        System.currentTimeMillis()
                );
            }

            // If execution failed
            return new StudentResult(
                    studentId,
                    "Failed",
                    "Process exited with code " + exitCode,
                    output,
                    "",
                    System.currentTimeMillis()
            );

        } catch (Exception e) {
            return new StudentResult(
                    studentId,
                    "Failed",
                    e.getMessage(),
                    "",
                    "",
                    System.currentTimeMillis()
            );
        }
    }

    private String compareOutputs(String actual, String expected) {
        if (actual.equals(expected)) {
            return "Outputs match exactly";
        }
        return "Expected:\n" + expected + "\nActual:\n" + actual;
    }
}