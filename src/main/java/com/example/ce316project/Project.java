package com.example.ce316project;

import java.util.ArrayList;
import java.util.List;

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
        this.projectName = projectName;
        this.configurationPath = configurationPath;
        this.submissionsDir = submissionsDir;
        this.resultDir = resultDir;
        this.testCase = testCase;
        this.results = new ArrayList<>();
        this.configurations = new ArrayList<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public String getSubmissionsDir() {
        return submissionsDir;
    }

    public void setSubmissionsDir(String submissionsDir) {
        this.submissionsDir = submissionsDir;
    }

    public String getResultDir() {
        return resultDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public List<StudentResult> getResults() {
        return results;
    }

    public void setResults(List<StudentResult> results) {
        this.results = results;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    static class TestCase {
        private String inputFile;
        private String expectedOutputFile;

        public TestCase(String inputFile, String expectedOutputFile) {
            this.inputFile = inputFile;
            this.expectedOutputFile = expectedOutputFile;
        }

        public String getInputFile() {
            return inputFile;
        }

        public void setInputFile(String inputFile) {
            this.inputFile = inputFile;
        }

        public String getExpectedOutputFile() {
            return expectedOutputFile;
        }

        public void setExpectedOutputFile(String expectedOutputFile) {
            this.expectedOutputFile = expectedOutputFile;
        }
    }
}