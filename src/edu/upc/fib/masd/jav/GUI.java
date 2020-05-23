package edu.upc.fib.masd.jav;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public final class GUI {
    private static final GUI instance = new GUI();
    private static Environment environment;

    DefaultTableModel tableModel;

    private final Map<String, Integer> idToRowIdx;
    private final Map<String, Integer> fieldToColIdx;


    private GUI() {
        JFrame frame = new JFrame("Soar - AoE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 800);

        idToRowIdx = new HashMap<>();
        fieldToColIdx = new HashMap<>();

        fieldToColIdx.put("ID", 0);
        fieldToColIdx.put("Action", 1);
        fieldToColIdx.put("Food", 2);
        fieldToColIdx.put("Satiety", 3);
        fieldToColIdx.put("Wood", 4);

        String[] columns = {"ID", "Action", "Food", "Satiety", "Wood"};

        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String id = (String) tableModel.getValueAt(row, 0);

                if (id.contains("Baron")) {
                    c.setBackground(new Color(148, 168, 208));
                } else if (id.contains("Builder")) {
                    c.setBackground(new Color(253, 202, 162));
                } else if (id.contains("Collector")) {
                    c.setBackground(new Color(224, 243, 176));
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        table.setBounds(30, 40, 200, 300);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        JButton button = new JButton("Run");
        button.setActionCommand("Run");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = e.getActionCommand();
                if (action.equals("Run")) {
                    environment.runSystemStep();
                }
            }
        });

        frame.add(button, BorderLayout.SOUTH);

        frame.setVisible(true);

    }

    public static GUI getInstance() {
        return instance;
    }

    public static void setEnvironment(Environment env) {
        environment = env;
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

    public void setAgentWood(String id, String s) {
        setValue(id, "Wood", s);
    }


    private void setValue(String id, String field, String value) {
        if (!idToRowIdx.containsKey(id)) {
            idToRowIdx.put(id, tableModel.getRowCount());
            tableModel.addRow(new String[]{id, "-", "-", "-"});
        }

        int row = idToRowIdx.get(id);
        int col = fieldToColIdx.get(field);

        tableModel.setValueAt(value, row, col);
        tableModel.fireTableCellUpdated(row, col);
    }
}
