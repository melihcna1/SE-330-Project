package com.example.ce316project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Project {
    private String projectName;
    private String configurationPath;
    private String submissionsDir;
    private String resultDir;
    private TestCase testCase;
    private List<StudentResult> results;
    private List<Configuration> configurations;
    private String schemaVersion = "1.0";

    public Project(String projectName, String configurationPath, String submissionsDir, String resultDir, TestCase testCase) {
        this.projectName = Objects.requireNonNull(projectName, "Project name cannot be null");
        this.configurationPath = Objects.requireNonNull(configurationPath, "Configuration path cannot be null");
        this.submissionsDir = Objects.requireNonNull(submissionsDir, "Submissions directory cannot be null");
        this.resultDir = Objects.requireNonNull(resultDir, "Result directory cannot be null");
        this.testCase = Objects.requireNonNull(testCase, "Test case cannot be null");
        this.results = new ArrayList<>();
        this.configurations = new ArrayList<>();
        validatePaths();
    }

    private void validatePaths() {
        validateDirectory(submissionsDir, "Submissions directory");
        validateDirectory(resultDir, "Result directory");
        validateFile(configurationPath, "Configuration file");
        validateFile(testCase.getInputFile(), "Input file");
        validateFile(testCase.getExpectedOutputFile(), "Expected output file");
    }

    private void validateDirectory(String path, String name) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(name + " must be a directory: " + path);
        }
    }

    private void validateFile(String path, String name) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(name + " does not exist: " + path);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(name + " must be a file: " + path);
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = Objects.requireNonNull(projectName, "Project name cannot be null");
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = Objects.requireNonNull(configurationPath, "Configuration path cannot be null");
        validateFile(configurationPath, "Configuration file");
    }

    public String getSubmissionsDir() {
        return submissionsDir;
    }

    public void setSubmissionsDir(String submissionsDir) {
        this.submissionsDir = Objects.requireNonNull(submissionsDir, "Submissions directory cannot be null");
        validateDirectory(submissionsDir, "Submissions directory");
    }

    public String getResultDir() {
        return resultDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = Objects.requireNonNull(resultDir, "Result directory cannot be null");
        validateDirectory(resultDir, "Result directory");
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = Objects.requireNonNull(testCase, "Test case cannot be null");
        validateFile(testCase.getInputFile(), "Input file");
        validateFile(testCase.getExpectedOutputFile(), "Expected output file");
    }

    public List<StudentResult> getResults() {
        return new ArrayList<>(results);
    }

    public void setResults(List<StudentResult> results) {
        this.results = new ArrayList<>(Objects.requireNonNull(results, "Results cannot be null"));
    }

    public void addResult(StudentResult result) {
        Objects.requireNonNull(result, "Result cannot be null");
        results.removeIf(r -> r.getStudentId().equals(result.getStudentId()));
        results.add(result);
    }

    public List<Configuration> getConfigurations() {
        return new ArrayList<>(configurations);
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = new ArrayList<>(Objects.requireNonNull(configurations, "Configurations cannot be null"));
    }

    public void addConfiguration(Configuration configuration) {
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        configurations.removeIf(c -> c.getName().equals(configuration.getName()));
        configurations.add(configuration);
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = Objects.requireNonNull(schemaVersion, "Schema version cannot be null");
    }

    public static class TestCase {
        private String inputFile;
        private String expectedOutputFile;

        public TestCase(String inputFile, String expectedOutputFile) {
            this.inputFile = Objects.requireNonNull(inputFile, "Input file cannot be null");
            this.expectedOutputFile = Objects.requireNonNull(expectedOutputFile, "Expected output file cannot be null");
        }

        public String getInputFile() {
            return inputFile;
        }

        public void setInputFile(String inputFile) {
            this.inputFile = Objects.requireNonNull(inputFile, "Input file cannot be null");
        }

        public String getExpectedOutputFile() {
            return expectedOutputFile;
        }

        public void setExpectedOutputFile(String expectedOutputFile) {
            this.expectedOutputFile = Objects.requireNonNull(expectedOutputFile, "Expected output file cannot be null");
        }
    }
}