package com.example.ce316project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Executor {
    Configuration c;
    File[] files;
    Process[] p;
    boolean[] notCompiled;
    public Executor(Configuration c,File[] files) {
        this.c = c;
        this.files = files;
        p = new Process[files.length];
        notCompiled = new boolean[files.length];
    }
    private void execute(List<String> cmd,String log) {    //runs the operation at addresses with the cmd in configuration and returns all the outputs array. Don't have the time to test it
        for (int i=0;i<files.length;i++) {
            if (!notCompiled[i]) {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.directory(files[i]);
                pb.redirectOutput(new File(files[i],log+"Log.txt"));
                pb.redirectError(new File(files[i],log+"Err.txt"));
                try {
                    p[i] = pb.start();
                } catch (IOException ex) {}
            }
        }
    }
    
    public StudentResult[] run(String solution,List<String> args) throws InterruptedException, IOException {
        StudentResult[] results = new StudentResult[files.length];
        ArrayList<String> argst = new ArrayList<String>();
        if (!c.getCompileCmd().isEmpty()) {
            argst.add(c.getCompileCmd());
            execute(argst,"compile");
        }
        for (int i=0;i<p.length;i++) {
            p[i].waitFor();
            if (p[i].exitValue()!=0) {
                notCompiled[i]=true;
            }
        }
        argst.clear();
        argst.add(c.getRunCmd());
        argst.addAll(args);
        execute(argst,"run");
        for (int i=0;i<results.length;i++) {
            String err = "";
            String output = "";
            String comErr = "";
            String comOut = "";
            String l;
            BufferedReader logBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/compileLog.txt"));
            while ((l = logBufferedReader.readLine())!=null) {
                comOut += l;
            }
            BufferedReader errBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/compileErr.txt"));
            while ((l = errBufferedReader.readLine())!=null) {
                comErr += l;
            }
            if (!notCompiled[i]) {
                try {
                    p[i].waitFor();
                } 
                catch (InterruptedException ex) {
                }
                
                logBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/runLog.txt"));
                while ((l = logBufferedReader.readLine())!=null) {
                    output += l;
                }
                errBufferedReader = new BufferedReader(new FileReader(files[i].getAbsolutePath()+"/runErr.txt"));
                while ((l = errBufferedReader.readLine())!=null) {
                    err += l;
                }
            }
            results[i] = new StudentResult(files[i].getName(),"",comErr , comOut, "", 0);
            if (notCompiled[i]) {
                results[i].setStatus("Compile fail");
            }
            else {
                results[i].setErrors(err);
                results[i].setLog(output);
                if (output.equals(solution)) {
                results[i].setStatus("passed");
                }
                else results[i].setStatus("failed");
            }
            errBufferedReader.close();
            logBufferedReader.close();
        }
        return results;

        
    }

}
