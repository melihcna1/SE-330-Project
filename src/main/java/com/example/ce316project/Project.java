package com.example.ce316project;


import java.util.List;

public class Project {
    private String projectName;
    private String configurationPath;
    private String submissionsDir;
    private List<TestCase> testCases;  // inputFile & expectedOutputFile
    private List<StudentResult> results;
    private String schemaVersion = "1.0";

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

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public List<StudentResult> getResults() {
        return results;
    }

    public void setResults(List<StudentResult> results) {
        this.results = results;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    class TestCase {
        private String inputFile;
        private String expectedOutputFile;

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

