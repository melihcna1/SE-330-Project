package com.example.ce316project;

/**
 * Holds compile/run parameters and test-case definitions.
 */
public class Configuration {
    private String name;
    private String language;
    private ToolSpec tool;
    private ToolType toolType;
    private ToolType toolType;

    public Configuration(String name, String language, ToolSpec tool, ToolType toolType) {
        this.name = name;
        this.language = language;
        this.tool = tool;
        this.toolType = toolType;
    }
    public Configuration(String name, String language, ToolSpec tool) {
        this.name = name;
        this.language = language;
        this.tool = tool;
    }
    public Configuration(String name, String language, String toolType, String executable,String compilerArgs,String location){
        this.name = name;
        this.language=language;
        this.toolType=ToolType.valueOf(toolType);
        this.tool=new ToolSpec(ToolType.valueOf(toolType),executable,compilerArgs,location);
    }


    public ToolType getToolType() {
        return toolType;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCompileCmd() {
        if (toolType == ToolType.COMPILER && tool != null) {
            return tool.getExecutable() + (tool.getCompilerArgs() != null ? " " + tool.getCompilerArgs() : "");
        }
        return null;
    }

    public String getRunCmd() {
        if ((toolType == ToolType.INTERPRETER || toolType == ToolType.CUSTOM) && tool != null) {
            return tool.getExecutable() + (tool.getTestArgs() != null ? " " + String.join(" ", tool.getTestArgs()) : "");
        }
        return null;
    }

    public String getRunCall(){
        return tool.getExecutable();
    }
    public String getCompilerArguments(){
        return tool.getCompilerArgs();
    }



    @Override
    public String toString() {
        return name;
    }
}