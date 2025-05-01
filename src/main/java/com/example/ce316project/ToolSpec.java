package com.example.ce316project;

import java.util.List;

public class ToolSpec {
    private final ToolType type;
    private final String executable;// e.g. "gcc", "unzip", "diff"
    private String compilerArgs;
    private  List<String> testArgs;     // optional lightweight test, e.g. ["-t"] for unzip

    public ToolSpec(ToolType type, String executable, String compilerArgs) {
        this.type = type;
        this.executable = executable;
        this.compilerArgs = compilerArgs;
    }
    public ToolSpec(ToolType type, String executable,
                    List<String> testArgs) {
        this.type = type;
        this.executable = executable;
        this.testArgs = testArgs;
    }


    public ToolType getType() {
        return type;
    }

    public String getExecutable() {
        return executable;
    }

    public List<String> getTestArgs() {
        return testArgs;
    }
}
