package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ManageCategoriesWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    public ManageCategoriesWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        this.setTitle("Manage Product Categories - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);
        this.updateButtonsEnabled();
    }

    private void categoryAdd() {
        String[] cat = EditCategoryInfoWindow.showNewCategoryDialog(this);
        if (cat[0] != null) {
            this.refresh();
            // select the added category
            for (int i = 0; i < table.getRowCount(); i++) {
                if (cat[0].equals(table.getValueAt(i, 0))) {
                    table.setRowSelectionInterval(i, i);
                    return;
                }
            }
        }
    }

    private void categoryRename() {
        int row = table.getSelectedRow();
        String id = table.getValueAt(row, 0).toString();
        String name = table.getValueAt(row, 1).toString();
        String[] cat = EditCategoryInfoWindow.showEditCategoryDialog(this, id, name);
        if (cat[0] != null) {
            this.refresh();
            // select the added category
            if (cat[0].equals(id)) {
                table.setRowSelectionInterval(row, row);
                return;
            }
            for (int i = 0; i < table.getRowCount(); i++) {
                if (cat[0].equals(table.getValueAt(i, 0))) {
                    table.setRowSelectionInterval(i, i);
                    return;
                }
            }
        }
    }

    private void categoryDelete() {
        int row = table.getSelectedRow();
        String id = table.getValueAt(row, 0).toString();
        String name = table.getValueAt(row, 1).toString();

        boolean deleted = database.tryDeleteCategory(id, name, this);
        if (deleted) {
            this.refresh();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Query the category list from the database, and update the table model and
     * also its UI.
     */
    @Override
    public void refresh() {
        System.out.println("ManageCategoriesWindow.refresh()");
        TableModel model = database.queryCategoryOverview();
        table.setModel(model);

        // setting column headers and sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        final int width_id = 90, width_numbers = 90;
        TableColumnModel colm = table.getColumnModel();

        colm.getColumn(0).setHeaderValue("Category ID");
        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setHeaderValue("Category Name");

        colm.getColumn(2).setHeaderValue("Product Count");
        colm.getColumn(2).setMinWidth(width_numbers);
        colm.getColumn(2).setMaxWidth(width_numbers);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(3).setHeaderValue("Stock Quantity");
        colm.getColumn(3).setMinWidth(width_numbers);
        colm.getColumn(3).setMaxWidth(width_numbers);
        colm.getColumn(3).setResizable(false);

        // set alignments
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        colm.getColumn(2).setCellRenderer(rightRenderer);
        colm.getColumn(3).setCellRenderer(rightRenderer);

        table.updateUI();

        // select the last row
        int rowIndex = Math.max(0, table.getRowCount() - 1);
        table.setRowSelectionInterval(rowIndex, rowIndex);

        this.updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
        boolean selectionNotEmpty = table.getSelectedRowCount() > 0;
        btnRenameCategory.setEnabled(selectionNotEmpty);
        btnDeleteCategory.setEnabled(selectionNotEmpty);
    }

    private void setColorTheme() {
        table.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        table.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        table.setGridColor(Const.COLOR_TABLE_GRID);
        table.setFont(Const.FONT_DEFAULT_12);
        table.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        table.setRowHeight(24);
    }

    private void initListeners() {
        btnRefresh.addActionListener((ActionEvent) -> {
            this.refresh();
        });
        btnNewCategory.addActionListener((ActionEvent) -> {
            this.categoryAdd();
        });
        btnRenameCategory.addActionListener((ActionEvent) -> {
            this.categoryRename();
        });
        btnDeleteCategory.addActionListener((ActionEvent) -> {
            this.categoryDelete();
        });

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            this.updateButtonsEnabled();
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
        btnRefresh = new javax.swing.JButton();
        tableCategories_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnNewCategory = new javax.swing.JButton();
        btnRenameCategory = new javax.swing.JButton();
        btnDeleteCategory = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(520, 540));
        setMinimumSize(new java.awt.Dimension(520, 540));
        setPreferredSize(new java.awt.Dimension(520, 540));
        setResizable(false);
        setSize(new java.awt.Dimension(520, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Product Categories");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.setMaximumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setMinimumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setName(""); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 8);
        panel_header.add(btnRefresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        table.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category ID", "Category Name", "Product Count", "Stock Quantity"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(20);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        tableCategories_scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        getContentPane().add(tableCategories_scrollPane, gridBagConstraints);

        btnNewCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnNewCategory.setText("New Category...");
        btnNewCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewCategory.setMaximumSize(new java.awt.Dimension(128, 26));
        btnNewCategory.setMinimumSize(new java.awt.Dimension(128, 26));
        btnNewCategory.setName(""); // NOI18N
        btnNewCategory.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        getContentPane().add(btnNewCategory, gridBagConstraints);

        btnRenameCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnRenameCategory.setText("Rename Selected...");
        btnRenameCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRenameCategory.setMaximumSize(new java.awt.Dimension(128, 26));
        btnRenameCategory.setMinimumSize(new java.awt.Dimension(128, 26));
        btnRenameCategory.setName(""); // NOI18N
        btnRenameCategory.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnRenameCategory, gridBagConstraints);

        btnDeleteCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDeleteCategory.setText("Delete Selected");
        btnDeleteCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteCategory.setMaximumSize(new java.awt.Dimension(128, 26));
        btnDeleteCategory.setMinimumSize(new java.awt.Dimension(128, 26));
        btnDeleteCategory.setName(""); // NOI18N
        btnDeleteCategory.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        getContentPane().add(btnDeleteCategory, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteCategory;
    private javax.swing.JButton btnNewCategory;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRenameCategory;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableCategories_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
