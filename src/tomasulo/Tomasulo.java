package tomasulo;

import java.util.List;

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

    public Tomasulo() {
        //instrs = InstrLoader.parseInstr(filePath);
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

    void execNewFile(String path) {
        instrs = InstrLoader.parseInstr(path);
        reset();
    }

    void reset() { //used when new file loaded
        pc = 0;
        hasJump = false;
        for (int i = 0; i < REGNUM; i++) {
            regs[i].clear();
        }
        for (int i = 0; i < ADDERNUM; i++) {
            adders[i].clear();
        }
        for (int i = 0; i < MULNUM; i++) {
            multers[i].clear();
        }
        for (int i = 0; i < LOADNUM; i++) {
            loaders[i].clear();
        }

        for (int i = 0; i < ARSNUM; i++) {
            addReserv[i].clear();
        }
        for (int i = 0; i < MRSNUM; i++) {
            mulReserv[i].clear();
        }
        for (int i = 0; i < LBNUM; i++) {
            loadBuffer[i].clear();
        }
    }

    //execute for one round
    public boolean executeByStep() {
        // if(hasJump == true || pc < instrs.size()) {

        // } else {
        //     System.out.println("Finished");
        //     return;
        // }
        this.clock++;
        System.out.println("------------------------------------");
        System.out.println("Clock: " + clock);

        if(this.pc >= instrs.size() && checkFinish() == true) {
            System.out.println("Finished yet!");
            printTime();
            return true;
        }
        exec();
        writeBack();
        if(issue() == true) {
            this.pc++;
        }
        checkFU();
        printStatus();
        return false;
    }

    void printTime() {
        for(Instr instr : instrs) {
            System.out.println(instr.issue + " " + instr.ready + " " + instr.exec + " " + instr.write);
        }
    }
    boolean checkFinish() {
        boolean finished = true;
        for (int i = 0; i < ADDERNUM; i++) {
            if(adders[i].busy) finished = false;
        }
        for (int i = 0; i < MULNUM; i++) {
            if(multers[i].busy) finished = false;
        }
        for (int i = 0; i < LOADNUM; i++) {
            if(loaders[i].busy) finished = false;
        }
        for (int i = 0; i < ARSNUM; i++) {
            if(addReserv[i].busy) finished = false;
        }
        for (int i = 0; i < MRSNUM; i++) {
            if(mulReserv[i].busy) finished = false;
        }
        for (int i = 0; i < LBNUM; i++) {
            if(loadBuffer[i].busy) finished = false;
        }
        return finished;
    }

    void checkFU() {
        setReadyTime();
        for(int i = 0; i < ADDERNUM; i++) {
            if(adders[i].busy == false) {
                //find available reservation
                int tepReadyTime = 0x7fffffff;
                int tepInstrIndex = 0x7fffffff;
                int tepRSIndex = -1;
                for(int j = 0; j < ARSNUM; j++) {
                    if(addReserv[j].readyTime > 0 && addReserv[j].exec == false) {
                        if(addReserv[j].readyTime < tepReadyTime
                        || (addReserv[j].readyTime == tepReadyTime && addReserv[j].index < tepInstrIndex)) {
                            tepReadyTime = addReserv[j].readyTime;
                            tepInstrIndex = addReserv[j].index;
                            tepRSIndex = j;
                        }
                    }
                }
                if(tepRSIndex >= 0) { //find rs to fu
                    System.out.println(addReserv[tepRSIndex].instr.instrStr+" to " + adders[i].name);
                    adders[i].busy = true;
                    adders[i].runtimeLeft = addReserv[tepRSIndex].instr.latency;
                    adders[i].reserv = addReserv[tepRSIndex];
                    addReserv[tepRSIndex].exec = true;
                    adders[i].instr = addReserv[tepRSIndex].instr;
                }
            }
        }
        for(int i = 0; i < MULNUM; i++) {
            if(multers[i].busy == false) {
                //find available reservation
                int tepReadyTime = 0x7fffffff;
                int tepInstrIndex = 0x7fffffff;
                int tepRSIndex = -1;
                for(int j = 0; j < MRSNUM; j++) {
                    if(mulReserv[j].readyTime > 0 && mulReserv[j].exec == false) {
                        if(mulReserv[j].readyTime < tepReadyTime
                        || (mulReserv[j].readyTime == tepReadyTime && mulReserv[j].index < tepInstrIndex)) {
                            tepReadyTime = mulReserv[j].readyTime;
                            tepInstrIndex = mulReserv[j].index;
                            tepRSIndex = j;
                        }
                    }
                }
                if(tepRSIndex >= 0) { //find rs to fu
                    System.out.println(mulReserv[tepRSIndex].instr.instrStr+" to " + multers[i].name);
                    multers[i].busy = true;
                    multers[i].runtimeLeft = mulReserv[tepRSIndex].instr.latency;
                    if(mulReserv[tepRSIndex].op == InstrType.DIV && mulReserv[tepRSIndex].vk == 0) {
                        multers[i].runtimeLeft = 1;
                    }
                    multers[i].reserv = mulReserv[tepRSIndex];
                    mulReserv[tepRSIndex].exec = true;
                    multers[i].instr = mulReserv[tepRSIndex].instr;
                }
            }
        }
        for(int i = 0; i < LOADNUM; i++) {
            if(loaders[i].busy == false) {
                //find available reservation
                int tepReadyTime = 0x7fffffff;
                int tepInstrIndex = 0x7fffffff;
                int tepRSIndex = -1;
                for(int j = 0; j < LBNUM; j++) {
                    if(loadBuffer[j].readyTime > 0 && loadBuffer[j].exec == false) {
                        // System.out.println("lb"+j);
                        if(loadBuffer[j].readyTime < tepReadyTime
                        || (loadBuffer[j].readyTime == tepReadyTime && loadBuffer[j].index < tepInstrIndex)) {
                            tepReadyTime = loadBuffer[j].readyTime;
                            tepInstrIndex = loadBuffer[j].index;
                            tepRSIndex = j;
                        }
                    }
                }
                if(tepRSIndex >= 0) { //find rs to fu
                    System.out.println(loadBuffer[tepRSIndex].instr.instrStr+" to " + loaders[i].name);
                    loaders[i].busy = true;
                    loaders[i].runtimeLeft = loadBuffer[tepRSIndex].instr.latency;
                    loaders[i].loadBuffer = loadBuffer[tepRSIndex];
                    loadBuffer[tepRSIndex].exec = true;
                    loaders[i].instr = loadBuffer[tepRSIndex].instr;
                }
            }
        }
    }

    void setReadyTime() { //set ready time and set result
        for(int i = 0; i < ARSNUM; i++) {
            if(addReserv[i].busy && addReserv[i].readyTime == -1) {//not ready
                if(addReserv[i].op == InstrType.JUMP) {
                    if(addReserv[i].qj == null) {
                        if(addReserv[i].instr.ready == -1) addReserv[i].instr.ready = clock;
                        addReserv[i].readyTime = clock;
                    }
                } else {
                    if(addReserv[i].qj == null && addReserv[i].qk == null) {
                        if(addReserv[i].instr.ready == -1) addReserv[i].instr.ready = clock;
                        addReserv[i].readyTime = clock;
                        // System.out.println()
                        if(addReserv[i].op == InstrType.ADD) {
                            addReserv[i].res = addReserv[i].vj + addReserv[i].vk;
                        } else if(addReserv[i].op == InstrType.SUB) {
                            addReserv[i].res = addReserv[i].vj - addReserv[i].vk;
                        } else {
                            System.out.println("ERROR1");
                        }
                    }
                }
            }
        }
        for(int i = 0; i < MRSNUM; i++) {
            if(mulReserv[i].busy && mulReserv[i].readyTime == -1) {//not ready
                if(mulReserv[i].qj == null && mulReserv[i].qk == null) {
                    if(mulReserv[i].instr.ready == -1) mulReserv[i].instr.ready = clock;
                    mulReserv[i].readyTime = clock;
                    if(mulReserv[i].op == InstrType.MUL) {
                        mulReserv[i].res = addReserv[i].vj * addReserv[i].vk;
                    } else if(mulReserv[i].op == InstrType.DIV) {
                        if(mulReserv[i].vk == 0) {
                            mulReserv[i].res = mulReserv[i].vj;
                        } else {
                            mulReserv[i].res = mulReserv[i].vj / mulReserv[i].vk;
                        }
                    } else {
                        System.out.println("ERROR2");
                    }
                }
            }
        }
        for(int i = 0; i < LBNUM; i++) {
            if(loadBuffer[i].busy && loadBuffer[i].readyTime == -1) {
                if(loadBuffer[i].instr.ready == -1) loadBuffer[i].instr.ready = clock;
                loadBuffer[i].readyTime = clock;
            }
        }
    }

    void writeBack() {
        //don't forget to release rs
        for(int i = 0; i < ARSNUM; i++) {
            if(addReserv[i].writeTime == clock) {
                if(addReserv[i].instr.write == -1) addReserv[i].instr.write = clock;
                if(addReserv[i].op == InstrType.JUMP) { // if write is jump
                    System.out.println(addReserv[i].name + addReserv[i].writeTime);
                    hasJump = false;
                    // JPInstr instr = ((JPStr)((ArithFU)fu).reserv.instr);
                    if( addReserv[i].vj == ((JPInstr)addReserv[i].instr).value ) {
                        System.out.println("current pc "+pc);
                        this.pc -= 1;//issue jump has added pc
                        this.pc += ((JPInstr)(addReserv[i].instr)).off;
                        System.out.println("after pc " + pc);
                    }
                }
                checkAndWrite(addReserv[i].name, addReserv[i].res);
                addReserv[i].clear(); 
            }
        }
        for(int i = 0; i < MRSNUM; i++) {
            if(mulReserv[i].writeTime == clock) {
                if(mulReserv[i].instr.write == -1) mulReserv[i].instr.write = clock;
                checkAndWrite(mulReserv[i].name, mulReserv[i].res);
                mulReserv[i].clear();
            } 
        }
        for(int i = 0; i < LBNUM; i++) {
            if(loadBuffer[i].writeTime == clock) {
                if(loadBuffer[i].instr.write == -1) loadBuffer[i].instr.write = clock;
                checkAndWrite(loadBuffer[i].name, loadBuffer[i].res);
                loadBuffer[i].clear();
            }
        }
    }

    void checkAndWrite(String name, int value) {
        System.out.println("Write " + value + " to " + name);
        for (int i = 0; i < ARSNUM; i++) {
            if(addReserv[i].qj != null && addReserv[i].qj.equals(name)) {
                addReserv[i].qj = null;
                addReserv[i].vj = value;
            }
            if(addReserv[i].qk != null && addReserv[i].qk.equals(name)) {
                addReserv[i].qk = null;
                addReserv[i].vk = value;
            }
        }
        for (int i = 0; i < MRSNUM; i++) {
            if(mulReserv[i].qj != null && mulReserv[i].qj.equals(name)) {
                mulReserv[i].qj = null;
                mulReserv[i].vj = value;
            }
            if(mulReserv[i].qk != null && mulReserv[i].qk.equals(name)) {
                mulReserv[i].qk = null;
                mulReserv[i].vk = value;
            } 
        }
        // for (int i = 0; i < LBNUM; i++) {
        //     loadBuffer[i] = new LoadBuffer("LB"+(i+1));
        // }
        //update regs
        for(int i = 0; i < REGNUM; i++) {
            if(regs[i].valid == false && regs[i].rs.equals(name)) {
                regs[i].valid = true;
                regs[i].value = value;
            }
        }
    }

    void exec() {
        //iterate all fu and check if it is busy
        for(int i = 0; i < ADDERNUM; i++) {
            decRuntime(adders[i]);
        }
        for(int i = 0; i < MULNUM; i++) {
            decRuntime(multers[i]);
        }
        for(int i = 0; i < LOADNUM; i++) {
            decRuntime(loaders[i]);
        }
    }

    void decRuntime(FU fu) {
        if(fu.busy) {
            fu.runtimeLeft--;
            if(fu.runtimeLeft == 0) { //release fu
                System.out.println("Finished " + fu.instr.instrStr);
                //save result to reservation
                if(fu instanceof ArithFU) {

                    ((ArithFU)fu).reserv.writeTime = clock + 1;
                    if(((ArithFU)fu).reserv.instr.exec == -1) {
                        ((ArithFU)fu).reserv.instr.exec = clock;
                    }
                    fu.busy = false;
                } else if(fu instanceof LoadFU) {
                    ((LoadFU)fu).loadBuffer.writeTime = clock + 1;
                    if(((LoadFU)fu).loadBuffer.instr.exec == -1) {
                        ((LoadFU)fu).loadBuffer.instr.exec = clock;
                    }
                    fu.busy = false;
                }
            }
        }
    }

    boolean issue() {
        if (hasJump) return false; // if the result of jump doesn't come out
        if (pc >= instrs.size()) {
            System.out.println("No instrs");
            return false;
        }
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
                    hasJump = true;
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
        ldbuffer.index = pc;
        ldbuffer.issueTime = clock;
        ldbuffer.busy = true;
        if(instr.issue == -1) instr.issue = clock;
        ldbuffer.instr = instr;
        ldbuffer.op = InstrType.LD;
        ldbuffer.res = instr.addr; //the res of LOAD can be accessed directly

        regs[instr.rd].rs = ldbuffer.name;
        regs[instr.rd].valid = false;
    }

    void issueArith(Reserv reserv, ArithInstr instr) {
        reserv.index = pc;
        reserv.issueTime = clock;
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

        regs[instr.rd].rs = reserv.name;
        regs[instr.rd].valid = false;
    }

    void issueJP(Reserv reserv, JPInstr instr) {
        reserv.index = pc;
        reserv.issueTime = clock;
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

    public String[][] getLB() {
        String[][] res = new String[3][2];
        for(int i = 0; i < LBNUM; i++) {
            if(loadBuffer[i].busy) {
                res[i][0] = "Yes";
                res[i][1] = Integer.toString(loadBuffer[i].res);
            } else {
                res[i][0] = "No";
                res[i][1] = "";
            }
        }
        return res;
    }

    public String[][] getRS() {
        String[][] res = new String[9][6];
        for(int i = 0; i < 6; i++) {
            if(addReserv[i].busy) {
                res[i][0] = "Yes";
                if(addReserv[i].op != null) res[i][1] = addReserv[i].op.toString();
                if(addReserv[i].qj == null) {
                    res[i][2] = Integer.toString(addReserv[i].vj);
                    res[i][4] = "";
                } else {
                    res[i][2] = "";
                    res[i][4] = addReserv[i].qj;
                }
                if(addReserv[i].qk == null) {
                    res[i][3] = Integer.toString(addReserv[i].vk);
                    res[i][5] = "";
                } else {
                    res[i][3] = "";
                    res[i][5] = addReserv[i].qk;
                }
                if(addReserv[i].op == InstrType.JUMP) {
                    res[i][3] = res[i][5] = "";
                }
            } else {
                res[i][0] = "No";
                res[i][1] = res[i][2] = res[i][3] = res[i][4] = res[i][5] = "";
            }
        }
        for(int i = 6; i < 9; i++) {
            if(mulReserv[i-6].busy) {
                res[i][0] = "Yes";
                if(mulReserv[i-6].op != null) res[i][1] = mulReserv[i-6].op.toString();
                if(mulReserv[i-6].qj == null) {
                    res[i][2] = Integer.toString(mulReserv[i-6].vj);
                    res[i][4] = "";
                } else {
                    res[i][2] = "";
                    res[i][4] = mulReserv[i-6].qj;
                }
                if(mulReserv[i-6].qk == null) {
                    res[i][3] = Integer.toString(mulReserv[i-6].vk);
                    res[i][5] = "";
                } else {
                    res[i][3] = "";
                    res[i][5] = mulReserv[i-6].qk;
                }
            } else {
                res[i][0] = "No";
                res[i][1] = res[i][2] = res[i][3] = res[i][4] = res[i][5] = "";
            }
        }
        return res;
    }

    public String[] getRegs() {
        String[] res = new String[REGNUM];
        for(int i = 0; i < REGNUM; i++) {
            if(regs[i].valid) {
                res[i] = Integer.toString(regs[i].value);
            } else {
                res[i] = regs[i].rs;
            }
        }
        return res;
    }

    public String[][] getFU() {
        String[][] res = new String[7][2];
        for(int i = 0; i < ADDERNUM; i++) {
            if(adders[i].busy) {
                res[i][0] = adders[i].instr.instrStr;
                res[i][1] = Integer.toString(adders[i].runtimeLeft);
            } else {
                res[i][0] = res[i][1] = "";
            }
        }
        for(int i = 0; i < MULNUM; i++) {
            if(multers[i].busy) {
                res[i+3][0] = multers[i].instr.instrStr;
                res[i+3][1] = Integer.toString(multers[i].runtimeLeft);
            } else {
                res[i+3][0] = res[i+3][1] = "";
            }
        }
        for(int i = 0; i < LOADNUM; i++) {
            if(loaders[i].busy) {
                res[i+5][0] = loaders[i].instr.instrStr;
                res[i+5][1] = Integer.toString(loaders[i].runtimeLeft);
            } else {
                res[i+5][0] = res[i+5][1] = "";
            }
        }
        return res;
    }

    void printStatus() {
        System.out.println("Reservation Station");
        System.out.println("\tBusy\tOp\tvj\tvk\tqj\tqk");
        for(int i = 0; i < ARSNUM; i++) System.out.println(addReserv[i]);
        for(int i = 0; i < MRSNUM; i++) System.out.println(mulReserv[i]);
        System.out.println("Load buffer");
        System.out.println("\tBusy\tAddr");
        for(int i = 0; i < LBNUM; i++) System.out.println(loadBuffer[i]);
        System.out.println("Register status");
        // for(int i = 0; i < 32; i++) {
        //     System.out.println("F"+i+":"+regs[i].rs);
        // }
        for(int i = 0; i < 2; i++) {
            String regName = "";
            String regState = "";
            for(int j = 0; j < 16; j++) {
                regName += "F"+(i*16+j) + "\t";
                if(regs[i*8+j].valid == false) regState += regs[i*16+j].rs;
                else regState += regs[i*16+j].value;
                regState += "\t";
            }
            System.out.println(regName);
            System.out.println(regState);
        }
        // System.out.println("Register Value");
        // for(int i = 0; i < 2; i++) {
        //     String regName = "";
        //     String regValue = "";
        //     for(int j = 0; j < 16; j++) {
        //         regName += "F"+(i*16+j) + "\t";
        //         if(regs[i*8+j].valid == true) regValue += regs[i*16+j].value;
        //         regValue += "\t";
        //     }
        //     System.out.println(regName);
        //     System.out.println(regValue);
        // }
        System.out.println("Function Unit");
        for(int i = 0; i < ADDERNUM; i++) {
            System.out.println(adders[i]);
        }
        for(int i = 0; i < MULNUM; i++) {
            System.out.println(multers[i]);
        }
        for(int i = 0; i < LOADNUM; i++) {
            System.out.println(loaders[i]);
        }
    }
}
