package tomasulo;

class FU {
    int runtimeLeft;
    boolean busy;
    int res;
    Instr instr;
    String name;
    FU (String name) {
        busy = false;
    }
}

class ArithFU extends FU {
    Reserv reserv;
    ArithFU(String name) {
        super(name);
        reserv = null;
    }
}

class LoadFU extends FU {
    LoadBuffer loadBuffer;
    LoadFU (String name) {
        super(name);
        loadBuffer = null;
    }
}