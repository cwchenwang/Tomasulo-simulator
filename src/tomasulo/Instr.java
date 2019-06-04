package tomasulo;

enum InstrType {
    ADD, SUB, MUL, DIV, LD, JUMP
}

abstract class Instr {
    int issue, exec, write; //issue, exec and write time
    InstrType type;
    Instr (InstrType type) {
        this.type = type;
        this.issue = -1;
        this.exec = -1;
        this.write = -1;
    }
}

class LDInstr extends Instr {
    int rd, addr;
    LDInstr(InstrType type, int rd, int addr) {
        super(type);
        this.rd = rd;
        this.addr = addr;
    }
    @Override
    public String toString() {
        return type + " " + rd + " " + addr;
    }
}

class ArithInstr extends Instr {
    int rd, rs1, rs2;
    ArithInstr(InstrType type, int rd, int rs1, int rs2) {
        super(type);
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }
    @Override
    public String toString() {
        return type + " " + rd + " " + rs1 + " " + rs2;
    }
}

class JPInstr extends Instr {
    int value, rs, off;
    JPInstr(InstrType type, int value, int rs, int off) {
        super(type);
        this.value = value;
        this.rs = rs;
        this.off = off;
    }
    @Override
    public String toString() {
        return type + " " + value + " " + rs + " " + off;
    }
}
