package tomasulo;

class FU {
    int runtimeLeft;
    boolean busy;
    int result;
    Instr instr;
    FU () {
        busy = false;
    }
}

class ArithFU extends FU {
    Reserv reserv;
    ArithFU() {
        super();
        reserv = null;
    }
}

class LoadFU extends FU {
    LoadBuffer loadBuffer;
    LoadFU () {
        super();
        loadBuffer = null;
    }
}