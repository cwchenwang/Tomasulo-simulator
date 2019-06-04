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
    int nextInstr;

    Tomasulo(String filePath) {
        instrs = InstrLoader.parseInstr(filePath);
        //init register
        regs = new Register[REGNUM];
        for (int i = 0; i < REGNUM; i++) {
            regs[i] = new Register();
        }

        adders = new ArithFU[ADDERNUM];
        multers = new ArithFU[MULNUM];
        loaders = new LoadFU[LOADNUM];
        for (int i = 0; i < ADDERNUM; i++) {
            adders[i] = new ArithFU();
        }
        for (int i = 0; i < MULNUM; i++) {
            multers[i] = new ArithFU();
        }
        for (int i = 0; i < LOADNUM; i++) {
            loaders[i] = new LoadFU();
        }

        addReserv = new Reserv[ARSNUM];
        mulReserv = new Reserv[MRSNUM];
        loadBuffer = new LoadBuffer[LBNUM];
        for (int i = 0; i < ARSNUM; i++) {
            addReserv[i] = new Reserv();
        }
        for (int i = 0; i < MRSNUM; i++) {
            mulReserv[i] = new Reserv();
        }
        for (int i = 0; i < LBNUM; i++) {
            loadBuffer[i] = new LoadBuffer();
        }
    }

    //execute for one round
    public void executeByStep() {
        this.clock++;
        System.out.println("-----------------------");
        System.out.println("Clock: " + clock);

    }



}
