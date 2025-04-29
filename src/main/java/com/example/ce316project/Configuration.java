package com.example.ce316project;

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

    public String getRunCmd() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRunCmd'");
    }

    public String getCompileCmd() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCompileCmd'");
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
