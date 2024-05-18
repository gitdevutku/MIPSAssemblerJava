# MIPS Assembler

## Overview
This project presents a modified version of a MIPS assembler. The assembler is capable of converting MIPS assembly code into machine code. This documentation provides an overview of the code structure, functionalities, and usage.

## Code Structure
The code is structured into a single Java class, `MIPSAssembler`, which contains all the necessary methods and functionalities to assemble MIPS code.

### Methods
- `assemble(String filename)`: Assembles MIPS code from the specified file.
- `initialize()`: Initializes instruction codes and registers.
- `assembleCode()`: Assembles the MIPS code read from the file.
- `parseInstruction(String[] parts)`: Parses MIPS instructions and converts them into machine code.
- `getLabels()`: Extracts labels and their corresponding line numbers from the MIPS code.
- `parse8DigitHex(int dec)`: Converts decimal integers to 8-digit hexadecimal strings.
- `getFile()`: Opens a file browser dialog for selecting a file.
- `main(String[] args)`: Entry point of the program. Prompts the user to select a file and assembles the code.

## Usage
To use the Modified MIPS Assembler:
1. Compile the Java file: `javac MIPSAssembler.java`
2. Run the compiled program: `java MIPSAssembler`
3. Select a MIPS assembly file using the file browser dialog.
4. The assembled code will be displayed in the console.

## Example
```java
public class Main {
    public static void main(String[] args) {
        String filename = ModifiedMIPSAssembler.getFile();
        System.out.println("Assembled code:\n" + ModifiedMIPSAssembler.assemble(filename));
    }
}
```
## Dependencies
- Java Swing library for file browser dialog.
## License 
- This project is licensed under the MIT License.
