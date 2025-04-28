package com.example.ce316project;


import java.util.List;

public class Project {
    private String projectName;
    private String configurationPath;
    private String submissionsDir;
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
}

