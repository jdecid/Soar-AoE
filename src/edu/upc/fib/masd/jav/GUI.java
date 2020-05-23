package edu.upc.fib.masd.jav;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public final class GUI {
    private static final GUI instance = new GUI();

    DefaultTableModel tableModel;

    private final Map<String, Integer> idToRowIdx;
    private final Map<String, Integer> fieldToColIdx;


    private GUI() {
        JFrame frame = new JFrame("Soar - AoE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        idToRowIdx = new HashMap<>();
        fieldToColIdx = new HashMap<>();

        fieldToColIdx.put("ID", 0);
        fieldToColIdx.put("Action", 1);
        fieldToColIdx.put("Food", 2);
        fieldToColIdx.put("Satiety", 3);

        String[] columns = {"ID", "Action", "Food", "Satiety"};

        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        table.setBounds(30, 40, 200, 300);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setSize(300, 400);
        frame.setVisible(true);

    }

    public static GUI getInstance() {
        return instance;
    }

    public void setAgentAction(String id, String s) {
        setValue(id, "Action", s);
    }

    public void setAgentFood(String id, String s) {
        setValue(id, "Food", s);
    }

    public void setAgentFoodSatiety(String id, String s) {
        setValue(id, "Satiety", s);
    }

    private void setValue(String id, String field, String value) {
        if (!idToRowIdx.containsKey(id)) {
            idToRowIdx.put(id, tableModel.getRowCount());
            tableModel.addRow(new String[]{id, "-", "-", "-"});
        }

        tableModel.setValueAt(value, idToRowIdx.get(id), fieldToColIdx.get(field));
    }
}
