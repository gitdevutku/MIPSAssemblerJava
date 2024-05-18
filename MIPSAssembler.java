import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.util.Scanner;
import java.util.HashMap;

public class MIPSAssembler {

    private static File file;
    private static int lineNumber = 0;
    private static final HashMap<String, String> instructionCodes = new HashMap<>();
    private static final HashMap<String, instructionParser> instructions = new HashMap<>();
    private static final HashMap<String, String> registers = new HashMap<>();
    private static final HashMap<String, Integer> labels = new HashMap<>();

    public static String assembleFile(String filename) {
        initInstructionCodes();
        initInstructions();
        initRegisterCodes();
        file = new File(filename);
        getLabels();
        return assemble();
    }

    private static void initInstructionCodes() {
        // Initialization code for instruction codes
        instructionCodes.put("add", "100000");
        instructionCodes.put("sub", "100010");
        instructionCodes.put("and", "100100");
        instructionCodes.put("or", "100101");
        instructionCodes.put("nor", "100111");
        instructionCodes.put("slt", "101010");
        instructionCodes.put("sll", "000000");
        instructionCodes.put("srl", "000010");
        instructionCodes.put("sllv", "000100");
        instructionCodes.put("srlv", "000110");
        instructionCodes.put("addi", "001000");
        instructionCodes.put("andi", "001100");
        instructionCodes.put("ori", "001101");
        instructionCodes.put("beq", "000100");
        instructionCodes.put("bne", "000101");
        instructionCodes.put("blez", "000110");
        instructionCodes.put("bgtz", "000111");
        instructionCodes.put("lw", "100011");
        instructionCodes.put("sw", "101011");
        instructionCodes.put("j", "000010");
    }

    private static void initInstructions() {
        // Initialization code for instructions
        instructions.put("add", instructionR_std);
        instructions.put("sub", instructionR_std);
        instructions.put("and", instructionR_std);
        instructions.put("or", instructionR_std);
        instructions.put("nor", instructionR_std);
        instructions.put("slt", instructionR_std);
        instructions.put("sll", instructionR_shift);
        instructions.put("srl", instructionR_shift);
        instructions.put("sllv", instructionR_shift_var);
        instructions.put("srlv", instructionR_shift_var);

        instructions.put("addi", instructionI_std);
        instructions.put("andi", instructionI_std);
        instructions.put("ori", instructionI_std);
        instructions.put("beq", instructionI_branch);
        instructions.put("bne", instructionI_branch);
        instructions.put("blez", instructionI_branch_zero);
        instructions.put("bgtz", instructionI_branch_zero);
        instructions.put("lw", instructionI_word);
        instructions.put("sw", instructionI_word);
        instructions.put("j", instructionJ);
    }

    private static void initRegisterCodes() {
        for (int i = 0; i <= 31; i++) {
            String regName = "$" + i;
            String binaryCode = String.format("%5s", Integer.toBinaryString(i)).replace(' ', '0');
            registers.put(regName, binaryCode);
        }
        registers.put("$zero", "00000");

        registers.put("$v0", "00010");
        registers.put("$v1", "00011");

        registers.put("$a0", "00100");
        registers.put("$a1", "00101");
        registers.put("$a2", "00110");
        registers.put("$a3", "00111");

        registers.put("$t0", "01000");
        registers.put("$t1", "01001");
        registers.put("$t2", "01010");
        registers.put("$t3", "01011");
        registers.put("$t4", "01100");
        registers.put("$t5", "01101");
        registers.put("$t6", "01110");
        registers.put("$t7", "01111");
        registers.put("$t8", "11000");
        registers.put("$t9", "11001");

        registers.put("$s0", "10000");
        registers.put("$s1", "10001");
        registers.put("$s2", "10010");
        registers.put("$s3", "10011");
        registers.put("$s4", "10100");
        registers.put("$s5", "10101");
        registers.put("$s6", "10110");
        registers.put("$s7", "10111");
    }

    private interface instructionParser {
        String parse(String[] parts);
    }

