package com.example.ce316project;

import com.example.ce316project.ToolSpec;

import java.util.List;

/**
 * Holds compile/run parameters and test-case definitions.
 */
public class Configuration {
    private String language;
    private List<ToolSpec> tools;
    private List<TestCase> testCases;  // inputFile & expectedOutputFile

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<ToolSpec> getTools() {
        return tools;
    }

    public void setTools(List<ToolSpec> tools) {
        this.tools = tools;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
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
