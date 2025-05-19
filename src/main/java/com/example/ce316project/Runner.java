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
                    return new StudentResult(studentId, status, errors, log.toString(), "", System.currentTimeMillis());
                }
            }

            // Run the program
            boolean runSuccess = runProgram(submissionDir, actualOutputFile, log);
            if (!runSuccess) {
                errors = "Program execution failed";
                return new StudentResult(studentId, status, errors, log.toString(), "", System.currentTimeMillis());
            }

            // Compare output
            diffOutput = compareOutput(expectedOutputFile, actualOutputFile);
            status = diffOutput.isEmpty() ? "Passed" : "Failed";
            errors = diffOutput.isEmpty() ? "" : "Output mismatch";

        } catch (Exception e) {
            errors = "Error: " + e.getMessage();
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

        return process.waitFor() == 0;
    }

    private boolean runProgram(File dir, File outputFile, StringBuilder log) throws IOException, InterruptedException {
        String runCmd = config.getToolType() == ToolType.COMPILER ?
                "java Main" : config.getRunCmd();

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(dir);
        pb.command(runCmd.split(" "));
        pb.redirectInput(inputFile);
        pb.redirectOutput(outputFile);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String output = captureOutput(process);
        log.append("Execution output:\n").append(output).append("\n");

        return process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS) && process.exitValue() == 0;
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

        List<String> expectedLines = Files.readAllLines(expected.toPath());
        List<String> actualLines = Files.readAllLines(actual.toPath());

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
                diff.append(String.format("Expected: %s\n", expectedLine));
                diff.append(String.format("Actual  : %s\n", actualLine));
            }
        }

        if (!hasDifferences) {
            diff.append("\nFiles match exactly.\n");
            diff.append("Content summary:\n");
            diff.append("First line: ").append(expectedLines.isEmpty() ? "<empty>" : expectedLines.get(0)).append("\n");
            diff.append("Last line: ").append(expectedLines.isEmpty() ? "<empty>" : expectedLines.get(expectedLines.size() - 1)).append("\n");
        }

        return diff.toString();
    }
}