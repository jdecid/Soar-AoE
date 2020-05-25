package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Field;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public final class GUI {
    private static final GUI instance = new GUI();
    private static Environment environment;
    private static JFrame frame;

    // Table with agents (rows) and their attributes (cols)
    private final JTable agentsTable;
    // Maps agentID to row index in `agentsTable`
    private final Map<String, Integer> agentsIdToRowIdx;
    // Maps agentAttributeName to col index in `agentsTable`
    private final Map<String, Integer> agentsAttrToColIdx;

    // Panel with collectors' fields
    private final JPanel fieldsPanel;
    // Maps agentID to the table with its fields
    private final Map<String, JTable> agentFieldsTables;
    // Maps agentID and fieldID to the corresponding row in its corresponding fields
    private final Map<String, Map<String, Integer>> agentFieldsIDToRowIdx;
    // Maps fieldAttributeName to col index in `fieldsPanel[i]`
    private final Map<String, Integer> fieldsAttrToColIdx;

    private final String[] fieldColumns;


    private GUI() {
        frame = new JFrame("Soar - AoE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 800);

        String[] agentColumns = new String[]{"ID", "Job", "Action", "Food", "Satiety", "Wood"};
        agentsIdToRowIdx = new HashMap<>();
        agentsAttrToColIdx = initAttrToColIdx(agentColumns);
        agentsTable = initAgentsTable(agentColumns);

        fieldColumns = new String[]{"ID", "State", "Yield"};
        agentFieldsIDToRowIdx = new HashMap<>();
        agentFieldsTables = new HashMap<>();

        fieldsAttrToColIdx = initAttrToColIdx(fieldColumns);
        fieldsPanel = new JPanel();

        JScrollPane agentsScrollPane = new JScrollPane(agentsTable);
        JScrollPane fieldsScrollPane = new JScrollPane(fieldsPanel);

        frame.add(agentsScrollPane, BorderLayout.NORTH);
        frame.add(fieldsScrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(createRunButton());
        btnPanel.add(createRun10Button());
        btnPanel.add(createExitButton());
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    }

    public static JFrame getFrame() {
        return frame;
    }

    private Map<String, Integer> initAttrToColIdx(String[] columns) {
        Map<String, Integer> columnsIdx = new HashMap<>();
        for (int i = 0; i < columns.length; ++i) {
            columnsIdx.put(columns[i], i);
        }
        return columnsIdx;
    }

    private JTable initAgentsTable(String[] columns) {
        JTable table = new JTable(new MyModel(columns, 0));
        table.setBounds(30, 40, 800, 300);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    spawnAgentDebugger(row);
                }
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String id = (String) table.getModel().getValueAt(row, 1);

                if ("Baron".equals(id)) {
                    c.setBackground(new Color(210, 220, 243));
                } else if ("Builder".equals(id)) {
                    c.setBackground(new Color(238, 217, 204));
                } else if ("Collector".equals(id)) {
                    c.setBackground(new Color(226, 241, 192));
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });
        return table;
    }

    private JTable initFieldsTable(String id, String[] columns) {
        JTable table = new JTable(new MyModel(columns, 0));
        table.setBounds(30, 40, 800, 100);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    decreaseFieldYield(id, row);
                }
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String state = (String) table.getModel().getValueAt(row, 1);

                if (state.equals("dry")) {
                    c.setBackground(new Color(208, 193, 121));
                } else if (state.equals("sown")) {
                    c.setBackground(new Color(161, 246, 172));
                } else if (state.equals("harvestable")) {
                    c.setBackground(new Color(238, 239, 162));
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });
        return table;
    }

    private JButton createRunButton() {
        JButton runButton = new JButton("Run Step");
        runButton.setActionCommand("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = e.getActionCommand();
                if (action.equals("Run")) {
                    environment.runSystemStep();
                }
            }
        });
        return runButton;
    }

    private JButton createRun10Button() {
        JButton run10Button = new JButton("Run 10 Steps");
        run10Button.setActionCommand("Run 10 Steps");
        run10Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = e.getActionCommand();
                if (action.equals("Run 10 Steps")) {
                    for (int i = 0; i < 10; ++i) {
                        environment.runSystemStep();
                    }
                }
            }
        });
        return run10Button;
    }

    private JButton createExitButton() {
        JButton exitButton = new JButton("Exit");
        exitButton.setActionCommand("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = e.getActionCommand();
                if (action.equals("Exit")) {
                    environment.shutdown();
                }
            }
        });
        return exitButton;
    }

    public static GUI getInstance() {
        return instance;
    }

    public static void refresh() {
        frame.revalidate();
        frame.repaint();
    }

    public static void setEnvironment(Environment env) {
        environment = env;
    }

    public void setAgentJob(String id, String s) {
        setAgentsValue(id, "Job", s);
    }

    public void setAgentAction(String id, String s) {
        setAgentsValue(id, "Action", s);
    }

    public void setAgentFood(String id, String s) {
        setAgentsValue(id, "Food", s);
    }

    public void setAgentFoodSatiety(String id, String s) {
        setAgentsValue(id, "Satiety", s);
    }

    public void setAgentWood(String id, String s) {
        setAgentsValue(id, "Wood", s);
    }

    public void setAgentFields(String id, Map<String, Field> fields) {
        if (!agentFieldsIDToRowIdx.containsKey(id)) {
            agentFieldsIDToRowIdx.put(id, new HashMap<String, Integer>());
            JTable fieldsTable = initFieldsTable(id, fieldColumns);
            agentFieldsTables.put(id, fieldsTable);

            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), id, TitledBorder.LEFT,
                    TitledBorder.TOP));
            panel.add(fieldsTable);
            fieldsPanel.add(panel);
        }

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            Field f = entry.getValue();
            setFieldsValue(id, f.getId(), "State", f.getState().string);
            setFieldsValue(id, f.getId(), "Yield", Integer.toString(f.getYield()));
        }

    }

    public void deleteAgent(String agentId) {
        if (agentsIdToRowIdx.containsKey(agentId)) {
            int index = agentsIdToRowIdx.get(agentId);
            ((DefaultTableModel) agentsTable.getModel()).removeRow(index);
            ((DefaultTableModel) agentsTable.getModel()).fireTableDataChanged();

            // deleteAgentFields(id);

            agentsIdToRowIdx.remove(agentId);
            for (Map.Entry<String, Integer> a : agentsIdToRowIdx.entrySet()) {
                if (a.getValue() > index) {
                    agentsIdToRowIdx.put(a.getKey(), a.getValue() - 1);
                }
            }
        }
    }

    /*
    public void deleteAgentFields(String id) {
        if (agentsIdToRowIdx.containsKey(id) && agentFieldsToFieldsIdx.containsKey(id)) {
            int index = agentFieldsToFieldsIdx.get(id);
            fieldsPanel.remove(index);
            agentFieldsTables.remove(id);

            agentFieldsToFieldsIdx.remove(id);
            for (Map.Entry<String, Integer> a : agentFieldsToFieldsIdx.entrySet()) {
                if (a.getValue() > index) {
                    agentFieldsToFieldsIdx.put(a.getKey(), a.getValue() - 1);
                }
            }
        }
    }
    */

    private void setAgentsValue(String id, String attr, String value) {
        setValue(agentsTable, agentsIdToRowIdx, agentsAttrToColIdx, id, attr, value);
    }

    private void setFieldsValue(String agentId, String fieldId, String attr, String value) {
        setValue(agentFieldsTables.get(agentId), agentFieldsIDToRowIdx.get(agentId), fieldsAttrToColIdx, fieldId, attr, value);
    }

    private void spawnAgentDebugger(int row) {
        String agentId = (String) agentsTable.getModel().getValueAt(row, 0);
        environment.spawnDebugger(agentId);
    }

    private void decreaseFieldYield(String agentId, int row) {
        String fieldId = (String) agentFieldsTables.get(agentId).getModel().getValueAt(row, 0);
        environment.decreaseFieldYield(agentId, fieldId);
    }

    private void setValue(JTable table, Map<String, Integer> idToRowIdx, Map<String, Integer> attrToColIdx, String id,
                          String attr, String value) {
        if (!idToRowIdx.containsKey(id)) {
            idToRowIdx.put(id, table.getModel().getRowCount());
            ((DefaultTableModel) table.getModel()).addRow(new String[]{id, "-", "-", "- (-)", "- (-)", "- (-)"});
        }

        int row = idToRowIdx.get(id);
        int col = attrToColIdx.get(attr);

        if (col >= 3) {
            String previousData = (String) table.getModel().getValueAt(row, col);
            String previousValue = previousData.split(" ")[0];
            value = String.format("%s (%s)", value, previousValue);
        }

        table.getModel().setValueAt(value, row, col);
        ((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, col);
    }

    static class MyModel extends DefaultTableModel {
        public MyModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
