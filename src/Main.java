import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String filename;
        System.out.print("Enter name of file to assemble: ");
        filename = sc.next();
        String filename2 = "output.obj"; // Name of the output file
        // Content to be written to the file
        String content = MIPSAssembler.assembleFile(filename);
        // Try-with-resources to automatically close the BufferedWriter>
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
            // Write the content to the file
            writer.write(content);
            System.out.println("File '" + filename + "' created successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        sc.close();
    }
}