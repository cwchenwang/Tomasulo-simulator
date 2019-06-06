package tomasulo;

import java.awt.*;
import javax.swing.*;


class BtnPanel extends JPanel{
    private static final long serialVersionUID = 1L;
    FlowLayout flowLayout;
    JButton runBtn;
    JLabel clkLabel;
    public BtnPanel() {
        super();
        flowLayout = new FlowLayout();
        this.setLayout(flowLayout);

        runBtn = new JButton("Run");
        this.add(runBtn);
        this.runBtn.setPreferredSize(new Dimension(80, 60));
        runBtn.setBackground(Color.BLUE);

        clkLabel = new JLabel("CLOCK: 0", JLabel.CENTER);
        clkLabel.setFont(new Font("CLOCK: 0", Font.BOLD, 20));
        this.add(clkLabel);
        this.setPreferredSize(new Dimension(100, 100));
    }
}

class LBPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    JTable lbTable;
    JScrollPane scrollTable;
    public LBPanel() {
        super();
        this.setLayout(new GridBagLayout());
        Object[][] data = {
            { "LB1", "", ""},
            { "LB2", "", "" },
            { "LB3", "", ""}, };
        final String[] lbHead = { " ", "Busy", "Address" };
        
        lbTable = new JTable(data, lbHead);
        lbTable.setEnabled(false);
        lbTable.setPreferredScrollableViewportSize(new Dimension(550, 50));
        scrollTable = new JScrollPane(lbTable);
        this.add(scrollTable);
    }

    void updateData(String[][] data) {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 2; j++) {
                lbTable.setValueAt(data[i][j], i, j+1);
            }
        }
    }

    void clear() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 2; j++) {
                lbTable.setValueAt("", i, j+1);
            }
        }
    }
}

class RSPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    JTable rsTable;
    JScrollPane scrollTable;
    public RSPanel() {
        super();
        // this.setLayout(new BorderLayout());
        Object[][] data = {
            { "Ars1", "No", "", "", "", "", ""},
            { "Ars2", "No", "", "", "", "", ""},
            { "Ars3", "No", "", "", "", "", ""},
            { "Ars4", "No", "", "", "", "", ""},
            { "Ars5", "No", "", "", "", "", ""},
            { "Ars6", "No", "", "", "", "", ""},
            { "Mrs1", "No", "", "", "", "", ""},
            { "Mrs2", "No", "", "", "", "", ""},
            { "Mrs3", "No", "", "", "", "", ""},};
        final String[] lbHead = { " ", "Busy", "Op", "Vj", "Vk", "Qj", "Qk" };
        
        rsTable = new JTable(data, lbHead);
        rsTable.setEnabled(false);
        rsTable.setPreferredScrollableViewportSize(new Dimension(800, 150));
        // rsTable.setPreferredSize(new Dimension(800, 250));
        scrollTable = new JScrollPane(rsTable);
        this.add(scrollTable);
        // this.add(rsTable.getTableHeader(), BorderLayout.PAGE_START);
        // this.add(rsTable, BorderLayout.CENTER);
        //this.add(rsTable);
    }

    void updateData(String[][] data) {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 6; j++) {
                rsTable.setValueAt(data[i][j], i, j+1);
            }
        }
    }

    void clear() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 6; j++) {
                rsTable.setValueAt("No", i, j+1);
            }
        }
    }
}

class RegPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    JTable regTable;
    JScrollPane scrollTable;

    public RegPanel() {
        super();
        this.setLayout(new GridBagLayout());
        Object[][] data = {
            { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
            { "F16", "F17", "F18", "F19", "F20", "F21", "F22", "F23", "F24", "F25", "F26", "F27", "F28", "F29", "F30", "F31"},
            { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},};
        final String[] lbHead = { "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14", "F15"};
        
        regTable = new JTable(data, lbHead);
        regTable.setEnabled(false);
        regTable.setPreferredScrollableViewportSize(new Dimension(700, 50));
        scrollTable = new JScrollPane(regTable);
        this.add(scrollTable);
        // this.add(regTable);
    }

    void updateData(String[] data) {
        for(int i = 0; i < 16; i++) {
            regTable.setValueAt(data[i], 0, i);
            regTable.setValueAt(data[i+16], 2, i);
        }
    }

    void clear() {
        for(int i = 0; i < 16; i++) {
            regTable.setValueAt("0", 0, i);
            regTable.setValueAt("0", 2, i);
        }
    }
}

class FUPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    JTable fuTable;
    JScrollPane scrollTable;

    public FUPanel() {
        super();
        this.setLayout(new GridBagLayout());
        Object[][] data = {
            { "Add1", "", ""},
            { "Add2", "", ""},
            { "Add3", "",  ""},
            { "Mult1", "", ""},
            { "Mult2", "", ""},
            { "Load1", "", ""},
            { "Load2", "", ""},};
        final String[] lbHead = { " ", "当前执行指令", "剩余周期" }; 
        
        fuTable = new JTable(data, lbHead);
        fuTable.setEnabled(false);
        fuTable.setPreferredScrollableViewportSize(new Dimension(700, 115));
        scrollTable = new JScrollPane(fuTable);
        this.add(scrollTable);
    }

    void updateData(String[][] data) {
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 2; j++) {
                fuTable.setValueAt(data[i][j], i, j+1);
            }
        }
    }

    void clear() {
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 2; j++) {
                fuTable.setValueAt("", i, j+1);
            }
        }
    }
}
