import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String filename;
        filename = MIPSAssembler.getFile();
        String filename2 = "output.obj";
        String content = MIPSAssembler.assemble(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
            writer.write(content);
            System.out.println("File '" + filename + "' created successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        sc.close();
    }
}