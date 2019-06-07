package tomasulo;

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


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
        tomasulo = new Tomasulo();
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
                runSingle();
            }
        });
        btnPanel.runxBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(null, "Please input running steps");
                int steps = Integer.parseInt(s);
                runX(steps);
            }
        });
        btnPanel.brsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Choose file!");
                JFileChooser jf = new JFileChooser("../test");
                jf.setMultiSelectionEnabled(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "*.nel", "nel");
                jf.setFileFilter(filter);
                jf.showOpenDialog(null);
                File f = jf.getSelectedFile();
                String path = f.getAbsolutePath();
                loadNelFile(path);
            }
        });
        frame.add(btnPanel);
        frame.add(rsPanel);
        frame.add(lbPanel);
        frame.add(fuPanel);
        frame.add(regPanel);
        frame.setVisible(true);
    }

    void loadNelFile(String path) {
        System.out.println(path);
        tomasulo.execNewFile(path);
        btnPanel.clear();
        lbPanel.clear();
        rsPanel.clear();
        regPanel.clear();
        fuPanel.clear();
        JOptionPane.showMessageDialog(null, "Nel file has been loaded!", "Tips", JOptionPane.INFORMATION_MESSAGE);
    }

    void runX(int steps) {
        if(tomasulo.instrs == null || tomasulo.instrs.size() <= 0) {
            JOptionPane.showMessageDialog(null, "Please choose a legal file!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }
        if(steps < 0) {
            JOptionPane.showMessageDialog(null, "Steps must be positive integer!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }
        for(int i = 0; i < steps; i++) {
            if(tomasulo.executeByStep() == true) {
                JOptionPane.showMessageDialog(null, "Exec has finished!", "Tips", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
        updateInfo();
    }

    void updateInfo() {
        btnPanel.clkLabel.setText("CLOCK: "+Integer.toString(tomasulo.clock));
        lbPanel.updateData(tomasulo.getLB());
        rsPanel.updateData(tomasulo.getRS());
        regPanel.updateData(tomasulo.getRegs());
        fuPanel.updateData(tomasulo.getFU());
    }
    void runSingle() {
        if(tomasulo.instrs == null || tomasulo.instrs.size() <= 0) {
            JOptionPane.showMessageDialog(null, "Please choose a legal file!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }
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