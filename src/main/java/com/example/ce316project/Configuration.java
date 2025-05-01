package com.example.ce316project;

import java.util.List;

/**
 * Holds compile/run parameters and test-case definitions.
 */
public class Configuration {
    private String name;
    private String language;
    private ToolSpec tool;

    public Configuration(String name, String language, ToolSpec tool) {
        this.name = name;
        this.tool = tool;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToolSpec getTools() {
        return tool;
    }

    public void setTools(ToolSpec tool) {
        this.tool = tool;
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


