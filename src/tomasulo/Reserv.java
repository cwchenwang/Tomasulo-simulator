package tomasulo;

class LoadBuffer {
    int issueTime;
    int writeTime;

    boolean busy;
    InstrType op;

    int addr;

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
    @Override
    public String toString() {
        String res = name;
        if(busy == true) res += "\tYes";
        else res += "\tNo";
        res += "\t"+addr;
        return res;
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

    Reserv(String name) {
        this.busy = false;
        this.name = name;
        this.op = null;
        this.qj = this.qk = null;
        readyTime = -1;
        writeTime = -1;
    }
    void clear() {
        this.busy = false;
        this.op = null;
        this.qj = this.qk = null;
        this.readyTime = -1;
        this.writeTime = -1;
    }
    @Override
    public String toString() {
        String res = name;
        if(busy) res += "\tYes";
        else res += "\tNo";
        res += "\t";
        if(op != null) res += op;
        if(qj != null) res += "\t" + vj;
        else res += "\t";
        if(qk != null) res += "\t" + vk;
        else res += "\t";
        if(qj != null) res += "\t" + qj;
        else res += "\t";
        if(qk != null) res += "\t" + qk;
        else res += "\t";
        return res;
    }
}