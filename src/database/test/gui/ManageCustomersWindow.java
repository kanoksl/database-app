package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Customer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ManageCustomersWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();
    private List<Customer> customerList;

    public ManageCustomersWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        this.setTitle("Manage Customers - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);
        this.updateButtonsEnabled();
    }

    private void customerAdd() {
        Customer c = EditCustomerInfoWindow.showNewCustomerDialog(this);
        if (c != null) {
            customerList.add(c);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            int viewRow = table.convertRowIndexToView(customerList.size() - 1);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    private Customer getSelection() {
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        return customerList.get(modelRow);
    }

    private void customerView() {
        if (table.getSelectedRowCount() == 0 || customerList.isEmpty()) {
            return;
        }
        EditCustomerInfoWindow.showViewCustomerDialog(this, this.getSelection());
    }

    private void customerEdit() {
        if (table.getSelectedRowCount() == 0 || customerList.isEmpty()) {
            return;
        }
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);
        Customer selected = customerList.get(modelRow);
        Customer copy = database.queryCustomer(selected.getID());
        copy = EditCustomerInfoWindow.showEditCustomerDialog(this, copy);
        if (copy != null) {
            customerList.set(modelRow, copy);
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            viewRow = table.convertRowIndexToView(modelRow);
            table.setRowSelectionInterval(viewRow, viewRow);
        }
    }

    private void customerDelete() {
        if (table.getSelectedRowCount() == 0 || customerList.isEmpty()) {
            return;
        }
        Customer c = this.getSelection();
        boolean deleted = database.tryDeleteCustomer(c, this);
        if (deleted) {
            customerList.remove(c);
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
            customerList = database.queryAllCustomers();
            System.out.println("ManageCustomersWindow.refresh(): all customers");
        } else {
            customerList = database.queryCustomersByName(searchString);
            System.out.println("ManageCustomersWindow.refresh(): search for '" + searchString + "'");
        }
        TableModel model = Customer.createTableModel(customerList);
        table.setModel(model);

        // setting column headers and sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        final int width_id = 80, width_gender = 50, width_date = 80, width_phone = 90;
        TableColumnModel colm = table.getColumnModel();

        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(3).setMinWidth(width_gender);
        colm.getColumn(3).setMaxWidth(width_gender);
        colm.getColumn(3).setResizable(false);

        colm.getColumn(4).setMinWidth(width_date);
        colm.getColumn(4).setMaxWidth(width_date);
        colm.getColumn(4).setResizable(false);
        colm.getColumn(5).setMinWidth(width_date);
        colm.getColumn(5).setMaxWidth(width_date);
        colm.getColumn(5).setResizable(false);

        colm.getColumn(6).setMinWidth(width_phone);
        colm.getColumn(6).setMaxWidth(width_phone);
        colm.getColumn(6).setResizable(false);

        table.updateUI();

        // select the last row
        if (!customerList.isEmpty()) {
            int rowIndex = Math.max(0, table.getRowCount() - 1);
            table.setRowSelectionInterval(rowIndex, rowIndex);
        }

        this.updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
        if (table.getSelectedRowCount() > 0) {
            Customer selected = this.getSelection();
            if (selected.getID().equals(Const.UNREGISTERED_CUSTOMER_ID)
                    || selected.getID().equals(Const.DELETED_CUSTOMER_ID)) {
                btnEditCustomer.setEnabled(false);
                btnDeleteCustomer.setEnabled(false);
            } else {
                btnEditCustomer.setEnabled(true);
                btnDeleteCustomer.setEnabled(true);
            }
            btnViewCustomer.setEnabled(true);
        } else {
            btnEditCustomer.setEnabled(false);
            btnDeleteCustomer.setEnabled(false);
            btnViewCustomer.setEnabled(false);
        }
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
        btnNewCustomer.addActionListener((ActionEvent) -> {
            this.customerAdd();
        });
        btnViewCustomer.addActionListener((ActionEvent) -> {
            this.customerView();
        });
        btnEditCustomer.addActionListener((ActionEvent) -> {
            this.customerEdit();
        });
        btnDeleteCustomer.addActionListener((ActionEvent) -> {
            this.customerDelete();
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
        tableCustomers_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnNewCustomer = new javax.swing.JButton();
        btnEditCustomer = new javax.swing.JButton();
        btnDeleteCustomer = new javax.swing.JButton();
        btnViewCustomer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 340));
        setPreferredSize(new java.awt.Dimension(900, 540));
        setSize(new java.awt.Dimension(900, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Registered Customers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        tbxSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSearch.setToolTipText("Search customers by first name or last name.");
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
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Customer ID", "First Name", "Last Name", "Gender", "Birthday", "Registered", "Phone", "Email"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(20);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableCustomers_scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        getContentPane().add(tableCustomers_scrollPane, gridBagConstraints);

        btnNewCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnNewCustomer.setText("New Customer...");
        btnNewCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnNewCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnNewCustomer.setName(""); // NOI18N
        btnNewCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        getContentPane().add(btnNewCustomer, gridBagConstraints);

        btnEditCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnEditCustomer.setText("Edit Selected...");
        btnEditCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnEditCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnEditCustomer.setName(""); // NOI18N
        btnEditCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnEditCustomer, gridBagConstraints);

        btnDeleteCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDeleteCustomer.setText("Delete Selected");
        btnDeleteCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnDeleteCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnDeleteCustomer.setName(""); // NOI18N
        btnDeleteCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        getContentPane().add(btnDeleteCustomer, gridBagConstraints);

        btnViewCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewCustomer.setText("View Selected...");
        btnViewCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnViewCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnViewCustomer.setName(""); // NOI18N
        btnViewCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnViewCustomer, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteCustomer;
    private javax.swing.JButton btnEditCustomer;
    private javax.swing.JButton btnNewCustomer;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewCustomer;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableCustomers_scrollPane;
    private javax.swing.JTextField tbxSearch;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
}
