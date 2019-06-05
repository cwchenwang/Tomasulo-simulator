package tomasulo;

import java.util.List;
import java.util.ArrayList;


public class Tomasulo {
    public static final int REGNUM = 32; //num of registers

    //3个加法器，2个乘法器，2个load部件
    public static final int ADDERNUM = 3; // 3 adders
    public static final int MULNUM = 2; // 2 mul and div
    public static final int LOADNUM = 2; // 2 load

    //6个加减法保留栈，3个乘除法保留栈，3个loadbuffer
    public static final int ARSNUM = 6; // 6 adder reservations
    public static final int MRSNUM = 3; // 3 mul reservations
    public static final int LBNUM = 3; // 3 load buffer

    int clock = 0;

    List<Instr> instrs;
    Register[] regs;

    ArithFU[] adders;
    ArithFU[] multers;
    LoadFU[] loaders;

    Reserv[] addReserv;
    Reserv[] mulReserv;
    LoadBuffer[] loadBuffer;

    boolean hasJump;
    int pc; //pc, should change when jump exec comp

    Tomasulo(String filePath) {
        instrs = InstrLoader.parseInstr(filePath);
        pc = 0;
        hasJump = false;
        //init register
        regs = new Register[REGNUM];
        for (int i = 0; i < REGNUM; i++) {
            regs[i] = new Register();
        }

        adders = new ArithFU[ADDERNUM];
        multers = new ArithFU[MULNUM];
        loaders = new LoadFU[LOADNUM];
        for (int i = 0; i < ADDERNUM; i++) {
            adders[i] = new ArithFU("Add"+(i+1));
        }
        for (int i = 0; i < MULNUM; i++) {
            multers[i] = new ArithFU("Mult"+(i+1));
        }
        for (int i = 0; i < LOADNUM; i++) {
            loaders[i] = new LoadFU("Load"+(i+1));
        }

        addReserv = new Reserv[ARSNUM];
        mulReserv = new Reserv[MRSNUM];
        loadBuffer = new LoadBuffer[LBNUM];
        for (int i = 0; i < ARSNUM; i++) {
            addReserv[i] = new Reserv("Ars"+(i+1));
        }
        for (int i = 0; i < MRSNUM; i++) {
            mulReserv[i] = new Reserv("Mrs"+(i+1));
        }
        for (int i = 0; i < LBNUM; i++) {
            loadBuffer[i] = new LoadBuffer("LB"+(i+1));
        }
    }

    //execute for one round
    public void executeByStep() {
        this.clock++;
        System.out.println("-----------------------");
        System.out.println("Clock: " + clock);
        if(issue() == true) {
            this.pc++;
        }
    }

    boolean issue() {
        if (hasJump) return false; // if the result of jump doesn't come out
        boolean canIssue = false;
        Instr instr = instrs.get(pc);
        String name = "";
        if(instr.type == InstrType.ADD || instr.type == InstrType.SUB) {
            for (int i = 0; i < ARSNUM; i++) {
                if (addReserv[i].busy == false) {
                    canIssue = true;
                    name = addReserv[i].name;
                    issueArith(addReserv[i], (ArithInstr)instr);
                    break;
                }
            }
        }
        else if(instr.type == InstrType.MUL || instr.type == InstrType.DIV) {
            for(int i = 0; i < MRSNUM; i++) {
                if (mulReserv[i].busy == false) {
                    canIssue = true;
                    name = mulReserv[i].name;
                    issueArith(mulReserv[i], (ArithInstr)instr);
                    break;
                }
            }
        } else if(instr.type == InstrType.JUMP) {
            for(int i = 0; i < ARSNUM; i++) {
                if(addReserv[i].busy == false) {
                    canIssue = true;
                    name = addReserv[i].name;
                    issueJP(addReserv[i], (JPInstr)instr); 
                    break;
                }
            }
        } else if(instr.type == InstrType.LD) {
            for(int i = 0; i < LBNUM; i++) {
                if(loadBuffer[i].busy == false) {
                    canIssue = true;
                    name = loadBuffer[i].name;
                    issueLD(loadBuffer[i], (LDInstr)instr);
                    break;
                }
            }
        }
        if(canIssue) {
            System.out.println("Issue: "+instr.instrStr + " to " + name);
        }
        return canIssue;
    }

    void issueLD(LoadBuffer ldbuffer, LDInstr instr) {
        ldbuffer.busy = true;
        if(instr.issue == -1) instr.issue = clock;
        ldbuffer.instr = instr;
        ldbuffer.op = InstrType.LD;
    }

    void issueArith(Reserv reserv, ArithInstr instr) {
        reserv.busy = true;
        if(instr.issue == -1) instr.issue = clock;
        reserv.instr = instr;
        reserv.op = instr.type;
        //check registers
        int rs1 = instr.rs1, rs2 = instr.rs2;
        if(regs[rs1].valid) {
            reserv.vj = regs[rs1].value;
            reserv.qj = null;
        }
        else reserv.qj = regs[rs1].rs;
        if(regs[rs2].valid) {
            reserv.vk = regs[rs2].value;
            reserv.qk = null;
        }
        else reserv.qk = regs[rs2].rs;
    }

    void issueJP(Reserv reserv, JPInstr instr) {
        reserv.busy = true;
        if(instr.issue == -1) instr.issue = clock;
        reserv.instr = instr;
        reserv.op = instr.type;
        int rs = instr.rs;
        if(regs[rs].valid) {
            reserv.vj = regs[rs].value;
            reserv.qj = null;
        }
        else reserv.qj = regs[rs].rs;
        reserv.qk = null;
    }
}
