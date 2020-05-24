package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Field;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private static JFrame frame;

    private JTable agentsTable;
    private final Map<String, Integer> agentsIdToRowIdx;
    private final Map<String, Integer> agentsAttrToColIdx;
    private final String[] agentColumns;

    private JPanel fieldsPanel;
    private Map<String,JTable> fieldsTables;
    private final Map<String, Map<String, Integer>> fieldsIdToRowIdx;
    private final Map<String, Integer> fieldsAttrToColIdx;
    private final String[] fieldColumns;


    private GUI() {
        frame = new JFrame("Soar - AoE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 800);

        agentColumns = new String[] {"ID", "Action", "Food", "Satiety", "Wood"};
        agentsIdToRowIdx = new HashMap<>();
        agentsAttrToColIdx = initAttrToColIdx(agentColumns);
        agentsTable = initAgentsTable(agentColumns);

        fieldColumns = new String[] {"ID", "State", "Yield"};
        fieldsIdToRowIdx = new HashMap<>();
        fieldsTables = new HashMap<>();
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
        for(int i=0; i<columns.length; ++i) {
            columnsIdx.put(columns[i], i);
        }
        return columnsIdx;
    }

    private JTable initAgentsTable(String[] columns) {
        JTable table = new JTable(new MyModel(columns, 0));
        table.setBounds(30, 40, 800, 300);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String id = (String) table.getModel().getValueAt(row, 0);
                

                if (id.contains("Baron")) {
                    c.setBackground(new Color(210, 220, 243));
                } else if (id.contains("Builder")) {
                    c.setBackground(new Color(238, 217, 204));
                } else if (id.contains("Collector")) {
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
                    for(int i = 0; i < 10; ++i) {
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

    public void setAgentAction(String id, String s) { setAgentsValue(id, "Action", s); }

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
        if (!fieldsIdToRowIdx.containsKey(id)) {
            fieldsIdToRowIdx.put(id, new HashMap<>());
            JTable fieldsTable = initFieldsTable(id, fieldColumns);
            fieldsTables.put(id, fieldsTable);

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
    private void setAgentsValue(String id, String attr, String value) {
        setValue(agentsTable, agentsIdToRowIdx, agentsAttrToColIdx, id, attr, value);
    }

    private void setFieldsValue(String agentId, String fieldId, String attr, String value) {
        setValue(fieldsTables.get(agentId), fieldsIdToRowIdx.get(agentId), fieldsAttrToColIdx, fieldId, attr, value);
    }

    private void setValue(JTable table, Map<String, Integer> idToRowIdx, Map<String, Integer> attrToColIdx, String id,
                          String attr, String value) {
        if (!idToRowIdx.containsKey(id)) {
            idToRowIdx.put(id, table.getModel().getRowCount());
            ((DefaultTableModel) table.getModel()).addRow(new String[]{id, "-", "-", "-"});
        }

        int row = idToRowIdx.get(id);
        int col = attrToColIdx.get(attr);

        table.getModel().setValueAt(value, row, col);
        ((DefaultTableModel) table.getModel()).fireTableCellUpdated(row, col);
    }

    class MyModel extends DefaultTableModel {
        public MyModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
