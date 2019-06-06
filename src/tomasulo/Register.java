package tomasulo;

class Register {
    int value;
    String rs; // the name of waiting rs
    boolean valid; // invalid if it is waiting
    Register() {
        value = 0;
        rs = null;
        valid = true;
    }
    void writeValue(int res) {
        value = res;
        valid = true;
        rs = null;
    }
    void clear() {
        value = 0;
        rs = null;
        valid = true;
    }
}