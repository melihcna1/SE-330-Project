package com.example.ce316project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ToolSpec {
    private final ToolType type;
    private final String executable; // e.g. "gcc", "unzip", "diff"
    private String compilerArgs;
    private List<String> testArgs;
    private String location;// optional lightweight test, e.g. ["-t"] for unzip

    public ToolSpec(ToolType type, String executable, String compilerArgs, String location) {
        this.type = type;
        this.executable = executable;
        this.compilerArgs = compilerArgs != null ? compilerArgs : "";
        this.testArgs = null;
        this.location = location;
    }
    public ToolSpec(String type, String executable, String compilerArgs, String location) {
        this.type = ToolType.valueOf(type);
        this.executable = executable;
        this.compilerArgs = compilerArgs != null ? compilerArgs : "";
        this.testArgs = null;
        this.location = location;
    }


    public ToolSpec(ToolType type, String executable, String compilerArgs) {
        this.type = type;
        this.executable = executable;
        this.compilerArgs = compilerArgs != null ? compilerArgs : "";
        this.testArgs = null;
        this.location = null;
    }

    public ToolSpec(ToolType type, String executable, List<String> testArgs) {
        this.type = type;
        this.executable = executable;
        this.testArgs = testArgs != null ? testArgs : Collections.emptyList();
        this.compilerArgs = null;
    }

    public ToolType getType() {
        return type;
    }

    public String getExecutable() {
        return executable;
    }

    public String getCompilerArgs() {
        return compilerArgs;
    }

    public List<String> getTestArgs() {
        return testArgs;
    }

    public String getLocation() {
        return location;
    }
}