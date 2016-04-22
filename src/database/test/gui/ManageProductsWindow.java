package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Product;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ManageProductsWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private List<Product> productList;

    public ManageProductsWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        this.setTitle("Manage Products - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);

        chkFiltering.setSelected(false);
        cbxStatus.setEnabled(false);
        tbxCategory.setEnabled(false);
        this.updateButtonsEnabled();
    }

    private void productAdd() {
        Product s = EditProductInfoWindow.showNewProductDialog(this);
        if (s != null) {
            productList.add(s);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            int viewRow = table.convertRowIndexToView(productList.size() - 1);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    private Product getSelection() {
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        return productList.get(modelRow);
    }

    private void productView() {
        if (table.getSelectedRowCount() == 0 || productList.isEmpty()) {
            return;
        }
        EditProductInfoWindow.showViewProductDialog(this, this.getSelection());
    }

    private void productEdit() {
        if (table.getSelectedRowCount() == 0 || productList.isEmpty()) {
            return;
        }
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        Product selected = productList.get(modelRow);
        Product copy = database.queryProduct(selected.getID());
        copy = EditProductInfoWindow.showEditProductDialog(this, copy);
        if (copy != null) {
            productList.set(modelRow, copy);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            viewRow = table.convertRowIndexToView(modelRow);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    @Override
    public void refresh() {
        System.out.println("ManageProductsWindow.refresh()");
        productList = database.queryProductsByFilter(this.getSQLQueryConditions());

        TableModel model = Product.createTableModel(productList);
        table.setModel(model);

        // setting column headers and sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        final int width_id = 80, width_num = 90;
        TableColumnModel colm = table.getColumnModel();

        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(2).setMinWidth(width_id);
        colm.getColumn(2).setMaxWidth(width_id);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(3).setMinWidth(width_num);
        colm.getColumn(3).setMaxWidth(width_num);
        colm.getColumn(3).setResizable(false);

        colm.getColumn(4).setMinWidth(width_num);
        colm.getColumn(4).setMaxWidth(width_num);
        colm.getColumn(4).setResizable(false);

        colm.getColumn(3).setCellRenderer(Util.TABLE_CELL_STOCK_QUANTITY);
        colm.getColumn(4).setCellRenderer(Util.TABLE_CELL_MONEY);

        table.updateUI();

        // select the last row
        if (!productList.isEmpty()) {
            int rowIndex = Math.max(0, table.getRowCount() - 1);
            table.setRowSelectionInterval(rowIndex, rowIndex);
        }

        this.updateButtonsEnabled();
        System.out.println("ManageProductsWindow.refresh(): done. " + productList.size() + " results.");
    }

    private void updateButtonsEnabled() {
        boolean selectionNotEmpty = table.getSelectedRowCount() > 0;
        btnEditProduct.setEnabled(selectionNotEmpty);
        btnViewProduct.setEnabled(selectionNotEmpty);
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
        btnNewProduct.addActionListener((ActionEvent) -> {
            this.productAdd();
        });
        btnViewProduct.addActionListener((ActionEvent) -> {
            this.productView();
        });
        btnEditProduct.addActionListener((ActionEvent) -> {
            this.productEdit();
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
        tbxCategory.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                }
            }
        });
        cbxStatus.addActionListener((ActionEvent) -> {
            this.refresh();
        });

        chkFiltering.addActionListener((ActionEvent) -> {
            cbxStatus.setEnabled(chkFiltering.isSelected());
            tbxCategory.setEnabled(chkFiltering.isSelected());
            this.refresh();
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
        tbxSearch = new javax.swing.JTextField();
        panel_filter = new javax.swing.JPanel();
        chkFiltering = new javax.swing.JCheckBox();
        javax.swing.JLabel l_filterCat = new javax.swing.JLabel();
        tbxCategory = new javax.swing.JTextField();
        javax.swing.JLabel l_filterStatus = new javax.swing.JLabel();
        cbxStatus = new javax.swing.JComboBox<>();
        tableProducts_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnNewProduct = new javax.swing.JButton();
        btnEditProduct = new javax.swing.JButton();
        btnViewProduct = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 350));
        setSize(new java.awt.Dimension(520, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Products");
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

        tbxSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSearch.setToolTipText("Search products by product name.");
        tbxSearch.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_header.add(tbxSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        panel_filter.setLayout(new java.awt.GridBagLayout());

        chkFiltering.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkFiltering.setSelected(true);
        chkFiltering.setText("Enable Filtering:");
        chkFiltering.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        panel_filter.add(chkFiltering, gridBagConstraints);

        l_filterCat.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterCat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterCat.setText("Category:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        panel_filter.add(l_filterCat, gridBagConstraints);

        tbxCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxCategory.setToolTipText("Category ID or name.");
        tbxCategory.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel_filter.add(tbxCategory, gridBagConstraints);

        l_filterStatus.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterStatus.setText("Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 4);
        panel_filter.add(l_filterStatus, gridBagConstraints);

        cbxStatus.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available for Sale", "Out of Stock", "--------", "Still Selling", "Discontinued", "--------", "Any" }));
        cbxStatus.setMinimumSize(new java.awt.Dimension(128, 22));
        cbxStatus.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        panel_filter.add(cbxStatus, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 0, 8);
        getContentPane().add(panel_filter, gridBagConstraints);

        table.setAutoCreateRowSorter(true);
        table.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Product ID", "Product Name", "Category ID", "Stock Quantity", "Current Price"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(20);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableProducts_scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        getContentPane().add(tableProducts_scrollPane, gridBagConstraints);

        btnNewProduct.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnNewProduct.setText("New Product...");
        btnNewProduct.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewProduct.setMaximumSize(new java.awt.Dimension(128, 26));
        btnNewProduct.setMinimumSize(new java.awt.Dimension(128, 26));
        btnNewProduct.setName(""); // NOI18N
        btnNewProduct.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        getContentPane().add(btnNewProduct, gridBagConstraints);

        btnEditProduct.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnEditProduct.setText("Edit Selected Product Info...");
        btnEditProduct.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditProduct.setMaximumSize(new java.awt.Dimension(196, 26));
        btnEditProduct.setMinimumSize(new java.awt.Dimension(196, 26));
        btnEditProduct.setName(""); // NOI18N
        btnEditProduct.setPreferredSize(new java.awt.Dimension(196, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        getContentPane().add(btnEditProduct, gridBagConstraints);

        btnViewProduct.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewProduct.setText("View Selected...");
        btnViewProduct.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewProduct.setMaximumSize(new java.awt.Dimension(128, 26));
        btnViewProduct.setMinimumSize(new java.awt.Dimension(128, 26));
        btnViewProduct.setName(""); // NOI18N
        btnViewProduct.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnViewProduct, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditProduct;
    private javax.swing.JButton btnNewProduct;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewProduct;
    private javax.swing.JComboBox<String> cbxStatus;
    private javax.swing.JCheckBox chkFiltering;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_filter;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableProducts_scrollPane;
    private javax.swing.JTextField tbxCategory;
    private javax.swing.JTextField tbxSearch;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    private static final int FILTER_STATUS_AVAILABLE = 0;
    private static final int FILTER_STATUS_OUT_OF_STOCK = 1;
    private static final int FILTER_STATUS_SELLING = 3;
    private static final int FILTER_STATUS_DISCONTINUED = 4;
    private static final int NO_FILTER_STATUS = 6;

    /**
     * Translate GUI search options into SQL query.
     *
     * @return A part of SQL WHERE clause, specifying the selected filter.
     * Example: " AND (pd.product_name LIKE %abc%);"
     */
    public String getSQLQueryConditions() {
        StringBuilder sql = new StringBuilder();

        String nameSearch = tbxSearch.getText().trim();
        if (!nameSearch.isEmpty()) {
            sql.append(" AND (pd.product_name LIKE '%")
                    .append(nameSearch).append("%')");
        }

        if (!chkFiltering.isSelected()) {
            return sql.append(";").toString();
        }

        String catSearch = tbxCategory.getText().trim();
        if (!catSearch.isEmpty()) {
            sql.append(" AND (pd.category_id LIKE '%").append(catSearch)
                    .append("%' OR c.category_name LIKE '%")
                    .append(catSearch).append("%')");
        }

        switch (cbxStatus.getSelectedIndex()) {
            case FILTER_STATUS_AVAILABLE:
                sql.append(" AND (selling_status = 1) AND (stock_quantity > 0)");
                break;
            case FILTER_STATUS_OUT_OF_STOCK:
                sql.append(" AND (selling_status = 1) AND (stock_quantity <= 0)");
                break;
            case FILTER_STATUS_SELLING:
                sql.append(" AND (selling_status = 1)");
                break;
            case FILTER_STATUS_DISCONTINUED:
                sql.append(" AND (selling_status = 0)");
                break;
            default:
        }

        return sql.append(";").toString();
    }
}
