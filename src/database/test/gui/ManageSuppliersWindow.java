package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Supplier;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ManageSuppliersWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();
    private List<Supplier> supplierList;

    public ManageSuppliersWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        this.setTitle("Manage Suppliers - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);
        this.updateButtonsEnabled();
    }

    private void supplierAdd() {
        Supplier s = EditSupplierInfoWindow.showNewSupplierDialog(this);
        if (s != null) {
            supplierList.add(s);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            int viewRow = table.convertRowIndexToView(supplierList.size() - 1);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    private Supplier getSelection() {
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        return supplierList.get(modelRow);
    }

    private void supplierView() {
        if (table.getSelectedRowCount() == 0 || supplierList.isEmpty()) {
            return;
        }
        EditSupplierInfoWindow.showViewSupplierDialog(this, this.getSelection());
    }

    private void supplierEdit() {
        if (table.getSelectedRowCount() == 0 || supplierList.isEmpty()) {
            return;
        }
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        Supplier selected = supplierList.get(modelRow);
        Supplier copy = database.querySupplier(selected.getID());
        copy = EditSupplierInfoWindow.showEditSupplierDialog(this, copy);
        if (copy != null) {
            supplierList.set(modelRow, copy);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            viewRow = table.convertRowIndexToView(modelRow);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    private void supplierDelete() {
        if (table.getSelectedRowCount() == 0 || supplierList.isEmpty()) {
            return;
        }
        Supplier s = this.getSelection();
        boolean deleted = database.tryDeleteSupplier(s, this);
        if (deleted) {
            supplierList.remove(s);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Query the category list from the database, and update the table model and
     * also its UI.
     */
    @Override
    public void refresh() {
        String searchString = tbxSearch.getText().trim();
        if (searchString.isEmpty()) {
            supplierList = database.queryAllSuppliers();
            System.out.println("ManageSuppliersWindow.refresh()");
        } else {
            supplierList = database.querySuppliersByName(searchString);
            System.out.println("ManageSuppliersWindow.refresh(): search for '" + searchString + "'");
        }
        TableModel model = Supplier.createTableModel(supplierList);
        table.setModel(model);

        // setting column headers and sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        final int width_id = 80, width_phone = 90;
        TableColumnModel colm = table.getColumnModel();

        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(3).setMinWidth(width_phone);
        colm.getColumn(3).setMaxWidth(width_phone);
        colm.getColumn(3).setResizable(false);

        table.updateUI();

        // select the last row
        if (!supplierList.isEmpty()) {
            int rowIndex = Math.max(0, table.getRowCount() - 1);
            table.setRowSelectionInterval(rowIndex, rowIndex);
        }

        this.updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
        boolean selectionNotEmpty = table.getSelectedRowCount() > 0;
        btnEditSupplier.setEnabled(selectionNotEmpty);
        btnDeleteSupplier.setEnabled(selectionNotEmpty);
        btnViewSupplier.setEnabled(selectionNotEmpty);
    }

    private void setColorTheme() {
        table.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        table.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        table.setGridColor(Const.COLOR_TABLE_GRID);
        table.setFont(Const.FONT_DEFAULT_12);
        table.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        table.setRowHeight(24);

        tbxSearch.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        tbxSearch.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
    }

    private void initListeners() {
        btnRefresh.addActionListener((ActionEvent) -> {
            this.refresh();
        });
        btnNewSupplier.addActionListener((ActionEvent) -> {
            this.supplierAdd();
        });
        btnViewSupplier.addActionListener((ActionEvent) -> {
            this.supplierView();
        });
        btnEditSupplier.addActionListener((ActionEvent) -> {
            this.supplierEdit();
        });
        btnDeleteSupplier.addActionListener((ActionEvent) -> {
            this.supplierDelete();
        });

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            this.updateButtonsEnabled();
        });

        tbxSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                }
            }
        });
    }

    //</editor-fold>
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

        panel_header = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        tbxSearch = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        tableSuppliers_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnNewSupplier = new javax.swing.JButton();
        btnEditSupplier = new javax.swing.JButton();
        btnDeleteSupplier = new javax.swing.JButton();
        btnViewSupplier = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 340));
        setPreferredSize(new java.awt.Dimension(900, 540));
        setSize(new java.awt.Dimension(900, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Suppliers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        tbxSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSearch.setToolTipText("Search suppliers by name.");
        tbxSearch.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_header.add(tbxSearch, gridBagConstraints);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.setMaximumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setMinimumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setName(""); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_header.add(btnRefresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        table.setAutoCreateRowSorter(true);
        table.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Supplier ID", "Name", "Address", "Phone", "Email", "Website"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(20);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableSuppliers_scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        getContentPane().add(tableSuppliers_scrollPane, gridBagConstraints);

        btnNewSupplier.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnNewSupplier.setText("New Supplier...");
        btnNewSupplier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewSupplier.setMaximumSize(new java.awt.Dimension(128, 26));
        btnNewSupplier.setMinimumSize(new java.awt.Dimension(128, 26));
        btnNewSupplier.setName(""); // NOI18N
        btnNewSupplier.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        getContentPane().add(btnNewSupplier, gridBagConstraints);

        btnEditSupplier.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnEditSupplier.setText("Edit Selected...");
        btnEditSupplier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditSupplier.setMaximumSize(new java.awt.Dimension(128, 26));
        btnEditSupplier.setMinimumSize(new java.awt.Dimension(128, 26));
        btnEditSupplier.setName(""); // NOI18N
        btnEditSupplier.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnEditSupplier, gridBagConstraints);

        btnDeleteSupplier.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDeleteSupplier.setText("Delete Selected");
        btnDeleteSupplier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSupplier.setMaximumSize(new java.awt.Dimension(128, 26));
        btnDeleteSupplier.setMinimumSize(new java.awt.Dimension(128, 26));
        btnDeleteSupplier.setName(""); // NOI18N
        btnDeleteSupplier.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        getContentPane().add(btnDeleteSupplier, gridBagConstraints);

        btnViewSupplier.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewSupplier.setText("View Selected...");
        btnViewSupplier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewSupplier.setMaximumSize(new java.awt.Dimension(128, 26));
        btnViewSupplier.setMinimumSize(new java.awt.Dimension(128, 26));
        btnViewSupplier.setName(""); // NOI18N
        btnViewSupplier.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnViewSupplier, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteSupplier;
    private javax.swing.JButton btnEditSupplier;
    private javax.swing.JButton btnNewSupplier;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewSupplier;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableSuppliers_scrollPane;
    private javax.swing.JTextField tbxSearch;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
}
