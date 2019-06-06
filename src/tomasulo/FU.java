package tomasulo;

class FU {
    int runtimeLeft;
    boolean busy;
    // int res;
    Instr instr;
    String name;
    FU (String name) {
        this.name = name;
        this.busy = false;
    }
    @Override
    public String toString() {
        String str = name;
        if(busy) {
            str += "\t" + instr.instrStr + "\t" + runtimeLeft;
        } else {
            str += "\t\t";
        }
        return str;
    }
}

class ArithFU extends FU {
    Reserv reserv;
    ArithFU(String name) {
        super(name);
        reserv = null;
    }
    void clear() {
        this.busy = false;
        this.instr = null;
        this.reserv = null;
    }
}

class LoadFU extends FU {
    LoadBuffer loadBuffer;
    LoadFU (String name) {
        super(name);
        loadBuffer = null;
    }
    void clear() {
        this.busy = false;
        this.instr = null;
        this.loadBuffer = null;
    }
}