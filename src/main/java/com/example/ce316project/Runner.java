package com.example.ce316project;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
            results[i] = processSubmission(dir, studentId);
        }

        return results;
    }

    private StudentResult processSubmission(File submissionDir, String studentId) {
        StringBuilder log = new StringBuilder();
        String errors = "";
        String status = "Failed";
        String diffOutput = "";
        File actualOutputFile = new File(submissionDir, "output.txt");

        try {
            // Compile if using compiler
            if (config.getToolType() == ToolType.COMPILER) {
                boolean compileSuccess = compile(submissionDir, log);
                if (!compileSuccess) {
                    errors = "Compilation failed";
                    diffOutput = "Compilation Error:\n" + log.toString();
                    return new StudentResult(studentId, status, errors, log.toString(), diffOutput, System.currentTimeMillis());
                }
            }

            // Run the program
            boolean runSuccess = runProgram(submissionDir, actualOutputFile, log);
            if (!runSuccess) {
                errors = "Program execution failed";
                diffOutput = "Runtime Error:\n" + log.toString();
                return new StudentResult(studentId, status, errors, log.toString(), diffOutput, System.currentTimeMillis());
            }

            // Compare output
            diffOutput = compareOutput(expectedOutputFile, actualOutputFile);
            status = diffOutput.contains("Files match exactly") ? "Passed" : "Failed";
            errors = diffOutput.contains("Files match exactly") ? "" : "Output mismatch";

        } catch (Exception e) {
            errors = "Error: " + e.getMessage();
            diffOutput = "Exception occurred:\n" + e.getMessage();
            e.printStackTrace();
        }

        return new StudentResult(studentId, status, errors, log.toString(), diffOutput, System.currentTimeMillis());
    }

    private boolean compile(File dir, StringBuilder log) throws IOException, InterruptedException {
        if (config.getCompileCmd() == null) return true;

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(dir);
        pb.command(config.getCompileCmd().split(" "));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String output = captureOutput(process);
        log.append("Compilation output:\n").append(output).append("\n");

        return process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS) && process.exitValue() == 0;
    }

    private boolean runProgram(File dir, File outputFile, StringBuilder log) throws IOException, InterruptedException {
        String runCmd = config.getToolType() == ToolType.COMPILER ?
                "java Main" : config.getRunCmd();

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(dir);
        pb.command(runCmd.split(" "));

        // Redirect input from the test input file
        if (inputFile.exists()) {
            pb.redirectInput(inputFile);
        }

        // Create a temporary file for error output
        File errorFile = new File(dir, "error.txt");
        pb.redirectError(errorFile);
        pb.redirectOutput(outputFile);

        Process process = pb.start();
        boolean completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Read error output if any
        if (errorFile.exists()) {
            String errorOutput = Files.readString(errorFile.toPath());
            if (!errorOutput.isEmpty()) {
                log.append("Error output:\n").append(errorOutput).append("\n");
            }
            errorFile.delete();
        }

        if (!completed) {
            process.destroyForcibly();
            log.append("Process timed out after ").append(TIMEOUT_SECONDS).append(" seconds\n");
            return false;
        }

        return process.exitValue() == 0;
    }

    private String captureOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    private String compareOutput(File expected, File actual) throws IOException {
        if (!actual.exists()) {
            return "No output file generated";
        }

        // Read and normalize the lines (trim whitespace and remove empty lines at the end)
        List<String> expectedLines = Files.readAllLines(expected.toPath()).stream()
            .map(String::trim)
            .collect(java.util.stream.Collectors.toList());
        List<String> actualLines = Files.readAllLines(actual.toPath()).stream()
            .map(String::trim)
            .collect(java.util.stream.Collectors.toList());

        // Remove trailing empty lines
        while (!expectedLines.isEmpty() && expectedLines.get(expectedLines.size() - 1).isEmpty()) {
            expectedLines.remove(expectedLines.size() - 1);
        }
        while (!actualLines.isEmpty() && actualLines.get(actualLines.size() - 1).isEmpty()) {
            actualLines.remove(actualLines.size() - 1);
        }

        StringBuilder diff = new StringBuilder();
        int maxLines = Math.max(expectedLines.size(), actualLines.size());

        // Add header information
        diff.append("Comparison Results:\n");
        diff.append("Expected file: ").append(expected.getName()).append("\n");
        diff.append("Actual file: ").append(actual.getName()).append("\n");
        diff.append("Total lines in expected: ").append(expectedLines.size()).append("\n");
        diff.append("Total lines in actual: ").append(actualLines.size()).append("\n\n");

        boolean hasDifferences = false;
        for (int i = 0; i < maxLines; i++) {
            String expectedLine = i < expectedLines.size() ? expectedLines.get(i) : "";
            String actualLine = i < actualLines.size() ? actualLines.get(i) : "";

            if (!expectedLine.equals(actualLine)) {
                hasDifferences = true;
                diff.append(String.format("Line %d:\n", i + 1));
                diff.append(String.format("Expected: '%s'\n", expectedLine));
                diff.append(String.format("Actual  : '%s'\n", actualLine));
            }
        }

        if (!hasDifferences) {
            diff.append("\nFiles match exactly.\n");
            diff.append("Content summary:\n");
            diff.append("First line: '").append(expectedLines.isEmpty() ? "<empty>" : expectedLines.get(0)).append("'\n");
            diff.append("Last line: '").append(expectedLines.isEmpty() ? "<empty>" : expectedLines.get(expectedLines.size() - 1)).append("'\n");
        }

        return diff.toString();
    }
}

