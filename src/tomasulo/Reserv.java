package tomasulo;

class LoadBuffer {
    int issueTime;
    int writeTime;

    boolean busy;
    InstrType op;

    int res;

    Instr instr;
    String name;

    LoadBuffer(String name) {
        this.busy = false;
        this.name = name;
        this.issueTime = this.writeTime = -1;
    }
    void clear() {
        this.busy = false;
        this.issueTime = this.writeTime = -1;
    }
}

class Reserv {
    int issueTime;
    int readyTime;
    int writeTime;

    boolean busy;
    InstrType op;

    int res;
    String qj, qk; // the reservation that generates value
    int vj, vk; // the value, check Q first, if Q null then ready

    Instr instr;
    String name;
    InstrType operation;

    Reserv(String name) {
        this.busy = false;
        this.name = name;
        this.qj = this.qk = null;
        readyTime = -1;
        writeTime = -1;
    }
    void clear() {
        this.busy = false;
        this.qj = this.qk = null;
        this.readyTime = -1;
        this.writeTime = -1;
    }
}