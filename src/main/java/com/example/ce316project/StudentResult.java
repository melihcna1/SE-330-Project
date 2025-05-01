package com.example.ce316project;

public class StudentResult {
    private String studentId;
    private String status;     // "Passed"/"Failed"
    private String errors;     // any compile/runtime errors
    private String log;        // detailed log of execution
    private String diffOutput; // difference between expected and actual output
    private long timestampMs;

    // Constructor
    public StudentResult(String studentId, String status, String errors, String log, String diffOutput, long timestampMs) {
        this.studentId = studentId;
        this.status = status;
        this.errors = errors;
        this.log = log;
        this.diffOutput = diffOutput;
        this.timestampMs = timestampMs;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrors() {
        return errors != null ? errors : "No errors";
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getLog() {
        return log != null ? log : "No log available";
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getDiffOutput() {
        return diffOutput != null ? diffOutput : "No diff output available";
    }

    public void setDiffOutput(String diffOutput) {
        this.diffOutput = diffOutput;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }
}