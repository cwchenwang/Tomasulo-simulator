package tomasulo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class MainWin {

    JFrame frame;
    BtnPanel btnPanel;
    LBPanel lbPanel;
    RSPanel rsPanel;
    FUPanel fuPanel;
    RegPanel regPanel;

    Tomasulo tomasulo;

    public static void main(String[] args) {  
        MainWin win = new MainWin();
        
    }

    public MainWin() {
        tomasulo = new Tomasulo("../test/test0.nel");
        initUI();
    }

    void initUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Tomasulo Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);
        frame.setLayout(new GridLayout(5, 1));

        // clkLabel = new JLabel("CLOCK: 0", JLabel.CENTER);
        

        lbPanel = new LBPanel();
        rsPanel = new RSPanel();
        fuPanel = new FUPanel();
        regPanel = new RegPanel();

        btnPanel = new BtnPanel();
        btnPanel.runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("hello world");
                updateData();
            }
        });
        frame.add(btnPanel);
        frame.add(rsPanel);
        frame.add(lbPanel);
        frame.add(fuPanel);
        frame.add(regPanel);
        frame.setVisible(true);
    }

    void updateData() {
        if( tomasulo.executeByStep() == true) { // finished
            JOptionPane.showMessageDialog(null, "Exec has finished!", "Tips", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            btnPanel.clkLabel.setText("CLOCK: "+Integer.toString(tomasulo.clock));
        }

        System.out.println("update");

        lbPanel.updateData(tomasulo.getLB());
        rsPanel.updateData(tomasulo.getRS());
        regPanel.updateData(tomasulo.getRegs());
        fuPanel.updateData(tomasulo.getFU());
    }

}