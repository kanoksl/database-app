package database.test.gui;

public class ManageCategoriesWindow
        extends javax.swing.JFrame {

    /**
     * Creates new form EditCustomerInfoWindow.
     */
    public ManageCategoriesWindow() {
        this.initComponents();
        this.initListeners();
        this.setLocationRelativeTo(null);
        this.setColorTheme();

        this.setTitle("" + " - " + Const.APP_TITLE);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void setColorTheme() {

    }

    private void initListeners() {

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
        tableCategories_scrollPane = new javax.swing.JScrollPane();
        tableCategories = new javax.swing.JTable();
        btnNewCategory = new javax.swing.JButton();
        btnRenameCategory = new javax.swing.JButton();
        btnDeleteCategory = new javax.swing.JButton();

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        tableCategories.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category ID", "Category Name", "Number of Products", "Total Quantity"
            }
        ));
        tableCategories.setGridColor(new java.awt.Color(204, 204, 204));
        tableCategories.setRowHeight(20);
        tableCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableCategories_scrollPane.setViewportView(tableCategories);

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
    private javax.swing.JButton btnRenameCategory;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable tableCategories;
    private javax.swing.JScrollPane tableCategories_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}