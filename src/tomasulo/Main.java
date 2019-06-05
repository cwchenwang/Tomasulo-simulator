package tomasulo;

public class Main {
    public static void main(String[] args) {
        Tomasulo tomasulo = new Tomasulo("../test/test0.nel");
        for(int i = 0; i < 1; i++) {
            tomasulo.executeByStep();
        }
    }
}
