package com.example.ce316project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Executor {
    Configuration c;
    File[] files;
    Process[] p;
    public Executor(Configuration c,File[] files) {
        this.c = c;
        this.files = files;
        p = new Process[files.length];
    }
    private void execute(String cmd,String log) {     //runs the operation at addresses with the cmd in configuration and returns all the outputs array. Don't have the time to test it
        ProcessBuilder pb = new ProcessBuilder(cmd);
        for (int i=0;i<files.length;i++) {
            pb.directory(files[i]);
            pb.redirectOutput(new File(files[i],log+"Log.txt"));
            pb.redirectError(new File(files[i],log+"Err.txt"));
            try {
                p[i] = pb.start();
            } catch (IOException ex) {}
        }
    }
    
    public StudentResult[] run(String solution) throws FileNotFoundException, InterruptedException {
        StudentResult[] results = new StudentResult[files.length];
        if (c.getCompileCmd()!=null) {
            execute(c.getCompileCmd(),"compoile");
        }
        for (int i=0;i<p.length;i++) {
            p.wait();
            if (p[i].exitValue()!=0) {

            }
        }
        execute(c.getRunCmd(),"run");
        for (int i=0;i<results.length;i++) {
            String err = "";
            String output = "";
            try {
                p[i].wait();
            } catch (InterruptedException ex) {
            }
            Scanner logScanner = new Scanner(new File(files[i],"runLog.txt"));
            while (logScanner.hasNext()) {
                output += logScanner.nextLine();
            }
            Scanner errScanner = new Scanner(new File(files[i],"runErr.txt"));
            while (errScanner.hasNext()) {
                err += errScanner.nextLine();
            }
            results[i] = new StudentResult(files[i].getName(),"",err , output, "", 10);
            if (solution.isBlank()) {
                solution = output;
            }
            if (output==solution) {
                results[i].setStatus("passed");
            }
            else results[i].setStatus("failed");
        }
        return results;
    }

}
