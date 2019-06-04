package tomasulo;

class LoadBuffer{
    int issueTime;
    int writeTime;

    boolean busy;
    boolean exec;

    Instr instr;

    LoadBuffer() {
        busy = false;
        exec = false;
        writeTime = -1;
    }
}

class Reserv {
    int issueTime;
    int readyTime;
    int writeTime;

    boolean busy; // whether the reservation is claimed
    boolean exec;

    String Qj, Qk; // the reservation that generates value
    int Vj, Vk; // the value, check Q first, if Q null then ready

    Instr instr;
    InstrType operation;

    Reserv() {
        exec = false;
        busy = false;
        Qj = Qk = null;
        readyTime = 0;
        writeTime = -1;
    }
}