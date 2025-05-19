package com.example.ce316project;

import java.util.Objects;

/**
 * Holds compile/run parameters and test-case definitions.
 */
public class Configuration {
    private String name;
    private String language;
    private ToolSpec tool;
    private ToolType toolType;

    public Configuration(String name, String language, ToolSpec tool, ToolType toolType) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.language = Objects.requireNonNull(language, "Language cannot be null");
        this.tool = Objects.requireNonNull(tool, "Tool cannot be null");
        this.toolType = Objects.requireNonNull(toolType, "Tool type cannot be null");
        validateConfiguration();
    }

    public Configuration(String name, String language, ToolSpec tool) {
        this(name, language, tool, tool.getType());
    }

    public Configuration(String name, String language, String toolType, String executable, String compilerArgs, String location) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.language = Objects.requireNonNull(language, "Language cannot be null");
        this.toolType = ToolType.valueOf(Objects.requireNonNull(toolType, "Tool type cannot be null"));
        this.tool = new ToolSpec(this.toolType,
                               Objects.requireNonNull(executable, "Executable cannot be null"),
                               compilerArgs,
                               Objects.requireNonNull(location, "Location cannot be null"));
        validateConfiguration();
    }

    private void validateConfiguration() {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be empty");
        }
        if (tool.getType() != toolType) {
            throw new IllegalArgumentException("Tool type mismatch between tool and configuration");
        }
    }

    public ToolType getToolType() {
        return toolType;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = Objects.requireNonNull(toolType, "Tool type cannot be null");
        validateConfiguration();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    public ToolSpec getTool() {
        return tool;
    }

    public void setTool(ToolSpec tool) {
        this.tool = Objects.requireNonNull(tool, "Tool cannot be null");
        validateConfiguration();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = Objects.requireNonNull(language, "Language cannot be null");
        if (language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be empty");
        }
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

    public String getRunCall() {
        return tool.getExecutable();
    }

    public String getCompilerArguments() {
        return tool.getCompilerArgs();
    }

    @Override
    public String toString() {
        return name;
    }
}