    private static final instructionParser instructionR_std = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = "000000";
            String rs = getRegister(parts[2]);
            String rt = getRegister(parts[3]);
            String rd = getRegister(parts[1]);
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);

            String binaryInstruction = opcode + rs + rt + rd + shamt + funct;
            String hexInstruction = Integer.toHexString(Integer.parseInt(binaryInstruction, 2));
            int count0 = 8 - hexInstruction.length();
            String zero = "0";
            zero = zero.repeat(count0);
            return "0x" + zero + hexInstruction;
        }
    };

    private static final instructionParser instructionR_shift = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = "000000";
            String rs = "00000";
            String rt = getRegister(parts[2]);
            String rd = getRegister(parts[1]);
            String shamt = parseUnsigned5BitBin(Integer.parseInt(parts[3]));
            String funct = instructionCodes.get(parts[0]);

            String binaryInstruction = opcode + rs + rt + rd + shamt + funct;
            String hexInstruction = Integer.toHexString(Integer.parseInt(binaryInstruction, 2));
            int count0 = 8 - hexInstruction.length();
            String zero = "0";
            zero = zero.repeat(count0);
            return "0x" + zero + hexInstruction;
        }
    };
    private static final instructionParser instructionR_shift_var = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = "000000";
            String rs = getRegister(parts[3]);
            String rt = getRegister(parts[2]);
            String rd = getRegister(parts[1]);
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);
            String binaryInstruction = opcode + rs + rt + rd + shamt + funct;
            String hexInstruction = Long.toHexString(Long.parseLong(binaryInstruction, 2));
            int count0 = 8 - hexInstruction.length();
            String zero = "0";
            zero = zero.repeat(count0);
            return "0x" + zero + hexInstruction;
        }
    };


    private static final instructionParser instructionI_std = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[2]);
            String rt = getRegister(parts[1]);
            String immediate = parseSigned16BitBin(Integer.parseInt(parts[3]));

            String binaryInstruction = opcode + rs + rt + immediate;
            String hexInstruction = Long.toHexString(Long.parseLong(binaryInstruction, 2));
            if (hexInstruction.length() != 10) {
                int count0 = 8 - hexInstruction.length();
                String zero = "0";
                zero = zero.repeat(count0);
                return "0x" + zero + hexInstruction;
            }
            return "0x" + hexInstruction;
        }
    };
    private static final instructionParser instructionI_branch_zero = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[1]);
            String rt = "00000"; // Unused for these instructions
            Integer labelLine = labels.get(parts[2]);

            if (labelLine != null) {
                String immediate = parseSigned16BitBin(labelLine - lineNumber);
                String binaryInstruction = opcode + rs + rt + immediate;
                String hexInstruction = Long.toHexString(Long.parseLong(binaryInstruction, 2));
                if (hexInstruction.length() != 8) {
                    int count0 = 8 - hexInstruction.length();
                    String zero = "0".repeat(count0);
                    return "0x" + zero + hexInstruction;
                }
                return "0x" + hexInstruction;
            } else {
                System.err.println("Error: Label '" + parts[2] + "' not found.");
            }
            return "ERROR";
        }
    };
    private static final instructionParser instructionI_branch = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[1]);
            String rt = getRegister(parts[2]);
            Integer labelLine = labels.get(parts[3]);
            if (labelLine != null) {
                String immediate = parseSigned16BitBin(labelLine - lineNumber);
                String binaryInstruction = opcode + rs + rt + immediate;
                String hexInstruction = Long.toHexString(Long.parseLong(binaryInstruction, 2));
                if (hexInstruction.length() != 10) {
                    int count0 = 8 - hexInstruction.length();
                    String zero = "0";
                    zero = zero.repeat(count0);
                    return "0x" + zero + hexInstruction;
                }
                return ("0x" + hexInstruction);
            } else {
                System.err.println("Error: Label '" + parts[3] + "' not found.");
            }
            return "ERROR";
        }
    };


    private static final instructionParser instructionI_word = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[3]);
            String rt = getRegister(parts[1]);

            // Extract the immediate value from the instruction
            String immediateString = parts[2];
            if (immediateString.startsWith("$")) {
                // If the immediate value is a register, get its binary representation
                immediateString = getRegister(immediateString);
            } else {
                // If the immediate value is a decimal, parse it and convert to binary
                int immediateDecimal = Integer.parseInt(immediateString);
                immediateString = parseSigned16BitBin(immediateDecimal);
            }


            // Combine opcode, rs, rt, and immediate fields to form the binary instruction
            String binaryInstruction = opcode + rs + rt + immediateString;


            // Convert the binary instruction to hexadecimal
            long binaryInt = Long.parseLong(binaryInstruction, 2);
            String hexInstruction = Long.toHexString(binaryInt);
            if (hexInstruction.length() != 10) {
                int count0 = 8 - hexInstruction.length();
                String zero = "0";
                zero = zero.repeat(count0);
                return "0x" + zero + hexInstruction;
            }
            return ("0x" + hexInstruction);
        }
    };

    private static final instructionParser instructionJ = new instructionParser() {
        public String parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            int fullAddress = 0x00400000 + 4 * labels.get(parts[1]);
            String address = parseUnsigned32BitBin(fullAddress).substring(4, 30);
            String binaryInstruction = opcode + address;
            String hexInstruction = Long.toHexString(Long.parseLong(binaryInstruction, 2));
            if (hexInstruction.length() != 10) {
                int count0 = 8 - hexInstruction.length();
                String zero = "0";
                zero = zero.repeat(count0);
                return "0x" + zero + hexInstruction;
            }
            return ("0x" + hexInstruction);
        }
    };

    private static void getLabels() {
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty())
                    continue;

                if (line.matches(".+:.*")) {
                    String labelName = line.substring(0, line.indexOf(':'));
                    labels.put(labelName, lineNumber);
                }

                line = line.replaceAll("^.+:(\\s+)?", "");

                if (!line.isEmpty())
                    lineNumber++;
            }
            scanner.close();
            lineNumber = 0;
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static String assemble() {
        StringBuilder assembledCode = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine().trim();
                line = line.replaceAll("^.+:(\\s+)?", "");
                line = line.replaceAll("#.+", "");
                line = line.replace("(", ",");
                line = line.replace(")", "");

                if (line.isEmpty())
                    continue;

                String[] parts = line.split("[,\\s]+");
                int fullAddress = 0x00400000 + 4 * lineNumber;
                if (lineNumber == 0) {
                    assembledCode.append("Address");
                    assembledCode.append("  Machine Code");
                    assembledCode.append('\n');
                    assembledCode.append("---------");
                    assembledCode.append(' ');
                    assembledCode.append("---------\n");
                    lineNumber++;
                }
                assembledCode.append(parse8DigitHex(fullAddress)).append(": ");
                assembledCode.append(instructions.get(parts[0]).parse(parts)).append("\n");
                lineNumber++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error");
        }
        return assembledCode.toString();
    }

    private static String parseUnsigned5BitBin(int dec) {
        String bin = Integer.toBinaryString(dec);

        int l = bin.length();
        if (l < 5) {
            bin = "0".repeat(5 - l) + bin;
        }

        return bin;
    }

    private static String parseSigned16BitBin(int dec) {
        String bin = Integer.toBinaryString(dec);
        int l = bin.length();
        if (l < 16 && dec >= 0) {
            bin = "0".repeat(16 - l) + bin;
        } else if (dec < 0) {
            bin = bin.substring(l - 16);
        }
        return bin;
    }

    private static String parseUnsigned32BitBin(int dec) {
        String bin = Integer.toBinaryString(dec);
        int l = bin.length();
        if (l < 32) {
            bin = "0".repeat(32 - l) + bin;
        }
        return bin;
    }


    private static String parse8DigitHex(int dec) {
        String hex = Integer.toHexString(dec);
        int l = hex.length();
        if (l < 8) {
            hex = "0".repeat(8 - l) + hex;
        }
        return hex;
    }

    private static String getRegister(String reg) {
        if (reg.matches("[$]\\d+"))
            return parseUnsigned5BitBin(Integer.parseInt(reg.substring(1)));
        return registers.get(reg);
    }

    public static String fileBrowser(){
        String filename = "";
        JFrame frame = new JFrame();
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setTitle("File Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        frame.add(chooser);
        frame.repaint();
        frame.revalidate();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().getAbsolutePath();
        }
        else {
            throw new IOError(new FileNotFoundException("File not found"));
        }
        return filename;
    }
}
