package tomasulo;

public class Main {
    public static void main(String[] args) {
        Tomasulo tomasulo = new Tomasulo();
        for(int i = 0; i < 26; i++) {
            tomasulo.executeByStep();
        }
    }
}
