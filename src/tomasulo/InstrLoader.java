package tomasulo;

import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

//Parse the instructions
public class InstrLoader {
    //read the instr file line by line
    static List<String> readFileByLine(String filePath)
    {
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                FileInputStream fileIS = new FileInputStream(file);
                InputStreamReader isReader = new InputStreamReader(fileIS);
                BufferedReader br = new BufferedReader(isReader);

                String line = null;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }
                br.close();
                isReader.close();
            } else {
                System.out.println("File not found");
            }
        } catch (Exception e) {
            System.out.println("Error reading file");
            e.printStackTrace();
        }
        return list;
    }

    static InstrType convertStrToType(String str) {
        if (str.equals("LD")) return InstrType.LD;
        else if (str.equals("ADD")) return InstrType.ADD;
        else if (str.equals("SUB")) return InstrType.SUB;
        else if (str.equals("MUL")) return InstrType.MUL;
        else if (str.equals("DIV")) return InstrType.DIV;
        else if (str.equals("JUMP")) return InstrType.JUMP;
        else {
            System.out.println("Unknown instruction type");
            return InstrType.ADD;
        }
    }

    public static List<Instr> parseInstr(String filePath) {
        List<String> instrStrList = readFileByLine(filePath);
        List<Instr> instrList = new ArrayList<Instr>();
        for(String instrStr : instrStrList) {
            String[] opList = instrStr.split(",");
            InstrType type = convertStrToType(opList[0]);
            // System.out.print(opList[0]+" ");
            // System.out.println(type);
            if (type == InstrType.LD) {
                int rd = Integer.parseInt(opList[1].substring(1));
                int addr = Integer.parseUnsignedInt(opList[2].substring(2).toLowerCase(), 16);
                instrList.add(new LDInstr(type, instrStr, 3, rd, addr));
            } else if (type == InstrType.JUMP) {
                int value = Integer.parseUnsignedInt(opList[1].substring(2).toLowerCase(), 16);
                int rs = Integer.parseInt(opList[2].substring(1));
                int off = Integer.parseUnsignedInt(opList[3].substring(2).toLowerCase(), 16);
                instrList.add(new JPInstr(type, instrStr, 1, value, rs, off));
            } else {
                int rd = Integer.parseInt(opList[1].substring(1));
                int rs1 = Integer.parseInt(opList[2].substring(1));
                int rs2 = Integer.parseInt(opList[3].substring(1));
                int latency;
                switch (type) {
                    case ADD:
                    case SUB:
                        latency = 3;
                        break;
                    case MUL:
                        latency = 4;
                        break;
                    case DIV:
                        latency = 4;
                        break;
                    default:
                        System.out.println("Unknown instruction type.");
                        latency = 0;
                }
                instrList.add(new ArithInstr(type, instrStr, latency, rd, rs1, rs2));
            }
        }
        System.out.println(instrList.size());
        for (Instr instr : instrList) {
            System.out.println(instr);
        }
        return instrList;
    }
}