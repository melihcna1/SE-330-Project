package com.example.ce316project;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ToolSpec {
    private final ToolType type;
    private final String executable;
    private final String compilerArgs;
    private final List<String> testArgs;
    private final String location;

    public ToolSpec(ToolType type, String executable, String compilerArgs, String location) {
        this.type = Objects.requireNonNull(type, "Tool type cannot be null");
        this.executable = Objects.requireNonNull(executable, "Executable cannot be null");
        this.compilerArgs = compilerArgs != null ? compilerArgs : "";
        this.testArgs = null;
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        validateToolSpec();
    }

    public ToolSpec(String type, String executable, String compilerArgs, String location) {
        this(ToolType.valueOf(Objects.requireNonNull(type, "Tool type string cannot be null")),
             executable, compilerArgs, location);
    }

    public ToolSpec(ToolType type, String executable, String compilerArgs) {
        this(type, executable, compilerArgs, executable);
    }

    public ToolSpec(ToolType type, String executable, List<String> testArgs) {
        this.type = Objects.requireNonNull(type, "Tool type cannot be null");
        this.executable = Objects.requireNonNull(executable, "Executable cannot be null");
        this.testArgs = testArgs != null ? Collections.unmodifiableList(testArgs) : Collections.emptyList();
        this.compilerArgs = null;
        this.location = executable;
        validateToolSpec();
    }

    private void validateToolSpec() {
        if (executable.trim().isEmpty()) {
            throw new IllegalArgumentException("Executable cannot be empty");
        }
        if (location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }

        // Validate tool type specific requirements
        switch (type) {
            case COMPILER:
                if (compilerArgs == null) {
                    throw new IllegalArgumentException("Compiler arguments required for COMPILER type");
                }
                break;
            case INTERPRETER:
                // No specific requirements
                break;
            case CUSTOM:
                // No specific requirements
                break;
            case UNZIP:
            case DIFF:
                if (testArgs == null) {
                    throw new IllegalArgumentException("Test arguments required for " + type + " type");
                }
                break;
        }
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
        return testArgs != null ? testArgs : Collections.emptyList();
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("ToolSpec[type=%s, executable=%s, location=%s]", type, executable, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolSpec)) return false;
        ToolSpec other = (ToolSpec) o;
        return type == other.type &&
               executable.equals(other.executable) &&
               Objects.equals(compilerArgs, other.compilerArgs) &&
               Objects.equals(testArgs, other.testArgs) &&
               location.equals(other.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, executable, compilerArgs, testArgs, location);
    }
}

