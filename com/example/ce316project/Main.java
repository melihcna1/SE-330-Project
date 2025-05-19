package com.example.ce316project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        try {
            // Create reader for standard input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // Get standard output stream
            PrintStream out = System.out;
            
            // Read and echo each line
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        } catch (Exception e) {
            // Handle any errors gracefully
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
} 