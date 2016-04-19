package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.SQLStrings;

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

public class SelectorWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private String sql = "";
    private List<String[]> data = new ArrayList<>();
    private List<String> selection;

    private SelectorWindow() {
        this.initComponents();
        this.setColorTheme();

        tbxSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                }
            }
        });
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            btnSelect.setEnabled(!data.isEmpty() && table.getSelectedRow() >= 0);
        });
        btnSelect.addActionListener((ActionEvent) -> {
            selection = new ArrayList<>();
            for (int i : table.getSelectedRows()) {
                selection.add((String) table.getValueAt(i, 0));
            }
            SwingUtilities.getWindowAncestor(btnSelect).dispose();
        });
        btnCancel.addActionListener((ActionEvent) -> {
            selection = null;
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });

        table.setModel(new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return (column == 0) ? "ID" : "Name";
            }
            
            @Override
            public int getRowCount() {
                return data.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return data.get(rowIndex)[columnIndex];
            }
        });
    }

    private void refresh() {
        System.out.println("SelectorWindow.refresh()");
        data = database.queryIDsAndNames(sql, tbxSearch.getText().trim());
        btnSelect.setEnabled(!data.isEmpty() && table.getSelectedRow() >= 0);
        table.updateUI();
    }

    private void setColorTheme() {
        table.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        table.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        table.setGridColor(Const.COLOR_TABLE_GRID);
        table.setFont(Const.FONT_DEFAULT_12);
        table.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        table.setRowHeight(20);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Automatically Generated by NetBeans">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel_content = new javax.swing.JPanel();
        table_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        panel_commandButtons = new javax.swing.JPanel();
        btnSelect = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        tbxSearch = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 400));
        setPreferredSize(new java.awt.Dimension(400, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_content.setLayout(new java.awt.GridBagLayout());

        table.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID", "Name"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(20);
        table_scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        panel_content.add(table_scrollPane, gridBagConstraints);

        panel_commandButtons.setLayout(new java.awt.GridBagLayout());

        btnSelect.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSelect.setText("Select");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setMaximumSize(new java.awt.Dimension(128, 36));
        btnSelect.setMinimumSize(new java.awt.Dimension(128, 36));
        btnSelect.setName(""); // NOI18N
        btnSelect.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        panel_commandButtons.add(btnSelect, gridBagConstraints);

        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setMaximumSize(new java.awt.Dimension(128, 36));
        btnCancel.setMinimumSize(new java.awt.Dimension(128, 36));
        btnCancel.setName(""); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        panel_commandButtons.add(btnCancel, gridBagConstraints);

        tbxSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSearch.setToolTipText("Search the list by ID or name.");
        tbxSearch.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        panel_commandButtons.add(tbxSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panel_content.add(panel_commandButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panel_content, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSelect;
    private javax.swing.JPanel panel_commandButtons;
    private javax.swing.JPanel panel_content;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane table_scrollPane;
    private javax.swing.JTextField tbxSearch;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
    public static List<String> showProductSelectorDialog(Frame owner) {
        SelectorWindow win = new SelectorWindow();
        win.sql = SQLStrings.SQL_ID_AND_NAME_PRODUCTS;
        win.refresh();

        Util.createAndShowDialog(owner, "Select Product(s)",
                win.panel_content, win.getPreferredSize());
        System.out.println("showProductSelectorDialog() returning: " + win.selection);
        return win.selection;
    }

    public static List<String> showSupplierSelectorDialog(Frame owner) {
        SelectorWindow win = new SelectorWindow();
        win.sql = SQLStrings.SQL_ID_AND_NAME_SUPPLIERS;
        win.refresh();

        Util.createAndShowDialog(owner, "Select Product(s)",
                win.panel_content, win.getPreferredSize());
        System.out.println("showProductSelectorDialog() returning: " + win.selection);
        return win.selection;
    }

}
