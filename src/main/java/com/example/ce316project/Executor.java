package com.example.ce316project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {
    Configuration c;
    File[] files;
    public Executor(Configuration c,File[] files) {
        this.c = c;
        this.files = files;
    }
    private String[] execute(String cmd) {     //runs the operation at addresses with the cmd in configuration and returns all the outputs array. Don't have the time to test it
        String[] output = new String[files.length];
        String s;
        ProcessBuilder pb = new ProcessBuilder(cmd);
        int count=0;
        for (File address:files) {
            output[count]="";
            pb.directory(address);
            pb.redirectErrorStream(true);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s=reader.readLine())!=null) {
                    output[count] += s;
                }
            } catch (IOException ex) {
                output[count] = ex.getMessage();
            }
            count++;
        }
        return output;
    }
    
    public void compile() {
            String[] result = execute(c.getCompileCmd());
    }

    public void run() {
        String[] result = execute(c.getRunCmd());
        for (String s:result) {
            s.compareTo(s);
        }
    }
    
    //------------------------------------------------------- WARNING: Code not done yet, still in progress. -------------------------------------------------------

}
