import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;
            
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
} 