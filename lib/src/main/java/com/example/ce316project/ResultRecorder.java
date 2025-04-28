package com.example.ce316project;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * After each student is processed, record summary in both master and per-student files.
 */
public class ResultRecorder {
    private final Path masterResults;
    private final Path studentResultsDir;
    private final Gson gson = new Gson();
    private Project project;

    public ResultRecorder(Project project, Path masterResults, Path studentResultsDir) {
        this.project = project;
        this.masterResults = masterResults;
        this.studentResultsDir = studentResultsDir;
    }

    /** Call once after each student-run batch to persist updates. */
    public synchronized void recordAll() throws IOException {
        // master
        byte[] masterBytes = gson.toJson(project).getBytes();
        FileUtil.atomicWrite(masterResults, masterBytes);

        // individual
        for (StudentResult res : project.getResults()) {
            Path p = studentResultsDir.resolve(res.getStudentId() + ".json");
            byte[] bytes = gson.toJson(res).getBytes();
            FileUtil.atomicWrite(p, bytes);
        }
    }

    /** Add or update a single student’s result in memory and disk. */
    public synchronized void recordOne(StudentResult result) throws IOException {
        // update in-memory
        List<StudentResult> list = project.getResults();
        list.removeIf(r -> r.getStudentId().equals(result.getStudentId()));
        list.add(result);
        // persist master + that one student
        recordAll();
    }
}
