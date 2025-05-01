package com.example.ce316project;


import java.util.List;

public class Project {
    private String projectName;
    private String configurationPath;
    private String submissionsDir;
    private String resultDir;
    private TestCase testCase;  // inputFile & expectedOutputFile
    private List<StudentResult> results;
    private String schemaVersion = "1.0";

    public Project(String projectName, String configurationPath, String submissionsDir, String resultDir, TestCase testCase) {
        this.projectName = projectName;
        this.configurationPath = configurationPath;
        this.submissionsDir = submissionsDir;
        this.testCase = testCase;
        this.resultDir = resultDir;
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

