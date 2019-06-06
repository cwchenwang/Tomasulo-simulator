package tomasulo;

class LoadBuffer {
    int index;
    int issueTime;
    int writeTime;
    int readyTime;

    boolean exec;
    boolean busy;
    InstrType op;

    int res;

    Instr instr;
    String name;

    LoadBuffer(String name) {
        this.busy = false;
        this.exec = false;
        this.name = name;
        this.issueTime = this.readyTime = this.writeTime = -1;
    }
    void clear() {
        this.busy = false;
        this.exec = false;
        this.op = null;
        this.issueTime = this.readyTime = this.writeTime = -1;
    }
    @Override
    public String toString() {
        String str = name;
        if(busy == true) str += "\tYes";
        else {
            str += "\tNo\t";
            return str;
        }
        str += "\t"+res;
        return str;
    }
}

class Reserv {
    int index;
    int issueTime;
    int readyTime;
    int writeTime;

    boolean exec; //true if it is executing or execute completed
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
        this.exec = false;
        this.op = null;
        this.qj = this.qk = null;
        issueTime = -1;
        readyTime = -1;
        writeTime = -1;
    }
    void clear() {
        this.busy = false;
        this.exec = false;
        this.op = null;
        this.qj = this.qk = null;
        this.issueTime = -1;
        this.readyTime = -1;
        this.writeTime = -1;
    }
    @Override
    public String toString() {
        String str = name;
        if(busy) str += "\tYes";
        else {
            str += "\tNo\t\t\t\t\t";
            return str;
        }
        str += "\t";
        if(op != null) str += op;
        if(qj != null) str += "\t";
        else str += "\t" + vj;
        if(qk != null || op == InstrType.JUMP) str += "\t";
        else str += "\t" + vk;
        if(qj != null) str += "\t" + qj;
        else str += "\t";
        if(qk != null) str += "\t" + qk;
        else str += "\t";
        return str;
    }
}