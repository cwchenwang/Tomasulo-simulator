package tomasulo;

public class Main {
    public static void main(String[] args) {
        Tomasulo tomasulo = new Tomasulo();
        tomasulo.execNewFile("../test/test2.nel");
        for(int i = 0; i < 60; i++) {
            tomasulo.executeByStep();
        }
    }
}
