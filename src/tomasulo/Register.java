package tomasulo;

class Register {
    int value;
    String rs; // the name of waiting 
    boolean waiting; //if waiting, value is invalid
    Register() {
        value = 0;
        rs = null;
        waiting = false;
    }
}