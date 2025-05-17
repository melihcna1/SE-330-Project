package com.example.ce316project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class Runner {
    Configuration c;
    File[] files;
    boolean[] compiled;
    File input;
    File output;
    public Runner(Configuration c,File[] files,File input,File output) {
        this.c = c;
        this.files = files;
        this.input = input;
        this.output = output;
        compiled = new boolean[files.length];
    }
    
    public StudentResult[] run() throws InterruptedException, IOException {
        StudentResult[] results = new StudentResult[files.length];
        Process[] pc = new Process[files.length];
        Process[] pr = new Process[files.length];
        if (!c.getCompileCmd().isEmpty()) {
            ProcessBuilder pbc = new ProcessBuilder(c.getCompileCmd().split(" "));
            for (int i=0;i<files.length;i++) {
                pbc.directory(files[i]);
                pbc.redirectErrorStream();
                pbc.redirectOutput(new File(files[i],"compileLog.txt"));
                try {
                    pc[i] = pbc.start();
                } catch (IOException ex) {}
            }
        }
        ProcessBuilder pbr = new ProcessBuilder(c.getRunCall().split(" "));
        for (int i=0;i<files.length;i++) {
            if (pc[i].waitFor()==0) {
                compiled[i]=true;
                pbr.directory(files[i]);
                pbr.redirectInput(input);
                pbr.redirectOutput(new File(files[i],"runLog.txt"));
                pbr.redirectError(new File(files[i],"runErr.txt"));
                try {
                    pr[i] = pbr.start();
                } catch (IOException ex) {}
            }
        }
        for (int i=0;i<results.length;i++) {
            String err = "";
            String log = "";
            String comErr = "";
            String comOut = "";
            String l;
            if (compiled[i]) {}
            BufferedReader logBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/compileLog.txt"));
            while ((l = logBufferedReader.readLine())!=null) {
                comOut += l;
            }
            if (!compiled[i]) {
                try {
                    pr[i].waitFor();
                } 
                catch (InterruptedException ex) {
                }
                
                logBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/runLog.txt"));
                while ((l = logBufferedReader.readLine())!=null) {
                    log += l;
                }
            }
            results[i] = new StudentResult(files[i].getName(),"",comErr , comOut, "", 31);
            if (!compiled[i]) {
                results[i].setStatus("Compiler fail");
            }
            else {
                results[i].setErrors(err);
                results[i].setLog(log);
                if (Files.mismatch(output.toPath(), new File(files[i].getAbsolutePath()+"/runLog.txt").toPath()) == -1) {
                results[i].setStatus("passed");
                }
                else results[i].setStatus("failed");
            }
            logBufferedReader.close();
        }
        return results;

        
    }

}
